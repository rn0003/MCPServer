package com.example.mcpserver.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "job_applications")
public class JobApplication {

    public enum Status {
        APPLIED, PHONE_SCREEN, TECHNICAL_INTERVIEW, ONSITE_INTERVIEW, OFFER, ACCEPTED, REJECTED, WITHDRAWN
    }

    @Id
    private String id;
    private String company;
    private String position;
    private LocalDate dateApplied;
    private Status status;
    private String notes;
    private List<InterviewNote> interviewNotes;

    public JobApplication() {
        this.interviewNotes = new ArrayList<>();
    }

    public JobApplication(String id, String company, String position, LocalDate dateApplied, Status status, String notes) {
        this.id = id;
        this.company = company;
        this.position = position;
        this.dateApplied = dateApplied;
        this.status = status;
        this.notes = notes;
        this.interviewNotes = new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public LocalDate getDateApplied() { return dateApplied; }
    public void setDateApplied(LocalDate dateApplied) { this.dateApplied = dateApplied; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<InterviewNote> getInterviewNotes() { return interviewNotes; }
    public void setInterviewNotes(List<InterviewNote> interviewNotes) { this.interviewNotes = interviewNotes; }

    @Override
    public String toString() {
        return String.format("[%s] %s at %s — Status: %s (Applied: %s)", id, position, company, status, dateApplied);
    }

    public static class InterviewNote {
        private LocalDate date;
        private String round;
        private String note;

        public InterviewNote() {}

        public InterviewNote(LocalDate date, String round, String note) {
            this.date = date;
            this.round = round;
            this.note = note;
        }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }

        public String getRound() { return round; }
        public void setRound(String round) { this.round = round; }

        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }

        @Override
        public String toString() {
            return String.format("  [%s] %s: %s", date, round, note);
        }
    }
}