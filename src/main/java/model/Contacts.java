package model;

public class Contacts {
    private final int contactID;
    private final String contactName;
    private final String contactEmail;

    public Contacts(int contactID, String contactName, String contactEmail) {
        this.contactID = contactID;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
    }

    /**
     * @return contactID
     */
    public int getContactID() {
        return contactID;
    }
    /**
     * @return contactName
     */
    public String getContactName() {
        return contactName;
    }
    /**
     * @return contactEmail
     */
    public String getContactEmail() {
        return contactEmail;
    }
}

