package com.example.wagetrack.service;

import com.example.wagetrack.dto.*;
import com.example.wagetrack.entity.Worker;
import com.example.wagetrack.exception.*;
import com.example.wagetrack.repository.WorkerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class WorkerService {
    private final WorkerRepository repository;
    public WorkerService(WorkerRepository repository) { this.repository = repository; }

    public WorkerResponse create(WorkerRequest request) {
        if (repository.existsByWorkerCode(request.workerCode()))
            throw new DuplicateResourceException("Worker code already exists.");
        Worker worker = new Worker();
        apply(worker, request);
        worker.setActive(request.active() == null || request.active());
        return toResponse(repository.save(worker));
    }

    @Transactional(readOnly = true)
    public List<WorkerResponse> getAll() { return repository.findAll().stream().map(this::toResponse).toList(); }

    @Transactional(readOnly = true)
    public WorkerResponse getById(Long id) { return toResponse(find(id)); }

    public WorkerResponse update(Long id, WorkerRequest request) {
        Worker worker = find(id);
        if (repository.existsByWorkerCodeAndIdNot(request.workerCode(), id))
            throw new DuplicateResourceException("Worker code already exists.");
        apply(worker, request);
        if (request.active() != null) worker.setActive(request.active());
        return toResponse(repository.save(worker));
    }

    public void deactivate(Long id) { Worker worker = find(id); worker.setActive(false); }

    private Worker find(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Worker not found: " + id));
    }
    private void apply(Worker w, WorkerRequest r) {
        w.setWorkerCode(r.workerCode().trim()); w.setName(r.name().trim()); w.setPhone(r.phone().trim());
        w.setAddress(r.address()); w.setDailyWage(r.dailyWage());
    }
    private WorkerResponse toResponse(Worker w) {
        return new WorkerResponse(w.getId(), w.getWorkerCode(), w.getName(), w.getPhone(),
                w.getAddress(), w.getDailyWage(), w.isActive(), w.getCreatedAt());
    }
}
