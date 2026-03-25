package com.timesheet.management.repository;

import com.timesheet.management.entity.Timesheet;
import com.timesheet.management.entity.TimesheetStatus;
import com.timesheet.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimesheetRepository extends JpaRepository<Timesheet, Long> {
    List<Timesheet> findByEmployee(User employee);
    List<Timesheet> findByStatus(TimesheetStatus status);
    Optional<Timesheet> findByIdAndEmployee(Long id, User employee);
    List<Timesheet> findByEmployeeAndWeekStartDate(User employee, LocalDate weekStartDate);
    List<Timesheet> findByEmployeeAndStatusOrderByCreatedAtDesc(User employee, TimesheetStatus status);
}
