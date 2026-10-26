package com.internproject.studentms.service;

import com.internproject.studentms.dto.StudentDTO;
import com.internproject.studentms.entity.Department;
import com.internproject.studentms.entity.Student;
import com.internproject.studentms.exception.ResourceNotFoundException;
import com.internproject.studentms.repository.DepartmentRepository;
import com.internproject.studentms.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;

    public StudentService(StudentRepository studentRepository, DepartmentRepository departmentRepository) {
        this.studentRepository = studentRepository;
        this.departmentRepository = departmentRepository;
    }

    public StudentDTO save(StudentDTO studentDTO) {
        Student student = mapToEntity(studentDTO);
        Student savedStudent = studentRepository.save(student);
        return mapToDTO(savedStudent);
    }

    public List<StudentDTO> getAll() {
        return studentRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public StudentDTO getById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return mapToDTO(student);
    }

    public StudentDTO update(Long id, StudentDTO updatedDTO) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        existingStudent.setFirstName(updatedDTO.getFirstName());
        existingStudent.setLastName(updatedDTO.getLastName());
        existingStudent.setEmail(updatedDTO.getEmail());
        existingStudent.setDob(updatedDTO.getDob());
        existingStudent.setGender(updatedDTO.getGender());
        existingStudent.setPhone(updatedDTO.getPhone());
        existingStudent.setAddress(updatedDTO.getAddress());

        if (updatedDTO.getDepartmentId() != null) {
            Department department = departmentRepository.findById(updatedDTO.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + updatedDTO.getDepartmentId()));
            existingStudent.setDepartment(department);
        }

        Student updatedStudent = studentRepository.save(existingStudent);
        return mapToDTO(updatedStudent);
    }

    public void delete(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        studentRepository.delete(student);
    }

    private Student mapToEntity(StudentDTO dto) {
        Student student = new Student();
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setEmail(dto.getEmail());
        student.setDob(dto.getDob());
        student.setGender(dto.getGender());
        student.setPhone(dto.getPhone());
        student.setAddress(dto.getAddress());

        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + dto.getDepartmentId()));
            student.setDepartment(department);
        }
        return student;
    }

    private StudentDTO mapToDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setEmail(student.getEmail());
        dto.setDob(student.getDob());
        dto.setGender(student.getGender());
        dto.setPhone(student.getPhone());
        dto.setAddress(student.getAddress());

        if (student.getDepartment() != null) {
            dto.setDepartmentId(student.getDepartment().getId());
            dto.setDepartmentName(student.getDepartment().getDepartmentName());
        }
        return dto;
    }
}