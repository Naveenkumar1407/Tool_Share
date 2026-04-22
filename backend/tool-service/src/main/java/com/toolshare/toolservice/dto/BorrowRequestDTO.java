package com.toolshare.toolservice.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class BorrowRequestDTO {

    @NotNull(message = "Tool ID is required")
    private Long toolId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private String message;

    public Long getToolId() { return toolId; }
    public void setToolId(Long toolId) { this.toolId = toolId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
