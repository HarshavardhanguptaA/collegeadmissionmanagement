package com.internproject.studentms.dto;

public record AuthResponse(String accessToken, Long userId, String fullName, String email, String role, Long studentId) {}
