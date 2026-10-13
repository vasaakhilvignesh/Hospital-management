package com.hospital.reportservice.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class PharmacyServiceClient {

    private final RestClient restClient;

    public PharmacyServiceClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("http://PHARMACY-SERVICE")
                .build();
    }

    public List<Map<String, Object>> getPrescriptionsByPatient(Long patientId, String token) {
        try {
            return restClient.get()
                    .uri("/api/prescriptions?patientId={patientId}", patientId)
                    .header("Authorization", token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Map<String, Object>> getAllPrescriptions(String token) {
        try {
            return restClient.get()
                    .uri("/api/prescriptions")
                    .header("Authorization", token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Map<String, Object>> getLowStock(String token) {
        try {
            return restClient.get()
                    .uri("/api/inventory/low-stock")
                    .header("Authorization", token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
