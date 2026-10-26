package com.internproject.studentms.dto;

import com.internproject.studentms.entity.AdmissionStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record ApplicationView(Long id, String applicationNumber, Long studentId, String studentName,
    String studentEmail, Long courseId, String courseName, String courseCode, String qualification,
    BigDecimal marksPercentage, AdmissionStatus status, Instant submittedAt, String decisionNotes,
    Instant decisionAt, String decidedBy) {}
