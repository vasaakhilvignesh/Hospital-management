package com.hospital.reportservice.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class DoctorServiceClient {

    private final RestClient restClient;

    public DoctorServiceClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("http://DOCTOR-SERVICE")
                .build();
    }

    public Map<String, Object> getDoctor(Long doctorId, String token) {
        return restClient.get()
                .uri("/api/doctors/{id}", doctorId)
                .header("Authorization", token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, resp) -> {
                    throw new RuntimeException("Doctor not found with id: " + doctorId);
                })
                .body(Map.class);
    }

    public List<Map<String, Object>> getAllDoctors(String token) {
        try {
            return restClient.get()
                    .uri("/api/doctors")
                    .header("Authorization", token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
