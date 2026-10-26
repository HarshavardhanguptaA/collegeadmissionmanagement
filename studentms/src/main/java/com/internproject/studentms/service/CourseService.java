package com.internproject.studentms.service;

import com.internproject.studentms.dto.CourseDTO;
import com.internproject.studentms.entity.Course;
import com.internproject.studentms.exception.ResourceNotFoundException;
import com.internproject.studentms.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public CourseDTO save(CourseDTO courseDTO) {
        Course course = mapToEntity(courseDTO);
        Course savedCourse = courseRepository.save(course);
        return mapToDTO(savedCourse);
    }

    public List<CourseDTO> getAll() {
        return courseRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CourseDTO getById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return mapToDTO(course);
    }

    public CourseDTO update(Long id, CourseDTO updatedDTO) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        existingCourse.setCourseName(updatedDTO.getCourseName());
        existingCourse.setCourseCode(updatedDTO.getCourseCode());
        existingCourse.setCredits(updatedDTO.getCredits());
        existingCourse.setDepartment(updatedDTO.getDepartment() == null || updatedDTO.getDepartment().isBlank() ? existingCourse.getDepartment() : updatedDTO.getDepartment().trim());
        existingCourse.setDurationYears(updatedDTO.getDurationYears() == null ? (existingCourse.getDurationYears() == null ? 4 : existingCourse.getDurationYears()) : updatedDTO.getDurationYears());
        existingCourse.setTotalSeats(updatedDTO.getTotalSeats() == null ? (existingCourse.getTotalSeats() == null ? 60 : existingCourse.getTotalSeats()) : updatedDTO.getTotalSeats());
        existingCourse.setActive(updatedDTO.getActive() == null ? (existingCourse.getActive() == null || existingCourse.getActive()) : updatedDTO.getActive());

        Course updatedCourse = courseRepository.save(existingCourse);
        return mapToDTO(updatedCourse);
    }

    public void delete(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        // Keep course rows because historical admission/enrollment records may reference them.
        course.setActive(false);
        courseRepository.save(course);
    }

    private Course mapToEntity(CourseDTO dto) {
        Course course = new Course();
        course.setCourseName(dto.getCourseName());
        course.setCourseCode(dto.getCourseCode());
        course.setCredits(dto.getCredits());
        course.setDepartment(dto.getDepartment() == null || dto.getDepartment().isBlank() ? "General" : dto.getDepartment().trim());
        course.setDurationYears(dto.getDurationYears() == null ? 4 : dto.getDurationYears());
        course.setTotalSeats(dto.getTotalSeats() == null ? 60 : dto.getTotalSeats());
        course.setActive(dto.getActive() == null ? true : dto.getActive());
        return course;
    }

    private CourseDTO mapToDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setCourseName(course.getCourseName());
        dto.setCourseCode(course.getCourseCode());
        dto.setCredits(course.getCredits());
        dto.setDepartment(course.getDepartment() == null ? "General" : course.getDepartment());
        dto.setDurationYears(course.getDurationYears() == null ? 4 : course.getDurationYears());
        dto.setTotalSeats(course.getTotalSeats() == null ? 60 : course.getTotalSeats());
        dto.setActive(course.getActive() == null || course.getActive());
        return dto;
    }
}