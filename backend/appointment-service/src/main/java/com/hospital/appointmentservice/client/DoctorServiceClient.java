package com.hospital.appointmentservice.client;

import com.hospital.appointmentservice.exception.DoctorNotFoundException;
import com.hospital.appointmentservice.exception.DoctorUnavailableException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class DoctorServiceClient {

    private final RestClient restClient;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

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

    /**
     * Checks doctor availability via GET /api/doctors/{doctorId}/availability/check?day=...&time=...
     */
    public boolean checkAvailability(Long doctorId, DayOfWeek day, LocalTime time, String token) {
        Map<String, Object> resp = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/doctors/{doctorId}/availability/check")
                        .queryParam("day", day.name())
                        .queryParam("time", time.format(TIME_FORMAT))
                        .build(doctorId))
                .header("Authorization", token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new DoctorNotFoundException("Doctor availability check failed for doctor id: " + doctorId);
                })
                .body(Map.class);

        if (resp != null && Boolean.TRUE.equals(resp.get("available"))) {
            return true;
        }
        return false;
    }

    /**
     * Gets doctor weekly availability intervals via GET /api/doctors/{doctorId}/availability
     */
    @SuppressWarnings("unchecked")
    public java.util.List<Map<String, Object>> getDoctorAvailabilities(Long doctorId, String token) {
        return restClient.get()
                .uri("/api/doctors/{doctorId}/availability", doctorId)
                .header("Authorization", token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new DoctorNotFoundException("Doctor availability check failed for doctor id: " + doctorId);
                })
                .body(java.util.List.class);
    }
}
