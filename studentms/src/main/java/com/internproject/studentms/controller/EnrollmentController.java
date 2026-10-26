package com.internproject.studentms.controller;

import com.internproject.studentms.dto.EnrollmentDTO;
import com.internproject.studentms.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@CrossOrigin(origins = "*")
public class EnrollmentController {

    private final EnrollmentService service;

    public EnrollmentController(EnrollmentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EnrollmentDTO> create(@Valid @RequestBody EnrollmentDTO enrollmentDTO) {
        EnrollmentDTO createdEnrollment = service.save(enrollmentDTO);
        return new ResponseEntity<>(createdEnrollment, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EnrollmentDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnrollmentDTO> update(@PathVariable Long id, @Valid @RequestBody EnrollmentDTO enrollmentDTO) {
        return ResponseEntity.ok(service.update(id, enrollmentDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Enrollment deleted successfully");
    }
}