package com.internproject.studentms.dto;

import java.time.Instant;

public record AdmissionRecordView(Long id, String admissionNumber, String applicationNumber,
    String studentName, String studentEmail, String courseName, Instant admittedAt) {}
