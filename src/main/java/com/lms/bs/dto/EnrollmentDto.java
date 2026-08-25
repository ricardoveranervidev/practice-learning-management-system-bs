package com.lms.bs.dto;

import com.lms.bs.domain.entity.EnrollmentStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentDto {
    private Long id;
    private Long courseId;
    private String courseTitle;
    private String courseDescription;
    private String category;
    private String instructor;
    private String imageUrl;
    private LocalDateTime enrolledAt;
    private EnrollmentStatus status;
    private Long completedTasksCount;
    private Long totalTasksCount;
    private Double progressPercentage;
}
