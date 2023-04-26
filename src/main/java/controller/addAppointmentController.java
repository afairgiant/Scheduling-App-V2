package controller;

import helper.JDBC;
import helper.appointmentHelper;
import helper.common;
import helper.contactHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Appointment;
import model.Contacts;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Objects;
import java.util.ResourceBundle;

public class addAppointmentController implements Initializable {

    @FXML
    private TextField appointmentDescriptionTxt;

    @FXML
    private DatePicker appointmentEndDatePick;

    @FXML
    private TextField appointmentIdTxt;

    @FXML
    private TextField appointmentLocationTxt;

    @FXML
    private DatePicker appointmentStartDatePick;

    @FXML
    private TextField appointmentTitleTxt;

    @FXML
    private TextField appointmentTypeTxt;

    @FXML
    private ComboBox<String> appointmentContactComBx;

    @FXML
    private ComboBox<String> appointmentEndTimeComBx;

    @FXML
    private ComboBox<String> appointmentStartTimeComBx;

    @FXML
    private Button cancelBtn;

    @FXML
    private TextField customerIdTxt;

    @FXML
    private Button saveBtn;

    @FXML
    private TextField userIdTxt;

    Connection connection = JDBC.getConnection();
    ZoneId localZone = ZoneId.systemDefault();
    ZoneId estZoneId = ZoneId.of("America/New_York");

    /**
     * Goes back to appointmentView.fxml
     * @param event when the cancel button is selected
     * @throws IOException If an error occurs while loading the appointment view FXML scene
     */
    @FXML
    void actionCancel(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/appointmentView.fxml")));
        Scene scene = new Scene(root);
        Stage mainReturn = (Stage) ((Node)event.getSource()).getScene().getWindow();
        mainReturn.setScene(scene);
        mainReturn.show();
    }

