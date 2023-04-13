package helper;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Common methods used across application
 */
public class common {
    /**
     * This method displays an error message in an alert dialog box.
     * @param title The title to be displayed on the error alert box
     * @param message The message to be displayed on the error alert box
     */
    // Show an error message
    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    /**
     * This method displays a confirmation dialog box with the specified title and message.
     * @param title The title to be displayed in the dialog box
     * @param message The message to be displayed in the dialog box
     * @return true if the user clicks the OK button, false if the user clicks Cancel or closes the dialog box
     */
    public static boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    /**
     * This method displays an information message in a dialog box with a specified title and message.
     * @param title the title of the dialog box
     * @param message the message to be displayed in the dialog box
     */
    public static void showInformation(String title, String message) {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle(title);
        info.setHeaderText(null);
        info.setContentText(message);
        info.showAndWait();
    }
    /**
     * This method takes a LocalDate object and a time string in "h:mm a" format, creates a LocalDateTime object using
     * the given parameters, and then converts it to UTC time zone. The converted date and time is returned in a string
     * format of "yyyy-MM-dd HH:mm:ss".
     * @param date The date for which the time is to be converted to UTC
     * @param time The time to be converted to UTC in "h:mm a" format
     * @return A string representation of the date and time in UTC time zone in "yyyy-MM-dd HH:mm:ss" format
     */
    public static String getUTCDateTime(LocalDate date, String time) {
        LocalDateTime localDateTime = LocalDateTime.of(date, LocalTime.parse(time, DateTimeFormatter.ofPattern("h:mm a")));
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        return zonedDateTime.withZoneSameInstant(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
