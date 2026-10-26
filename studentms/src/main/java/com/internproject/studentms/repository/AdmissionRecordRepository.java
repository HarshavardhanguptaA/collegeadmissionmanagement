package com.internproject.studentms.repository;

import com.internproject.studentms.entity.AdmissionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AdmissionRecordRepository extends JpaRepository<AdmissionRecord, Long> {
    boolean existsByApplication_Id(Long applicationId);
    List<AdmissionRecord> findAllByOrderByAdmittedAtDesc();
    long countByApplication_Course_Id(Long courseId);
}