    /**
     * Saves a new appointment to the database with the provided details and loads the appointment view FXML scene.
     * @param event when the save button is clicked
     * @throws SQLException If an error occurs while accessing the database
     * @throws IOException  If an error occurs while loading the appointment view FXML scene
     */
    @FXML
    void actionSaveAppt(ActionEvent event) throws SQLException, IOException {
        // Check if all required fields are filled
        //validateAppointmentFields();
        if (appointmentTitleTxt.getText().isEmpty() || appointmentDescriptionTxt.getText().isEmpty() || appointmentLocationTxt.getText().isEmpty() || appointmentTypeTxt.getText().isEmpty() || appointmentStartDatePick.getValue() == null || appointmentEndDatePick.getValue() == null || appointmentStartTimeComBx.getValue() == null || appointmentEndTimeComBx.getValue() == null || customerIdTxt.getText().isEmpty() || userIdTxt.getText().isEmpty()) {
            common.showError("All fields must be filled in", "Please fill in all fields.");
            return;
        }

        //Get local dates from pickers and combo boxes and combine
        LocalDate localDateStart = appointmentStartDatePick.getValue();
        LocalDate localDateEnd = appointmentEndDatePick.getValue();

        //Pull Strings from Time Combo Boxes for Start/End
        LocalTime localTimeStart = LocalTime.parse(appointmentStartTimeComBx.getValue(), DateTimeFormatter.ofPattern("h:mm a"));
        LocalTime localTimeEnd = LocalTime.parse(appointmentEndTimeComBx.getValue(), DateTimeFormatter.ofPattern("h:mm a"));

        //Merge Date and Time into LocalDateTime format
        LocalDateTime dateTimeStart = LocalDateTime.of(localDateStart, localTimeStart);
        LocalDateTime dateTimeEnd = LocalDateTime.of(localDateEnd, localTimeEnd);

        ZonedDateTime zoneDtStart = ZonedDateTime.of(dateTimeStart, ZoneId.systemDefault());
        ZonedDateTime zoneDtEnd = ZonedDateTime.of(dateTimeEnd, ZoneId.systemDefault());

        ZonedDateTime convertStartEST = zoneDtStart.withZoneSameInstant(ZoneId.of("America/New_York"));
        ZonedDateTime convertEndEST = zoneDtEnd.withZoneSameInstant(ZoneId.of("America/New_York"));

        LocalTime startAppointmentTimeToCheck = convertStartEST.toLocalTime();
        LocalTime endAppointmentTimeToCheck = convertEndEST.toLocalTime();

        int workWeekStart = DayOfWeek.MONDAY.getValue();
        int workWeekEnd = DayOfWeek.FRIDAY.getValue();

        LocalTime estBusinessHoursStart = LocalTime.of(8, 0, 0);
        LocalTime estBusinessHoursEnd = LocalTime.of(22, 0, 0);

        //Convert 8am and 10PM to user timezone for error messages. //TODO - turn into a method passing user ZoneID
        ZonedDateTime userTime8am = ZonedDateTime.of(
                LocalDate.now(),
                LocalTime.of(8, 0),
                ZoneId.of("America/New_York")
        ).withZoneSameInstant(ZoneId.systemDefault());

        ZonedDateTime userTime10pm = ZonedDateTime.of(
                LocalDate.now(),
                LocalTime.of(22, 0),
                ZoneId.of("America/New_York")
        ).withZoneSameInstant(ZoneId.systemDefault());

        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

        String formattedUserTime8am = userTime8am.format(formatter);
        String formattedUserTime10pm = userTime10pm.format(formatter);

        System.out.println("The time in user timezone is " + formattedUserTime8am + " (8am in EST) and " + formattedUserTime10pm + " (10pm in EST)");


        int customerID = Integer.parseInt(customerIdTxt.getText());
        try {
            customerID = Integer.parseInt(String.valueOf(customerIdTxt.getText()));
            System.out.println("The customer ID is: " + customerID);
        } catch (NumberFormatException e) {
            System.out.println("Input string is not a valid integer.");
        }
        int randomAppointmentID = (int) (Math.random() * 10);

        //If any inputs are blank
        if (appointmentTitleTxt.getText().isEmpty() || appointmentDescriptionTxt.getText().isEmpty() || appointmentLocationTxt.getText().isEmpty() || appointmentTypeTxt.getText().isEmpty() || appointmentStartDatePick.getValue() == null || appointmentEndDatePick.getValue() == null || appointmentStartTimeComBx.getValue().isEmpty() || appointmentEndTimeComBx.getValue().isEmpty() || customerIdTxt.getText().isEmpty() || userIdTxt.getText().isEmpty()) {
            String message = "Please fill in all fields.";
            System.out.println("Error called: " + message);
            common.showError("All fields must be filled in", message);
            return;
        }
        //If outside of work days(M-F)
        if (convertStartEST.getDayOfWeek().getValue() < workWeekStart || convertStartEST.getDayOfWeek().getValue() > workWeekEnd || convertEndEST.getDayOfWeek().getValue() < workWeekStart || convertEndEST.getDayOfWeek().getValue() > workWeekEnd) {
            String message = "Appointment day is outside of work week.";
            System.out.println("Error called: " + message);
            common.showError("Day is outside of business operations (Monday-Friday)", message);
            return;
        }
        //If outside of business hours EST 8am-10PM
        if (startAppointmentTimeToCheck.isBefore(estBusinessHoursStart) || startAppointmentTimeToCheck.isAfter(estBusinessHoursEnd) || endAppointmentTimeToCheck.isBefore(estBusinessHoursStart) || endAppointmentTimeToCheck.isAfter(estBusinessHoursEnd)) {
            String message = ("Time is outside of business hours (8am-10pm EST)\n" +
                    " " + formattedUserTime8am + " to " + formattedUserTime10pm + " local time.");
            System.out.println("Error called: " + message);
            common.showError("Time is outside of business hours (8am-10pm EST)", message);
            return;
        }

        //If Start is After End
        if (dateTimeStart.isAfter(dateTimeEnd)) {
            String message = "Appointment has start time after end time";
            System.out.println("Error called: " + message);
            common.showError("Appointment has start time after end time", message);
            return;
        }
        //If Same Start and End Time
        if (dateTimeStart.isEqual(dateTimeEnd)) {
            String message = "Appointment has same start and end time";
            System.out.println("Error called: " + message);
            common.showError("Appointment has same start and end time", message);
            return;
        }

        ObservableList<Appointment> getAllAppointments = appointmentHelper.getAllAppointments(connection);
        // Loop through all existing appointments to check for overlaps
        for (Appointment existingAppointments : getAllAppointments) {
            LocalDateTime checkStart = existingAppointments.getStart();
            LocalDateTime checkEnd = existingAppointments.getEnd();

            // Check if new appointment overlaps with an existing appointment
            if (customerID == existingAppointments.getCustomerID() &&
                    (dateTimeStart.isBefore(checkEnd)) && (dateTimeEnd.isAfter(checkStart))) {
                common.showError("Appointment overlap", "Appointment overlaps with an existing appointment");
                return;
            }

            // Check if new appointment start time overlaps with an existing appointment
            if (customerID == existingAppointments.getCustomerID() &&
                    (dateTimeStart.isBefore(checkEnd)) && (checkStart.isBefore(dateTimeEnd))) {
                common.showError("Start Time Overlap", "Appointment start time overlaps with an existing appointment");
            }

            // Check if new appointment end time overlaps with an existing appointment
            if (customerID == existingAppointments.getCustomerID()  &&
                    (dateTimeEnd.isAfter(checkStart)) && (dateTimeEnd.isBefore(checkEnd))) {
                common.showError("End Time Overlap", "Appointment end time overlaps with an existing appointment");
            }
        }

        // Convert the start and end dates and times to UTC
        String startUTC = common.getUTCDateTime(appointmentStartDatePick.getValue(), String.valueOf(appointmentStartTimeComBx.getValue()));
        String endUTC = common.getUTCDateTime(appointmentEndDatePick.getValue(), String.valueOf(appointmentEndTimeComBx.getValue()));


        String title = appointmentTitleTxt.getText();
        String location = appointmentLocationTxt.getText();
        String description = appointmentDescriptionTxt.getText();
        String type = appointmentTypeTxt.getText();
        int userID = Integer.parseInt(userIdTxt.getText());
        int contactID = Integer.parseInt(contactHelper.findContact(appointmentContactComBx.getValue()));
        System.out.println(customerID);
        //Insert new appointment into database
        int addAppt = appointmentHelper.addNewAppointment(title, location, description, type, dateTimeStart, dateTimeEnd, customerID, userID, contactID);
        
        if(addAppt == -1){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/appointmentView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } else {
            String message = "There has been an issue with adding new appointment";
            System.out.println(message);
            common.showError("Error", message);
        }



    }

