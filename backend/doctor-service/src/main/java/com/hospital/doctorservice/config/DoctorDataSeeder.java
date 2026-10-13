package com.hospital.doctorservice.config;

import com.hospital.doctorservice.entity.*;
import com.hospital.doctorservice.repository.DoctorAvailabilityRepository;
import com.hospital.doctorservice.repository.DoctorRepository;
import com.hospital.doctorservice.repository.StaffRepository;
import com.hospital.doctorservice.repository.StaffScheduleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Configuration
public class DoctorDataSeeder {

    @Bean
    public CommandLineRunner seedDemoDoctorsAndStaff(DoctorRepository doctorRepository,
                                                     DoctorAvailabilityRepository availabilityRepository,
                                                     StaffRepository staffRepository,
                                                     StaffScheduleRepository scheduleRepository) {
        return args -> {
            if (doctorRepository.count() == 0) {
                // Doctor 1: Dr. John Smith (Cardiology) - linked to demo user 'doctor' (userId = 2)
                Doctor d1 = new Doctor(
                        "DOC-1001",
                        2L,
                        "John",
                        "Smith",
                        "doctor@hospital.com",
                        "555-0102",
                        "Cardiology",
                        "Cardiovascular Sciences",
                        "MD, FACC",
                        12,
                        new BigDecimal("150.00"),
                        DoctorStatus.ACTIVE
                );
                d1 = doctorRepository.save(d1);

                // Add Monday & Wednesday availability for Doctor 1
                availabilityRepository.save(new DoctorAvailability(
                        d1.getId(),
                        DayOfWeek.MONDAY,
                        LocalTime.of(9, 0),
                        LocalTime.of(13, 0),
                        true
                ));
                availabilityRepository.save(new DoctorAvailability(
                        d1.getId(),
                        DayOfWeek.WEDNESDAY,
                        LocalTime.of(14, 0),
                        LocalTime.of(18, 0),
                        true
                ));

                // Doctor 2: Dr. Emily Clark (General Medicine)
                Doctor d2 = new Doctor(
                        "DOC-1002",
                        null,
                        "Emily",
                        "Clark",
                        "emily.clark@hospital.com",
                        "555-0150",
                        "General Medicine",
                        "Internal Medicine",
                        "MBBS, MD",
                        8,
                        new BigDecimal("100.00"),
                        DoctorStatus.ACTIVE
                );
                d2 = doctorRepository.save(d2);

                availabilityRepository.save(new DoctorAvailability(
                        d2.getId(),
                        DayOfWeek.TUESDAY,
                        LocalTime.of(10, 0),
                        LocalTime.of(16, 0),
                        true
                ));

                // Doctor 3: Dr. Robert Patel (Neurology)
                Doctor d3 = new Doctor(
                        "DOC-1003",
                        null,
                        "Robert",
                        "Patel",
                        "robert.patel@hospital.com",
                        "555-0175",
                        "Neurology",
                        "Neurosciences",
                        "MD, DM Neurology",
                        15,
                        new BigDecimal("200.00"),
                        DoctorStatus.ACTIVE
                );
                doctorRepository.save(d3);
            }

            doctorRepository.findByDoctorCode("DOC-1002").ifPresent(d2 -> {
                if (d2.getUserId() == null) {
                    d2.setUserId(12L);
                    doctorRepository.save(d2);
                }
                if (availabilityRepository.findByDoctorId(d2.getId()).size() <= 1) {
                    availabilityRepository.save(new DoctorAvailability(
                            d2.getId(), DayOfWeek.THURSDAY, LocalTime.of(9, 0), LocalTime.of(15, 0), true
                    ));
                    availabilityRepository.save(new DoctorAvailability(
                            d2.getId(), DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(16, 0), true
                    ));
                }
            });

            doctorRepository.findByDoctorCode("DOC-1003").ifPresent(d3 -> {
                if (d3.getUserId() == null) {
                    d3.setUserId(13L);
                    doctorRepository.save(d3);
                }
                if (availabilityRepository.findByDoctorId(d3.getId()).isEmpty()) {
                    availabilityRepository.save(new DoctorAvailability(
                            d3.getId(), DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(16, 0), true
                    ));
                    availabilityRepository.save(new DoctorAvailability(
                            d3.getId(), DayOfWeek.WEDNESDAY, LocalTime.of(10, 0), LocalTime.of(16, 0), true
                    ));
                    availabilityRepository.save(new DoctorAvailability(
                            d3.getId(), DayOfWeek.FRIDAY, LocalTime.of(13, 0), LocalTime.of(18, 0), true
                    ));
                }
            });

            if (staffRepository.count() == 0) {
                // Staff 1: Sarah Staff (Senior Nurse) - linked to demo user 'staff' (userId = 3)
                Staff s1 = new Staff(
                        "STF-1001",
                        3L,
                        "Sarah",
                        "Staff",
                        "staff@hospital.com",
                        "555-0103",
                        "Head Nurse",
                        "Emergency & Inpatient",
                        LocalDate.of(2021, 3, 1),
                        StaffStatus.ACTIVE
                );
                s1 = staffRepository.save(s1);

                scheduleRepository.save(new StaffSchedule(
                        s1.getId(),
                        DayOfWeek.MONDAY,
                        LocalTime.of(8, 0),
                        LocalTime.of(16, 0),
                        Shift.MORNING
                ));

                // Staff 2: Rachel Receptionist - linked to demo user 'receptionist' (userId = 4)
                Staff s2 = new Staff(
                        "STF-1002",
                        4L,
                        "Rachel",
                        "Receptionist",
                        "receptionist@hospital.com",
                        "555-0104",
                        "Front Desk Lead",
                        "Administration",
                        LocalDate.of(2022, 6, 15),
                        StaffStatus.ACTIVE
                );
                s2 = staffRepository.save(s2);

                scheduleRepository.save(new StaffSchedule(
                        s2.getId(),
                        DayOfWeek.MONDAY,
                        LocalTime.of(7, 30),
                        LocalTime.of(15, 30),
                        Shift.MORNING
                ));
            }
        };
    }
}
