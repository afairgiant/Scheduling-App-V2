package model;

import java.time.Month;
/**
 * AppointmentMonthCount represents the count of appointments
 * in a particular month. Used for reports.
 */
public class AppointmentMonthCount {
    private final Month month;
    private final int count;
    /**
     * Constructs an AppointmentMonthCount object.
     * @param month the month for which count is being tracked
     * @param count the count of appointments for that month
     */
    public AppointmentMonthCount(Month month, int count) {
        this.month = month;
        this.count = count;
    }
    /**
     * Gets the month for which the count is being tracked.
     * @return the month
     */
    public Month getAppointmentMonth(){
        return month;
    }
    /**
     * Gets the count of appointments for the month.
     * @return the appointment count
     */
    public int getCount(){
        return count;
    }
}
