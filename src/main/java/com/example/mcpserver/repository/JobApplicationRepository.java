package com.example.mcpserver.repository;

import com.example.mcpserver.model.JobApplication;
import com.example.mcpserver.model.JobApplication.Status;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface JobApplicationRepository extends MongoRepository<JobApplication, String> {
    List<JobApplication> findByStatus(Status status);
}