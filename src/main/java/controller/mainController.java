package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class mainController {

    @FXML
    private Button appointmentsBtn;

    @FXML
    private Button customerBtn;

    @FXML
    private Button exitBtn;

    @FXML
    private Label menuLabel;

    @FXML
    private Button reportsBtn;

    /**
     * Exits the program
     * @param event exit button is clicked.
     */
    @FXML
    void actionExit(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();

    }

    /**
     * Loads the appointment scene when the appointment button is clicked
     * @param event when the appointment button is clicked
     * @throws IOException If the FXML file for the appointment scene is not found.
     */
    @FXML
    public void actionLoadAppointments(ActionEvent event) throws IOException {
        Parent appointments = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/appointmentView.fxml")));
        Scene scene = new Scene(appointments);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     * Loads the Customer Directory scene when the customer button is clicked
     * @param event when the customer button is clicked
     * @throws IOException If the FXML file for the Customer scene is not found.
     */
    @FXML
    public void actionLoadCustomer(ActionEvent event) throws IOException {
        Parent customer = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/customerView.fxml")));
        Scene scene = new Scene(customer);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
    /**
     * Loads the Reports scene when the Reports button is clicked.
     *
     * @param event when the Reports button is clicked
     * @throws IOException If the FXML file for the Reports scene is not found.
     */
    @FXML
    public void actionLoadReports(ActionEvent event) throws IOException {
        Parent reports = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/reports.fxml")));
        Scene scene = new Scene(reports);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
}
