package com.internproject.studentms.service;

import com.internproject.studentms.dto.EnrollmentDTO;
import com.internproject.studentms.entity.Course;
import com.internproject.studentms.entity.Enrollment;
import com.internproject.studentms.entity.Student;
import com.internproject.studentms.exception.ResourceNotFoundException;
import com.internproject.studentms.repository.CourseRepository;
import com.internproject.studentms.repository.EnrollmentRepository;
import com.internproject.studentms.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             StudentRepository studentRepository,
                             CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    public EnrollmentDTO save(EnrollmentDTO enrollmentDTO) {
        Enrollment enrollment = mapToEntity(enrollmentDTO);
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return mapToDTO(savedEnrollment);
    }

    public List<EnrollmentDTO> getAll() {
        return enrollmentRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public EnrollmentDTO getById(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));
        return mapToDTO(enrollment);
    }

    public EnrollmentDTO update(Long id, EnrollmentDTO updatedDTO) {
        Enrollment existingEnrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));

        existingEnrollment.setSemester(updatedDTO.getSemester());
        existingEnrollment.setGrade(updatedDTO.getGrade());

        if (updatedDTO.getStudentId() != null) {
            Student student = studentRepository.findById(updatedDTO.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + updatedDTO.getStudentId()));
            existingEnrollment.setStudent(student);
        }

        if (updatedDTO.getCourseId() != null) {
            Course course = courseRepository.findById(updatedDTO.getCourseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + updatedDTO.getCourseId()));
            existingEnrollment.setCourse(course);
        }

        Enrollment updatedEnrollment = enrollmentRepository.save(existingEnrollment);
        return mapToDTO(updatedEnrollment);
    }

    public void delete(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));
        enrollmentRepository.delete(enrollment);
    }

    private Enrollment mapToEntity(EnrollmentDTO dto) {
        Enrollment enrollment = new Enrollment();
        enrollment.setSemester(dto.getSemester());
        enrollment.setGrade(dto.getGrade());

        if (dto.getStudentId() != null) {
            Student student = studentRepository.findById(dto.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + dto.getStudentId()));
            enrollment.setStudent(student);
        }

        if (dto.getCourseId() != null) {
            Course course = courseRepository.findById(dto.getCourseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + dto.getCourseId()));
            enrollment.setCourse(course);
        }

        return enrollment;
    }

    private EnrollmentDTO mapToDTO(Enrollment enrollment) {
        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setId(enrollment.getId());
        dto.setStudentId(enrollment.getStudent().getId());
        dto.setStudentName(enrollment.getStudent().getFirstName() + " " + enrollment.getStudent().getLastName());

        dto.setCourseId(enrollment.getCourse().getId());
        dto.setCourseName(enrollment.getCourse().getCourseName());

        dto.setSemester(enrollment.getSemester());
        dto.setGrade(enrollment.getGrade());
        return dto;
    }
}