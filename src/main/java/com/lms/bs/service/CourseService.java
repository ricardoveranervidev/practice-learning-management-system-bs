package com.lms.bs.service;

import com.lms.bs.dto.CourseDto;
import com.lms.bs.dto.TaskDto;

import java.util.List;

public interface CourseService {
    List<CourseDto> getAllCourses(String query, Long currentUserId);
    CourseDto getCourseById(Long id, Long currentUserId);
    List<TaskDto> getTasksByCourseId(Long courseId);
    TaskDto getTaskById(Long courseId, Long taskId);
}
