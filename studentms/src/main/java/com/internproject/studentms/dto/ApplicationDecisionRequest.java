package com.internproject.studentms.dto;

import com.internproject.studentms.entity.AdmissionStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ApplicationDecisionRequest(@NotNull AdmissionStatus status, @Size(max = 1000) String notes) {}
