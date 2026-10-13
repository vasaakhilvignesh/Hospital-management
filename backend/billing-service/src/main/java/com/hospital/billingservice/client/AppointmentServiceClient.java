package com.hospital.billingservice.client;

import com.hospital.billingservice.exception.AppointmentNotFoundException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class AppointmentServiceClient {

    private final RestClient restClient;

    public AppointmentServiceClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("http://APPOINTMENT-SERVICE")
                .build();
    }

    public Map<String, Object> getAppointment(Long appointmentId, String token) {
        return restClient.get()
                .uri("/api/appointments/{id}", appointmentId)
                .header("Authorization", token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new AppointmentNotFoundException("Appointment not found with id: " + appointmentId);
                })
                .body(Map.class);
    }
}
