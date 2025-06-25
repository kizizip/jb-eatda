package com.jbeatda.domain.menus.repository;

import com.jbeatda.domain.menus.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Integer> {
    List<Menu> findAllByAreaId(Integer areaId);
}
