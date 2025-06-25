package com.jbeatda.domain.stamps.repository;

import com.jbeatda.domain.stamps.entity.Stamp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StampRepository extends JpaRepository<Stamp, Integer> {
    List<Stamp> findByUserId(Integer userId);

    // 최신 스탬프 1개 조회 (createdAt 내림차순)
    Optional<Stamp> findFirstByUserIdAndMenuIdOrderByCreatedAtDesc(Integer userId, Integer menuId);
}

