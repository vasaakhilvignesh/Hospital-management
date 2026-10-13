package com.hospital.doctorservice.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "staff")
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String staffCode;

    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false, length = 50)
    private String designation;

    @Column(nullable = false, length = 50)
    private String department;

    private LocalDate joiningDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StaffStatus status = StaffStatus.ACTIVE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Staff() {
    }

    public Staff(String staffCode, Long userId, String firstName, String lastName, String email,
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
        this.status = status != null ? status : StaffStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
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
