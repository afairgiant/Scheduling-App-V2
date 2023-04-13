package model;

public class Country {

    private final int countryID;
    private final String countryName;

    /**
     * @param countryID
     * @param countryName
     */
    public Country (int countryID, String countryName){
        this.countryID = countryID;
        this.countryName = countryName;
    }

    /**
     * @return countyID
     */
    public int getCountryID(){
        return countryID;
    }

    /**
     * @return countryName
     */
    public String getCountryName() {
        return countryName;
    }
}

