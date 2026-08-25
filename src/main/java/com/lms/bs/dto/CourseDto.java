package com.lms.bs.dto;

import com.lms.bs.domain.entity.CourseLevel;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDto {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String instructor;
    private Integer durationHours;
    private CourseLevel level;
    private String imageUrl;
    private LocalDateTime createdAt;
    private Integer totalTasks;
    private Boolean isEnrolled;
}
