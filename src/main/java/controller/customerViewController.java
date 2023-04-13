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
import model.Customers;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class customerViewController implements Initializable {
    @FXML
    public TableColumn<?, ?> customerTableCountry;
    @FXML
    private Button addCustomerBtn;

    @FXML
    private TextField customerAddressTxt;

    @FXML
    private ComboBox<String> customerCountryComboBox;

    @FXML
    private TextField customerIdTxt;

    @FXML
    private TextField customerNameTxt;

    @FXML
    private TextField customerPhoneTxt;

    @FXML
    private TableView<Customers> customerRecordsTable;

    @FXML
    private ComboBox<String> customerRegionComboBox;

    @FXML
    private TableColumn<?, ?> customerTableAddress;

    @FXML
    private TableColumn<?, Integer> customerTableID;

    @FXML
    private TableColumn<?, ?> customerTableName;

    @FXML
    private TableColumn<?, ?> customerTablePhone;

    @FXML
    private TableColumn<?, ?> customerTableDivisionName;

    @FXML
    private TableColumn<?, ?> customerTableZip;

    @FXML
    private TextField customerPostalCodeTxt;

    @FXML
    private Button deleteCustomerBtn;

    @FXML
    private Button backBtn;

    @FXML
    private Button saveBtn;

    @FXML
    private Button updateCustomerBtn;

    /**
     * Loads the add customer menu
     * @param event the add new customer button is clicked
     * @throws IOException If an error occurs while loading the add customer FXML scene
     */
    @FXML
    void actionAddNewCustomer(ActionEvent event) throws IOException{
        Parent customers = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/addCustomer.fxml")));
        Scene scene = new Scene(customers);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     * Deletes a selected customer and their associated appointments from the database and throws appropriate confirmations and errors
     * @param event when the delete customer button is clicked
     */
    @FXML
    void actionDeleteCustomer(ActionEvent event) {
        Customers selectedCustomer = customerRecordsTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null){
            Connection connection = JDBC.getConnection();
            // show first confirmation dialog to delete appointments
            boolean deleteAppointments = common.showConfirmationDialog("Confirm Appointment Deletion", "All of the customers appointments must be deleted prior to deleting the customer. \n Are you sure you want to delete all appointments for this customer?");
            if (deleteAppointments) {
                // show first confirmation dialog to delete appointments
                int selectedCustomerID = selectedCustomer.getCustomerID();
                appointmentHelper.deleteAppointmentsForCustomer(connection, selectedCustomerID);
                System.out.println("Appointments for customer " + selectedCustomer.getCustomerName() + " deleted successfully.");
            } else {
                return; // don't proceed with deleting customer if appointments are not deleted
            }

            // show second confirmation dialog to delete customer
            boolean deleteCustomer = common.showConfirmationDialog("Confirm Customer Deletion", "Are you sure you want to delete this customer?");
            if (deleteCustomer) {
                try {
                    // delete selected customer
                    int selectedCustomerID = selectedCustomer.getCustomerID();
                    customerHelper.deleteCustomer(connection, selectedCustomerID);
                    System.out.println("Customer " + selectedCustomer.getCustomerName() + " deleted successfully.");

                    // Refresh the customer list view
                    ObservableList<Customers> updatedCustomerList = customerHelper.getAllCustomers(JDBC.getConnection());
                    customerRecordsTable.setItems(updatedCustomerList);
                } catch (SQLException e) {
                    System.out.println("Failed to delete customer " + selectedCustomer.getCustomerName() + ": " + e.getMessage());
                }
            }
        } else {
            common.showConfirmationDialog("No Customer Selected", "Please select a customer to delete.");
        }
    }

    /**
     * Goes back to the main menu
     * @param event when the back button is clicked
     * @throws IOException If an error occurs while loading the main menu FXML scene
     */
    @FXML
    void actionBackToMain(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/mainScreen.fxml")));
        Scene scene = new Scene(root);
        Stage mainReturn = (Stage) ((Node)event.getSource()).getScene().getWindow();
        mainReturn.setScene(scene);
        mainReturn.show();

    }

    /**
     * Updates a selected customer with new information in the database
     * @param event when the update customer button is clicked
     */
    @FXML
    void actionUpdateCustomer(ActionEvent event) {
        Customers selectedCustomer = customerRecordsTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null){
            try {
                // Check if any of the boxes are empty
                if (customerIdTxt.getText().isEmpty() || customerNameTxt.getText().isEmpty() || customerAddressTxt.getText().isEmpty() || customerPostalCodeTxt.getText().isEmpty() || customerPhoneTxt.getText().isEmpty() || customerCountryComboBox.getSelectionModel().isEmpty() || customerRegionComboBox.getSelectionModel().isEmpty()) {
                    common.showError("Missing information", "You are missing information, please check");
                    System.out.println("Please fill in all required fields.");
                    return;
                }
                Connection connection = JDBC.getConnection();

                // Reverse lookup the Division_ID based on the Country and Region
                String country = customerCountryComboBox.getSelectionModel().getSelectedItem();
                String region = customerRegionComboBox.getSelectionModel().getSelectedItem();
                int divisionID = customerHelper.getDivisionIDForCountryAndRegion(connection, country, region);

                if (divisionID == -1) {
                    System.out.println("Failed to update customer: Could not find Division_ID for country " + country + " and region " + region);
                    return;
                }

                String updateStatement = "UPDATE customers SET Customer_ID = ?, Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Create_Date = ?, Created_By = ?, Last_Update = ?, Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?";
                JDBC.setPreparedStatement(connection, updateStatement);
                PreparedStatement preparedStatement = JDBC.getPreparedStatement();
                int customerID = Integer.parseInt(customerIdTxt.getText());
                String customerName = customerNameTxt.getText();
                String customerAddress = customerAddressTxt.getText();
                String customerPostalCode = customerPostalCodeTxt.getText();
                String customerPhone = customerPhoneTxt.getText();

                // Debugging print statements
                System.out.println("customerID: " +  customerID);
                System.out.println("customerName : " + customerName);
                System.out.println("customerAddress: " + customerAddress);
                System.out.println("customerPostalCode: " + customerPostalCode);
                System.out.println("customerPhone: " + customerPhone);
                System.out.println("divisionID: " + divisionID);

                // Set the values for the parameters in the prepared statement
                preparedStatement.setInt(1, Integer.parseInt(customerIdTxt.getText())); //Customer ID
                preparedStatement.setString(2,customerNameTxt.getText()); //Customer Name
                preparedStatement.setString(3, customerAddressTxt.getText()); //Address
                preparedStatement.setString(4, customerPhoneTxt.getText()); //Customer Phone #
                preparedStatement.setString(5, customerPostalCodeTxt.getText()); //Postal_Code
                preparedStatement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now(ZoneId.systemDefault()))); //Created Date - change to UTC?
                preparedStatement.setString(7, "admin"); //Created By - Replace with actual created by user
                preparedStatement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now(ZoneId.systemDefault()))); //Last_Update - change to UTC?
                preparedStatement.setString(9, "admin"); //Last_Updated_By - replace with actual user
                preparedStatement.setInt(10, divisionID); //Division_ID
                preparedStatement.setInt(11, customerID); //Customer_ID

                // Execute the prepared statement
                int rowsUpdated = preparedStatement.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Customer updated successfully.");
                } else {
                    System.out.println("Failed to update customer.");
                }

                // Refresh the customer list view
                ObservableList<Customers> updatedCustomerList = customerHelper.getAllCustomers(connection);
                customerRecordsTable.setItems(updatedCustomerList);


            } catch (SQLException e) {
                System.out.println("Failed to update customer: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Invalid customer ID: " + customerIdTxt.getText());
            } catch (NullPointerException e) {
                System.out.println("Please fill in all required fields.");
            }
        } else {
            System.out.println("Please select a customer to update.");
        }
    }

    /**
     * Initialize method to load all countries, first level divisions, and customers into table view
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Connection connection = JDBC.startConnection();
            //
            ObservableList<countryHelper> allCountries = countryHelper.getAllCountries(connection);
            ObservableList<firstLevelDivisionHelper> allFirstLevelDivisions = firstLevelDivisionHelper.getAllFirstLevelDivisions(connection);
            ObservableList<Customers> allCustomersList = customerHelper.getAllCustomers(connection);

            //Make ObservableArrayLists
            ObservableList<String> countryNames = FXCollections.observableArrayList();
            ObservableList<String> firstLevelDivisionNames = FXCollections.observableArrayList();


            //System.out.println(allCustomersList.size());
            customerRecordsTable.setItems(allCustomersList);
            customerTableID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            customerTableName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
            customerTableAddress.setCellValueFactory(new PropertyValueFactory<>("customerAddress"));
            customerTableZip.setCellValueFactory(new PropertyValueFactory<>("customerPostalCode"));
            customerTablePhone.setCellValueFactory(new PropertyValueFactory<>("customerPhone"));
            customerTableDivisionName.setCellValueFactory(new PropertyValueFactory<>("divisionName"));
            //customerTableCountry.setCellValueFactory(new PropertyValueFactory<>());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Load customer information into the Customer form upon selection from the Customer Records table.
     * @param mouseEvent When a customer is selected in the table
     */
    public void loadCustomer(MouseEvent mouseEvent) {
        try{
            Connection connection = JDBC.getConnection();
            Customers selectedCustomer = customerRecordsTable.getSelectionModel().getSelectedItem();

            if (selectedCustomer != null){

                ObservableList<countryHelper> allCountryList = countryHelper.getAllCountries(connection);
                ObservableList<String> allCountryNames = FXCollections.observableArrayList();
                allCountryList.forEach(Country -> allCountryNames.add(Country.getCountryName()));
                customerCountryComboBox.setItems(allCountryNames);

                ObservableList<firstLevelDivisionHelper> allFirstLevelDivisions = firstLevelDivisionHelper.getAllFirstLevelDivisions(connection);

                customerIdTxt.setText(String.valueOf(selectedCustomer.getCustomerID()));
                customerNameTxt.setText(String.valueOf(selectedCustomer.getCustomerName()));
                customerAddressTxt.setText(String.valueOf(selectedCustomer.getCustomerAddress()));
                customerPostalCodeTxt.setText(String.valueOf(selectedCustomer.getCustomerPostalCode()));
                customerPhoneTxt.setText(String.valueOf(selectedCustomer.getCustomerPhone()));

                Map<String, String> divisionAndCountryNames = firstLevelDivisionHelper.getDivisionAndCountryNames(selectedCustomer, allCountryList, allFirstLevelDivisions);
                customerCountryComboBox.setValue(divisionAndCountryNames.get("countryName"));
                customerRegionComboBox.setValue(divisionAndCountryNames.get("divisionName"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the selected country from a customer country combo box and updates the
     * options of a customer region combo box based on the selected country.
     * Lambda  Uses a lambda expression to iterate over the {@code allFirstLevelDivisions} list and conditionally add items
     * to three other lists based on the {@code countryID} property of each {@code FirstLevelDivision} object.
     * @param event When the countryCombo box is set
     * @throws SQLException when an error occurs while attempting to access the database.
     */
    //TODO - make this into method in customerHelper
    public void selectedCountry(ActionEvent event) throws SQLException {
        Connection connection = JDBC.getConnection();
        String selectedCountry = customerCountryComboBox.getSelectionModel().getSelectedItem();
        ObservableList<firstLevelDivisionHelper> allFirstLevelDivisions = firstLevelDivisionHelper.getAllFirstLevelDivisions(connection);

        ObservableList<String> firstLevelDivisionUS = FXCollections.observableArrayList();
        ObservableList<String> firstLevelDivisionUK = FXCollections.observableArrayList();
        ObservableList<String> firstLevelDivisionCanada = FXCollections.observableArrayList();

        allFirstLevelDivisions.forEach(firstLevelDivision -> {
            if (firstLevelDivision.getCountryID() == 1) {
                firstLevelDivisionUS.add(firstLevelDivision.getDivisionName());
            } else if (firstLevelDivision.getCountryID() == 2) {
                firstLevelDivisionUK.add(firstLevelDivision.getDivisionName());
            } else if (firstLevelDivision.getCountryID() == 3) {
                firstLevelDivisionCanada.add(firstLevelDivision.getDivisionName());
            }
        });


        if (selectedCountry != null){
            // Clear the current choices of the customerRegionComboBox
            customerRegionComboBox.getItems().clear();
            // Update the choices of the customerRegionComboBox based on selected country.
            switch (selectedCountry) {
                case "U.S" -> customerRegionComboBox.setItems(firstLevelDivisionUS);
                case "UK" -> customerRegionComboBox.setItems(firstLevelDivisionUK);
                case "Canada" -> customerRegionComboBox.setItems(firstLevelDivisionCanada);
            }
        }
    }
}
