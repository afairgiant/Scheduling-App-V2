package helper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Contacts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class contactHelper {
    /**
     * Retrieve all Contacts from the database
     * @param connection a connection to the database
     * @return an ObservableList of Contacts
     * @throws SQLException if an error occurs while querying the database
     */
    public static ObservableList<Contacts> getAllContacts(Connection connection) throws SQLException {
        try {
            String query = "SELECT * FROM client_schedule.contacts";
            JDBC.setPreparedStatement(JDBC.getConnection(), query);
            PreparedStatement preparedStatement = JDBC.getPreparedStatement();
            ResultSet resultSet = JDBC.executePreparedStatement();

            ObservableList<Contacts> contactsObservableList = FXCollections.observableArrayList();
            while (resultSet.next()) {
                int contactID = resultSet.getInt("Contact_ID");
                String contactName = resultSet.getString("Contact_Name");
                String contactEmail = resultSet.getString("Email");
                Contacts contact = new Contacts(contactID, contactName, contactEmail);
                contactsObservableList.add(contact);
                //System.out.println(contactsObservableList);
            }
            return contactsObservableList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Finds the contact in the database using the provided contact ID.
     * @param contactID the ID of the contact to be searched
     * @return the contact ID
     * @throws SQLException if a database access error occurs
     */
    public static String findContact(String contactID) throws SQLException {
        try {
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement("SELECT * FROM contacts WHERE Contact_Name = ?");
            preparedStatement.setString(1, contactID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                contactID = resultSet.getString("Contact_ID");
            }
            return contactID;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
