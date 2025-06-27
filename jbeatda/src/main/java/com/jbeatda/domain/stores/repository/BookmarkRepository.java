package com.jbeatda.domain.stores.repository;

import com.jbeatda.domain.stores.entity.Bookmark;
import com.jbeatda.domain.stores.entity.Store;
import com.jbeatda.domain.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface BookmarkRepository extends JpaRepository <Bookmark, Integer>  {

    // 사용자와 매장으로 북마크 찾기 (중복 확인용)
    Optional<Bookmark> findByUserAndStore(User user, Store store);

    // 사용자의 모든 북마크 조회
    List<Bookmark> findByUserOrderByCreatedAtDesc(User user);

}
