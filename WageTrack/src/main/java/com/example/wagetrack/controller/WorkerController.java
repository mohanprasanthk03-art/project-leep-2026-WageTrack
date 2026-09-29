package com.example.wagetrack.controller;

import com.example.wagetrack.dto.*;
import com.example.wagetrack.service.WorkerService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/workers")
public class WorkerController {
    private final WorkerService service;
    public WorkerController(WorkerService service) { this.service = service; }
    @PostMapping public ResponseEntity<WorkerResponse> create(@Valid @RequestBody WorkerRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r));
    }
    @GetMapping public List<WorkerResponse> getAll() { return service.getAll(); }
    @GetMapping("/{id}") public WorkerResponse getById(@PathVariable Long id) { return service.getById(id); }
    @PutMapping("/{id}") public WorkerResponse update(@PathVariable Long id, @Valid @RequestBody WorkerRequest r) { return service.update(id, r); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) { service.deactivate(id); return ResponseEntity.noContent().build(); }
}
