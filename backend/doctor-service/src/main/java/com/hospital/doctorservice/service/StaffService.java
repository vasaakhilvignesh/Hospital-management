package com.hospital.doctorservice.service;

import com.hospital.doctorservice.dto.StaffRequest;
import com.hospital.doctorservice.dto.StaffResponse;
import com.hospital.doctorservice.entity.Staff;
import com.hospital.doctorservice.entity.StaffStatus;
import com.hospital.doctorservice.exception.DuplicateStaffException;
import com.hospital.doctorservice.exception.StaffNotFoundException;
import com.hospital.doctorservice.repository.StaffRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StaffService {

    private final StaffRepository staffRepository;

    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @Transactional
    public StaffResponse createStaff(StaffRequest request) {
        if (staffRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateStaffException("Staff with email '" + request.getEmail() + "' already exists");
        }

        String code = request.getStaffCode();
        if (code == null || code.isBlank()) {
            code = "STF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } else if (staffRepository.existsByStaffCode(code)) {
            throw new DuplicateStaffException("Staff code '" + code + "' already exists");
        }

        Staff staff = new Staff(
                code,
                request.getUserId(),
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhone(),
                request.getDesignation(),
                request.getDepartment(),
                request.getJoiningDate(),
                request.getStatus() != null ? request.getStatus() : StaffStatus.ACTIVE
        );

        Staff saved = staffRepository.save(staff);
        return StaffResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<StaffResponse> getAllStaff() {
        return staffRepository.findAll().stream()
                .map(StaffResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StaffResponse getStaffById(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new StaffNotFoundException("Staff not found with id: " + id));
        return StaffResponse.fromEntity(staff);
    }

    @Transactional(readOnly = true)
    public List<StaffResponse> searchStaff(String query) {
        return staffRepository.searchStaff(query).stream()
                .map(StaffResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public StaffResponse updateStaff(Long id, StaffRequest request) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new StaffNotFoundException("Staff not found with id: " + id));

        staff.setFirstName(request.getFirstName());
        staff.setLastName(request.getLastName());
        staff.setEmail(request.getEmail());
        staff.setPhone(request.getPhone());
        staff.setDesignation(request.getDesignation());
        staff.setDepartment(request.getDepartment());
        if (request.getJoiningDate() != null) {
            staff.setJoiningDate(request.getJoiningDate());
        }
        if (request.getStatus() != null) {
            staff.setStatus(request.getStatus());
        }

        Staff updated = staffRepository.save(staff);
        return StaffResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteStaff(Long id) {
        if (!staffRepository.existsById(id)) {
            throw new StaffNotFoundException("Staff not found with id: " + id);
        }
        staffRepository.deleteById(id);
    }
}
