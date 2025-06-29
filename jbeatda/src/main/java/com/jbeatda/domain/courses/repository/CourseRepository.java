package com.jbeatda.domain.courses.repository;

import com.jbeatda.domain.courses.entity.Course;
import com.jbeatda.domain.users.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Integer> {

    @Query("SELECT c FROM Course c " +
            "LEFT JOIN FETCH c.courseStores cs " +
            "LEFT JOIN FETCH cs.store s " +
            "WHERE c.user.id = :userId")
    List<Course> findByUserIdWithStores (int userId);




        @Query("SELECT c FROM Course c "+
                "LEFT JOIN FETCH c.courseStores cs "+
                "LEFT JOIN FETCH cs.store s "+
                "WHERE c.id = :courseId AND c.user = :user"
        )
    Optional<Course>  findByIdAndUser (int courseId, User user);


    List<Course> findByIdInAndUser(List<Integer> ids, User user);


    @Query("SELECT c.id FROM Course c WHERE c.id IN :ids AND c.user = :user")
    List<Integer> findIdsByIdInAndUser(@Param("ids") List<Integer> ids, @Param("user") User user);



    @Modifying
    @Query("DELETE FROM Course c WHERE c.id IN :ids AND c.user = :user")
    int deleteByIdInAndUser(@Param("ids") List<Integer> ids, @Param("user") User user);

}
