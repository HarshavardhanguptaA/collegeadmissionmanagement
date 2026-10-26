package com.internproject.studentms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Course name is required")
    @Column(nullable = false)
    private String courseName;

    @NotBlank(message = "Course code is required")
    @Column(unique = true, nullable = false)
    private String courseCode;

    @NotNull(message = "Credits cannot be null")
    @Min(value = 1, message = "Credits must be at least 1")
    @Column(nullable = false)
    private Integer credits;

    @Column(length = 120)
    private String department = "General";

    @Column(name = "duration_years")
    private Integer durationYears = 4;

    @Column(name = "total_seats")
    private Integer totalSeats = 60;

    @Column(name = "active")
    private Boolean active = true;

    @JsonIgnore
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Enrollment> enrollments;
}