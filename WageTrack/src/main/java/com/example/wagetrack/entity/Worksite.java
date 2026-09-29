package com.example.wagetrack.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "worksites", uniqueConstraints = @UniqueConstraint(name = "uk_site_code", columnNames = "site_code"))
public class Worksite {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "site_code", nullable = false, unique = true, length = 30)
    private String siteCode;
    @Column(name = "site_name", nullable = false, length = 120)
    private String siteName;
    @Column(nullable = false, length = 200)
    private String location;
    @Column(length = 500)
    private String description;
    @Column(nullable = false)
    private boolean active = true;

    @JsonIgnore
    @OneToMany(mappedBy = "worksite")
    private List<Attendance> attendanceRecords = new ArrayList<>();

    public Long getId() { return id; }
    public String getSiteCode() { return siteCode; }
    public void setSiteCode(String siteCode) { this.siteCode = siteCode; }
    public String getSiteName() { return siteName; }
    public void setSiteName(String siteName) { this.siteName = siteName; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
