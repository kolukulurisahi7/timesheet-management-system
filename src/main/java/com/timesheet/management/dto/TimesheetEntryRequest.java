package com.timesheet.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimesheetEntryRequest {
    private LocalDate workDate;
    private BigDecimal hoursWorked;
    private String task;
}
