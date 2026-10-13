package com.hospital.doctorservice.dto;

import com.hospital.doctorservice.entity.DoctorStatus;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class DoctorRequest {

    private String doctorCode;
    private Long userId;

    @NotBlank(message = "First name is required")
    @Size(max = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    private String phone;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    @NotBlank(message = "Department is required")
    private String department;

    private String qualification;

    @PositiveOrZero(message = "Experience must be non-negative")
    private Integer experienceYears;

    @DecimalMin(value = "0.0", inclusive = true, message = "Consultation fee must be non-negative")
    private BigDecimal consultationFee;

    private DoctorStatus status;

    public DoctorRequest() {
    }

    public DoctorRequest(String doctorCode, Long userId, String firstName, String lastName, String email,
                         String phone, String specialization, String department, String qualification,
                         Integer experienceYears, BigDecimal consultationFee, DoctorStatus status) {
        this.doctorCode = doctorCode;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.specialization = specialization;
        this.department = department;
        this.qualification = qualification;
        this.experienceYears = experienceYears;
        this.consultationFee = consultationFee;
        this.status = status;
    }

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
}
