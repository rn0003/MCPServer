package com.example.mcpserver.service;

import com.example.mcpserver.model.JobApplication;
import com.example.mcpserver.model.JobApplication.InterviewNote;
import com.example.mcpserver.model.JobApplication.Status;
import com.example.mcpserver.repository.JobApplicationRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JobInterviewTrackerService {

    private final JobApplicationRepository repository;

    public JobInterviewTrackerService(JobApplicationRepository repository) {
        this.repository = repository;
    }

    @Tool(description = "Add a new job application. Provide company name, position title, date applied (YYYY-MM-DD), and optional notes.")
    public String addApplication(String company, String position, String dateApplied, String notes) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        LocalDate date = LocalDate.parse(dateApplied);
        JobApplication app = new JobApplication(id, company, position, date, Status.APPLIED, notes);
        repository.save(app);
        return "Created application " + app;
    }

    @Tool(description = "Update the status of a job application. Valid statuses: APPLIED, PHONE_SCREEN, TECHNICAL_INTERVIEW, ONSITE_INTERVIEW, OFFER, ACCEPTED, REJECTED, WITHDRAWN.")
    public String updateStatus(String applicationId, String newStatus) {
        JobApplication app = repository.findById(applicationId).orElse(null);
        if (app == null) {
            return "Application not found: " + applicationId;
        }
        app.setStatus(Status.valueOf(newStatus.toUpperCase()));
        repository.save(app);
        return "Updated: " + app;
    }

    @Tool(description = "Add an interview note to a job application. Provide the application ID, interview round name (e.g. 'Phone Screen', 'Technical Round 1'), date (YYYY-MM-DD), and your notes.")
    public String addInterviewNote(String applicationId, String round, String date, String note) {
        JobApplication app = repository.findById(applicationId).orElse(null);
        if (app == null) {
            return "Application not found: " + applicationId;
        }
        InterviewNote interviewNote = new InterviewNote(LocalDate.parse(date), round, note);
        app.getInterviewNotes().add(interviewNote);
        repository.save(app);
        return "Added note to " + app.getCompany() + ": " + interviewNote;
    }

    @Tool(description = "List all job applications, optionally filtered by status. Pass status as null or empty to list all.")
    public String listApplications(String statusFilter) {
        List<JobApplication> results;
        if (statusFilter == null || statusFilter.isBlank()) {
            results = repository.findAll();
        } else {
            Status filter = Status.valueOf(statusFilter.toUpperCase());
            results = repository.findByStatus(filter);
        }

        if (results.isEmpty()) {
            return "No applications found.";
        }

        StringBuilder sb = new StringBuilder("Job Applications (" + results.size() + "):\n");
        for (JobApplication app : results) {
            sb.append("• ").append(app).append("\n");
        }
        return sb.toString();
    }

    @Tool(description = "Get full details of a specific job application by its ID, including all interview notes.")
    public String getApplication(String applicationId) {
        JobApplication app = repository.findById(applicationId).orElse(null);
        if (app == null) {
            return "Application not found: " + applicationId;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Application Details:\n");
        sb.append("  ID: ").append(app.getId()).append("\n");
        sb.append("  Company: ").append(app.getCompany()).append("\n");
        sb.append("  Position: ").append(app.getPosition()).append("\n");
        sb.append("  Date Applied: ").append(app.getDateApplied()).append("\n");
        sb.append("  Status: ").append(app.getStatus()).append("\n");
        sb.append("  Notes: ").append(app.getNotes() != null ? app.getNotes() : "—").append("\n");

        if (!app.getInterviewNotes().isEmpty()) {
            sb.append("  Interview Notes:\n");
            for (InterviewNote note : app.getInterviewNotes()) {
                sb.append(note).append("\n");
            }
        }
        return sb.toString();
    }

    @Tool(description = "Delete a job application by its ID.")
    public String deleteApplication(String applicationId) {
        JobApplication app = repository.findById(applicationId).orElse(null);
        if (app == null) {
            return "Application not found: " + applicationId;
        }
        repository.delete(app);
        return "Deleted: " + app;
    }

    @Tool(description = "Get a summary of all job applications grouped by status, with counts for each status.")
    public String getSummary() {
        List<JobApplication> all = repository.findAll();
        if (all.isEmpty()) {
            return "No applications tracked yet.";
        }

        Map<Status, Long> counts = all.stream()
                .collect(Collectors.groupingBy(JobApplication::getStatus, Collectors.counting()));

        StringBuilder sb = new StringBuilder("Job Search Summary (" + all.size() + " total):\n");
        for (Status status : Status.values()) {
            long count = counts.getOrDefault(status, 0L);
            if (count > 0) {
                sb.append("  ").append(status).append(": ").append(count).append("\n");
            }
        }
        return sb.toString();
    }
}