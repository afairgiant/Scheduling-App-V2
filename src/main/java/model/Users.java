package model;

public class Users {

    public int userID;
    public String userName;
    public String userPassword;
    public static Users currentUser;

    /**
     * @param userID User_ID
     * @param userName User_name
     * @param userPassword Password
     */
    public Users(int userID, String userName, String userPassword) {
        this.userID = userID;
        this.userName = userName;
        this.userPassword = userPassword;
    }

    /**
     * @return userID
     */
    public int getUserID(){
        return userID;
    }

    /**
     * @return userName
     */
    public String getUserName(){
        return userName;
    }

    /**
     * @return userPassword
     */
    public String getUserPassword(){
        return userPassword;
    }

    /**
     * @return currentUser
     */
    public static Users getCurrentUser() {
        return currentUser;
    }

    /**
     * //TODO - somehow use for setting the updated_by and created_by columns in tables.
     * @param currentUser
     */
    public static void setCurrentUser(Users currentUser) {
        Users.currentUser = currentUser;
    }
}
