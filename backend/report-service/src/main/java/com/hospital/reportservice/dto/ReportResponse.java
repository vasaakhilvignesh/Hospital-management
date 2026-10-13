package com.hospital.reportservice.dto;

import com.hospital.reportservice.entity.Report;
import com.hospital.reportservice.entity.ReportStatus;
import com.hospital.reportservice.entity.ReportType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReportResponse {

    private Long id;
    private String reportCode;
    private Long patientId;
    private ReportType reportType;
    private String title;
    private String description;
    private LocalDate reportDate;
    private ReportStatus status;
    private String data;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReportResponse() {}

    public static ReportResponse fromEntity(Report r) {
        ReportResponse res = new ReportResponse();
        res.setId(r.getId());
        res.setReportCode(r.getReportCode());
        res.setPatientId(r.getPatientId());
        res.setReportType(r.getReportType());
        res.setTitle(r.getTitle());
        res.setDescription(r.getDescription());
        res.setReportDate(r.getReportDate());
        res.setStatus(r.getStatus());
        res.setData(r.getData());
        res.setCreatedAt(r.getCreatedAt());
        res.setUpdatedAt(r.getUpdatedAt());
        return res;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReportCode() { return reportCode; }
    public void setReportCode(String reportCode) { this.reportCode = reportCode; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public ReportType getReportType() { return reportType; }
    public void setReportType(ReportType reportType) { this.reportType = reportType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }

    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
