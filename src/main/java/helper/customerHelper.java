package helper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Customers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class customerHelper {
    /**
     * Retrieves a list of all customers from the database using the provided connection.
     * @param connection the connection to the database
     * @return an ObservableList of Customers containing all customers in the database
     * @throws SQLException if an error occurs while accessing the database
     */
    public static ObservableList<Customers> getAllCustomers(Connection connection) throws SQLException {
        String query = ("SELECT customers.Customer_ID, customers.Customer_Name, customers.Address, customers.Postal_Code, customers.Phone, customers.Division_ID, first_level_divisions.Division from customers INNER JOIN  first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID");
        JDBC.setPreparedStatement(JDBC.getConnection(), query);
        PreparedStatement preparedStatement = JDBC.getPreparedStatement();
        ResultSet resultSet = JDBC.executePreparedStatement();

        ObservableList<Customers> customersObservableList = FXCollections.observableArrayList();
        while (resultSet.next()) {
            int customerID = resultSet.getInt("Customer_ID");
            String customerName = resultSet.getString("Customer_Name");
            String customerAddress = resultSet.getString("Address");
            String customerPostalCode = resultSet.getString("Postal_Code");
            String customerPhone = resultSet.getString("Phone");
            int divisionID = resultSet.getInt("Division_ID");
            String divisionName = resultSet.getString("Division");
            Customers customer = new Customers(customerID, customerName, customerAddress, customerPostalCode, customerPhone, divisionID, divisionName);
            customersObservableList.add(customer);
            System.out.println(customersObservableList);
        }
        return customersObservableList;
    }
    /**
     * Retrieves the Division ID for a given country and region.
     * @param connection a connection to the database
     * @param country the name of the country
     * @param region the name of the region
     * @return the Division ID, or -1 if not found
     * @throws SQLException if an error occurs while querying the database
     */
    // Method to perform reverse lookup of Division_ID based on Country and Region
    public static int getDivisionIDForCountryAndRegion(Connection connection, String country, String region) throws SQLException {
        String query = "SELECT Division_ID FROM first_level_divisions " +
                "WHERE Country_ID = (SELECT Country_ID FROM countries WHERE Country = ?) AND Division = ?";
        JDBC.setPreparedStatement(connection, query);
        PreparedStatement preparedStatement = JDBC.getPreparedStatement();
        preparedStatement.setString(1, country);
        preparedStatement.setString(2, region);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt(1);
        } else {
            return -1;
        }
    }
    /**
     * Deletes a customer from the database by their unique ID.
     * @param connection a Connection object representing a connection to the database
     * @param customerId the unique ID of the customer to be deleted
     * @throws SQLException if a database access error occurs or the SQL statement fails
     */
    public static void deleteCustomer(Connection connection, int customerId) throws SQLException {
        String deleteStatement = "DELETE FROM customers WHERE Customer_ID = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(deleteStatement);
        preparedStatement.setInt(1, customerId);
        preparedStatement.executeUpdate();
    }
}
