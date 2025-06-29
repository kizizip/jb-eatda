package com.jbeatda.domain.stores.repository;

import com.jbeatda.domain.stores.entity.Bookmark;
import com.jbeatda.domain.stores.entity.Store;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> {

    List<Store> findAllByOrderByIdAsc();

    Optional<Store> findBySno(String sno);



}
