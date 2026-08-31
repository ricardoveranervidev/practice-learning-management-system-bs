package com.lms.bs.service;

import com.lms.bs.dto.EnrollmentDto;

import java.util.List;

public interface EnrollmentService {
    EnrollmentDto enroll(Long userId, Long courseId);
    void withdraw(Long userId, Long courseId);
    List<EnrollmentDto> getUserEnrollments(Long userId);
}