    /**
     * Save an appointment in UTC format to the database
     * @param startUTC a string representation of the start date/time in UTC format
     * @param endUTC   a string representation of the end date/time in UTC format
     */
    // Save the appointment to the database
    private void saveAppointment(String startUTC, String endUTC) {
        try (Connection connection = JDBC.getConnection()) {
            // Get the required appointment fields
            int customerId = Integer.parseInt(customerIdTxt.getText());
            int userId = Integer.parseInt(userIdTxt.getText());
            String title = appointmentTitleTxt.getText();
            String description = appointmentDescriptionTxt.getText();
            String location = appointmentLocationTxt.getText();
            String type = appointmentTypeTxt.getText();
            int contactId = Integer.parseInt(contactHelper.findContact(appointmentContactComBx.getValue()));
            //TODO

            //LocalDateTime startDateTime = LocalDateTime.parse(startUTC, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            //LocalDateTime endDateTime = LocalDateTime.parse(endUTC, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            //ZonedDateTime startUTCZoned = ZonedDateTime.of(startDateTime, ZoneId.of("UTC"));
            //ZonedDateTime endUTCZoned = ZonedDateTime.of(endDateTime, ZoneId.of("UTC"));

            String insertStatement = "INSERT INTO appointments (Appointment_ID, Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            JDBC.setPreparedStatement(JDBC.getConnection(), insertStatement);
            PreparedStatement preparedStatement = JDBC.getPreparedStatement();

            preparedStatement.setInt(1, 0); //AppointmentID
            preparedStatement.setString(2, title); //Appointment Title
            preparedStatement.setString(3, description); //Appointment Description
            preparedStatement.setString(4, location); //Appointment Location
            preparedStatement.setString(5, type); //Appointment Type
            preparedStatement.setTimestamp(6, Timestamp.valueOf(startUTC)); //Appointment Start Date/Time in UTC
            preparedStatement.setTimestamp(7, Timestamp.valueOf(endUTC)); //Appointment End Date/Time in UTC
            preparedStatement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now())); //Appointment Created_Date
            preparedStatement.setString(9, "admin"); //Appointment Created_By. Eventually maybe change so pulls in current user.
            preparedStatement.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now())); //Appointment Last_Update
            preparedStatement.setString(11, "admin"); //Appointment Last_Updated_By. Eventually maybe change so pulls in current user.
            preparedStatement.setInt(12, customerId); //Customer_ID
            preparedStatement.setInt(13, contactId); //Contact_ID
            preparedStatement.setInt(14, userId); //User_ID

            preparedStatement.executeUpdate();
            // Load the new FXML scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/appointmentView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();


        } catch (SQLIntegrityConstraintViolationException e) {
            if (e.getMessage().contains("fk_customer_id")) {
                // handle foreign key constraint violation for customer ID
                String message = "The selected customer does not exist in the database.";
                System.out.println("[ERROR] " + message);
                common.showError("Cannot add or update appointment", message);
            } else if (e.getMessage().contains("fk_user_id")) {
                // handle foreign key constraint violation for user ID
                String message = "The selected user does not exist in the database.";
                System.out.println("[ERROR] " + message);
                common.showError("Cannot add or update appointment", message);
            } else if (e.getMessage().contains("fk_contact_id")) {
                // handle foreign key constraint violation for contact ID
                String message = "The selected contact does not exist in the database.";
                System.out.println("[ERROR] " + message);
                common.showError("Cannot add or update appointment", message);
            } else {
                // handle other integrity constraint violation errors
                String message = "An error occurred while adding or updating the appointment.";
                System.out.println("[ERROR] " + message);
                common.showError("Cannot add or update appointment", message);
            }
        } catch (SQLException e) {
            // handle other SQL exceptions
            String message = "An error occurred while adding or updating the appointment.";
            System.out.println("[ERROR] " + message);
            common.showError("Cannot add or update appointment", message);
        } catch (IOException e){
            System.out.println("FXML Loader Error: " + e);
        }
    }

    /**
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Connection connection = JDBC.startConnection();
            ObservableList<Contacts> allContactsList = contactHelper.getAllContacts(connection);
            ObservableList<String> allContactNames = FXCollections.observableArrayList();

            //Populates allContactNames using foreach loop to iterate over allContactsList
            for (Contacts contacts : allContactsList){
                allContactNames.add(contacts.getContactName());
            }

            ObservableList<String> times = FXCollections.observableArrayList();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");

            LocalTime time = LocalTime.of(0, 0);
            times.add(time.format(formatter));

            for (int i = 1; i < 96; i++) {
                time = time.plusMinutes(15);
                times.add(time.format(formatter));
            }
            //Populate All the Things!
            appointmentStartTimeComBx.setItems(times);
            appointmentEndTimeComBx.setItems(times);
            appointmentContactComBx.setItems(allContactNames);

        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
