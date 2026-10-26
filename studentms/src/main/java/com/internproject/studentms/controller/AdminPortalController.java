package com.internproject.studentms.controller;

import com.internproject.studentms.dto.*;
import com.internproject.studentms.security.UserPrincipal;
import com.internproject.studentms.service.AdmissionService;
import com.internproject.studentms.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminPortalController {
    private final AdmissionService admissions;
    private final CourseService courses;
    public AdminPortalController(AdmissionService admissions, CourseService courses) { this.admissions = admissions; this.courses = courses; }
    @GetMapping("/dashboard") public AdminDashboardResponse dashboard() { return admissions.adminDashboard(); }
    @GetMapping("/applications") public List<ApplicationView> applications(@RequestParam(required = false) String status, @RequestParam(required = false) String q, @RequestParam(required = false) Long courseId) { return admissions.allApplications(status, q, courseId); }
    @PatchMapping("/applications/{id}/decision") public ApplicationView decide(@PathVariable Long id, @Valid @RequestBody ApplicationDecisionRequest request, @AuthenticationPrincipal UserPrincipal principal) { return admissions.decide(id, request, principal); }
    @GetMapping("/courses") public List<CourseDTO> courses() { return courses.getAll(); }
    @PostMapping("/courses") @ResponseStatus(HttpStatus.CREATED) public CourseDTO createCourse(@Valid @RequestBody CourseDTO dto) { return courses.save(dto); }
    @PutMapping("/courses/{id}") public CourseDTO updateCourse(@PathVariable Long id, @Valid @RequestBody CourseDTO dto) { return courses.update(id, dto); }
    @GetMapping("/admissions") public List<AdmissionRecordView> admissions() { return admissions.admissionRecords(); }
}
