package com.hospital.appointmentservice.config;

import com.hospital.appointmentservice.entity.Appointment;
import com.hospital.appointmentservice.entity.AppointmentStatus;
import com.hospital.appointmentservice.repository.AppointmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

@Configuration
public class AppointmentDataSeeder {

    @Bean
    public CommandLineRunner seedDemoAppointments(AppointmentRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                // Next Monday for demo doctor 1 (Dr. John Smith, who is available on Monday 09:00 - 13:00)
                LocalDate nextMonday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));

                Appointment apt1 = new Appointment(
                        "APT-1001",
                        1L, // Peter Patient
                        1L, // Dr. John Smith
                        nextMonday,
                        LocalTime.of(9, 30),
                        LocalTime.of(10, 0),
                        "Routine Cardiology Follow-up",
                        AppointmentStatus.CONFIRMED,
                        "Initial consult confirmed"
                );
                repository.save(apt1);
            }
        };
    }
}
