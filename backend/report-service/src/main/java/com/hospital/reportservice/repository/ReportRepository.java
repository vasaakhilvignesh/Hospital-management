package com.hospital.reportservice.repository;

import com.hospital.reportservice.entity.Report;
import com.hospital.reportservice.entity.ReportStatus;
import com.hospital.reportservice.entity.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByReportCode(String reportCode);
    List<Report> findByPatientId(Long patientId);
    List<Report> findByReportType(ReportType reportType);
    List<Report> findByStatus(ReportStatus status);
}
