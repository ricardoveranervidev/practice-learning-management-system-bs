package com.lms.bs.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityMeEndpointIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Acceso a /api/v1/me/enrollments sin token debe retornar 401 Unauthorized")
    void testGetMyEnrollmentsWithoutTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/v1/me/enrollments"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Acceso a /api/v1/me/tasks sin token debe retornar 401 Unauthorized")
    void testGetMyTasksWithoutTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/v1/me/tasks?courseId=1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Acceso a /api/v1/courses público sin token debe retornar 200 OK")
    void testGetCoursesPublicReturns200() throws Exception {
        mockMvc.perform(get("/api/v1/courses"))
                .andExpect(status().isOk());
    }
}
