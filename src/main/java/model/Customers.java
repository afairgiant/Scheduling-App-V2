package model;

public class Customers {

    private String divisionName;
    private int customerID;
    private String customerName;
    private String customerAddress;
    private String customerPostalCode;
    private String customerPhone;
    private int divisionID;


    public Customers(int customerID, String customerName, String customerAddress, String customerPostalCode, String customerPhone, int divisionID, String divisionName) {
        this.customerID = customerID;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.customerPostalCode = customerPostalCode;
        this.customerPhone = customerPhone;
        this.divisionID = divisionID;
        this.divisionName = divisionName;
    }

    /**
     * @return customerID
     */
    public Integer getCustomerID() {
        return customerID;
    }

    /**
     * @param customerID
     */
    public void setCustomerID(Integer customerID){
        this.customerID = customerID;
    }

    /**
     * @return customerName
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * @param customerName
     */
    public void setCustomerName(String customerName){
        this.customerName = customerName;
    }

    /**
     * @return customerAddress
     */
    public String getCustomerAddress() {
        return customerAddress;
    }
    /**
     * @param customerAddress
     */
    public void setCustomerAddress(String customerAddress){
        this.customerAddress = customerAddress;
    }

    /**+
     * @return customerPostalCode
     */
    public String getCustomerPostalCode() {
        return customerPostalCode;
    }

    /**
     * @param customerPostalCode
     */
    public void setCustomerPostalCode(String customerPostalCode){
        this.customerPostalCode = customerPostalCode;
    }

    /**
     * @return divisionID
     */
    public Integer getCustomerDivisionID(){
        return divisionID;
    }

    /**
     * @param divisionID
     */
    public void setCustomerDivisionID(Integer divisionID){
        this.divisionID = divisionID;
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
     * @return customerPhoneNumber
     */
    public String getCustomerPhone() {
        return customerPhone;
    }

    /**
     * @param customerPhone
     */
    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
}
