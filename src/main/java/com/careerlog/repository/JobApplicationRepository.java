package com.careerlog.repository;

import com.careerlog.model.JobApplication;
import com.careerlog.model.JobApplication.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    // Search by company name (case-insensitive, partial match)
    List<JobApplication> findByCompanyNameContainingIgnoreCase(String companyName);

    // Filter by status
    List<JobApplication> findByStatus(Status status);

    // Sort by date applied
    List<JobApplication> findAllByOrderByDateAppliedAsc();
    List<JobApplication> findAllByOrderByDateAppliedDesc();

    // Counts for stats
    long countByStatus(Status status);
}
