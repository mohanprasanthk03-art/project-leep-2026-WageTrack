package com.example.wagetrack.service;

import com.example.wagetrack.dto.*;
import com.example.wagetrack.entity.*;
import com.example.wagetrack.enums.PaymentStatus;
import com.example.wagetrack.exception.*;
import com.example.wagetrack.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PaymentService {
    private final PaymentRecordRepository paymentRepository;
    private final WorkerRepository workerRepository;
    private final PayrollService payrollService;

    public PaymentService(PaymentRecordRepository p, WorkerRepository w, PayrollService payroll) {
        this.paymentRepository = p; this.workerRepository = w; this.payrollService = payroll;
    }

    public PaymentResponse create(PaymentRequest r) {
        validateDates(r.weekStartDate(), r.weekEndDate());
        Worker worker = findWorker(r.workerId());
        if (!worker.isActive()) throw new InvalidPaymentException("Inactive workers cannot receive new payments.");
        if (paymentRepository.existsByWorkerIdAndWeekStartDate(r.workerId(), r.weekStartDate()))
            throw new InvalidPaymentException("A payment record already exists for this worker and week start date.");
        PaymentRecord p = new PaymentRecord(); apply(p, r, worker, null);
        return toResponse(paymentRepository.save(p));
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getAll() { return paymentRepository.findAll().stream().map(this::toResponse).toList(); }
    @Transactional(readOnly = true)
    public PaymentResponse getById(Long id) { return toResponse(findPayment(id)); }

    public PaymentResponse update(Long id, PaymentRequest r) {
        validateDates(r.weekStartDate(), r.weekEndDate());
        PaymentRecord p = findPayment(id); Worker worker = findWorker(r.workerId());
        if (!worker.isActive()) throw new InvalidPaymentException("Inactive workers cannot receive new payments.");
        if (paymentRepository.existsByWorkerIdAndWeekStartDateAndIdNot(r.workerId(), r.weekStartDate(), id))
            throw new InvalidPaymentException("A payment record already exists for this worker and week start date.");
        apply(p, r, worker, id);
        return toResponse(paymentRepository.save(p));
    }

    private void apply(PaymentRecord p, PaymentRequest r, Worker worker, Long existingId) {
        WeeklyPayrollResponse summary = payrollService.calculate(r.workerId(), r.weekStartDate(), r.weekEndDate());
        BigDecimal amountPaid = r.amountPaid();
        if (amountPaid.compareTo(BigDecimal.ZERO) < 0) throw new InvalidPaymentException("Amount paid cannot be negative.");
        if (amountPaid.compareTo(summary.totalPayable()) > 0)
            throw new InvalidPaymentException("Amount paid cannot exceed the calculated payable amount of " + summary.totalPayable() + ".");
        p.setWorker(worker); p.setWeekStartDate(r.weekStartDate()); p.setWeekEndDate(r.weekEndDate());
        p.setPayableAmount(summary.totalPayable()); p.setAmountPaid(amountPaid);
        p.setPaymentDate(r.paymentDate()); p.setRemarks(r.remarks());
        if (amountPaid.compareTo(BigDecimal.ZERO) == 0) p.setPaymentStatus(PaymentStatus.PENDING);
        else if (amountPaid.compareTo(summary.totalPayable()) < 0) p.setPaymentStatus(PaymentStatus.PARTIALLY_PAID);
        else p.setPaymentStatus(PaymentStatus.PAID);
    }

    private void validateDates(LocalDate start, LocalDate end) {
        if (end.isBefore(start)) throw new InvalidPaymentException("weekEndDate must be on or after weekStartDate.");
    }
    private Worker findWorker(Long id) {
        return workerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Worker not found: " + id));
    }
    private PaymentRecord findPayment(Long id) {
        return paymentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + id));
    }
    private PaymentResponse toResponse(PaymentRecord p) {
        return new PaymentResponse(p.getId(), p.getWorker().getId(), p.getWorker().getName(),
                p.getWeekStartDate(), p.getWeekEndDate(), p.getPayableAmount(), p.getAmountPaid(),
                p.getPaymentDate(), p.getPaymentStatus(), p.getRemarks());
    }
}
