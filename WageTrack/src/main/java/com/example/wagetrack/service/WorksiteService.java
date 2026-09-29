package com.example.wagetrack.service;

import com.example.wagetrack.dto.*;
import com.example.wagetrack.entity.Worksite;
import com.example.wagetrack.exception.*;
import com.example.wagetrack.repository.WorksiteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class WorksiteService {
    private final WorksiteRepository repository;
    public WorksiteService(WorksiteRepository repository) { this.repository = repository; }

    public WorksiteResponse create(WorksiteRequest r) {
        if (repository.existsBySiteCode(r.siteCode())) throw new DuplicateResourceException("Site code already exists.");
        Worksite w = new Worksite(); apply(w, r); w.setActive(r.active() == null || r.active());
        return toResponse(repository.save(w));
    }
    @Transactional(readOnly = true)
    public List<WorksiteResponse> getAll() { return repository.findAll().stream().map(this::toResponse).toList(); }
    @Transactional(readOnly = true)
    public WorksiteResponse getById(Long id) { return toResponse(find(id)); }
    public WorksiteResponse update(Long id, WorksiteRequest r) {
        Worksite w = find(id);
        if (repository.existsBySiteCodeAndIdNot(r.siteCode(), id)) throw new DuplicateResourceException("Site code already exists.");
        apply(w, r); if (r.active() != null) w.setActive(r.active());
        return toResponse(repository.save(w));
    }
    public void deactivate(Long id) { find(id).setActive(false); }
    private Worksite find(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Worksite not found: " + id));
    }
    private void apply(Worksite w, WorksiteRequest r) {
        w.setSiteCode(r.siteCode().trim()); w.setSiteName(r.siteName().trim());
        w.setLocation(r.location().trim()); w.setDescription(r.description());
    }
    private WorksiteResponse toResponse(Worksite w) {
        return new WorksiteResponse(w.getId(), w.getSiteCode(), w.getSiteName(),
                w.getLocation(), w.getDescription(), w.isActive());
    }
}
