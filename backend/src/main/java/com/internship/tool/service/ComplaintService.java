package com.internship.tool.service;

import com.internship.tool.entity.Complaint;
import com.internship.tool.repository.ComplaintRepository;
import com.internship.tool.exception.ComplaintNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComplaintService {

    private final ComplaintRepository repository;

    @Autowired
    private EmailService emailService;

    public ComplaintService(ComplaintRepository repository) {
        this.repository = repository;
    }

    // ✅ CREATE (clear cache) + Send email notification
    
    public Complaint createComplaint(Complaint complaint) {
        complaint.setStatus("OPEN");
        complaint.setCreatedAt(LocalDateTime.now());
        complaint.setUpdatedAt(LocalDateTime.now());
        Complaint saved = repository.save(complaint);
        
        // Send email notification
        try {
            emailService.sendComplaintCreatedEmail("admin@whistleblower.com", saved.getTitle(), saved.getId());
        } catch (Exception e) {
            System.err.println("⚠️ Failed to send complaint creation email: " + e.getMessage());
        }
        
        return saved;
    }

    // ✅ GET ALL (cached)

    public List<Complaint> getAllComplaints() {
        return repository.findAll();
    }

    // ✅ GET ALL PAGINATED (optional cache)
    
    public Page<Complaint> getAllPaginated(Pageable pageable) {
        return repository.findAll(pageable);
    }

    // ✅ GET BY ID (cached)
    public Complaint getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ComplaintNotFoundException("Complaint not found with id " + id));
    }

    // ✅ UPDATE Send status update email
    
    public Complaint updateComplaint(Long id, Complaint complaint) {

    Complaint existing = getById(id);

    if (complaint.getTitle() != null) {
        existing.setTitle(complaint.getTitle());
    }

    if (complaint.getDescription() != null) {
        existing.setDescription(complaint.getDescription());
    }

    if (complaint.getStatus() != null) {
        existing.setStatus(complaint.getStatus());
    }

    existing.setUpdatedAt(LocalDateTime.now());

    Complaint updated = repository.save(existing);

    return updated;
}

    // ✅ DELETE (clear cache)
    public void deleteComplaint(Long id) {
        if (!repository.existsById(id)) {
            throw new ComplaintNotFoundException("Complaint not found with id " + id);
        }
        repository.deleteById(id);
    }
}