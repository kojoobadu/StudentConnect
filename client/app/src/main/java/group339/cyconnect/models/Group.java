package group339.cyconnect.models;

import java.util.ArrayList;


/**
 * This is a model for the group object.  It has all the fields that a Group would have. To be used
 * for handling the responses from the server.
 * Created by apple on 10/11/15.
 */
public class Group {

    private String groupName;
    private String groupDescription;
    private ArrayList<Account> groupMembers;
    private String groupOwner;
    private String groupId;

    /**
     * The default constructor for the group
     * @param groupName
     * @param groupDescription - a short description of the group
     * @param groupOwner - email of the owner of the group
     * @param groupId - Key in the database that allows us to easily find the group we want
     */
    public Group(String groupName, String groupDescription, String groupOwner, String groupId) {
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.groupOwner = groupOwner;
        this.groupId = groupId;

    }

    /**
     *
     * @return The name of the group
     */
    public String getGroupName() {
        return this.groupName;
    }

    /**
     *
     * @return The description of the group
     */
    public String getGroupDescription() {
        return this.groupDescription;
    }

    /**
     *
     * @return The email of the owner of the group
     */
    public String getGroupOwner() {
        return this.groupOwner;
    }

    /**
     *
     * @return the id of the group
     */
    public String getGroupId() {
        return this.groupId;
    }

    /**
     * Convert the group to a string for displaying in the listview
     * @return - the group converted to a string
     */
    @Override
    public String toString() {
        return "GroupName: " + this.getGroupName() + ", " +
                "Description: " + this.getGroupDescription() + ", " +
                "Owner: " + this.getGroupOwner() + ", " +
                "GroupTag: " + "TagHolder" + ", " +
                "GroupID: " + this.getGroupId();
    }
}
