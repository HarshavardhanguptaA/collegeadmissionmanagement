package com.internproject.studentms.repository;

import com.internproject.studentms.entity.AdmissionApplication;
import com.internproject.studentms.entity.AdmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AdmissionApplicationRepository extends JpaRepository<AdmissionApplication, Long> {
    List<AdmissionApplication> findByStudent_IdOrderBySubmittedAtDesc(Long studentId);
    List<AdmissionApplication> findAllByOrderBySubmittedAtDesc();
    boolean existsByStudent_IdAndCourse_Id(Long studentId, Long courseId);
    long countByStatus(AdmissionStatus status);
    long countByStudent_Id(Long studentId);
    long countByStudent_IdAndStatus(Long studentId, AdmissionStatus status);
}
