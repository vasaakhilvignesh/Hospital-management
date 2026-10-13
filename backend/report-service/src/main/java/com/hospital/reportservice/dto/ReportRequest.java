package com.hospital.reportservice.dto;

import com.hospital.reportservice.entity.ReportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ReportRequest {

    private Long patientId;

    @NotNull(message = "Report type is required")
    private ReportType reportType;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private String data;

    public ReportRequest() {}

    public ReportRequest(Long patientId, ReportType reportType, String title, String description, String data) {
        this.patientId = patientId;
        this.reportType = reportType;
        this.title = title;
        this.description = description;
        this.data = data;
    }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public ReportType getReportType() { return reportType; }
    public void setReportType(ReportType reportType) { this.reportType = reportType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
}
