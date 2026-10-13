package com.hospital.appointmentservice.repository;

import com.hospital.appointmentservice.entity.Appointment;
import com.hospital.appointmentservice.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByAppointmentCode(String appointmentCode);

    List<Appointment> findByPatientIdOrderByAppointmentDateDescStartTimeDesc(Long patientId);

    List<Appointment> findByDoctorIdOrderByAppointmentDateDescStartTimeDesc(Long doctorId);

    List<Appointment> findByDoctorIdAndAppointmentDateAndStatusNot(Long doctorId, LocalDate appointmentDate, AppointmentStatus status);

    // Overlapping doctor appointment conflict: startA < endB && endA > startB (excluding CANCELLED)
    @Query("SELECT a FROM Appointment a WHERE a.doctorId = :doctorId AND a.appointmentDate = :date " +
           "AND a.status <> :cancelledStatus " +
           "AND (:id IS NULL OR a.id <> :id) " +
           "AND (:startTime < a.endTime AND :endTime > a.startTime)")
    List<Appointment> findDoctorConflicts(@Param("doctorId") Long doctorId,
                                          @Param("date") LocalDate date,
                                          @Param("startTime") LocalTime startTime,
                                          @Param("endTime") LocalTime endTime,
                                          @Param("id") Long id,
                                          @Param("cancelledStatus") AppointmentStatus cancelledStatus);

    // Overlapping patient appointment conflict: startA < endB && endA > startB (excluding CANCELLED)
    @Query("SELECT a FROM Appointment a WHERE a.patientId = :patientId AND a.appointmentDate = :date " +
           "AND a.status <> :cancelledStatus " +
           "AND (:id IS NULL OR a.id <> :id) " +
           "AND (:startTime < a.endTime AND :endTime > a.startTime)")
    List<Appointment> findPatientConflicts(@Param("patientId") Long patientId,
                                           @Param("date") LocalDate date,
                                           @Param("startTime") LocalTime startTime,
                                           @Param("endTime") LocalTime endTime,
                                           @Param("id") Long id,
                                           @Param("cancelledStatus") AppointmentStatus cancelledStatus);
}
