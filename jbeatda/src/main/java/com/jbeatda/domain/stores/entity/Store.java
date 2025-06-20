package com.jbeatda.domain.stores.entity;

import com.jbeatda.domain.courses.entity.CourseStore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
    @Table(name = "store")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class Store {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "store_id")
        private Integer id;

        @Column(name = "store_name", nullable = false)
        private String storeName;

        @Column(name = "store_image")
        private String storeImage;

        @Column(name = "area")
        private String area;

        @Column(name = "address")
        private String address;

        @Column(name = "smenu")
        private String smenu;

        @Column(name = "time")
        private String time;

        @Column(name = "holiday")
        private String holiday;

        @Column(name = "sno")
        private String sno;

        @Column(name = "tel")
        private String tel;

        @Column(name = "park")
        private Boolean park;

        // 연관관계
        @Builder.Default
        @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<CourseStore> courseStores = new ArrayList<>();
    }
