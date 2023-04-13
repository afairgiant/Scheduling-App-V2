package helper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class userHelper extends Users {

    /**
     *  Constructs a UserHelper object with specified user ID, username and password
     *  @param userID the ID of the user
     *  @param userName the name of the user
     *  @param userPassword the password of the user
     */
    public userHelper(int userID, String userName, String userPassword) {
        super(userID, userName, userPassword);
    }

    /**
     * Validates a user's login credentials by checking if the provided username and password match the database records.
     * @param userName the username entered by the user
     * @param password the password entered by the user
     * @return the user ID of the validated user, or -1 if validation fails
     * @throws RuntimeException if a database error occurs
     */
    public static int validateUser(String userName, String password) {
        try {
            String query = "SELECT * FROM users WHERE user_name = ? AND password = ?";
            JDBC.setPreparedStatement(JDBC.getConnection(), query);
            PreparedStatement preparedStatement = JDBC.getPreparedStatement();
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, password);
            ResultSet resultSet = JDBC.executePreparedStatement();

            if (resultSet.next()) {
                int userId = resultSet.getInt("User_ID");
                logging.logUserActivity(userName, true);
                return userId;
            } else {
                logging.logUserActivity(userName, false);
                //common.showError("Invalid Login", "Incorrect username or password.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    /**
     * Retrieves a list of all users from the database.
     * @param connection database connection
     * @return a list of users as an ObservableList
     * @throws SQLException if there was an error executing the SQL query
     */
    public static ObservableList<Users> getAllUsers(Connection connection) throws SQLException{
        ObservableList<Users> usersObservableList = FXCollections.observableArrayList();

        String query = "SELECT * FROM client_schedule.users";
        JDBC.setPreparedStatement(JDBC.getConnection(), query);
        PreparedStatement preparedStatement = JDBC.getPreparedStatement();

        ResultSet resultSet = JDBC.executePreparedStatement();
        while(resultSet.next()){
            int userid=resultSet.getInt("User_ID");
            String userNameG=resultSet.getString("User_Name");
            String password=resultSet.getString("Password");
            Users user= new Users(userid, userNameG, password);
            usersObservableList.add(user);
            System.out.println(usersObservableList);

        }
        return usersObservableList;
    }
}
