package com.example.wagetrack.controller;

import com.example.wagetrack.dto.*;
import com.example.wagetrack.service.WorksiteService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/worksites")
public class WorksiteController {
    private final WorksiteService service;
    public WorksiteController(WorksiteService service) { this.service = service; }
    @PostMapping public ResponseEntity<WorksiteResponse> create(@Valid @RequestBody WorksiteRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r));
    }
    @GetMapping public List<WorksiteResponse> getAll() { return service.getAll(); }
    @GetMapping("/{id}") public WorksiteResponse getById(@PathVariable Long id) { return service.getById(id); }
    @PutMapping("/{id}") public WorksiteResponse update(@PathVariable Long id, @Valid @RequestBody WorksiteRequest r) { return service.update(id, r); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) { service.deactivate(id); return ResponseEntity.noContent().build(); }
}
