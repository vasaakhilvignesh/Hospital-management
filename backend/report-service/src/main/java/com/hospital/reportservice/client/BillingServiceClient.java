package com.hospital.reportservice.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class BillingServiceClient {

    private final RestClient restClient;

    public BillingServiceClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("http://BILLING-SERVICE")
                .build();
    }

    public List<Map<String, Object>> getBillsByPatient(Long patientId, String token) {
        try {
            return restClient.get()
                    .uri("/api/bills/patient/{patientId}", patientId)
                    .header("Authorization", token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Map<String, Object>> getAllBills(String token) {
        try {
            return restClient.get()
                    .uri("/api/bills")
                    .header("Authorization", token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
