package group339.cyconnect.models;

/**
 * This is a model for the event object.  It has all the fields that an Event would have. To be used
 * for handling the responses from the server.
 * Created by apple on 10/11/15.
 */
public class Event {

    private String eventName;
    private String eventDescription;
    private String ranking;
    private String ownerEmail;

    /**
     * Constructor for the event object
     * @param eventName
     * @param eventDescription
     * @param ownerEmail
     * @param ranking - the current ranking of the event, the higher the better.
     */
    public Event(String eventName, String eventDescription, String ownerEmail, String ranking) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.ownerEmail = ownerEmail;
        this.ranking = ranking;
    }



    /**
     *
     * @return the name of the event
     */
    public String getEventName() {
        return this.eventName;
    }

    /**
     *
     * @return the description of the event
     */
    public String getEventDescription() {
        return this.eventDescription;
    }

    /**
     *
     * @return the email of the owner of the event
     */
    public String getOwnerEmail() {
        return this.ownerEmail;
    }

    /**
     *
     * @return the current ranking of the event
     */
    public String getRanking() {
        return this.ranking;
    }

    /**
     * Convert the event to a string, to be used for displaying in the listView
     * @return - the event converted into a string
     */
    public String toString() {
        String eventString = "";
        eventString += "Name: " + this.getEventName() + ", " +
            "Description: " + this.getEventDescription() + ", " +
            "Owner: " + this.getOwnerEmail() + ", " +
            "Ranking: " + this.getRanking();
        return eventString;
    }
}
