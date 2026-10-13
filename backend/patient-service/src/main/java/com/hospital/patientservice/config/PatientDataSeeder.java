package com.hospital.patientservice.config;

import com.hospital.patientservice.entity.Immunization;
import com.hospital.patientservice.entity.MedicalHistory;
import com.hospital.patientservice.entity.Patient;
import com.hospital.patientservice.repository.ImmunizationRepository;
import com.hospital.patientservice.repository.MedicalHistoryRepository;
import com.hospital.patientservice.repository.PatientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class PatientDataSeeder {

    @Bean
    public CommandLineRunner seedDemoPatients(PatientRepository patientRepository,
                                             MedicalHistoryRepository historyRepository,
                                             ImmunizationRepository immunizationRepository) {
        return args -> {
            if (patientRepository.count() == 0) {
                // Seed demo patient linked to Auth Service demo patient user (userId = 5)
                Patient p1 = new Patient(
                        "PAT-1001",
                        5L,
                        "Peter",
                        "Patient",
                        LocalDate.of(1990, 5, 15),
                        "Male",
                        "555-0105",
                        "patient@hospital.com",
                        "123 Maple Street, Cityville",
                        "O+",
                        "Mary Patient (555-0199)"
                );
                p1 = patientRepository.save(p1);

                historyRepository.save(new MedicalHistory(
                        p1.getId(),
                        "Seasonal Influenza",
                        "Rest and hydration",
                        "Oseltamivir 75mg",
                        "Patient recovered smoothly without complications",
                        LocalDate.now().minusMonths(2),
                        "Dr. John Smith"
                ));

                immunizationRepository.save(new Immunization(
                        p1.getId(),
                        "Influenza Quadrivalent",
                        LocalDate.now().minusMonths(6),
                        LocalDate.now().plusMonths(6),
                        "Annual flu vaccination"
                ));

                // Seed second demo patient
                Patient p2 = new Patient(
                        "PAT-1002",
                        null,
                        "Alice",
                        "Johnson",
                        LocalDate.of(1985, 11, 23),
                        "Female",
                        "555-0202",
                        "alice.johnson@example.com",
                        "456 Oak Avenue, Metropolis",
                        "A+",
                        "Bob Johnson (555-0299)"
                );
                p2 = patientRepository.save(p2);

                historyRepository.save(new MedicalHistory(
                        p2.getId(),
                        "Mild Hypertension",
                        "Lifestyle counseling and daily BP monitoring",
                        "Amlodipine 5mg",
                        "BP checked 135/85",
                        LocalDate.now().minusWeeks(3),
                        "Dr. John Smith"
                ));
            }
        };
    }
}
