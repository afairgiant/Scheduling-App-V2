package helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class logging {
    /**
     * Logs user activity to a text file in program root.
     * @param userName the name of the user whose activity is being logged
     * @param success  boolean indicating whether the user's activity was successful or not
     * @throws RuntimeException if there is an IOException
     */
    public static void logUserActivity(String userName, boolean success) {
        String fileName = "login_activity.txt";
        //String filePath = System.getProperty("user.dir") + File.separator + fileName;
        String filePath = new File(fileName).getAbsolutePath();
        File logFile = new File(filePath);
        try {
            // Create the file if it doesn't exist
            if (!logFile.exists()) {
                logFile.createNewFile();
            }

            // Open the file for appending
            FileWriter fw = new FileWriter(logFile, true);
            BufferedWriter bw = new BufferedWriter(fw);

            // Get the current date and time
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String dateTimeString = dateFormat.format(date);

            // Write the log entry to the file
            String logEntry = userName + "\t" + dateTimeString + "\t" + (success ? "Successful" : "Failed") + "\n";
            bw.write(logEntry);

            // Close the file
            bw.close();
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
