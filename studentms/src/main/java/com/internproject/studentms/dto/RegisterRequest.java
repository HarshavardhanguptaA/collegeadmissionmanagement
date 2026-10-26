package com.internproject.studentms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
    @NotBlank(message = "First name is required") @Size(max = 80) String firstName,
    @NotBlank(message = "Last name is required") @Size(max = 80) String lastName,
    @NotBlank(message = "Email is required") @Email(message = "Enter a valid email") @Size(max = 255) String email,
    @NotBlank(message = "Password is required") @Size(min = 8, max = 100, message = "Password must contain at least 8 characters") String password,
    @Pattern(regexp = "^$|^\\d{10}$", message = "Phone must contain 10 digits") @Size(max = 20) String phone,
    @Size(max = 500) String address
) {}
