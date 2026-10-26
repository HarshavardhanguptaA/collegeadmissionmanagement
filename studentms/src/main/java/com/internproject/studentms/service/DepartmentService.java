package com.internproject.studentms.service;

import com.internproject.studentms.dto.DepartmentDTO;
import com.internproject.studentms.entity.Department;
import com.internproject.studentms.exception.ResourceNotFoundException;
import com.internproject.studentms.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public DepartmentDTO save(DepartmentDTO departmentDTO) {
        Department department = mapToEntity(departmentDTO);
        Department savedDepartment = departmentRepository.save(department);
        return mapToDTO(savedDepartment);
    }

    public List<DepartmentDTO> getAll() {
        return departmentRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public DepartmentDTO getById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        return mapToDTO(department);
    }

    public DepartmentDTO update(Long id, DepartmentDTO updatedDTO) {
        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));

        existingDepartment.setDepartmentName(updatedDTO.getDepartmentName());
        existingDepartment.setHodName(updatedDTO.getHodName());

        Department updatedDepartment = departmentRepository.save(existingDepartment);
        return mapToDTO(updatedDepartment);
    }

    public void delete(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        departmentRepository.delete(department);
    }

    private Department mapToEntity(DepartmentDTO dto) {
        Department department = new Department();
        department.setDepartmentName(dto.getDepartmentName());
        department.setHodName(dto.getHodName());
        return department;
    }

    private DepartmentDTO mapToDTO(Department department) {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(department.getId());
        dto.setDepartmentName(department.getDepartmentName());
        dto.setHodName(department.getHodName());
        return dto;
    }
}