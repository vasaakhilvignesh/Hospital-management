package com.hospital.doctorservice.repository;

import com.hospital.doctorservice.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    Optional<Staff> findByStaffCode(String staffCode);
    Optional<Staff> findByEmail(String email);
    Optional<Staff> findByUserId(Long userId);
    boolean existsByStaffCode(String staffCode);
    boolean existsByEmail(String email);

    @Query("SELECT s FROM Staff s WHERE " +
           "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.designation) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.department) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Staff> searchStaff(@Param("query") String query);
}
