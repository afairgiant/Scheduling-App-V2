package controller;

import helper.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Appointment;
import model.Contacts;
import model.Users;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;
import java.util.ResourceBundle;

public class appointmentViewController implements Initializable {

    @FXML
    private RadioButton allAppointmentsRBtn;

    @FXML
    private TableView<Appointment> appointmentViewTable;

    @FXML
    private Button appointmentAddBtn;

    @FXML
    private Button appointmentDeleteBtn;

    @FXML
    private TableColumn<?, ?> appointmentDescriptionCol;

    @FXML
    private TextField appointmentDescriptionTxt;

    @FXML
    private TableColumn<Appointment, LocalDateTime> appointmentEndCol;

    @FXML
    private DatePicker appointmentEndDatePick;

    @FXML
    private TableColumn<?, ?> appointmentIdCol;

    @FXML
    private TextField appointmentIdTxt;

    @FXML
    private TableColumn<?, ?> appointmentLocationCol;

    @FXML
    private TextField appointmentLocationTxt;

    @FXML
    private TableColumn<Appointment, LocalDateTime> appointmentStartCol;

    @FXML
    private DatePicker appointmentStartDatePick;

    @FXML
    private TableColumn<?, ?> appointmentTitleCol;

    @FXML
    private TextField appointmentTitleTxt;

    @FXML
    private TableColumn<?, ?> appointmentTypeCol;

    @FXML
    private TextField appointmentTypeTxt;

    @FXML
    private Button appointmentUpdateBtn;

    @FXML
    private ToggleGroup apptView;

    @FXML
    private ComboBox<String> appointmentContactComBx;

    @FXML
    private ComboBox<String> appointmentEndTimeComBx;

    @FXML
    private ComboBox<String> appointmentStartTimeComBx;

    @FXML
    private Button backBtn;

    @FXML
    private TableColumn<?, ?> appointmentContactIdCol;

    @FXML
    private TableColumn<?, ?> appointmentCustomerIdCol;

    @FXML
    private TextField customerIdTxt;

    @FXML
    private RadioButton monthRBtn;

    @FXML
    private TableColumn<?, ?> appointmentUserIdCol;

    @FXML
    private TextField userIdTxt;

    @FXML
    private RadioButton weekRBtn;

    Connection connection = JDBC.getConnection();
    ZoneId localZone = ZoneId.systemDefault();

    /**
     * Loads addAppointment.fxml
     * @param event Add appointment button clicked
     * @throws IOException if addAppointment.fxml cannot be loaded.
     */
    @FXML
    void actionAddAppointment(ActionEvent event) throws IOException {
        Parent appointments = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/addAppointment.fxml")));
        Scene scene = new Scene(appointments);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();

    }

    /**
     * Filters table  show appointments happening this the start and end of the current month
     * @param event when monthly radio button is selected
     */
    @FXML
    void actionAppointmentByMonth(ActionEvent event) {
        Connection connection = JDBC.getConnection();
        ObservableList<Appointment> allAppointmentsList = appointmentHelper.getAllAppointments(connection);
        ObservableList<Appointment> monthAppointmentsList = FXCollections.observableArrayList();

        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());

