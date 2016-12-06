package group339.cyconnect.models;

/**
 * This is a model for the comment object.  It has all the fields that a comment would have. To be used
 * for handling the responses from the server.
 * Created by apple on 10/11/15.
 */
public class Comment {

    private String comment;

    /**
     * Constructor for a comment
     * @param comment - string that is the comment
     */
    public Comment(String comment) {
        this.comment = comment;
    }
}
