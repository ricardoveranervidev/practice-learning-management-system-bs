package com.lms.bs.service;

import com.lms.bs.dto.AuthRequest;
import com.lms.bs.dto.AuthResponse;

public interface AuthService {
    AuthResponse login(AuthRequest request);
}
