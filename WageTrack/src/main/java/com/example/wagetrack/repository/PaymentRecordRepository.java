package com.example.wagetrack.repository;

import com.example.wagetrack.entity.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {
    boolean existsByWorkerIdAndWeekStartDate(Long workerId, java.time.LocalDate weekStartDate);
    boolean existsByWorkerIdAndWeekStartDateAndIdNot(Long workerId, java.time.LocalDate weekStartDate, Long id);
}
