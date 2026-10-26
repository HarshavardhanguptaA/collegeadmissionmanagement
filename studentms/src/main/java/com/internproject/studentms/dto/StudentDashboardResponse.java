package com.internproject.studentms.dto;

import java.util.List;

public record StudentDashboardResponse(String fullName, long totalApplications, long awaitingReview,
    long approved, long rejected, List<ApplicationView> recentApplications) {}
