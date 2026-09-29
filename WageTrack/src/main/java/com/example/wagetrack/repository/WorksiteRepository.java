package com.example.wagetrack.repository;

import com.example.wagetrack.entity.Worksite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorksiteRepository extends JpaRepository<Worksite, Long> {
    boolean existsBySiteCode(String siteCode);
    boolean existsBySiteCodeAndIdNot(String siteCode, Long id);
}
