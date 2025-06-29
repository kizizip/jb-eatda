package com.jbeatda.domain.stores.repository;

import com.jbeatda.domain.stores.entity.Bookmark;
import com.jbeatda.domain.stores.entity.Store;
import com.jbeatda.domain.users.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface BookmarkRepository extends JpaRepository <Bookmark, Integer>  {

    // 사용자와 매장으로 북마크 찾기 (중복 확인용)
    Optional<Bookmark> findByUserAndStore(User user, Store store);

    // 사용자의 모든 북마크 조회
    List<Bookmark> findByUserOrderByCreatedAtDesc(User user);


    Optional<Bookmark> findByUserIdAndStoreId(int userId, int storeId);

    // 2. 사용자의 여러 매장 북마크 조회 (연관관계 활용)
    @Query("SELECT b FROM Bookmark b WHERE b.user.id = :userId AND b.store.id IN :storeIds")
    List<Bookmark> findByUserIdAndStoreIdIn(@Param("userId") int userId, @Param("storeIds") List<Integer> storeIds);

    // 3. 성능 최적화를 위한 storeId만 조회 (연관관계 활용)
    @Query("SELECT b.store.id FROM Bookmark b WHERE b.user.id = :userId AND b.store.id IN :storeIds")
    List<Integer> findStoreIdsByUserIdAndStoreIdIn(@Param("userId") int userId, @Param("storeIds") List<Integer> storeIds);

    // 4. 일괄 삭제 메서드 (연관관계 활용)
    @Modifying
    @Query("DELETE FROM Bookmark b WHERE b.user.id = :userId AND b.store.id IN :storeIds")
    int deleteByUserIdAndStoreIdIn(@Param("userId") int userId, @Param("storeIds") List<Integer> storeIds);




}
