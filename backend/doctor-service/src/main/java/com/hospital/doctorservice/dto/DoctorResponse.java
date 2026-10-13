package com.hospital.doctorservice.dto;

import com.hospital.doctorservice.entity.Doctor;
import com.hospital.doctorservice.entity.DoctorStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DoctorResponse {

    private Long id;
    private String doctorCode;
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String specialization;
    private String department;
    private String qualification;
    private Integer experienceYears;
    private BigDecimal consultationFee;
    private DoctorStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DoctorResponse() {
    }

    public static DoctorResponse fromEntity(Doctor doctor) {
        DoctorResponse resp = new DoctorResponse();
        resp.setId(doctor.getId());
        resp.setDoctorCode(doctor.getDoctorCode());
        resp.setUserId(doctor.getUserId());
        resp.setFirstName(doctor.getFirstName());
        resp.setLastName(doctor.getLastName());
        resp.setEmail(doctor.getEmail());
        resp.setPhone(doctor.getPhone());
        resp.setSpecialization(doctor.getSpecialization());
        resp.setDepartment(doctor.getDepartment());
        resp.setQualification(doctor.getQualification());
        resp.setExperienceYears(doctor.getExperienceYears());
        resp.setConsultationFee(doctor.getConsultationFee());
        resp.setStatus(doctor.getStatus());
        resp.setCreatedAt(doctor.getCreatedAt());
        resp.setUpdatedAt(doctor.getUpdatedAt());
        return resp;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDoctorCode() { return doctorCode; }
    public void setDoctorCode(String doctorCode) { this.doctorCode = doctorCode; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }

    public BigDecimal getConsultationFee() { return consultationFee; }
    public void setConsultationFee(BigDecimal consultationFee) { this.consultationFee = consultationFee; }

    public DoctorStatus getStatus() { return status; }
    public void setStatus(DoctorStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
