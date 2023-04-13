package model;

public class AppointmentTypeCount {
    private final String appointmentType;
    private final int count;
    /**
     * Creates a new AppointmentTypeCount object with the given appointment type and count.
     * @param appointmentType the type of appointment
     * @param count the number of appointments of that type
     */
    public AppointmentTypeCount(String appointmentType, int count) {
        this.appointmentType = appointmentType;
        this.count = count;
    }
    /**
     * Returns the type of appointment for this object.
     * @return the appointment type
     */
    public String getAppointmentType() {
        return appointmentType;
    }
    /**
     * Returns the number of appointments of this object's appointment type.
     * @return the appointment count
     */
    public int getCount() {
        return count;
    }
}

