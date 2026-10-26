package com.internproject.studentms.dto;

public record AdminDashboardResponse(long totalStudents, long totalApplications, long submitted,
    long underReview, long approved, long rejected, long waitlisted, long activeCourses) {}