        allAppointmentsList.forEach(appointment -> {
            LocalDate appointmentStart = appointment.getStart().toLocalDate();
            LocalDate appointmentEnd = appointment.getEnd().toLocalDate();
            if ((appointmentStart.isAfter(monthStart.minusDays(1)) && appointmentStart.isBefore(monthEnd.plusDays(1)))
                    || (appointmentEnd.isAfter(monthStart.minusDays(1)) && appointmentEnd.isBefore(monthEnd.plusDays(1)))) {
                monthAppointmentsList.add(appointment);
            }
        });
        appointmentViewTable.setItems(monthAppointmentsList);
        System.out.println("Number of appointments this month: " + monthAppointmentsList.size());
    }

    /**
     * Filters table to show appointments happening this week between Sunday and Saturday.
     * @param event Weekly radio button is selected
     */
    @FXML
    void actionAppointmentByWeek(ActionEvent event) {
        LocalDate today = LocalDate.now();
        LocalDate sunday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate saturday = sunday.plusDays(6);

        ObservableList<Appointment> allAppointmentsList = appointmentHelper.getAllAppointments(connection);
        ObservableList<Appointment> weekAppointmentsList = allAppointmentsList.filtered(appointment -> {
            LocalDate appointmentStart = appointment.getStart().toLocalDate();
            LocalDate appointmentEnd = appointment.getEnd().toLocalDate();
            return ((appointmentStart.isAfter(sunday.minusDays(1)) && appointmentStart.isBefore(saturday.plusDays(1)))
                    || (appointmentEnd.isAfter(sunday.minusDays(1)) && appointmentEnd.isBefore(saturday.plusDays(1))));
        });

        appointmentViewTable.setItems(weekAppointmentsList);
        System.out.println("Number of appointments this week: " + weekAppointmentsList.size());
    }

    /**
     * No filter on table, shows all appointments in database.
     * @param event All Appointments radio button selected
     */
    @FXML
    void actionAppointmentShowAll(ActionEvent event) {
        ObservableList<Appointment> allAppointmentsList = appointmentHelper.getAllAppointments(connection);

        for (model.Appointment appointment : allAppointmentsList) {
            appointmentViewTable.setItems(allAppointmentsList);
        }
    }

    /**
     * Goes back to main screen.
     * @param event When the back button is clicked.
     * @throws IOException If it fails to load mainScreen.fxml
     */
    @FXML
    void actionBack(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/mainScreen.fxml")));
        Scene scene = new Scene(root);
        Stage mainReturn = (Stage) ((Node)event.getSource()).getScene().getWindow();
        mainReturn.setScene(scene);
        mainReturn.show();
    }

    /**
     * Deletes the selected appointment with the given ID from the database and refreshes the appointment view table.
     * @param event When the delete appointment button is clicked.
     */
    @FXML
    void actionDeleteAppointment(ActionEvent event) {
        try {
            Appointment selectedAppointment = appointmentViewTable.getSelectionModel().getSelectedItem();
            if (selectedAppointment != null) {
                //Get the appointment ID from appointmentIdTxt after an appointment has been selected.
                int appointmentID = Integer.parseInt(appointmentIdTxt.getText());
                String appointmentType = appointmentTypeTxt.getText();
                // Delete the appointment with the given ID from the database
                String sql = "DELETE FROM appointments WHERE Appointment_ID = ?";
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setInt(1, appointmentID);
                    ps.executeUpdate();
                    common.showInformation("Deleted Appointment", "The following appointment was deleted: \n" + "Appointment ID: " + appointmentID + " \n" + "Appointment Type: " + appointmentType);
                    System.out.println("Appointment number: " + appointmentID + " was deleted.");

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                // Clear the fields
                appointmentIdTxt.clear();
                appointmentTitleTxt.clear();
                appointmentDescriptionTxt.clear();
                appointmentLocationTxt.clear();
                appointmentTypeTxt.clear();
                customerIdTxt.clear();
                userIdTxt.clear();
                appointmentEndDatePick.setValue(null);
                appointmentStartDatePick.setValue(null);
                appointmentStartTimeComBx.getSelectionModel().clearSelection();
                appointmentEndTimeComBx.getSelectionModel().clearSelection();
                appointmentContactComBx.getSelectionModel().clearSelection();

                // Refresh the appointment view table
                ObservableList<Appointment> allAppointmentsList = appointmentHelper.getAllAppointments(connection);
                appointmentViewTable.setItems(allAppointmentsList);
            }else {
                //No appointment selected
                String message = "Please select an appointment to delete.";
                System.out.println(message);
                common.showError("Selection Error", message);
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Updates an existing appointment in the database with the information provided in the text fields.
     * Validates the appointment fields to ensure business hours are not exceeded and appointments do not overlap.
     * If the update is successful, the appointment list view is refreshed.
     * @param event the clicking the 'Update Appointment' button
     */
    @FXML
    void actionUpdateAppointment(ActionEvent event) {
        System.out.println("\n ---- \n Update Button Clicked");
        Appointment selectedAppointment = appointmentViewTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment != null) {
            try {
                //Get local dates from pickers and combo boxes and combine
                LocalDate localDateStart = appointmentStartDatePick.getValue();
                LocalDate localDateEnd = appointmentEndDatePick.getValue();

                //Pull Strings from Time Combo Boxes for Start/End
                LocalTime localTimeStart = LocalTime.parse(appointmentStartTimeComBx.getValue(), DateTimeFormatter.ofPattern("h:mm a"));
                LocalTime localTimeEnd = LocalTime.parse(appointmentEndTimeComBx.getValue(), DateTimeFormatter.ofPattern("h:mm a"));

                //Merge Date and Time into LocalDateTime format
                LocalDateTime dateTimeStart = LocalDateTime.of(localDateStart, localTimeStart);
                LocalDateTime dateTimeEnd = LocalDateTime.of(localDateEnd, localTimeEnd);

                //Convert to Zoned Date Time
                ZonedDateTime zoneDtStart = ZonedDateTime.of(dateTimeStart, ZoneId.systemDefault());
                ZonedDateTime zoneDtEnd = ZonedDateTime.of(dateTimeEnd, ZoneId.systemDefault());

                //Convert Local Start Time to EST for checks
                ZonedDateTime convertStartEST = zoneDtStart.withZoneSameInstant(ZoneId.of("America/New_York"));
                ZonedDateTime convertEndEST = zoneDtEnd.withZoneSameInstant(ZoneId.of("America/New_York"));

                //Convert to LocalTime in EST for time checks //TODO - I can probably simplify this.
                LocalTime startAppointmentTimeToCheck = convertStartEST.toLocalTime();
                LocalTime endAppointmentTimeToCheck = convertEndEST.toLocalTime();

                //Start and End of work week
                int workWeekStart = DayOfWeek.MONDAY.getValue();
                int workWeekEnd = DayOfWeek.FRIDAY.getValue();

                //EST Business Hours
                LocalTime estBusinessHoursStart = LocalTime.of(8, 0, 0);
                LocalTime estBusinessHoursEnd = LocalTime.of(22, 0, 0);

                int customerID = Integer.parseInt(customerIdTxt.getText());
                int appointmentID = Integer.parseInt(appointmentIdTxt.getText());

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



                //If any inputs are blank
                if (appointmentTitleTxt.getText().isEmpty() || appointmentDescriptionTxt.getText().isEmpty() || appointmentLocationTxt.getText().isEmpty() || appointmentTypeTxt.getText().isEmpty() || appointmentStartDatePick.getValue() == null || appointmentEndDatePick.getValue() == null || appointmentStartTimeComBx.getValue().isEmpty() || appointmentEndTimeComBx.getValue().isEmpty() || customerIdTxt.getText().isEmpty() || userIdTxt.getText().isEmpty()) {
                    String message = "Please fill in all fields.";
                    System.out.println("Error called: " + message);
                    common.showError("All fields must be filled in", message);
                    return;
                }
                //If outside of work days(M-F)
                if (convertStartEST.getDayOfWeek().getValue() < workWeekStart || convertStartEST.getDayOfWeek().getValue() > workWeekEnd || convertEndEST.getDayOfWeek().getValue() < workWeekStart || convertEndEST.getDayOfWeek().getValue() > workWeekEnd) {
                    String message = "Appointment day is outside of business hours";
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
                    if (customerID == existingAppointments.getCustomerID() && appointmentID != existingAppointments.getAppointmentID() &&
                            (dateTimeStart.isBefore(checkEnd)) && (dateTimeEnd.isAfter(checkStart))) {
                        common.showError("Appointment overlap", "Appointment overlaps with an existing appointment");
                        return;
                    }

                    // Check if new appointment start time overlaps with an existing appointment
                    if (customerID == existingAppointments.getCustomerID() && appointmentID != existingAppointments.getAppointmentID() &&
                            (dateTimeStart.isBefore(checkEnd)) && (checkStart.isBefore(dateTimeEnd))) {
                        common.showError("Start Time Overlap", "Appointment start time overlaps with an existing appointment");
                    }

                    // Check if new appointment end time overlaps with an existing appointment
                    if (customerID == existingAppointments.getCustomerID() && appointmentID != existingAppointments.getAppointmentID() &&
                            (dateTimeEnd.isAfter(checkStart)) && (dateTimeEnd.isBefore(checkEnd))) {
                        common.showError("End Time Overlap", "Appointment end time overlaps with an existing appointment");
                    }
                }


/*                    // Check if the new appointment overlaps with the existing appointment
                    if (customerID == existingAppointments.getCustomerID() && appointmentID != existingAppointments.getAppointmentID() &&
                            updatedAppointmentID != existingAppointments.getAppointmentID() && // ignore appointments with matching appointmentID
                            ((dateTimeStart.isEqual(checkStart) || dateTimeStart.isAfter(checkStart)) && dateTimeStart.isBefore(checkEnd)) ||
                            ((dateTimeEnd.isEqual(checkEnd) || dateTimeEnd.isBefore(checkEnd)) && dateTimeEnd.isAfter(checkStart)) ||
                            (dateTimeStart.isBefore(checkStart) && dateTimeEnd.isAfter(checkEnd))) {
                        String errorMessage = "Appointment Overlap: Appointment " + existingAppointments.getAppointmentID() + " overlaps with existing appointment.";
                        common.showError("Appointment Overlap", errorMessage);
                        System.out.println("Appointment Overlap error " + existingAppointments.getAppointmentID());
                        return;
                    }
                }
                */


                System.out.println("\n All Checks Passed!");

                String title = appointmentTitleTxt.getText();
                String location = appointmentLocationTxt.getText();
                String type = appointmentTypeTxt.getText();
                String description = appointmentDescriptionTxt.getText();
                int userID = Integer.parseInt(userIdTxt.getText());
                int contactID = Integer.parseInt(contactHelper.findContact(appointmentContactComBx.getValue()));
                //ZoneId userTimeZone = ZoneId.systemDefault();
                ZoneId userTimeZone = ZoneId.of("America/Los_Angeles"); // Pacific Time Zone

                System.out.println("\n-----------------\n Update Appointment Method Called \n ----------------\n");
                appointmentHelper.updateAppointment(appointmentID, title, location, type, description, dateTimeStart, dateTimeEnd, customerID, userID, contactID, userTimeZone);

                appointmentViewTable.getItems().clear();

                ObservableList<Appointment> updatedAppointmentsList = appointmentHelper.getAllAppointments(connection);
                appointmentViewTable.getItems().addAll(updatedAppointmentsList);


                ObservableList<String> times = FXCollections.observableArrayList();
                DateTimeFormatter formatter_set = DateTimeFormatter.ofPattern("h:mm a");

                LocalTime time = LocalTime.of(0, 0);
                times.add(time.format(formatter_set));

                for (int i = 1; i < 96; i++) {
                    time = time.plusMinutes(15);
                    times.add(time.format(formatter_set));
                }

                ObservableList<Contacts> allContactList = contactHelper.getAllContacts(connection);
                ObservableList<String> allContactsNames = FXCollections.observableArrayList();
                allContactList.forEach(contacts -> allContactsNames.add(contacts.getContactName()));
                appointmentContactComBx.setItems(allContactsNames);
                //Clear input boxes
                appointmentIdTxt.clear();
                appointmentTitleTxt.clear();
                appointmentDescriptionTxt.clear();
                appointmentLocationTxt.clear();
                appointmentTypeTxt.clear();
                customerIdTxt.clear();
                appointmentStartTimeComBx.setItems(times);
                appointmentEndTimeComBx.setItems(times);
                customerIdTxt.clear();
                userIdTxt.clear();


            } catch (NumberFormatException | SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            //No appointment selected
            String message = "Please select an appointment to update.";
            System.out.println(message);
            common.showError("Selection Error", message);
        }

    }


    /**
     * This method initializes the appointment view table and sets the cell value factories for each column.
     * @param url The URL location of the FXML file.
     * @param resourceBundle
     * @throws RuntimeException If there is an issue with the SQL statement or connection to the database
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try{
            Connection connection = JDBC.startConnection();
            ObservableList<Users> allUsersList = userHelper.getAllUsers(connection);
            ObservableList<Contacts> allContactsList = contactHelper.getAllContacts(connection);
            ObservableList<Appointment> allAppointmentsList = appointmentHelper.getAllAppointments(connection);



            ObservableList<String> allContactNames = FXCollections.observableArrayList();
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

            //Populate All the things!
            appointmentStartTimeComBx.setItems(times);
            appointmentEndTimeComBx.setItems(times);
            appointmentContactComBx.setItems(allContactNames);
            appointmentViewTable.setItems(allAppointmentsList);

            // Set the table
            appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
            appointmentTitleCol.setCellValueFactory(new PropertyValueFactory<>("appointmentTitle"));
            appointmentLocationCol.setCellValueFactory(new PropertyValueFactory<>("appointmentLocation"));
            appointmentDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("appointmentDescription"));
            appointmentTypeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
            appointmentStartCol.setCellValueFactory(new PropertyValueFactory<>("start"));
            appointmentEndCol.setCellValueFactory(new PropertyValueFactory<>("end"));
            appointmentCustomerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            appointmentUserIdCol.setCellValueFactory(new PropertyValueFactory<>("userID"));
            appointmentContactIdCol.setCellValueFactory(new PropertyValueFactory<>("contactID"));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Populates boxes below the table with selected appointment information
     * @param mouseEvent When an appointment in the table is selected it loads.
     */
    public void loadAppointment(MouseEvent mouseEvent) {
        try {
            Connection connection = JDBC.getConnection();
            Appointment selectedAppointment = appointmentViewTable.getSelectionModel().getSelectedItem();

            if (selectedAppointment != null) {

                ObservableList<Contacts> allContactList = contactHelper.getAllContacts(connection);
                ObservableList<String> allContactsNames = FXCollections.observableArrayList();
                //ObservableList<String> estBusinessHoursList = appointmentHelper.getEstBusinessHours();
                String appointmentContact = "";

                allContactList.forEach(contacts -> allContactsNames.add(contacts.getContactName()));
                appointmentContactComBx.setItems(allContactsNames);

                for (Contacts contacts : allContactList) {
                    if (selectedAppointment.getContactID() == contacts.getContactID()) {
                        appointmentContact = contacts.getContactName();
                    }
                }



                //Timezone Debugging
                String appointmentID = (String.valueOf(selectedAppointment.getAppointmentID()));
                String start = String.valueOf(selectedAppointment.getStart());
                String end = String.valueOf(selectedAppointment.getEnd());
                System.out.println("The selected has the following info \n Appointment ID: " + appointmentID + " Start Date Time " + start + " End Date Time " + end );


                //Set all the text boxes, combo boxes, and date pick with the selected appointment's information
                appointmentIdTxt.setText(String.valueOf(selectedAppointment.getAppointmentID()));
                appointmentTitleTxt.setText(String.valueOf(selectedAppointment.getAppointmentTitle()));
                appointmentDescriptionTxt.setText(String.valueOf(selectedAppointment.getAppointmentDescription()));
                appointmentLocationTxt.setText(String.valueOf(selectedAppointment.getAppointmentLocation()));
                appointmentTypeTxt.setText(String.valueOf(selectedAppointment.getAppointmentType()));
                customerIdTxt.setText(String.valueOf(selectedAppointment.getCustomerID()));
                appointmentStartDatePick.setValue(selectedAppointment.getStart().toLocalDate());
                appointmentEndDatePick.setValue(selectedAppointment.getEnd().toLocalDate());
                appointmentStartTimeComBx.setValue(selectedAppointment.getStart().toLocalTime().format(DateTimeFormatter.ofPattern("h:mm a")));
                appointmentEndTimeComBx.setValue(selectedAppointment.getEnd().toLocalTime().format(DateTimeFormatter.ofPattern("h:mm a")));
                userIdTxt.setText(String.valueOf(selectedAppointment.getUserID()));
                appointmentContactComBx.setValue(appointmentContact);

            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if there is any overlap between the start and end time of the new appointment and the existing appointments of a customer.
     * If an overlap is found, shows an error message with the type of overlap detected.
     * @param dateTimeStart The start time of the new appointment
     * @param dateTimeEnd The end time of the new appointment
     * @param customerID The ID of the customer for which the appointments are being checked
     * @param newAppointmentID The ID of the new appointment that is being added
     */
    private void checkForOverlappingAppointments(LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd, int customerID, int newAppointmentID) {
        ObservableList<Appointment> getAllAppointments = appointmentHelper.getAllAppointments(connection);
        for (Appointment appointment : getAllAppointments) {
            LocalDateTime checkStart = appointment.getStart();
            LocalDateTime checkEnd = appointment.getEnd();
            if (customerID == appointment.getCustomerID() && (newAppointmentID != appointment.getAppointmentID()) &&
                    (dateTimeStart.isEqual(checkStart) || dateTimeStart.isAfter(checkStart)) &&
                    (dateTimeStart.isEqual(checkEnd) || dateTimeStart.isBefore(checkEnd))) {
                common.showError("Start Time Overlap", "Start time overlaps with existing appointment.");
                return;
            }
            if (customerID == appointment.getCustomerID() && (newAppointmentID != appointment.getAppointmentID()) &&
                    (dateTimeEnd.isEqual(checkStart) || dateTimeEnd.isAfter(checkStart)) &&
                    (dateTimeEnd.isEqual(checkEnd) || dateTimeEnd.isBefore(checkEnd))) {
                common.showError("End Time Overlap", "End time overlaps with existing appointment.");
                return;
            }
            if ((customerID == appointment.getCustomerID()) && (newAppointmentID != appointment.getAppointmentID()) &&
                    (dateTimeStart.isBefore(checkStart)) && (dateTimeEnd.isAfter(checkEnd))) {
                common.showError("Appointment Overlap", "Appointment overlaps with existing appointment.");
                return;
            }
        }
    }

    /**
     * Validates the fields of an appointment
     * Checks the start and end dates and times of the appointment against business hours and days, and ensures that all necessary fields are filled.
     * @throws Error exception for invalid input
     */
    public void validateAppointmentFields() {
        LocalDate localDateStart = appointmentStartDatePick.getValue();
        LocalDate localDateEnd = appointmentEndDatePick.getValue();

        LocalTime localTimeStart = LocalTime.parse(appointmentStartTimeComBx.getValue(), DateTimeFormatter.ofPattern("h:mm a"));
        LocalTime localTimeEnd = LocalTime.parse(appointmentEndTimeComBx.getValue(), DateTimeFormatter.ofPattern("h:mm a"));

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

        if (convertStartEST.getDayOfWeek().getValue() < workWeekStart || convertStartEST.getDayOfWeek().getValue() > workWeekEnd || convertEndEST.getDayOfWeek().getValue() < workWeekStart || convertEndEST.getDayOfWeek().getValue() > workWeekEnd) {
            common.showError("Day is outside of business operations (Monday-Friday)", "Day is outside of business hours");
            return;
        }

        if (startAppointmentTimeToCheck.isBefore(estBusinessHoursStart) || startAppointmentTimeToCheck.isAfter(estBusinessHoursEnd) || endAppointmentTimeToCheck.isBefore(estBusinessHoursStart) || endAppointmentTimeToCheck.isAfter(estBusinessHoursEnd)) {
            common.showError("Time is outside of business hours (8am-10pm EST)", "Time is outside of business hours (8am-10pm EST): " + startAppointmentTimeToCheck + " - " + endAppointmentTimeToCheck + " EST");
            return;
        }


        if (dateTimeStart.isAfter(dateTimeEnd)) {
            common.showError("Appointment has start time after end time", "Appointment has start time after end time");
            return;
        }

        if (dateTimeStart.isEqual(dateTimeEnd)) {
            common.showError("Appointment has same start and end time", "Appointment has same start and end time");
            return;
        }

        if (!appointmentTitleTxt.getText().isEmpty() && !appointmentDescriptionTxt.getText().isEmpty() && !appointmentLocationTxt.getText().isEmpty() && !appointmentTypeTxt.getText().isEmpty() && appointmentStartDatePick.getValue() != null && appointmentEndDatePick.getValue() != null && !appointmentStartTimeComBx.getValue().isEmpty() && !appointmentEndTimeComBx.getValue().isEmpty() && !customerIdTxt.getText().isEmpty() && !userIdTxt.getText().isEmpty()) {
            common.showError("All fields must be filled in", "Please fill in all fields.");
        }
    }
}

