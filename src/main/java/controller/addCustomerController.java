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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
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
import java.util.ResourceBundle;

public class addCustomerController implements Initializable {

    @FXML
    private Button cancelBtn;

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
    private ComboBox<String> customerRegionComboBox;

    @FXML
    private TextField customerPostalCodeTxt;

    @FXML
    private Button saveBtn;

    /**
     * Cancels add new customer, goes back to customerView.fxml
     * @param event
     * @throws IOException
     */
    @FXML
    void actionCancel(ActionEvent event) throws IOException {
        // Load customerView.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/customerView.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Checks if any of the inputs are empty, then looks up the Division_ID by the country and region
     * Then it preps the JDBC prepared statement then inserts into the database.
     * @param event
     * @throws IOException
     */
    @FXML
    void actionSave(ActionEvent event) throws IOException {
        try {
            // Check if any of the boxes are empty
            if (customerNameTxt.getText().isEmpty() || customerAddressTxt.getText().isEmpty() || customerPostalCodeTxt.getText().isEmpty() || customerPhoneTxt.getText().isEmpty() || customerCountryComboBox.getSelectionModel().isEmpty() || customerRegionComboBox.getSelectionModel().isEmpty()) {
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
                System.out.println("Failed to add customer: Could not find Division_ID for country " + country + " and region " + region);
                return;
            }

            String insertStatement = "INSERT INTO customers(Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            JDBC.setPreparedStatement(connection, insertStatement);
            PreparedStatement preparedStatement = JDBC.getPreparedStatement();
            String customerName = customerNameTxt.getText();
            String customerAddress = customerAddressTxt.getText();
            String customerPostalCode = customerPostalCodeTxt.getText();
            String customerPhone = customerPhoneTxt.getText();

            // Debugging print statements
            System.out.println("customerName : " + customerName);
            System.out.println("customerAddress: " + customerAddress);
            System.out.println("customerPostalCode: " + customerPostalCode);
            System.out.println("customerPhone: " + customerPhone);
            System.out.println("divisionID: " + divisionID);

            // Set the values for the parameters in the prepared statement
            preparedStatement.setString(1,customerNameTxt.getText()); //Customer Name
            preparedStatement.setString(2, customerAddressTxt.getText()); //Address
            preparedStatement.setString(3, customerPostalCodeTxt.getText()); //Postal_Code
            preparedStatement.setString(4, customerPhoneTxt.getText()); //Customer Phone #
            preparedStatement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now(ZoneId.systemDefault()))); //Created Date - change to UTC?
            preparedStatement.setString(6, "admin"); //Created By - Replace with actual created by user
            preparedStatement.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now(ZoneId.systemDefault()))); //Last_Update - change to UTC?
            preparedStatement.setString(8, "admin"); //Last_Updated_By - replace with actual user
            preparedStatement.setInt(9, divisionID); //Division_ID

            // Execute the prepared statement
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Customer added successfully.");
            } else {
                System.out.println("Failed to add customer.");
            }
            // Load customerView.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/customerView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }catch (SQLException e){
            System.out.println("Failed to add customer" + e.getMessage());
        }
    }

    /**
     * Initialize method for the addCustomerController
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
            // Add country names to the countryNames list
            for (countryHelper country : allCountries) {
                countryNames.add(country.getCountryName());
            }
            customerCountryComboBox.setItems(countryNames);
            customerRegionComboBox.setItems(firstLevelDivisionNames);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates the list of available first-level divisions based on the selected country in the customerCountryComboBox.
     * The lambda checks each first-level division and add its division name to the appropriate list (US, UK, or Canada). The resulting lists
     * are used to populate the first-level division combo box.
     * Lambda : Checks the country of each first-level division and add its division name to the appropriate list (US, UK, or Canada)
     * @param event the event that triggered the method
     * @throws SQLException if a database access error occurs
     */
    public void selectedCountry(ActionEvent event) throws SQLException {
        Connection connection = JDBC.getConnection();
        String selectedCountry = customerCountryComboBox.getSelectionModel().getSelectedItem();
        ObservableList<firstLevelDivisionHelper> allFirstLevelDivisions = firstLevelDivisionHelper.getAllFirstLevelDivisions(connection);

        ObservableList<String> firstLevelDivisionUS = FXCollections.observableArrayList();
        ObservableList<String> firstLevelDivisionUK = FXCollections.observableArrayList();
        ObservableList<String> firstLevelDivisionCanada = FXCollections.observableArrayList();
        //Lambda - checks the firstLevelDivision as input and checks its countryID to add its DivisionName to correct list
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
