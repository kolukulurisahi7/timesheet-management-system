package com.timesheet.management.service;

import com.timesheet.management.dto.CreateTimesheetRequest;
import com.timesheet.management.dto.RejectionRequest;
import com.timesheet.management.dto.TimesheetEntryRequest;
import com.timesheet.management.dto.TimesheetResponse;
import com.timesheet.management.entity.Timesheet;
import com.timesheet.management.entity.TimesheetEntry;
import com.timesheet.management.entity.TimesheetStatus;
import com.timesheet.management.entity.User;
import com.timesheet.management.exception.ResourceNotFoundException;
import com.timesheet.management.exception.UnauthorizedException;
import com.timesheet.management.repository.TimesheetEntryRepository;
import com.timesheet.management.repository.TimesheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TimesheetService {

    @Autowired
    private TimesheetRepository timesheetRepository;

    @Autowired
    private TimesheetEntryRepository timesheetEntryRepository;

    @Autowired
    private AuthenticationService authenticationService;

    public TimesheetResponse createTimesheet(CreateTimesheetRequest request) {
        User currentUser = authenticationService.getCurrentUser();

        // Check if timesheet already exists for this week
        List<Timesheet> existingTimesheets = timesheetRepository
                .findByEmployeeAndWeekStartDate(currentUser, request.getWeekStartDate());
        if (!existingTimesheets.isEmpty()) {
            throw new RuntimeException("Timesheet already exists for this week");
        }

        Timesheet timesheet = Timesheet.builder()
                .employee(currentUser)
                .weekStartDate(request.getWeekStartDate())
                .weekEndDate(request.getWeekEndDate())
                .status(TimesheetStatus.DRAFT)
                .build();

        Timesheet savedTimesheet = timesheetRepository.save(timesheet);
        return mapToResponse(savedTimesheet);
    }

    public TimesheetResponse addEntry(Long timesheetId, TimesheetEntryRequest request) {
        User currentUser = authenticationService.getCurrentUser();

        Timesheet timesheet = timesheetRepository.findByIdAndEmployee(timesheetId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Timesheet not found"));

        if (timesheet.getStatus() != TimesheetStatus.DRAFT) {
            throw new RuntimeException("Cannot add entries to a submitted or approved timesheet");
        }

        TimesheetEntry entry = TimesheetEntry.builder()
                .timesheet(timesheet)
                .workDate(request.getWorkDate())
                .hoursWorked(request.getHoursWorked())
                .task(request.getTask())
                .build();

        timesheetEntryRepository.save(entry);

        Timesheet updatedTimesheet = timesheetRepository.findById(timesheetId)
                .orElseThrow(() -> new ResourceNotFoundException("Timesheet not found"));

        return mapToResponse(updatedTimesheet);
    }

    public List<TimesheetResponse> getMyTimesheets() {
        User currentUser = authenticationService.getCurrentUser();
        List<Timesheet> timesheets = timesheetRepository.findByEmployee(currentUser);
        return timesheets.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public TimesheetResponse getTimesheetById(Long id) {
        User currentUser = authenticationService.getCurrentUser();

        Timesheet timesheet = timesheetRepository.findByIdAndEmployee(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Timesheet not found"));

        return mapToResponse(timesheet);
    }

    public TimesheetResponse submitTimesheet(Long id) {
        User currentUser = authenticationService.getCurrentUser();

        Timesheet timesheet = timesheetRepository.findByIdAndEmployee(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Timesheet not found"));

        if (timesheet.getStatus() != TimesheetStatus.DRAFT) {
            throw new RuntimeException("Only draft timesheets can be submitted");
        }

        timesheet.setStatus(TimesheetStatus.SUBMITTED);
        timesheet.setSubmittedAt(LocalDateTime.now());

        Timesheet updatedTimesheet = timesheetRepository.save(timesheet);
        return mapToResponse(updatedTimesheet);
    }

    public TimesheetResponse approveTimesheet(Long id) {
        User currentUser = authenticationService.getCurrentUser();

        // Check if user is ADMIN
        if (currentUser.getRole().name().equals("USER")) {
            throw new UnauthorizedException("Only admins can approve timesheets");
        }

        Timesheet timesheet = timesheetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Timesheet not found"));

        if (timesheet.getStatus() != TimesheetStatus.SUBMITTED) {
            throw new RuntimeException("Only submitted timesheets can be approved");
        }

        timesheet.setStatus(TimesheetStatus.APPROVED);
        timesheet.setApprovedAt(LocalDateTime.now());
        timesheet.setApprovedBy(currentUser);

        Timesheet updatedTimesheet = timesheetRepository.save(timesheet);
        return mapToResponse(updatedTimesheet);
    }

    public TimesheetResponse rejectTimesheet(Long id, RejectionRequest request) {
        User currentUser = authenticationService.getCurrentUser();

        // Check if user is ADMIN
        if (currentUser.getRole().name().equals("USER")) {
            throw new UnauthorizedException("Only admins can reject timesheets");
        }

        Timesheet timesheet = timesheetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Timesheet not found"));

        if (timesheet.getStatus() != TimesheetStatus.SUBMITTED) {
            throw new RuntimeException("Only submitted timesheets can be rejected");
        }

        timesheet.setStatus(TimesheetStatus.REJECTED);
        timesheet.setRejectionReason(request.getReason());
        timesheet.setRejectedAt(LocalDateTime.now());

        Timesheet updatedTimesheet = timesheetRepository.save(timesheet);
        return mapToResponse(updatedTimesheet);
    }

    public List<TimesheetResponse> getPendingTimesheets() {
        User currentUser = authenticationService.getCurrentUser();

        // Check if user is ADMIN
        if (currentUser.getRole().name().equals("USER")) {
            throw new UnauthorizedException("Only admins can view pending timesheets");
        }

        List<Timesheet> timesheets = timesheetRepository.findByStatus(TimesheetStatus.SUBMITTED);
        return timesheets.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private TimesheetResponse mapToResponse(Timesheet timesheet) {
        List<TimesheetEntry> entries = timesheetEntryRepository.findByTimesheet(timesheet);

        return TimesheetResponse.builder()
                .id(timesheet.getId())
                .weekStartDate(timesheet.getWeekStartDate())
                .weekEndDate(timesheet.getWeekEndDate())
                .status(timesheet.getStatus().name())
                .rejectionReason(timesheet.getRejectionReason())
                .createdAt(timesheet.getCreatedAt())
                .submittedAt(timesheet.getSubmittedAt())
                .approvedAt(timesheet.getApprovedAt())
                .rejectedAt(timesheet.getRejectedAt())
                .approvedBy(timesheet.getApprovedBy() != null ? timesheet.getApprovedBy().getFirstName() + " " + timesheet.getApprovedBy().getLastName() : null)
                .entries(entries.stream()
                        .map(e -> new com.timesheet.management.dto.TimesheetEntryResponse(
                                e.getId(),
                                e.getWorkDate(),
                                e.getHoursWorked(),
                                e.getTask(),
                                e.getCreatedAt(),
                                e.getUpdatedAt()
                        ))
                        .collect(Collectors.toList()))
                .build();
    }
}
