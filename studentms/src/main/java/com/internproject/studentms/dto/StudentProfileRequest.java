package com.internproject.studentms.dto;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record StudentProfileRequest(
    @Size(max = 80) String firstName,
    @Size(max = 80) String lastName,
    @Past LocalDate dob,
    @Size(max = 40) String gender,
    @Pattern(regexp = "^$|^\\d{10}$", message = "Phone must contain 10 digits") String phone,
    @Size(max = 500) String address,
    @Size(max = 120) String qualification,
    @jakarta.validation.constraints.DecimalMin("0.0") @jakarta.validation.constraints.DecimalMax("100.0") java.math.BigDecimal marksPercentage
) {}
