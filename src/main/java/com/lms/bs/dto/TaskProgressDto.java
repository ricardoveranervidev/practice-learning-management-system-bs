package com.lms.bs.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskProgressDto {
    private Long taskId;
    private Long courseId;
    private String title;
    private String description;
    private Integer orderIndex;
    private Integer estimatedMinutes;
    private Boolean mandatory;
    private Boolean completed;
    private LocalDateTime completedAt;
}
