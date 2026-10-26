package com.internproject.studentms.controller;

import com.internproject.studentms.dto.CourseDTO;
import com.internproject.studentms.service.AdmissionService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicCourseController {
    private final AdmissionService admissionService;
    public PublicCourseController(AdmissionService admissionService) { this.admissionService = admissionService; }
    @GetMapping("/courses") public List<CourseDTO> courses() { return admissionService.activeCourses(); }
}
