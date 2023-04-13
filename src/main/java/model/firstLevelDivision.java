package model;

public class firstLevelDivision {
    private final int divisionID;
    private final String divisionName;
    public int countryID;

    /**
     * @param divisionID
     * @param divisionName
     * @param countryID
     */
    public firstLevelDivision(int divisionID, String divisionName, int countryID) {
        this.divisionID = divisionID;
        this.divisionName = divisionName;
        this.countryID = countryID;
    }

    /**
     * @return divisionID
     */
    public int getDivisionID() {
        return divisionID;
    }

    /**
     * @return divisionName
     */
    public String getDivisionName() {
        return divisionName;
    }

    /**
     * @return countryID
     */
    public int getCountryID() {
        return countryID;
    }
}
