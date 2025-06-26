package com.jbeatda.domain.courses.entity;

import com.jbeatda.DTO.requestDTO.CreateCourseRequestDTO;
import com.jbeatda.domain.stores.entity.Store;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_store")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_store_id")
    private Integer id;

    @Column(name = "visit_order", nullable = false)
    private Integer visitOrder;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    // 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;


    public static CourseStore fromBase(Course course, Store store, Integer visitOrder) {
        return CourseStore.builder()
                .course(course)
                .store(store)
                .visitOrder(visitOrder)
                .build();
    }

}