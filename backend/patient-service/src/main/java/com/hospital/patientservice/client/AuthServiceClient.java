package com.hospital.patientservice.client;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class AuthServiceClient {

    private final RestClient restClient;

    public AuthServiceClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("http://AUTH-SERVICE")
                .build();
    }

    public Map<String, Object> registerUser(Map<String, Object> request) {
        return restClient.post()
                .uri("/api/auth/register")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, resp) -> {
                    String msg = new String(resp.getBody().readAllBytes());
                    throw new RuntimeException("Auth registration failed: " + msg);
                })
                .body(Map.class);
    }

    public void deleteUser(Long userId) {
        try {
            restClient.delete()
                    .uri("/api/auth/users/{id}", userId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ignored) {
            // Best effort rollback
        }
    }
}
