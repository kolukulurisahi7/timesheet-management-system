package com.timesheet.management.controller;

import com.timesheet.management.dto.CreateTimesheetRequest;
import com.timesheet.management.dto.RejectionRequest;
import com.timesheet.management.dto.TimesheetEntryRequest;
import com.timesheet.management.dto.TimesheetResponse;
import com.timesheet.management.service.TimesheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timesheets")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TimesheetController {

    @Autowired
    private TimesheetService timesheetService;

    @PostMapping
    public ResponseEntity<TimesheetResponse> createTimesheet(@RequestBody CreateTimesheetRequest request) {
        TimesheetResponse response = timesheetService.createTimesheet(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/entries")
    public ResponseEntity<TimesheetResponse> addEntry(@PathVariable Long id, @RequestBody TimesheetEntryRequest request) {
        TimesheetResponse response = timesheetService.addEntry(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<TimesheetResponse>> getMyTimesheets() {
        List<TimesheetResponse> timesheets = timesheetService.getMyTimesheets();
        return ResponseEntity.ok(timesheets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimesheetResponse> getTimesheetById(@PathVariable Long id) {
        TimesheetResponse response = timesheetService.getTimesheetById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/submit")
    public ResponseEntity<TimesheetResponse> submitTimesheet(@PathVariable Long id) {
        TimesheetResponse response = timesheetService.submitTimesheet(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<TimesheetResponse> approveTimesheet(@PathVariable Long id) {
        TimesheetResponse response = timesheetService.approveTimesheet(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<TimesheetResponse> rejectTimesheet(@PathVariable Long id, @RequestBody RejectionRequest request) {
        TimesheetResponse response = timesheetService.rejectTimesheet(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<TimesheetResponse>> getPendingTimesheets() {
        List<TimesheetResponse> timesheets = timesheetService.getPendingTimesheets();
        return ResponseEntity.ok(timesheets);
    }
}
