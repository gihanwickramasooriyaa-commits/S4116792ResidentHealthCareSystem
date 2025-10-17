package model;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Represents a work shift assigned to a staff member.
 * Includes the day of week, start time, and end time.
 */
public class Shift implements Serializable {

    private DayOfWeek day;      // e.g., MONDAY, TUESDAY
    private LocalTime startTime;
    private LocalTime endTime;

    public Shift(DayOfWeek day, LocalTime startTime, LocalTime endTime) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * Calculates total hours in this shift.
     */
    public long getHours() {
        return java.time.Duration.between(startTime, endTime).toHours();
    }

    @Override
    public String toString() {
        return day + " " + startTime + " - " + endTime;
    }
}
