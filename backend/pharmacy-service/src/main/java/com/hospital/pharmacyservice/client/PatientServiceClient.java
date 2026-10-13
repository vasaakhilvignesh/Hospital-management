package com.hospital.pharmacyservice.client;

import com.hospital.pharmacyservice.exception.PatientNotFoundException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class PatientServiceClient {

    private final RestClient restClient;

    public PatientServiceClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("http://PATIENT-SERVICE")
                .build();
    }

    /**
     * Verifies patient existence via GET /api/patients/{id} using bearer token propagation.
     */
    public Map<String, Object> getPatient(Long patientId, String token) {
        return restClient.get()
                .uri("/api/patients/{id}", patientId)
                .header("Authorization", token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new PatientNotFoundException("Patient not found with id: " + patientId);
                })
                .body(Map.class);
    }

    public Map<String, Object> getPatientByUserId(Long userId, String token) {
        return restClient.get()
                .uri("/api/patients/by-user/{userId}", userId)
                .header("Authorization", token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new PatientNotFoundException("Patient not found for user id: " + userId);
                })
                .body(Map.class);
    }
}
