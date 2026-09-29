package com.example.wagetrack.service;

import com.example.wagetrack.dto.*;
import com.example.wagetrack.entity.*;
import com.example.wagetrack.enums.AttendanceStatus;
import com.example.wagetrack.exception.*;
import com.example.wagetrack.repository.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final WorkerRepository workerRepository;
    private final WorksiteRepository worksiteRepository;

    public AttendanceService(AttendanceRepository a, WorkerRepository w, WorksiteRepository s) {
        this.attendanceRepository = a; this.workerRepository = w; this.worksiteRepository = s;
    }

    public AttendanceResponse create(AttendanceRequest r) {
        validateStatusAndOvertime(r.status(), r.overtimeHours());
        Worker worker = findWorker(r.workerId()); Worksite site = findSite(r.worksiteId());
        if (!worker.isActive()) throw new InvalidAttendanceException("Inactive workers cannot receive new attendance.");
        if (!site.isActive()) throw new InvalidAttendanceException("Inactive worksites cannot receive new attendance.");
        if (attendanceRepository.existsByWorkerIdAndAttendanceDate(r.workerId(), r.attendanceDate()))
            throw new DuplicateAttendanceException("Attendance already exists for this worker and date.");
        Attendance a = new Attendance(); apply(a, r, worker, site);
        try { return toResponse(attendanceRepository.save(a)); }
        catch (DataIntegrityViolationException ex) {
            throw new DuplicateAttendanceException("Attendance already exists for this worker and date.");
        }
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAll() { return attendanceRepository.findAll().stream().map(this::toResponse).toList(); }
    @Transactional(readOnly = true)
    public AttendanceResponse getById(Long id) { return toResponse(findAttendance(id)); }

    public AttendanceResponse update(Long id, AttendanceRequest r) {
        validateStatusAndOvertime(r.status(), r.overtimeHours());
        Attendance a = findAttendance(id); Worker worker = findWorker(r.workerId()); Worksite site = findSite(r.worksiteId());
        if (!worker.isActive()) throw new InvalidAttendanceException("Inactive workers cannot receive new attendance.");
        if (!site.isActive()) throw new InvalidAttendanceException("Inactive worksites cannot receive new attendance.");
        if (attendanceRepository.existsByWorkerIdAndAttendanceDateAndIdNot(r.workerId(), r.attendanceDate(), id))
            throw new DuplicateAttendanceException("Attendance already exists for this worker and date.");
        apply(a, r, worker, site);
        return toResponse(attendanceRepository.save(a));
    }

    public void delete(Long id) { attendanceRepository.delete(findAttendance(id)); }

    private void validateStatusAndOvertime(AttendanceStatus status, BigDecimal hours) {
        if (hours == null || hours.compareTo(BigDecimal.ZERO) < 0)
            throw new InvalidAttendanceException("Overtime hours cannot be negative.");
        if (status == AttendanceStatus.ABSENT && hours.compareTo(BigDecimal.ZERO) > 0)
            throw new InvalidAttendanceException("Absent attendance cannot contain overtime hours.");
    }
    private Worker findWorker(Long id) {
        return workerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Worker not found: " + id));
    }
    private Worksite findSite(Long id) {
        return worksiteRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Worksite not found: " + id));
    }
    private Attendance findAttendance(Long id) {
        return attendanceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Attendance not found: " + id));
    }
    private void apply(Attendance a, AttendanceRequest r, Worker w, Worksite s) {
        a.setWorker(w); a.setWorksite(s); a.setAttendanceDate(r.attendanceDate());
        a.setStatus(r.status()); a.setOvertimeHours(r.overtimeHours());
    }
    private AttendanceResponse toResponse(Attendance a) {
        return new AttendanceResponse(a.getId(), a.getWorker().getId(), a.getWorker().getName(),
                a.getWorksite().getId(), a.getWorksite().getSiteName(), a.getAttendanceDate(),
                a.getStatus(), a.getOvertimeHours(), a.getCreatedAt());
    }
}
