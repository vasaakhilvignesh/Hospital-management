package com.hospital.appointmentservice.service;

import com.hospital.appointmentservice.client.DoctorServiceClient;
import com.hospital.appointmentservice.client.PatientServiceClient;
import com.hospital.appointmentservice.dto.AppointmentRequest;
import com.hospital.appointmentservice.dto.AppointmentRescheduleRequest;
import com.hospital.appointmentservice.dto.AppointmentResponse;
import com.hospital.appointmentservice.entity.Appointment;
import com.hospital.appointmentservice.entity.AppointmentStatus;
import com.hospital.appointmentservice.exception.*;
import com.hospital.appointmentservice.repository.AppointmentRepository;
import com.hospital.appointmentservice.security.UserPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientServiceClient patientServiceClient;
    private final DoctorServiceClient doctorServiceClient;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientServiceClient patientServiceClient,
                              DoctorServiceClient doctorServiceClient) {
        this.appointmentRepository = appointmentRepository;
        this.patientServiceClient = patientServiceClient;
        this.doctorServiceClient = doctorServiceClient;
    }

    @Transactional
    public AppointmentResponse createAppointment(AppointmentRequest request, UserPrincipal principal) {
        // Validation: Patient cannot book on behalf of another patientId
        validatePatientBookingAuthorization(request.getPatientId(), principal);

        // Validation: appointment date/time cannot be in the past
        if (request.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new InvalidAppointmentOperationException("Appointment date cannot be in the past");
        }
        if (request.getAppointmentDate().isEqual(LocalDate.now()) && request.getStartTime().isBefore(LocalTime.now())) {
            throw new InvalidAppointmentOperationException("Appointment start time cannot be in the past");
        }

        // Validation: start time must be before end time
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new InvalidAppointmentOperationException("Start time must be before end time");
        }

        // =========================================================================
        // REAL SERVICE-TO-SERVICE CALL 1: Patient Service
        // Verifies patient exists in Patient Service
        // =========================================================================
        patientServiceClient.getPatient(request.getPatientId(), principal.getRawToken());

        // =========================================================================
        // REAL SERVICE-TO-SERVICE CALL 2: Doctor Service
        // Verifies doctor exists in Doctor Service
        // =========================================================================
        Map<String, Object> doc = doctorServiceClient.getDoctor(request.getDoctorId(), principal.getRawToken());
        Object docStatus = doc != null ? doc.get("status") : null;
        if (docStatus != null && !"ACTIVE".equalsIgnoreCase(docStatus.toString())) {
            throw new DoctorUnavailableException("Doctor is not currently available for appointments (Status: " + docStatus + ")");
        }

        // =========================================================================
        // REAL SERVICE-TO-SERVICE CALL 3: Doctor Availability Check
        // =========================================================================
        boolean available = doctorServiceClient.checkAvailability(
                request.getDoctorId(),
                request.getAppointmentDate().getDayOfWeek(),
                request.getStartTime(),
                principal.getRawToken()
        );

        if (!available) {
            throw new DoctorUnavailableException(
                    "Doctor " + request.getDoctorId() + " is not available on " +
                    request.getAppointmentDate().getDayOfWeek() + " at " + request.getStartTime()
            );
        }

        // =========================================================================
        // CONFLICT CHECKS IN APPOINTMENT SERVICE'S DATABASE
        // =========================================================================
        checkForConflicts(request.getDoctorId(), request.getPatientId(), request.getAppointmentDate(),
                request.getStartTime(), request.getEndTime(), null);

        // Generate appointment code
        String code = "APT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Appointment appointment = new Appointment(
                code,
                request.getPatientId(),
                request.getDoctorId(),
                request.getAppointmentDate(),
                request.getStartTime(),
                request.getEndTime(),
                request.getReason(),
                AppointmentStatus.SCHEDULED,
                request.getNotes()
        );

        Appointment saved = appointmentRepository.save(appointment);
        return AppointmentResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointments(Long patientId, Long doctorId, AppointmentStatus status,
                                                     LocalDate date, UserPrincipal principal) {
        Long resolvedDoctorId = doctorId;
        if (principal != null && "DOCTOR".equals(principal.getRole()) && resolvedDoctorId == null) {
            try {
                Map<String, Object> doc = doctorServiceClient.getDoctorByUserId(principal.getUserId(), principal.getRawToken());
                if (doc != null && doc.get("id") != null) {
                    resolvedDoctorId = Long.valueOf(doc.get("id").toString());
                }
            } catch (Exception ignored) {}
        }

        final Long finalDocId = resolvedDoctorId;
        return appointmentRepository.findAll().stream()
                .filter(apt -> patientId == null || apt.getPatientId().equals(patientId))
                .filter(apt -> finalDocId == null || apt.getDoctorId().equals(finalDocId))
                .filter(apt -> status == null || apt.getStatus() == status)
                .filter(apt -> date == null || apt.getAppointmentDate().equals(date))
                .filter(apt -> canViewAppointment(apt, principal))
                .map(AppointmentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(Long id, UserPrincipal principal) {
        Appointment apt = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + id));

        if (!canViewAppointment(apt, principal)) {
            throw new AccessDeniedException("Forbidden: you cannot view this appointment");
        }

        return AppointmentResponse.fromEntity(apt);
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentByCode(String code, UserPrincipal principal) {
        Appointment apt = appointmentRepository.findByAppointmentCode(code)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with code: " + code));

        if (!canViewAppointment(apt, principal)) {
            throw new AccessDeniedException("Forbidden: you cannot view this appointment");
        }

        return AppointmentResponse.fromEntity(apt);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByPatient(Long patientId, UserPrincipal principal) {
        if ("PATIENT".equals(principal.getRole())) {
            // Validate patient belongs to caller
            validatePatientAccess(patientId, principal);
        }

        return appointmentRepository.findByPatientIdOrderByAppointmentDateDescStartTimeDesc(patientId).stream()
                .map(AppointmentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByDoctor(Long doctorId, UserPrincipal principal) {
        return appointmentRepository.findByDoctorIdOrderByAppointmentDateDescStartTimeDesc(doctorId).stream()
                .filter(apt -> canViewAppointment(apt, principal))
                .map(AppointmentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public AppointmentResponse rescheduleAppointment(Long id, AppointmentRescheduleRequest request, UserPrincipal principal) {
        Appointment apt = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + id));

        if (!canModifyAppointment(apt, principal)) {
            throw new AccessDeniedException("Forbidden: you cannot reschedule this appointment");
        }

        if (apt.getStatus() == AppointmentStatus.CANCELLED) {
            throw new InvalidAppointmentOperationException("Cannot reschedule a CANCELLED appointment");
        }

        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new InvalidAppointmentOperationException("Start time must be before end time");
        }

        // Verify doctor availability on new date/time
        boolean available = doctorServiceClient.checkAvailability(
                apt.getDoctorId(),
                request.getAppointmentDate().getDayOfWeek(),
                request.getStartTime(),
                principal.getRawToken()
        );

        if (!available) {
            throw new DoctorUnavailableException(
                    "Doctor " + apt.getDoctorId() + " is unavailable on " +
                    request.getAppointmentDate().getDayOfWeek() + " at " + request.getStartTime()
            );
        }

        // Check conflicts
        checkForConflicts(apt.getDoctorId(), apt.getPatientId(), request.getAppointmentDate(),
                request.getStartTime(), request.getEndTime(), apt.getId());

        apt.setAppointmentDate(request.getAppointmentDate());
        apt.setStartTime(request.getStartTime());
        apt.setEndTime(request.getEndTime());
        apt.setStatus(AppointmentStatus.RESCHEDULED);

        Appointment updated = appointmentRepository.save(apt);
        return AppointmentResponse.fromEntity(updated);
    }

    @Transactional
    public AppointmentResponse cancelAppointment(Long id, UserPrincipal principal) {
        Appointment apt = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + id));

        if (!canModifyAppointment(apt, principal)) {
            throw new AccessDeniedException("Forbidden: you cannot cancel this appointment");
        }

        if (apt.getStatus() == AppointmentStatus.COMPLETED) {
            throw new InvalidAppointmentOperationException("Cannot cancel a COMPLETED appointment");
        }
        if (apt.getStatus() == AppointmentStatus.CANCELLED) {
            return AppointmentResponse.fromEntity(apt);
        }

        apt.setStatus(AppointmentStatus.CANCELLED);
        Appointment updated = appointmentRepository.save(apt);
        return AppointmentResponse.fromEntity(updated);
    }

    @Transactional
    public AppointmentResponse updateStatus(Long id, AppointmentStatus status, UserPrincipal principal) {
        Appointment apt = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + id));

        if ("PATIENT".equals(principal.getRole()) && status != AppointmentStatus.CANCELLED) {
            throw new AccessDeniedException("Patients can only cancel appointments");
        }

        if (apt.getStatus() == AppointmentStatus.CANCELLED) {
            throw new InvalidAppointmentOperationException("Cannot change status of a CANCELLED appointment");
        }
        if (apt.getStatus() == AppointmentStatus.COMPLETED) {
            throw new InvalidAppointmentOperationException("Cannot change status of a COMPLETED appointment");
        }

        apt.setStatus(status);
        Appointment updated = appointmentRepository.save(apt);
        return AppointmentResponse.fromEntity(updated);
    }

    @Transactional(readOnly = true)
    public List<com.hospital.appointmentservice.dto.SlotResponse> getAvailableSlots(Long doctorId, LocalDate date, UserPrincipal principal) {
        if (date == null || date.isBefore(LocalDate.now())) {
            throw new InvalidAppointmentOperationException("Date must be today or in the future");
        }

        List<Map<String, Object>> availabilities = doctorServiceClient.getDoctorAvailabilities(doctorId, principal.getRawToken());
        if (availabilities == null || availabilities.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        DayOfWeek targetDay = date.getDayOfWeek();
        java.time.format.DateTimeFormatter timeFormatter = new java.time.format.DateTimeFormatterBuilder()
                .appendPattern("HH:mm")
                .optionalStart()
                .appendPattern(":ss")
                .optionalEnd()
                .toFormatter();

        List<Map<String, Object>> daySlots = availabilities.stream()
                .filter(av -> {
                    Object dayObj = av.get("dayOfWeek");
                    Boolean isAvail = (Boolean) av.get("available");
                    return dayObj != null && targetDay.name().equalsIgnoreCase(dayObj.toString()) && Boolean.TRUE.equals(isAvail);
                })
                .toList();

        if (daySlots.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        List<Appointment> existingApts = appointmentRepository
                .findByDoctorIdAndAppointmentDateAndStatusNot(doctorId, date, AppointmentStatus.CANCELLED);

        LocalTime nowTime = date.equals(LocalDate.now()) ? LocalTime.now() : LocalTime.MIN;
        List<com.hospital.appointmentservice.dto.SlotResponse> result = new java.util.ArrayList<>();

        for (Map<String, Object> av : daySlots) {
            LocalTime windowStart = LocalTime.parse(av.get("startTime").toString(), timeFormatter);
            LocalTime windowEnd = LocalTime.parse(av.get("endTime").toString(), timeFormatter);

            LocalTime slotStart = windowStart;
            while (slotStart.plusMinutes(30).compareTo(windowEnd) <= 0) {
                LocalTime slotEnd = slotStart.plusMinutes(30);

                final LocalTime currentStart = slotStart;
                final LocalTime currentEnd = slotEnd;
                boolean isPast = date.equals(LocalDate.now()) && currentStart.isBefore(nowTime);
                boolean conflict = existingApts.stream().anyMatch(apt ->
                        currentStart.isBefore(apt.getEndTime()) && currentEnd.isAfter(apt.getStartTime())
                );

                boolean available = !isPast && !conflict;
                result.add(new com.hospital.appointmentservice.dto.SlotResponse(currentStart, currentEnd, available));
                slotStart = slotEnd;
            }
        }

        return result;
    }

    // ================= Helper Methods =================

    private void checkForConflicts(Long doctorId, Long patientId, LocalDate date,
                                   LocalTime start, LocalTime end, Long currentAptId) {
        List<Appointment> docConflicts = appointmentRepository.findDoctorConflicts(
                doctorId, date, start, end, currentAptId, AppointmentStatus.CANCELLED
        );
        if (!docConflicts.isEmpty()) {
            throw new AppointmentConflictException("Doctor has an overlapping appointment at the selected time");
        }

        List<Appointment> patConflicts = appointmentRepository.findPatientConflicts(
                patientId, date, start, end, currentAptId, AppointmentStatus.CANCELLED
        );
        if (!patConflicts.isEmpty()) {
            throw new AppointmentConflictException("Patient already has an appointment scheduled at the selected time");
        }
    }

    private boolean canViewAppointment(Appointment apt, UserPrincipal principal) {
        if ("ADMIN".equals(principal.getRole()) || "RECEPTIONIST".equals(principal.getRole()) || "STAFF".equals(principal.getRole())) {
            return true;
        }
        if ("DOCTOR".equals(principal.getRole())) {
            try {
                Map<String, Object> doc = doctorServiceClient.getDoctorByUserId(principal.getUserId(), principal.getRawToken());
                Object docId = doc.get("id");
                return docId != null && docId.toString().equals(apt.getDoctorId().toString());
            } catch (Exception e) {
                return false;
            }
        }
        if ("PATIENT".equals(principal.getRole())) {
            try {
                Map<String, Object> patient = patientServiceClient.getPatient(apt.getPatientId(), principal.getRawToken());
                Object uid = patient.get("userId");
                return uid != null && uid.toString().equals(principal.getUserId().toString());
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    private boolean canModifyAppointment(Appointment apt, UserPrincipal principal) {
        if ("ADMIN".equals(principal.getRole()) || "RECEPTIONIST".equals(principal.getRole())) {
            return true;
        }
        if ("PATIENT".equals(principal.getRole())) {
            return canViewAppointment(apt, principal);
        }
        return false;
    }

    private void validatePatientBookingAuthorization(Long patientId, UserPrincipal principal) {
        if ("PATIENT".equals(principal.getRole())) {
            validatePatientAccess(patientId, principal);
        }
    }

    private void validatePatientAccess(Long patientId, UserPrincipal principal) {
        Map<String, Object> patient = patientServiceClient.getPatient(patientId, principal.getRawToken());
        Object uid = patient.get("userId");
        if (uid == null || !uid.toString().equals(principal.getUserId().toString())) {
            throw new AccessDeniedException("Patients can only manage their own appointments");
        }
    }
}
