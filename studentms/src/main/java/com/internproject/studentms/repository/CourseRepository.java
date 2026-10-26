package com.internproject.studentms.repository;

import com.internproject.studentms.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("select c from Course c where c.active = true or c.active is null order by c.courseName asc")
    List<Course> findPublicCourses();

    @Query("select count(c) from Course c where c.active = true or c.active is null")
    long countPublicCourses();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Course c where c.id = :id")
    Optional<Course> findByIdForUpdate(@Param("id") Long id);
}