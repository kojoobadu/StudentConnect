package group339.cyconnect.models;

/**
 * This is a model for the account object.  It has all the fields that an Account would have. To be used
 * for handling the responses from the server.
 * Created by apple on 10/11/15.
 */
public class Account {


    private String firstName;
    private String lastName;
    private String userName;
    private String eventName;

    /**
     * Constructor for the Account
     * @param firstName - of the user
     * @param lastName - of the user
     * @param userName - Iowa State netID of the user
     * @param eventName - event the user is associated with
     */
    public Account(String firstName, String lastName, String userName, String eventName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.eventName = eventName;
    }

    /**
     *
     * @return The account firstName
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     *
     * @return The account lastName
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     *
     * @return The account userName
     */
    public String getUserName() {
        return this.userName;
    }
}
