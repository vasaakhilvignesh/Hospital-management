package com.hospital.reportservice.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class AppointmentServiceClient {

    private final RestClient restClient;

    public AppointmentServiceClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("http://APPOINTMENT-SERVICE")
                .build();
    }

    public List<Map<String, Object>> getAppointmentsByPatient(Long patientId, String token) {
        try {
            return restClient.get()
                    .uri("/api/appointments/patient/{patientId}", patientId)
                    .header("Authorization", token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Map<String, Object>> getAllAppointments(String token) {
        try {
            return restClient.get()
                    .uri("/api/appointments")
                    .header("Authorization", token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
