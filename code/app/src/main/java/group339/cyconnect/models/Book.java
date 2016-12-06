package group339.cyconnect.models;

/**
 * This is a model for the Book object.  It has all the fields that a book would have. To be used
 * for handling the responses from the server.
 * Created by apple on 10/11/15.
 */
public class Book {

    private String description;
    private String ISBN;
    private String bookName;
    private String owner;
    private String bookID;


    /**
     * Constructor for the book
     * @param bookName
     * @param ISBN
     * @param description
     * @param owner - the userName of who created the book
     * @param bookID - to be used for tracking on the server
     */
    public Book(String bookName, String ISBN, String description, String owner, String bookID) {
        this.bookName = bookName;
        this.ISBN = ISBN;
        this.description = description;
        this.owner = owner;
        this.bookID = bookID;

    }

    /**
     *
     * @return The name of the book
     */
    public String getBookName() {
        return this.bookName;
    }

    /**
     *
     * @return the ISBN of the book
     */
    public String getISBN() {
        return this.ISBN;
    }

    /**
     *
     * @return The description for the book
     */
    public String getDescription() {
        return this.description;
    }


    /**
     * The owner for the book
     * @return The owner for the book
     */
    public String getOwner() {
        return this.owner;
    }

    /**
     *
     * @return The bookID of the book
     */
    public String getBookID() {
        return this.bookID;
    }

    /**
     * Convert the book to a string, to be used for displaying in the listView
     * @return - the book converted into a string
     */
    public String toString() {
        String bookString = "";
        bookString += "Name: " + this.getBookName() + ", " +
                "ISBN: " + this.getISBN() + ", "+
                "Description: " + this.getDescription() + ", " +
                "Owner: " + this.getOwner() + ", " +
                "BookID: "+ this.getBookID();
        return bookString;
    }
}
