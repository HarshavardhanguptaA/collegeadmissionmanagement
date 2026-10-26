package com.internproject.studentms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "admission_records", uniqueConstraints = {
    @UniqueConstraint(name = "uk_admission_record_application", columnNames = "application_id"),
    @UniqueConstraint(name = "uk_admission_record_number", columnNames = "admission_number")
})
@Getter @Setter @NoArgsConstructor
public class AdmissionRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    private AdmissionApplication application;

    @Column(nullable = false, unique = true, length = 40)
    private String admissionNumber = "ADM-" + UUID.randomUUID().toString().toUpperCase();

    @Column(nullable = false, updatable = false)
    private Instant admittedAt = Instant.now();
}
