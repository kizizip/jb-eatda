package com.jbeatda.domain.users.repository;

import com.jbeatda.domain.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // 스탬프 조회를 위한 유저 조회
    User findFirstById(Integer id); // or Optional<User> findById()
}
