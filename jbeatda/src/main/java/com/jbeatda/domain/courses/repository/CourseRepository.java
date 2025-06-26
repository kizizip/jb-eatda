package com.jbeatda.domain.courses.repository;

import com.jbeatda.domain.courses.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {

    @Query("SELECT c FROM Course c " +
            "LEFT JOIN FETCH c.courseStores cs " +
            "LEFT JOIN FETCH cs.store s " +
            "WHERE c.user.id = :userId")
    List<Course> findByUserIdWithStores (int userId);
}
