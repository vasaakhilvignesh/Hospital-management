package com.hospital.doctorservice.repository;

import com.hospital.doctorservice.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByDoctorCode(String doctorCode);
    Optional<Doctor> findByEmail(String email);
    Optional<Doctor> findByUserId(Long userId);
    boolean existsByDoctorCode(String doctorCode);
    boolean existsByEmail(String email);

    @Query("SELECT d FROM Doctor d WHERE " +
           "LOWER(d.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.specialization) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.department) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Doctor> searchDoctors(@Param("query") String query);
}
