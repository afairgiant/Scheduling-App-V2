package scheduling_app.scheduling_app;

import helper.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/loginScreen.fxml")));
            Scene scene = new Scene(root);
            stage.setTitle("Appointment Scheduling");
            stage.setScene(scene);
            stage.show();

    }

    public static void main(String[] args) {
       try {
           //Start SQL connection
           JDBC.startConnection();
           launch(args);
           //Close SQL Connection
           JDBC.closeConnection();
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
    }
}