package helper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class appointmentHelper {
    /**
     * Retrieves all appointments from teh database and returns an observable list of appointment objects
     * Creates an appointment object for each record and then adds it to the Observable list
     * @param connection the connection object to the database
     * @return an ObservableList of Appointment objects containing all appointment records from the database.
     */
    public static ObservableList<Appointment> getAllAppointments(Connection connection) {
        try {
            if (connection.isClosed()){ //check if connection is closed
                connection = JDBC.startConnection(); //Reconnect if necessary
            }
            String query = "SELECT * FROM client_schedule.appointments";

            JDBC.setPreparedStatement(JDBC.getConnection(), query);
            PreparedStatement preparedStatement = JDBC.getPreparedStatement();
            ResultSet resultSet = JDBC.executePreparedStatement();

            ObservableList<Appointment> appointmentsObservableList = FXCollections.observableArrayList();
            while (resultSet.next()) {
                int appointmentID = resultSet.getInt("Appointment_ID");
                String appointmentTitle = resultSet.getString("Title");
                String appointmentLocation = resultSet.getString("Location");
                String appointmentDescription = resultSet.getString("Description");
                String appointmentType = resultSet.getString("Type");
                LocalDateTime start = resultSet.getTimestamp("Start").toLocalDateTime();
                LocalDateTime end = resultSet.getTimestamp("End").toLocalDateTime();
                int customerID = resultSet.getInt("Customer_ID");
                int userID = resultSet.getInt("Customer_ID");
                int contactID = resultSet.getInt("Contact_ID");
                Appointment appointment = new Appointment(appointmentID, appointmentTitle, appointmentLocation, appointmentDescription, appointmentType, start, end, customerID, userID, contactID);
                appointmentsObservableList.add(appointment);
                //System.out.println("\nAppointment ID: " + appointmentID + " Local Start Date Time " + start + " Local End Date Time " + end );
            }
            //System.out.println(appointmentsObservableList.size());
            return appointmentsObservableList;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Deletes all appointments for a specified customer ID from the appointments table in the database.
     * @param connection The connection to the database.
     * @param customerId The ID of the customer whose appointments are to be deleted.
     */
    public static void deleteAppointmentsForCustomer(Connection connection, int customerId) {
        try {
            if (connection.isClosed()){ //check if connection is closed
                connection = JDBC.startConnection(); //Reconnect if necessary
            }
            String deleteStatement = "DELETE FROM appointments WHERE Customer_ID = ?";
            JDBC.setPreparedStatement(connection, deleteStatement);
            PreparedStatement preparedStatement = JDBC.getPreparedStatement();
            preparedStatement.setInt(1, customerId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Updates database with the updated appointment information
     * @param appointmentID
     * @param appointmentTitle
     * @param appointmentLocation
     * @param appointmentDescription
     * @param appointmentType
     * @param start
     * @param end
     * @param customerID
     * @param userID
     * @param contactID
     * @param userTimeZone
     * @throws SQLException
     */
    public static void updateAppointment(int appointmentID, String appointmentTitle, String appointmentLocation, String appointmentDescription, String appointmentType, LocalDateTime start, LocalDateTime end, int customerID, int userID, int contactID, ZoneId userTimeZone) throws SQLException {
        //Convert the start and end date/times to UTC
        //ZonedDateTime startUTC = start.atZone(userTimeZone).withZoneSameInstant(ZoneOffset.UTC);
        //ZonedDateTime endUTC = end.atZone(userTimeZone).withZoneSameInstant(ZoneOffset.UTC);


        // Create a prepared statement with the query to update the appointment
        String updateStatement = "UPDATE appointments SET Title=?, Description=?, Location=?, Type=?, Start=?, End=?, Last_Update=UTC_TIMESTAMP(), Customer_ID=?, User_ID=?, Contact_ID=? WHERE Appointment_ID=?";
        JDBC.setPreparedStatement(JDBC.getConnection(), updateStatement);
        try{PreparedStatement preparedStatement = JDBC.getPreparedStatement();

            preparedStatement.setString(1, appointmentTitle);
            preparedStatement.setString(2, appointmentDescription);
            preparedStatement.setString(3, appointmentLocation);
            preparedStatement.setString(4, appointmentType);
            preparedStatement.setTimestamp(5, Timestamp.valueOf(start));
            preparedStatement.setTimestamp(6, Timestamp.valueOf(end));
            preparedStatement.setInt(7, customerID);
            preparedStatement.setInt(8, userID);
            preparedStatement.setInt(9, contactID);
            preparedStatement.setInt(10, appointmentID);

            // Execute the query
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("\nAppointment updated successfully.");
            } else {
                System.out.println("\nFailed to update appointment.");
            }
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
        }
    }
    /**
     * Adds new appointment to the database
     * @param appointmentTitle
     * @param appointmentLocation
     * @param appointmentDescription
     * @param appointmentType
     * @param start
     * @param end
     * @param contactID
     * @param userID
     * @param contactID
     * @return result
     */
    public static int addNewAppointment(String appointmentTitle, String appointmentLocation, String appointmentDescription, String appointmentType, LocalDateTime start, LocalDateTime end, int customerID, int userID, int contactID) {
        int result = -0;
        try {
            String insertStatement = "INSERT INTO appointments (Appointment_ID, Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            JDBC.setPreparedStatement(JDBC.getConnection(), insertStatement);
            PreparedStatement preparedStatement = JDBC.getPreparedStatement();

            preparedStatement.setInt(1, 0); //AppointmentID
            preparedStatement.setString(2, appointmentTitle); //Appointment Title
            preparedStatement.setString(3, appointmentDescription); //Appointment Description
            preparedStatement.setString(4, appointmentLocation); //Appointment Location
            preparedStatement.setString(5, appointmentLocation); //Appointment Type
            preparedStatement.setTimestamp(6, Timestamp.valueOf(start));//Appointment Start Date/Time in UTC
            preparedStatement.setTimestamp(7, Timestamp.valueOf(end)); //Appointment End Date/Time in UTC
            preparedStatement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now())); //Appointment Created_Date
            preparedStatement.setString(9, "admin"); //Appointment Created_By. Eventually maybe change so pulls in current user.
            preparedStatement.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now())); //Appointment Last_Update
            preparedStatement.setString(11, "admin"); //Appointment Last_Updated_By. Eventually maybe change so pulls in current user.
            preparedStatement.setInt(12, customerID); //Customer_ID
            preparedStatement.setInt(13, contactID); //Contact_ID
            preparedStatement.setInt(14, userID); //User_ID

            //Execute the statement
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected >0){
                result = -1;// successful insertion, set result to -1
            }
            System.out.println("Appointment added successfully");

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
        }
        return result;
    }
}
