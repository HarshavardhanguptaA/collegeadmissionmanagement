package com.internproject.studentms.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ApplicationRequest(
    @NotNull Long courseId,
    @NotBlank @Size(max = 120) String qualification,
    @NotNull @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal marksPercentage
) {}
