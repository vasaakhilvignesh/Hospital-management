package com.hospital.doctorservice.service;

import com.hospital.doctorservice.dto.DoctorRequest;
import com.hospital.doctorservice.dto.DoctorResponse;
import com.hospital.doctorservice.entity.Doctor;
import com.hospital.doctorservice.entity.DoctorStatus;
import com.hospital.doctorservice.exception.DoctorNotFoundException;
import com.hospital.doctorservice.exception.DuplicateDoctorException;
import com.hospital.doctorservice.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Transactional
    public DoctorResponse createDoctor(DoctorRequest request) {
        if (doctorRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateDoctorException("Doctor with email '" + request.getEmail() + "' already exists");
        }

        String code = request.getDoctorCode();
        if (code == null || code.isBlank()) {
            code = "DOC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } else if (doctorRepository.existsByDoctorCode(code)) {
            throw new DuplicateDoctorException("Doctor code '" + code + "' already exists");
        }

        Doctor doctor = new Doctor(
                code,
                request.getUserId(),
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhone(),
                request.getSpecialization(),
                request.getDepartment(),
                request.getQualification(),
                request.getExperienceYears(),
                request.getConsultationFee(),
                request.getStatus() != null ? request.getStatus() : DoctorStatus.ACTIVE
        );

        Doctor saved = doctorRepository.save(doctor);
        return DoctorResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<DoctorResponse> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(DoctorResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DoctorResponse getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + id));
        return DoctorResponse.fromEntity(doctor);
    }

    @Transactional(readOnly = true)
    public DoctorResponse getDoctorByUserId(Long userId) {
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor record not found for user id: " + userId));
        return DoctorResponse.fromEntity(doctor);
    }

    @Transactional(readOnly = true)
    public List<DoctorResponse> searchDoctors(String query) {
        return doctorRepository.searchDoctors(query).stream()
                .map(DoctorResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public DoctorResponse updateDoctor(Long id, DoctorRequest request) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + id));

        doctor.setFirstName(request.getFirstName());
        doctor.setLastName(request.getLastName());
        doctor.setEmail(request.getEmail());
        doctor.setPhone(request.getPhone());
        doctor.setSpecialization(request.getSpecialization());
        doctor.setDepartment(request.getDepartment());
        doctor.setQualification(request.getQualification());
        doctor.setExperienceYears(request.getExperienceYears());
        doctor.setConsultationFee(request.getConsultationFee());
        if (request.getStatus() != null) {
            doctor.setStatus(request.getStatus());
        }

        Doctor updated = doctorRepository.save(doctor);
        return DoctorResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteDoctor(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new DoctorNotFoundException("Doctor not found with id: " + id);
        }
        doctorRepository.deleteById(id);
    }
}
