package helper;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Country;
import model.Customers;
import model.firstLevelDivision;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class firstLevelDivisionHelper extends firstLevelDivision {
    /**
     * @param divisionID
     * @param divisionName
     * @param countryID
     */
    public firstLevelDivisionHelper(int divisionID, String divisionName, int countryID) {
        super(divisionID, divisionName, countryID);
    }

    /**
     * Retrieve all first level divisions from the database
     * @param connection Connection to database
     * @return ObservableList of firstLevelDivisionHelper objects containing the Division_ID, Division name and Country_ID
     * @throws SQLException thrown when there is a problem accessing the database
     */
    public static ObservableList<firstLevelDivisionHelper> getAllFirstLevelDivisions(Connection connection) throws SQLException {
        String query = ("SELECT Division_ID, Division, Country_ID from first_level_divisions");
        JDBC.setPreparedStatement(JDBC.getConnection(), query);
        PreparedStatement preparedStatement = JDBC.getPreparedStatement();
        ResultSet resultSet = JDBC.executePreparedStatement();

        ObservableList<firstLevelDivisionHelper> firstLevelDivisions = FXCollections.observableArrayList();
        while (resultSet.next()){
            int divisionID = resultSet.getInt("Division_ID");
            String divisionName = resultSet.getString("Division");
            int countryID = resultSet.getInt("Country_ID");
            firstLevelDivisionHelper firstLevelDivision = new firstLevelDivisionHelper(divisionID, divisionName, countryID);
            firstLevelDivisions.add(firstLevelDivision);
        }
        return firstLevelDivisions;
    }

    /**
     * Retrieves the names of the division and country for a specified customer.
     * @param selectedCustomer The customer whose division and country names to retrieve.
     * @param allCountries A list of all countries.
     * @param firstLevelDivisions A list of all first level divisions.
     * @return A Map containing the divisionName and countryName for the specified customer.
     */
    public static Map<String, String> getDivisionAndCountryNames(Customers selectedCustomer, ObservableList<countryHelper> allCountries, ObservableList<firstLevelDivisionHelper> firstLevelDivisions) {
        String divisionName = "";
        String countryName = "";

        for (firstLevelDivisionHelper firstLevelDivision : firstLevelDivisions) {
            int countryIDToSet = firstLevelDivision.getCountryID();

            if (firstLevelDivision.getDivisionID() == selectedCustomer.getCustomerDivisionID()) {
                divisionName = firstLevelDivision.getDivisionName();

                for (Country country : allCountries) {
                    if (country.getCountryID() == countryIDToSet) {
                        countryName = country.getCountryName();
                    }
                }
            }
        }

        Map<String, String> divisionAndCountryNames = new HashMap<>();
        divisionAndCountryNames.put("divisionName", divisionName);
        divisionAndCountryNames.put("countryName", countryName);
        return divisionAndCountryNames;
    }
}
