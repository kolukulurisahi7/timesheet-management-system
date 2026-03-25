package com.timesheet.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimesheetEntryResponse {
    private Long id;
    private LocalDate workDate;
    private BigDecimal hoursWorked;
    private String task;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
