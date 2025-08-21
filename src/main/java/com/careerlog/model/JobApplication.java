package com.careerlog.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "job_applications")
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private String jobTitle;

    @Column(length = 1000)
    private String jobLink;

    private LocalDate dateApplied;

    @Enumerated(EnumType.STRING) // store enum as text, not number
    private Status status;

    @Column(length = 2000)
    private String notes;

    // --- Enum for application status ---
    public enum Status {
        APPLIED,
        UNDER_REVIEW,
        INTERVIEW,
        OFFER,
        REJECTED
    }

    // --- Getters & Setters ---
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJobTitle() {
        return jobTitle;
    }
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobLink() {
        return jobLink;
    }
    public void setJobLink(String jobLink) {
        this.jobLink = jobLink;
    }

    public LocalDate getDateApplied() {
        return dateApplied;
    }
    public void setDateApplied(LocalDate dateApplied) {
        this.dateApplied = dateApplied;
    }

    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
