package com.example.wagetrack.repository;

import com.example.wagetrack.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkerRepository extends JpaRepository<Worker, Long> {
    boolean existsByWorkerCode(String workerCode);
    boolean existsByWorkerCodeAndIdNot(String workerCode, Long id);
}
