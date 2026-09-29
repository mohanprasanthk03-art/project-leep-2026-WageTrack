package com.example.wagetrack.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workers", uniqueConstraints = @UniqueConstraint(name = "uk_worker_code", columnNames = "worker_code"))
public class Worker {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "worker_code", nullable = false, unique = true, length = 30)
    private String workerCode;
    @Column(nullable = false, length = 100)
    private String name;
    @Column(nullable = false, length = 16)
    private String phone;
    @Column(length = 500)
    private String address;
    @Column(name = "daily_wage", nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyWage;
    @Column(nullable = false)
    private boolean active = true;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "worker")
    private List<Attendance> attendanceRecords = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "worker")
    private List<PaymentRecord> paymentRecords = new ArrayList<>();

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public String getWorkerCode() { return workerCode; }
    public void setWorkerCode(String workerCode) { this.workerCode = workerCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public BigDecimal getDailyWage() { return dailyWage; }
    public void setDailyWage(BigDecimal dailyWage) { this.dailyWage = dailyWage; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
