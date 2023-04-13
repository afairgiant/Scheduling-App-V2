package controller;

import helper.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.*;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Month;
import java.util.*;

public class reportsController implements Initializable {
    @FXML
    private TableColumn<?, ?> appointmentCustomerIdCol;

    @FXML
    private TableColumn<?, ?> appointmentDescriptionCol;

    @FXML
    private TableColumn<?, ?> appointmentEndCol;

    @FXML
    private TableColumn<?, ?> appointmentIdCol;

    @FXML
    private TableColumn<?, ?> appointmentMonthCountCol;

    @FXML
    private TableColumn<?, ?> appointmentStartCol;

    @FXML
    private TableColumn<?, ?> appointmentTitleCol;

    @FXML
    private TableColumn<?, ?> appointmentTotalMonthCol;

    @FXML
    private TableColumn<?, ?> appointmentTotalTypeCol;

    @FXML
    private TableColumn<?, ?> appointmentTypeCol;

    @FXML
    private TableColumn<?, ?> appointmentTypeCountCol;

    @FXML
    private TableView<Appointment> contactScheduleTable;

    @FXML
    private TableView<AppointmentMonthCount> totalAppointmentMonthTable;

    @FXML
    private TableView<AppointmentTypeCount> totalAppointmentTypeTable;

    @FXML
    private Button backButton;

    @FXML
    private ComboBox<String> contactComboBox;

    @FXML
    private Tab appointmentTotalsTab;
    @FXML
    private TableColumn<?, ?> busiestCountCol;

    @FXML
    private TableColumn<?, ?> busiestDivisionCol;

    @FXML
    private Label busiestDivisionLabel;

    @FXML
    private Tab busiestDivisionTab;

    @FXML
    private TableView<DivisionCount> busiestDivisionTable;

    private final ObservableList<AppointmentTypeCount> appointmentTypeCounts = FXCollections.observableArrayList();
    private final ObservableList<AppointmentMonthCount> appointmentMonthCounts = FXCollections.observableArrayList();

