package helper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Country;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class countryHelper extends Country {
    /**
     * @param countryID
     * @param countryName
     */
    public countryHelper(int countryID, String countryName) {
        super(countryID, countryName);
    }

    /**
     * Returns an ObservableList of countryHelpers with Country_ID and Country values from the countries table in the database.
     * @param connection an established database connection
     * @throws SQLException if there is a database access error
     * @return ObservableList<countryHelper> a list of countryHelpers with Country_ID and Country values from the database
     */
    public static ObservableList<countryHelper> getAllCountries(Connection connection) throws SQLException {
      try {
          String query = ("SELECT Country_ID, Country from countries");
          JDBC.setPreparedStatement(JDBC.getConnection(), query);
          PreparedStatement preparedStatement = JDBC.getPreparedStatement();
          ResultSet resultSet = JDBC.executePreparedStatement();

          ObservableList<countryHelper> countryObservableList = FXCollections.observableArrayList();
          while (resultSet.next()) {
              int countryID = resultSet.getInt("Country_ID");
              String countryName = resultSet.getString("Country");
              countryHelper country = new countryHelper(countryID, countryName);
              countryObservableList.add(country);
          }

          return countryObservableList;
      } catch (SQLException e) {
          throw new RuntimeException(e);
      }
    }
}
