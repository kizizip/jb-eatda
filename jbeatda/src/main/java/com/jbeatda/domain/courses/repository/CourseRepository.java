package com.jbeatda.domain.courses.repository;

import com.jbeatda.domain.courses.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Integer> {
}
