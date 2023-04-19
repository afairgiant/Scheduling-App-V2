package controller;

import helper.JDBC;
import helper.appointmentHelper;
import helper.common;
import helper.userHelper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Appointment;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class loginController implements Initializable {
    Stage stage;

    @FXML
    private Button exitBtn;
    @FXML
    private TextField locationTxt;
    @FXML
    private Button loginBtn;
    @FXML
    private TextField loginPasswordTxt;
    @FXML
    private TextField loginUsernameTxt;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private Label locationLabel;
    @FXML
    private Label loginLabel;

    /**
     * Exits the program
     *
     * @param event when the exit button is clicked
     */
    @FXML
    void actionExit(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * This method is called when the login button is clicked. It first validates the user's
     * credentials and then loads the main screen. If there is an upcoming appointment within
     * 15 minutes of the login, it will display a custom message with the appointment information.
     *
     * @param event when the login button is clicked
     * @throws IOException If there is an error loading the main screen
     * @throws Exception If there is an unexpected error
     */
    @FXML
    private void actionLogin(ActionEvent event) throws IOException, Exception {
        String userNameIn = loginUsernameTxt.getText();
        String passwordIn = loginPasswordTxt.getText();
        int userId = userHelper.validateUser(userNameIn, passwordIn);

        if (userId == -1) {
            try {
                Locale locale = Locale.getDefault();
                ResourceBundle resourceBundle = ResourceBundle.getBundle("/language", locale);
                String errorMessage = resourceBundle.getString("login.error");
                common.showError(resourceBundle.getString("login.invalid"), errorMessage);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

/*        if (userId == -1) {
            common.showError("Invalid Login", "Incorrect username or password.");*/
        } else {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/mainScreen.fxml"));
                Scene scene = new Scene(root);
                stage = (Stage) loginBtn.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
                System.out.println("Login Successful!");
            } catch (IOException | RuntimeException e) {
                e.printStackTrace();
            }

            //Variables for appointment check
            Connection connection = JDBC.getConnection();
            ObservableList<Appointment> getAllAppointments = appointmentHelper.getAllAppointments(connection);
            LocalDateTime timeMinus15Min = LocalDateTime.now().minusMinutes(15);
            LocalDateTime timePlus15Min = LocalDateTime.now().plusMinutes(15);
            LocalDateTime appointmentStartTime;
            int getAppointmentID = 0;
            boolean appointmentWithin15Min = false;
            LocalDateTime displayTime = null;
            int apptUserID = 0;

            // Check for upcoming appointments within 15 minutes
            for (Appointment appointment : getAllAppointments) {
                appointmentStartTime = appointment.getStart();
                if ((appointmentStartTime.isAfter(timeMinus15Min) || appointmentStartTime.isEqual(timeMinus15Min)) && (appointmentStartTime.isBefore(timePlus15Min) || (appointmentStartTime.isEqual(timePlus15Min)))) {
                    getAppointmentID = appointment.getAppointmentID();
                    apptUserID = appointment.getUserID();
                    displayTime = appointmentStartTime;
                    appointmentWithin15Min = true;
                }
            }

            if (appointmentWithin15Min) {
                // Display custom message with appointment information
                String message = "You have an appointment within 15 minutes:\n" + "ID: " + getAppointmentID +
                        ", Date: " + displayTime.toLocalDate() +
                        ", Time: " + displayTime.toLocalTime() +
                        ", User ID: " + apptUserID +
                        "\n";
                common.showInformation("Upcoming Appointments", message);
                System.out.println("Appointment within 15 minutes");
            } else {
                // Display custom message indicating there are no upcoming appointments
                common.showInformation("Upcoming Appointments", "There are no upcoming appointments.");
            }
        }
    }
    /**
     * Initializes the user interface with the default locale and system zone.
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resource bundle containing localized objects, or null if the resource bundle is not known.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Locale locale = Locale.getDefault();
            Locale.setDefault(locale);
            ZoneId zone = ZoneId.systemDefault();
            locationTxt.setText(String.valueOf(zone));

            resourceBundle = ResourceBundle.getBundle("/language/language", Locale.getDefault());
            loginLabel.setText(resourceBundle.getString("Login"));
            usernameLabel.setText(resourceBundle.getString("username"));
            passwordLabel.setText(resourceBundle.getString("password"));
            loginBtn.setText(resourceBundle.getString("Login"));
            exitBtn.setText(resourceBundle.getString("Exit"));
            locationLabel.setText(resourceBundle.getString("Location"));

        } catch (MissingResourceException e) {
            System.out.println("Missing resource file: " + e);
        }
    }
}
