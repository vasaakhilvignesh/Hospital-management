package com.hospital.doctorservice.dto;

import com.hospital.doctorservice.entity.StaffStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class StaffRequest {

    private String staffCode;
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

    @NotBlank(message = "Designation is required")
    private String designation;

    @NotBlank(message = "Department is required")
    private String department;

    private LocalDate joiningDate;
    private StaffStatus status;

    public StaffRequest() {
    }

    public StaffRequest(String staffCode, Long userId, String firstName, String lastName, String email,
                        String phone, String designation, String department, LocalDate joiningDate, StaffStatus status) {
        this.staffCode = staffCode;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.designation = designation;
        this.department = department;
        this.joiningDate = joiningDate;
        this.status = status;
    }

    public String getStaffCode() { return staffCode; }
    public void setStaffCode(String staffCode) { this.staffCode = staffCode; }

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

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public LocalDate getJoiningDate() { return joiningDate; }
    public void setJoiningDate(LocalDate joiningDate) { this.joiningDate = joiningDate; }

    public StaffStatus getStatus() { return status; }
    public void setStatus(StaffStatus status) { this.status = status; }
}
