package com.example.wagetrack.controller;

import com.example.wagetrack.dto.*;
import com.example.wagetrack.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService service;
    public PaymentController(PaymentService service) { this.service = service; }
    @PostMapping public ResponseEntity<PaymentResponse> create(@Valid @RequestBody PaymentRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r));
    }
    @GetMapping public List<PaymentResponse> getAll() { return service.getAll(); }
    @GetMapping("/{id}") public PaymentResponse getById(@PathVariable Long id) { return service.getById(id); }
    @PutMapping("/{id}") public PaymentResponse update(@PathVariable Long id, @Valid @RequestBody PaymentRequest r) { return service.update(id, r); }
}