    /**
     * Goes back to main menu
     * @param event
     * @throws IOException
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
     * Initialize method for the reportsController.
     * Loads and initializes the tables and columns and the contact schedule tab since it is default
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try{
            Connection connection = JDBC.getConnection();
            ObservableList<Users> allUsersList = userHelper.getAllUsers(connection);
            ObservableList<Contacts> allContactsList = contactHelper.getAllContacts(connection);
            ObservableList<Appointment> allAppointmentsList = appointmentHelper.getAllAppointments(connection);
            ObservableList<String> allContactsNames = FXCollections.observableArrayList();
            ObservableList<DivisionCount> busiestDivisions = FXCollections.observableArrayList();
            allContactsList.forEach(contacts -> allContactsNames.add(contacts.getContactName()));
            contactComboBox.setItems(allContactsNames);

            //Fill the contact schedule table by default with all appointments
            contactScheduleTable.setItems(allAppointmentsList);
            appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
            appointmentTitleCol.setCellValueFactory(new PropertyValueFactory<>("appointmentTitle"));
            appointmentDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("appointmentDescription"));
            appointmentTypeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
            appointmentStartCol.setCellValueFactory(new PropertyValueFactory<>("start"));
            appointmentEndCol.setCellValueFactory(new PropertyValueFactory<>("end"));
            appointmentCustomerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));

            // Fill the appointment totals my type table
            totalAppointmentTypeTable.setItems(appointmentTypeCounts);
            appointmentTotalTypeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
            appointmentTypeCountCol.setCellValueFactory(new PropertyValueFactory<>("count"));

            // Fill the appointment totals by month table
            totalAppointmentMonthTable.setItems(appointmentMonthCounts);
            appointmentTotalMonthCol.setCellValueFactory(new PropertyValueFactory<>("month"));
            appointmentMonthCountCol.setCellValueFactory(new PropertyValueFactory<>("count"));

            // Initialize the busiestDivision table
            busiestDivisionTable.setItems(busiestDivisions);
            busiestDivisionCol.setCellValueFactory(new PropertyValueFactory<>("divisionName"));
            busiestCountCol.setCellValueFactory(new PropertyValueFactory<>("count"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the contact ID for selected contact in Contact Schedule Report
     * @param event
     */
    public void actionSelectContact(ActionEvent event) {
        try{
            Connection connection = JDBC.getConnection();
            ObservableList<Appointment> allAppointmentsList = appointmentHelper.getAllAppointments(connection);
            ObservableList<Contacts> allContactsList = contactHelper.getAllContacts(connection);
            //Get the selected contact's ID based off name
            String selectedContact = contactComboBox.getValue();
            int selectedContactID = -1; // default value
            for (Contacts contact : allContactsList) {
                if (contact.getContactName().equals(selectedContact)) {
                    selectedContactID = contact.getContactID();
                    break;
                }
            }
            ObservableList<Appointment> filteredAppointments = FXCollections.observableArrayList();
            for (Appointment appointment : allAppointmentsList) {
                if (appointment.getContactID() == (selectedContactID)) {
                    filteredAppointments.add(appointment);
                }
            }
            contactScheduleTable.setItems(filteredAppointments);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * Loads data into two tables: totalAppointmentTypeTable and totalAppointmentMonthTable.
     * The data includes the count of appointments by type and month, respectively.
     * @param event when totalsTab is selected
     */
    public void totalsTabSelected(Event event) {
        try{
            Connection connection = JDBC.getConnection();
            ObservableList<Appointment> allAppointmentsList = appointmentHelper.getAllAppointments(connection);

            // Appointment totals by type table
            ObservableList<String> appointmentTypes = FXCollections.observableArrayList();
            for (Appointment appointment : allAppointmentsList){
                appointmentTypes.add(appointment.getAppointmentType());
            }
            // Create a map to store the count of appointments for each type
            Map<String, Integer> appointmentTypeCountMap = new HashMap<>();

            // Loop through all appointments and update the count in the map
            for (Appointment appointment : allAppointmentsList) {
                String appointmentType = appointment.getAppointmentType();
                appointmentTypeCountMap.put(appointmentType, appointmentTypeCountMap.getOrDefault(appointmentType, 0) + 1);
            }

            // Create a new observable list that contains the appointment type and count data
            ObservableList<AppointmentTypeCount> appointmentTypeCounts = FXCollections.observableArrayList();
            for (String appointmentType : appointmentTypeCountMap.keySet()) {
                int count = appointmentTypeCountMap.get(appointmentType);
                appointmentTypeCounts.add(new AppointmentTypeCount(appointmentType, count));
            }
            for (AppointmentTypeCount appointmentTypeCount : appointmentTypeCounts) {
                System.out.println(appointmentTypeCount.getAppointmentType() + ": " + appointmentTypeCount.getCount());
            }
            // Load data into the table
            totalAppointmentTypeTable.setItems(appointmentTypeCounts);
            appointmentTotalTypeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
            appointmentTypeCountCol.setCellValueFactory(new PropertyValueFactory<>("count"));


            //Appointment totals by month table
            ObservableList<Month> appointmentMonths = FXCollections.observableArrayList();
            ObservableList<Month> monthWithAppointments = FXCollections.observableArrayList();
            // Extract the month of each appointment start date and add it to a list
            for (Appointment appointment : allAppointmentsList) {
                Month month = appointment.getStart().getMonth();
                appointmentMonths.add(month);
            }

            // Create a map to store the count of appointments for each month
            Map<Month, Integer> appointmentMonthCountMap = new HashMap<>();

            // Loop through all appointments and update the count in the map
            for (Month month : appointmentMonths) {
                appointmentMonthCountMap.put(month, appointmentMonthCountMap.getOrDefault(month, 0) + 1);
            }

            // Create a new observable list that contains the appointment month and count data
            ObservableList<AppointmentMonthCount> appointmentMonthCounts = FXCollections.observableArrayList();
            for (Month month : appointmentMonthCountMap.keySet()) {
                int count = appointmentMonthCountMap.get(month);
                appointmentMonthCounts.add(new AppointmentMonthCount(month, count));
            }

            for (AppointmentMonthCount appointmentMonthCount : appointmentMonthCounts) {
                System.out.println(appointmentMonthCount.getAppointmentMonth() + ": " + appointmentMonthCount.getCount());
            }
            // Load data into the table
            totalAppointmentMonthTable.setItems(appointmentMonthCounts);
            appointmentTotalMonthCol.setCellValueFactory(new PropertyValueFactory<>("appointmentMonth"));
            appointmentMonthCountCol.setCellValueFactory(new PropertyValueFactory<>("count"));


        } catch (RuntimeException e){
            System.out.println("load failed: " + e.getMessage());
        }
    }

    /**
     * This method calculates the busiest division based on number of appointments it has,
     * by iterating through all first level divisions and counting the number of appointments each one
     * has. It then displays the data in the busiestDivisionTable and adjusts the busiestDivisionLabel
     * @param event When the busiestDivision tab is selected
     */
    @FXML
    public void busiestDivisionTab(Event event) {
        try {
            Connection connection = JDBC.getConnection();
            ObservableList<Appointment> allAppointmentsList = appointmentHelper.getAllAppointments(connection);
            ObservableList<firstLevelDivisionHelper> firstLevelDivisions = firstLevelDivisionHelper.getAllFirstLevelDivisions(connection);
            ObservableList<Customers> allCustomers = customerHelper.getAllCustomers(connection);
            ObservableList<DivisionCount> busiestDivisions = FXCollections.observableArrayList(); // initialize observable list

            for (firstLevelDivision division : firstLevelDivisions) {
                int numAppointments = 0;

                // iterate through each appointment
                for (Appointment appointment : allAppointmentsList) {
                    int customerId = appointment.getCustomerID();

                    // find the customer object with the matching ID
                    Customers matchingCustomer = null;
                    for (Customers customer : allCustomers) {
                        if (customer.getCustomerID() == customerId) {
                            matchingCustomer = customer;
                            break;
                        }
                    }

                    // check if the customer's division name matches the current division name
                    if (matchingCustomer != null && matchingCustomer.getDivisionName().equals(division.getDivisionName())) {
                        numAppointments++;
                    }
                }

                // create DivisionCount object and add it to the observable list
                DivisionCount divisionCount = new DivisionCount(division.getDivisionName(), numAppointments);
                busiestDivisions.add(divisionCount);
            }

            // sort the list in descending order of appointment count
            busiestDivisions.sort(Comparator.comparingInt(DivisionCount::getCount).reversed());

            // display the data in the table
            busiestDivisionTable.setItems(busiestDivisions);
            busiestDivisionCol.setCellValueFactory(new PropertyValueFactory<>("divisionName"));
            busiestCountCol.setCellValueFactory(new PropertyValueFactory<>("count"));

            // calculate the busiest division
            String busiestDivision = busiestDivisions.get(0).getDivisionName();
            int maxAppointments = busiestDivisions.get(0).getCount();

            // convert the count to a String
            String count = Integer.toString(maxAppointments);

            // output the division with the most appointments
            System.out.println("The busiest division is " + busiestDivision + " with " + count + " appointments.");
            busiestDivisionLabel.setText(" " + busiestDivision + " with " + count + " appointments!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
