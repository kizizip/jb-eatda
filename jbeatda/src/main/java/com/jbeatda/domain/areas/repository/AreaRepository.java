package com.jbeatda.domain.areas.repository;

import com.jbeatda.domain.areas.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AreaRepository extends JpaRepository<Area, Integer> {
    List<Area> findAllByOrderByIdAsc();
    // Optional<Area> findById(Integer id); 이게 null 처리 자동으로 해준대요
    Area findFirstById(Integer id); // 존재하지 않으면 null
}
