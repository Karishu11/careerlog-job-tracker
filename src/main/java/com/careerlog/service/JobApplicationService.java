package com.careerlog.service;

import com.careerlog.model.JobApplication;
import com.careerlog.model.JobApplication.Status;
import com.careerlog.repository.JobApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class JobApplicationService {

    private final JobApplicationRepository repository;

    public JobApplicationService(JobApplicationRepository repository) {
        this.repository = repository;
    }

    // CRUD
    public List<JobApplication> getAllApplications() {
        return repository.findAll();
    }

    public JobApplication getApplicationById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public JobApplication saveApplication(JobApplication jobApplication) {
        return repository.save(jobApplication);
    }

    public void deleteApplication(Long id) {
        repository.deleteById(id);
    }

    // Search
    public List<JobApplication> searchByCompany(String company) {
        return repository.findByCompanyNameContainingIgnoreCase(company);
    }

    // Filter
    public List<JobApplication> filterByStatus(String statusText) {
        Status status = parseStatus(statusText);
        return repository.findByStatus(status);
    }

    // Sort
    public List<JobApplication> sortByDate(String order) {
        if (order == null) order = "asc";
        switch (order.toLowerCase(Locale.ROOT)) {
            case "desc": return repository.findAllByOrderByDateAppliedDesc();
            case "asc":  return repository.findAllByOrderByDateAppliedAsc();
            default: throw new IllegalArgumentException("Invalid order: " + order + " (use 'asc' or 'desc')");
        }
    }

    // Stats for dashboard
    public Map<String, Long> getStats() {
        long total = repository.count();
        Map<String, Long> map = new HashMap<>();
        map.put("totalApplications", total);
        map.put("applied", repository.countByStatus(Status.APPLIED));
        map.put("underReview", repository.countByStatus(Status.UNDER_REVIEW));
        map.put("interview", repository.countByStatus(Status.INTERVIEW));
        map.put("offer", repository.countByStatus(Status.OFFER));
        map.put("rejected", repository.countByStatus(Status.REJECTED));
        return map;
    }

    // Helper: convert text to enum (case-insensitive)
    public Status parseStatus(String text) {
        if (text == null) throw new IllegalArgumentException("Status must not be null");
        String normalized = text.trim().toUpperCase(Locale.ROOT).replace(' ', '_');
        try {
            return Status.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Invalid status: " + text + ". Allowed: APPLIED, UNDER_REVIEW, INTERVIEW, OFFER, REJECTED"
            );
        }
    }
}
