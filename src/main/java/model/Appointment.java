package model;

import java.time.LocalDateTime;

public class Appointment {
    private final int appointmentID;
    private final String appointmentTitle;
    private final String appointmentLocation;
    private final String appointmentDescription;
    private final String appointmentType;
    private LocalDateTime start;
    private LocalDateTime end;
    private final int customerID;
    private final int userID;
    private final int contactID;

    /**
     * Params that match to the database Fields
     * @param appointmentID Appointment_ID
     * @param appointmentTitle Title
     * @param appointmentLocation Location
     * @param appointmentDescription Description
     * @param appointmentType Type
     * @param start Start
     * @param end End
     * @param customerID Customer_ID
     * @param userID User_ID
     * @param contactID Contact_ID
     */
    public Appointment(int appointmentID, String appointmentTitle, String appointmentLocation, String appointmentDescription, String appointmentType, LocalDateTime start, LocalDateTime end, int customerID, int userID, int contactID) {
        this.appointmentID = appointmentID;
        this.appointmentTitle = appointmentTitle;
        this.appointmentLocation = appointmentLocation;
        this.appointmentDescription = appointmentDescription;
        this.appointmentType = appointmentType;
        this.start = start;
        this.end = end;
        this.customerID = customerID;
        this.userID = userID;
        this.contactID = contactID;
    }
    /**
     * @return appointmentID
     */
    public int getAppointmentID() {
        return appointmentID;
    }

    /**
     * @return appointmentTitle
     */
    public String getAppointmentTitle() {
        return appointmentTitle;
    }

    /**
     * @return appointmentLocation
     */
    public String getAppointmentLocation() {
        return appointmentLocation;
    }

    /**
     * @return appointmentDescription
     */
    public String getAppointmentDescription(){
        return appointmentDescription;
    }

    /**
     * @return appointmentType
     */
    public String getAppointmentType() {
        return appointmentType;
    }

    /**
     * @return appointmentStart
     */
    public LocalDateTime getStart() {
        return start;
    }

    /**
     * @return appointmentEnd
     */
    public LocalDateTime getEnd() {
        return end;
    }

    /**
     * @return customerID
     */
    public int getCustomerID() {
        return customerID;
    }

    /**
     * @return userID
     */
    public int getUserID() {
        return userID;
    }

    /**
     * @return contactID
     */
    public int getContactID() {
        return contactID;
    }

}
