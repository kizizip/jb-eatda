package com.jbeatda.domain.menus.entity;

import com.jbeatda.domain.areas.entity.Area;
import com.jbeatda.domain.stamps.entity.Stamp;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Menu {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "menu_id")
        private Integer id;

        @Column(name = "menu_name", nullable = false)
        private String menuName;

        // 연관관계
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "area_id", nullable = false)
        private Area area;

        @Builder.Default
        @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Stamp> stamps = new ArrayList<>();
}

