package com.hospital.doctorservice.dto;

import com.hospital.doctorservice.entity.Staff;
import com.hospital.doctorservice.entity.StaffStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class StaffResponse {

    private Long id;
    private String staffCode;
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String designation;
    private String department;
    private LocalDate joiningDate;
    private StaffStatus status;
    private LocalDateTime createdAt;

    public StaffResponse() {
    }

    public static StaffResponse fromEntity(Staff staff) {
        StaffResponse resp = new StaffResponse();
        resp.setId(staff.getId());
        resp.setStaffCode(staff.getStaffCode());
        resp.setUserId(staff.getUserId());
        resp.setFirstName(staff.getFirstName());
        resp.setLastName(staff.getLastName());
        resp.setEmail(staff.getEmail());
        resp.setPhone(staff.getPhone());
        resp.setDesignation(staff.getDesignation());
        resp.setDepartment(staff.getDepartment());
        resp.setJoiningDate(staff.getJoiningDate());
        resp.setStatus(staff.getStatus());
        resp.setCreatedAt(staff.getCreatedAt());
        return resp;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
