package com.jbeatda.domain.stores.repository;

import com.jbeatda.domain.stores.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> {

    List<Store> findAllByOrderByIdAsc();
}
