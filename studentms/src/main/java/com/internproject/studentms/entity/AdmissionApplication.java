package com.internproject.studentms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "admission_applications",
       uniqueConstraints = @UniqueConstraint(name = "uk_admission_student_course", columnNames = {"student_id", "course_id"}),
       indexes = {@Index(name = "idx_admission_status", columnList = "status"), @Index(name = "idx_admission_submitted", columnList = "submitted_at")})
@Getter @Setter @NoArgsConstructor
public class AdmissionApplication {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String applicationNumber = "APP-" + UUID.randomUUID().toString().toUpperCase();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, length = 120)
    private String qualification;

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal marksPercentage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private AdmissionStatus status = AdmissionStatus.SUBMITTED;

    @Column(nullable = false, updatable = false)
    private Instant submittedAt = Instant.now();

    @Column(length = 1000)
    private String decisionNotes;

    private Instant decisionAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decided_by_user_id")
    private UserAccount decidedBy;
}
