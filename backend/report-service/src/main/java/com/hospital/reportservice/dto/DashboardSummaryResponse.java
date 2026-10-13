package com.hospital.reportservice.dto;

import java.math.BigDecimal;

public class DashboardSummaryResponse {

    private long totalPatients;
    private long totalDoctors;
    private long totalAppointments;
    private long pendingAppointments;
    private long completedAppointments;
    private BigDecimal outstandingBilling;
    private long pendingPrescriptions;
    private long lowStockMedicineCount;

    public DashboardSummaryResponse() {
        this.outstandingBilling = BigDecimal.ZERO;
    }

    public DashboardSummaryResponse(long totalPatients, long totalDoctors, long totalAppointments,
                                    long pendingAppointments, long completedAppointments,
                                    BigDecimal outstandingBilling, long pendingPrescriptions,
                                    long lowStockMedicineCount) {
        this.totalPatients = totalPatients;
        this.totalDoctors = totalDoctors;
        this.totalAppointments = totalAppointments;
        this.pendingAppointments = pendingAppointments;
        this.completedAppointments = completedAppointments;
        this.outstandingBilling = outstandingBilling != null ? outstandingBilling : BigDecimal.ZERO;
        this.pendingPrescriptions = pendingPrescriptions;
        this.lowStockMedicineCount = lowStockMedicineCount;
    }

    public long getTotalPatients() { return totalPatients; }
    public void setTotalPatients(long totalPatients) { this.totalPatients = totalPatients; }

    public long getTotalDoctors() { return totalDoctors; }
    public void setTotalDoctors(long totalDoctors) { this.totalDoctors = totalDoctors; }

    public long getTotalAppointments() { return totalAppointments; }
    public void setTotalAppointments(long totalAppointments) { this.totalAppointments = totalAppointments; }

    public long getPendingAppointments() { return pendingAppointments; }
    public void setPendingAppointments(long pendingAppointments) { this.pendingAppointments = pendingAppointments; }

    public long getCompletedAppointments() { return completedAppointments; }
    public void setCompletedAppointments(long completedAppointments) { this.completedAppointments = completedAppointments; }

    public BigDecimal getOutstandingBilling() { return outstandingBilling; }
    public void setOutstandingBilling(BigDecimal outstandingBilling) { this.outstandingBilling = outstandingBilling; }

    public long getPendingPrescriptions() { return pendingPrescriptions; }
    public void setPendingPrescriptions(long pendingPrescriptions) { this.pendingPrescriptions = pendingPrescriptions; }

    public long getLowStockMedicineCount() { return lowStockMedicineCount; }
    public void setLowStockMedicineCount(long lowStockMedicineCount) { this.lowStockMedicineCount = lowStockMedicineCount; }
}
