package com.internproject.studentms.dto;

import java.time.LocalDate;

public record StudentProfileResponse(Long studentId, String firstName, String lastName, String email,
    LocalDate dob, String gender, String phone, String address, String qualification, java.math.BigDecimal marksPercentage) {}
