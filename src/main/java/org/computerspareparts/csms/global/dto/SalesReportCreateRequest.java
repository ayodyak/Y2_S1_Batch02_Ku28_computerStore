package org.computerspareparts.csms.global.dto;

import java.time.LocalDate;

public class SalesReportCreateRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private String summaryText;

    public SalesReportCreateRequest() {}

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getSummaryText() { return summaryText; }
    public void setSummaryText(String summaryText) { this.summaryText = summaryText; }
}

