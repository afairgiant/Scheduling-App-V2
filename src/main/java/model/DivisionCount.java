package model;

public class DivisionCount {
    private String divisionName;
    private int count;

    public DivisionCount(String divisionName, int count) {
        this.divisionName = divisionName;
        this.count = count;
    }

    /**
     * @return divisionName
     */
    public String getDivisionName() {
        return divisionName;
    }

    /**
     * @param divisionName
     */
    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    /**
     * @return count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count
     */
    public void setCount(int count) {
        this.count = count;
    }
}
