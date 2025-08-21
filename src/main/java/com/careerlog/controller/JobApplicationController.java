package com.careerlog.controller;

import com.careerlog.model.JobApplication;
import com.careerlog.service.JobApplicationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*") // allow frontend calls
public class JobApplicationController {

    private final JobApplicationService service;

    public JobApplicationController(JobApplicationService service) {
        this.service = service;
    }

    // ---- CRUD ----
    @GetMapping
    public List<JobApplication> getAll() {
        return service.getAllApplications();
    }

    @GetMapping("/{id}")
    public JobApplication getById(@PathVariable Long id) {
        return service.getApplicationById(id);
    }

    @PostMapping
    public JobApplication create(@RequestBody JobApplication jobApplication) {
        return service.saveApplication(jobApplication);
    }

    @PutMapping("/{id}")
    public JobApplication update(@PathVariable Long id, @RequestBody JobApplication updatedApp) {
        JobApplication existing = service.getApplicationById(id);
        if (existing == null) return null; // keep simple; you can throw 404 if you prefer

        existing.setCompanyName(updatedApp.getCompanyName());
        existing.setJobTitle(updatedApp.getJobTitle());
        existing.setJobLink(updatedApp.getJobLink());
        existing.setDateApplied(updatedApp.getDateApplied());
        existing.setStatus(updatedApp.getStatus());
        existing.setNotes(updatedApp.getNotes());
        return service.saveApplication(existing);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteApplication(id);
    }

    // ---- Enhancements ----

    // Search by company: /api/applications/search?company=google
    @GetMapping("/search")
    public List<JobApplication> searchByCompany(@RequestParam String company) {
        return service.searchByCompany(company);
    }

    // Filter by status (case-insensitive): /api/applications/status/INTERVIEW
    @GetMapping("/status/{status}")
    public List<JobApplication> getByStatus(@PathVariable String status) {
        return service.filterByStatus(status);
    }

    // Sort by date applied: /api/applications/sorted?order=asc|desc
    @GetMapping("/sorted")
    public List<JobApplication> getSorted(@RequestParam(defaultValue = "asc") String order) {
        return service.sortByDate(order);
    }

    // Dashboard stats
    @GetMapping("/stats")
    public Map<String, Long> getStats() {
        return service.getStats();
    }
}
