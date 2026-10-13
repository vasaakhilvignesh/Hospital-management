package com.hospital.doctorservice.service;

import com.hospital.doctorservice.dto.DoctorAvailabilityRequest;
import com.hospital.doctorservice.dto.DoctorAvailabilityResponse;
import com.hospital.doctorservice.entity.DoctorAvailability;
import com.hospital.doctorservice.exception.AvailabilityNotFoundException;
import com.hospital.doctorservice.exception.DoctorNotFoundException;
import com.hospital.doctorservice.repository.DoctorAvailabilityRepository;
import com.hospital.doctorservice.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DoctorAvailabilityService {

    private final DoctorAvailabilityRepository availabilityRepository;
    private final DoctorRepository doctorRepository;

    public DoctorAvailabilityService(DoctorAvailabilityRepository availabilityRepository,
                                     DoctorRepository doctorRepository) {
        this.availabilityRepository = availabilityRepository;
        this.doctorRepository = doctorRepository;
    }

    @Transactional
    public DoctorAvailabilityResponse addAvailability(Long doctorId, DoctorAvailabilityRequest request) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new DoctorNotFoundException("Doctor not found with id: " + doctorId);
        }

        DoctorAvailability av = new DoctorAvailability(
                doctorId,
                request.getDayOfWeek(),
                request.getStartTime(),
                request.getEndTime(),
                request.isAvailable()
        );

        DoctorAvailability saved = availabilityRepository.save(av);
        return DoctorAvailabilityResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<DoctorAvailabilityResponse> getAvailabilityByDoctor(Long doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new DoctorNotFoundException("Doctor not found with id: " + doctorId);
        }

        return availabilityRepository.findByDoctorId(doctorId).stream()
                .map(DoctorAvailabilityResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> checkAvailability(Long doctorId, DayOfWeek day, LocalTime time) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new DoctorNotFoundException("Doctor not found with id: " + doctorId);
        }

        List<DoctorAvailability> slots = availabilityRepository.findByDoctorIdAndDayOfWeek(doctorId, day);
        boolean isAvailable = slots.stream().anyMatch(slot ->
                slot.isAvailable() &&
                !time.isBefore(slot.getStartTime()) &&
                time.isBefore(slot.getEndTime())
        );

        return Map.of(
                "doctorId", doctorId,
                "dayOfWeek", day.name(),
                "time", time.toString(),
                "available", isAvailable
        );
    }

    @Transactional
    public DoctorAvailabilityResponse updateAvailability(Long doctorId, Long id, DoctorAvailabilityRequest request) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new DoctorNotFoundException("Doctor not found with id: " + doctorId);
        }

        DoctorAvailability av = availabilityRepository.findById(id)
                .orElseThrow(() -> new AvailabilityNotFoundException("Availability record not found with id: " + id));

        av.setDayOfWeek(request.getDayOfWeek());
        av.setStartTime(request.getStartTime());
        av.setEndTime(request.getEndTime());
        av.setAvailable(request.isAvailable());

        DoctorAvailability updated = availabilityRepository.save(av);
        return DoctorAvailabilityResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteAvailability(Long doctorId, Long id) {
        if (!availabilityRepository.existsById(id)) {
            throw new AvailabilityNotFoundException("Availability record not found with id: " + id);
        }
        availabilityRepository.deleteById(id);
    }
}
