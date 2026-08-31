package com.lms.bs.service;

import com.lms.bs.dto.TaskProgressDto;

import java.util.List;

public interface TaskProgressService {
    List<TaskProgressDto> getTasksWithUserProgress(Long userId, Long courseId);
    TaskProgressDto markTaskComplete(Long userId, Long taskId);
    TaskProgressDto unmarkTaskComplete(Long userId, Long taskId);
}
