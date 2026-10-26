package com.internproject.studentms.controller;

import com.internproject.studentms.dto.*;
import com.internproject.studentms.security.UserPrincipal;
import com.internproject.studentms.service.AdmissionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentPortalController {
    private final AdmissionService service;
    public StudentPortalController(AdmissionService service) { this.service = service; }
    @GetMapping("/dashboard") public StudentDashboardResponse dashboard(@AuthenticationPrincipal UserPrincipal principal) { return service.studentDashboard(principal); }
    @GetMapping("/profile") public StudentProfileResponse profile(@AuthenticationPrincipal UserPrincipal principal) { return service.getProfile(principal); }
    @PutMapping("/profile") public StudentProfileResponse updateProfile(@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody StudentProfileRequest request) { return service.updateProfile(principal, request); }
    @GetMapping("/applications") public List<ApplicationView> mine(@AuthenticationPrincipal UserPrincipal principal) { return service.myApplications(principal); }
    @PostMapping("/applications") @ResponseStatus(HttpStatus.CREATED)
    public ApplicationView apply(@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody ApplicationRequest request) { return service.apply(principal, request); }
}
