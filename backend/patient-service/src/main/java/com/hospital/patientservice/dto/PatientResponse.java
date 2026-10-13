package com.hospital.patientservice.dto;

import com.hospital.patientservice.entity.Patient;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PatientResponse {

    private Long id;
    private String patientCode;
    private Long userId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String phone;
    private String email;
    private String address;
    private String bloodGroup;
    private String emergencyContact;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PatientResponse() {
    }

    public static PatientResponse fromEntity(Patient patient) {
        PatientResponse resp = new PatientResponse();
        resp.setId(patient.getId());
        resp.setPatientCode(patient.getPatientCode());
        resp.setUserId(patient.getUserId());
        resp.setFirstName(patient.getFirstName());
        resp.setLastName(patient.getLastName());
        resp.setDateOfBirth(patient.getDateOfBirth());
        resp.setGender(patient.getGender());
        resp.setPhone(patient.getPhone());
        resp.setEmail(patient.getEmail());
        resp.setAddress(patient.getAddress());
        resp.setBloodGroup(patient.getBloodGroup());
        resp.setEmergencyContact(patient.getEmergencyContact());
        resp.setCreatedAt(patient.getCreatedAt());
        resp.setUpdatedAt(patient.getUpdatedAt());
        return resp;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPatientCode() { return patientCode; }
    public void setPatientCode(String patientCode) { this.patientCode = patientCode; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
