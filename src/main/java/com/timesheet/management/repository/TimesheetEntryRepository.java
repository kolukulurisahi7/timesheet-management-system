package com.timesheet.management.repository;

import com.timesheet.management.entity.Timesheet;
import com.timesheet.management.entity.TimesheetEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimesheetEntryRepository extends JpaRepository<TimesheetEntry, Long> {
    List<TimesheetEntry> findByTimesheet(Timesheet timesheet);
}
