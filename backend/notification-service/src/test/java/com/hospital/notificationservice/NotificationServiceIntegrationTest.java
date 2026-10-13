package com.hospital.notificationservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.notificationservice.dto.NotificationRequest;
import com.hospital.notificationservice.dto.NotificationResponse;
import com.hospital.notificationservice.entity.NotificationType;
import com.hospital.notificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "eureka.client.enabled=false"
})
@AutoConfigureMockMvc
class NotificationServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotificationRepository notificationRepository;

    private String adminToken;
    private String user5Token;
    private String user99Token;

    @BeforeEach
    void setUp() {
        adminToken = "Bearer " + generateRealToken(1L, "admin", "ADMIN");
        user5Token = "Bearer " + generateRealToken(5L, "patient", "PATIENT");
        user99Token = "Bearer " + generateRealToken(99L, "otheruser", "PATIENT");
    }

    private String generateRealToken(Long userId, String username, String role) {
        return io.jsonwebtoken.Jwts.builder()
                .claims(Map.of("userId", userId, "role", role))
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                        "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970".getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    @Test
    void testNotificationLifecycleAndUserIsolation() throws Exception {
        // 1. Admin creates notification for User 5
        NotificationRequest req = new NotificationRequest(
                5L, 1L, NotificationType.APPOINTMENT,
                "Test Appointment Alert", "Your appointment has been confirmed."
        );

        MvcResult res = mockMvc.perform(post("/api/notifications")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.notificationCode").isNotEmpty())
                .andExpect(jsonPath("$.status").value("UNREAD"))
                .andReturn();

        NotificationResponse created = objectMapper.readValue(res.getResponse().getContentAsString(), NotificationResponse.class);

        // 2. User 5 can retrieve their notification
        mockMvc.perform(get("/api/notifications/" + created.getId())
                        .header("Authorization", user5Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Appointment Alert"));

        // 3. User 99 cannot view User 5's notification -> 403 Forbidden
        mockMvc.perform(get("/api/notifications/" + created.getId())
                        .header("Authorization", user99Token))
                .andExpect(status().isForbidden());

        // 4. User 99 cannot view User 5's notifications by userId -> 403 Forbidden
        mockMvc.perform(get("/api/notifications/user/5")
                        .header("Authorization", user99Token))
                .andExpect(status().isForbidden());

        // 5. Mark notification as READ
        mockMvc.perform(patch("/api/notifications/" + created.getId() + "/read")
                        .header("Authorization", user5Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("READ"))
                .andExpect(jsonPath("$.readAt").isNotEmpty());

        // 6. Check unread count
        mockMvc.perform(get("/api/notifications/unread-count")
                        .header("Authorization", user5Token))
                .andExpect(status().isOk());

        // 7. Mark all as READ
        mockMvc.perform(patch("/api/notifications/read-all")
                        .header("Authorization", user5Token))
                .andExpect(status().isNoContent());

        // 8. Unread count is now 0
        mockMvc.perform(get("/api/notifications/unread-count")
                        .header("Authorization", user5Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unreadCount").value(0));
    }
}
