package com.hospital.billingservice.client;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class PharmacyServiceClient {

    private final RestClient restClient;

    public PharmacyServiceClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("http://PHARMACY-SERVICE")
                .build();
    }

    public Map<String, Object> getMedicine(Long medicineId, String token) {
        return restClient.get()
                .uri("/api/medicines/{id}", medicineId)
                .header("Authorization", token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new RuntimeException("Medicine not found with id: " + medicineId);
                })
                .body(Map.class);
    }

    public Map<String, Object> getPrescription(Long prescriptionId, String token) {
        return restClient.get()
                .uri("/api/prescriptions/{id}", prescriptionId)
                .header("Authorization", token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new RuntimeException("Prescription not found with id: " + prescriptionId);
                })
                .body(Map.class);
    }
}
