package com.hospital.reportservice.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class PatientServiceClient {

    private final RestClient restClient;

    public PatientServiceClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("http://PATIENT-SERVICE")
                .build();
    }

    public Map<String, Object> getPatient(Long patientId, String token) {
        return restClient.get()
                .uri("/api/patients/{id}", patientId)
                .header("Authorization", token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, resp) -> {
                    throw new RuntimeException("Patient not found with id: " + patientId);
                })
                .body(Map.class);
    }

    public List<Map<String, Object>> getAllPatients(String token) {
        try {
            return restClient.get()
                    .uri("/api/patients")
                    .header("Authorization", token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Map<String, Object>> getMedicalHistories(Long patientId, String token) {
        try {
            return restClient.get()
                    .uri("/api/patients/{id}/medical-history", patientId)
                    .header("Authorization", token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public Map<String, Object> getPatientByUserId(Long userId, String token) {
        return restClient.get()
                .uri("/api/patients/by-user/{userId}", userId)
                .header("Authorization", token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, resp) -> {
                    throw new RuntimeException("Patient not found for user id: " + userId);
                })
                .body(Map.class);
    }
}
