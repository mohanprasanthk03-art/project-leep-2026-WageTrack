package com.example.wagetrack.controller;

import com.example.wagetrack.dto.*;
import com.example.wagetrack.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    private final AttendanceService service;
    public AttendanceController(AttendanceService service) { this.service = service; }
    @PostMapping public ResponseEntity<AttendanceResponse> create(@Valid @RequestBody AttendanceRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r));
    }
    @GetMapping public List<AttendanceResponse> getAll() { return service.getAll(); }
    @GetMapping("/{id}") public AttendanceResponse getById(@PathVariable Long id) { return service.getById(id); }
    @PutMapping("/{id}") public AttendanceResponse update(@PathVariable Long id, @Valid @RequestBody AttendanceRequest r) { return service.update(id, r); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
