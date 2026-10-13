package com.hospital.pharmacyservice.client;

import com.hospital.pharmacyservice.exception.DoctorNotFoundException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class DoctorServiceClient {

    private final RestClient restClient;

    public DoctorServiceClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("http://DOCTOR-SERVICE")
                .build();
    }

    /**
     * Verifies doctor existence via GET /api/doctors/{id}.
     */
    public Map<String, Object> getDoctor(Long doctorId, String token) {
        return restClient.get()
                .uri("/api/doctors/{id}", doctorId)
                .header("Authorization", token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new DoctorNotFoundException("Doctor not found with id: " + doctorId);
                })
                .body(Map.class);
    }

    public Map<String, Object> getDoctorByUserId(Long userId, String token) {
        return restClient.get()
                .uri("/api/doctors/by-user/{userId}", userId)
                .header("Authorization", token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new DoctorNotFoundException("Doctor not found for user id: " + userId);
                })
                .body(Map.class);
    }
}
