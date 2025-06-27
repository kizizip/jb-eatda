package com.jbeatda.domain.stores.entity;

import com.jbeatda.DTO.responseDTO.BookmarkListResponseDTO;
import com.jbeatda.domain.users.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.awt.print.Book;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookmark",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "store_id"})
        })
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;


    public static Bookmark createEntity(User user, Store store){
        return Bookmark.builder()
                .user(user)
                .store(store)
                .build();
    }


    public BookmarkListResponseDTO.BookmarkItemDTO toBookmarkItemDTO(Bookmark bookmark) {
        return BookmarkListResponseDTO.BookmarkItemDTO.builder()
                .storeId(bookmark.getStore().getId())
                .sno(bookmark.getStore().getSno())
                .storeName(bookmark.getStore().getStoreName())
                .address(bookmark.getStore().getAddress())
                .smenu(bookmark.getStore().getSmenu())
                .build();
    }


}