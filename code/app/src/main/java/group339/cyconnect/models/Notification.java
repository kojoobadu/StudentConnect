package group339.cyconnect.models;

/**
 * This is a model for the notification object.  It has all the fields that an notification would have. To be used
 * for handling the responses from the server.
 * Created by apple on 10/11/15.
 */
public class Notification extends Object {

    private String text;
    private String identifier;
    private String tag;
    private String sender;
    private String time;

    /**
     * The default constructor for the notification object
     * @param text - the text that is in the object
     * @param identifier - who is receiving the notification
     * @param tag - the type of notification that is being sent
     * @param sender - who sent the notifcation, for invites and comments
     * @param time - the time that the notification occured
     */
    public Notification(String text, String identifier, String tag, String sender, String time) {
        this.text = text;
        this.identifier = identifier;
        this.tag = tag;
        this.sender = sender;
        this.time = time;
    }

    /**
     *
     * @return The text in the notification
     */
    public String getText() {
        return this.text;
    }

    /**
     *
     * @return the tag for the notifcation
     */
    public String getTag() {
        return this.tag;
    }

    /**
     *
     * @return Who is gettin gthe notification
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     *
     * @return Who sent the notification
     */
    public String getSender() {
        return this.sender;
    }

    /**
     *
     * @return the time the notifcation occured
     */
    public String getTime() {
        return this.time;
    }

    /**
     * Convert the notifcation to a string to be able to be shown in the listViews
     * @return - the notifcation converted to a string.
     */
    public String toString() {
        String notificationString = "";
        notificationString += "Text: " + this.getText() + ", " +
                "From: " + this.getIdentifier() + ", " +
                "Tag: " + this.getTag() + ", " +
                "Time: " + this.getTime() + ", " +
                "Sender: " + this.getSender();
         //        + ".";
        return notificationString;
    }
}
