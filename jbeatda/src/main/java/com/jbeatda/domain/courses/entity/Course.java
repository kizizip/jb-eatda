package com.jbeatda.domain.courses.entity;

import com.jbeatda.DTO.requestDTO.CreateCourseRequestDTO;
import com.jbeatda.DTO.responseDTO.CourseDetailResponseDTO;
import com.jbeatda.DTO.responseDTO.CourseListResponseDTO;
import com.jbeatda.domain.users.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "course")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Integer id;

    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    // 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("visitOrder ASC")
    @Builder.Default
    private List<CourseStore> courseStores = new ArrayList<>();

    /**
     * ai로 부터 받아온 코스를 DTO로 변환
     */
    public static Course fromBaseUser(User user, CreateCourseRequestDTO requestDTO) {

        return Course.builder()
                .courseName(requestDTO.getCourseName())
                .description(requestDTO.getDescription())
                .user(user)
                .build();
    }

    /**
     * 코스 리스트 반환
     */
    public static CourseListResponseDTO.MyCourse toMyCourseDTO(Course course){
        Set<String> positions = new HashSet<>();

        for(CourseStore courseStore: course.getCourseStores()){
            String area = courseStore.getStore().getArea();
            if (area != null && !area.trim().isEmpty()) {
                positions.add(area);
            }
        }

        return CourseListResponseDTO.MyCourse.builder()
                .courseId(course.getId())
                .courseName(course.getCourseName())
                .description(course.getDescription())
                .position(new ArrayList<>(positions))
                .build();

    }

    /**
     * 코스 상세 보기 반환
     */

    public static CourseDetailResponseDTO toCourseDetailDTO(Course course){
       List<CourseDetailResponseDTO.storeList> stores = new ArrayList<>();

       for(CourseStore courseStore: course.getCourseStores()){
           CourseDetailResponseDTO.storeList store = CourseDetailResponseDTO.storeList.builder()
                   . storeId(courseStore.getStore().getId())
                   .storeName(courseStore.getStore().getStoreName())
                   .address(courseStore.getStore().getAddress())
                   .smenu(courseStore.getStore().getSmenu())
                   .visitOrder(courseStore.getVisitOrder())
                   .lat(courseStore.getStore().getLat())
                   .lng(courseStore.getStore().getLng())
                   .build();
           stores.add(store);
       }

       return CourseDetailResponseDTO.builder()
               .courseId(course.getId())
               .courseName(course.getCourseName())
               .description(course.getDescription())
               .storeCount(course.getCourseStores().size())
               .stores(stores)
               .build();
    }




}


