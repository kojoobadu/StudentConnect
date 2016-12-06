package group339.cyconnect.activities.book;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import group339.cyconnect.R;
import group339.cyconnect.activities.MainActivity;

import group339.cyconnect.activities.MenuUtil;
import group339.cyconnect.serverutil.ServerRequestUtility;

import java.util.HashMap;
import java.util.Map;
/**
 * Activity for viewing a Book's information
 */
public class ViewBookActivity extends AppCompatActivity {
    //String sent from ListView to this activity
    private String thisBookString = "";
    //Name of this Book
    private String bookName = "";
    //Description for this Book
    private String bookDescription = "";
    //Owner of this Book
    private String bookOwner = "";
    //ISBN of this Book
    private String ISBN;
    //ID of this Book
    private String bookID;
    //Button to edit this Book
    private Button editButton;
    //Button to delete this Book
    private Button deleteButton;
    //Field to edit this Book's name
    private EditText bookNameText;
    //Field to edit this Book's description
    private EditText bookDescriptionText;
    //Display of this Book's owner
    private TextView bookOwnerText;
    //Email(username) of this User
    private String appOwnerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_book);
        Context context = MainActivity.getContextOfApplication();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        appOwnerEmail = prefs.getString("username", "");
        //Get volley RequestQueue
        final RequestQueue queue = Volley.newRequestQueue(this);
        if(this.getIntent().hasExtra("text")){
            //Get String sent from previous activity
            thisBookString = this.getIntent().getStringExtra("text");
            //Set Strings according to thisBookString
            readBookString(thisBookString);
        }
        //Get edit button and set its OnClickListener
        editButton = (Button) findViewById(R.id.edit_book_button);
        edit(editButton, queue);
        //Get delete button and set its OnClickListener
        deleteButton = (Button) findViewById(R.id.delete_book_button);
        delete(deleteButton, queue);
        //Get display fields and set them according to this Activity's Strings
        bookNameText = (EditText) findViewById(R.id.book_name_text);
        bookNameText.setText(bookName);
        bookDescriptionText = (EditText) findViewById(R.id.book_description_text);
        bookDescriptionText.setText(bookDescription);
        bookOwnerText = (TextView) findViewById(R.id.book_owner_text);
        bookOwnerText.setText(bookOwner);
        //Get comment button and set its onClickListener
        Button commentButton = (Button) findViewById(R.id.chat_book_comment);
        commentButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //Go to CommentActivity
                Intent createCommentIntent = new Intent(ViewBookActivity.this,
                        CommentActivity.class).putExtra("id", bookID).putExtra("name",bookNameText.getText().toString()).putExtra("ownerEmail",bookOwnerText.getText().toString());
                startActivity(createCommentIntent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_general, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent = MenuUtil.getMenuIntent(ViewBookActivity.this, id);

        if(intent != null) {
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /**
     * Parses String sent to this Activity and sets the String fields from it
     * @param bookString
     */
    private void readBookString(String bookString) {
        bookName = ServerRequestUtility.substringBetween(bookString, "Name: ", ", ISBN");
        bookDescription = ServerRequestUtility.substringBetween(bookString, "Description: ", ", Owner");
        bookOwner = ServerRequestUtility.substringBetween(bookString, "Owner: ", ", BookID");
        ISBN = ServerRequestUtility.substringBetween(bookString, "ISBN: ", ", Description");
        bookID = bookString.substring(bookString.indexOf("BookID:") + 8);
    }
    /**
     * Sets the OnClickListener for the edit button
     * @param edit
     * @param queue
     */
    private void edit(Button edit, final RequestQueue queue) {
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // only edit if the logged in user owns the event
                if (appOwnerEmail.equals(bookOwner) || appOwnerEmail.equals("admin@iastate.edu")) {
                    //Map of parameters to be sent to the server
                    Map<String, String> eventMap = new HashMap<String, String>();
                    eventMap.put("isbn", ISBN);
                    eventMap.put("type", "book");
                    eventMap.put("name", bookNameText.getText().toString());
                    eventMap.put("desc", bookDescriptionText.getText().toString());
                    eventMap.put("owner", bookOwner);
                    eventMap.put("id", bookID);
                    //Send request to the server
                    ServerRequestUtility.asyncCall(queue, eventMap, responseListener, errorListener, "edit");
                }
            }
        });
    }
    //Listener for response from the server when both editing and deleting
    private Response.Listener responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            //If action was a success, go to BookActivity
            if (response.toString().equals("success")) {
                Intent bookIntent = new Intent(ViewBookActivity.this, BookActivity.class);
                startActivity(bookIntent);
            }
        }
    };
    //Listener for error response from the server when both editing and deleting
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("VolleyError", error.toString());
        }
    };
    /**
     * Attach OnClikListener to delete button
     * @param delete
     * @param queue
     */
    private void delete(final Button delete, final RequestQueue queue) {
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // only delete if the logged in user owns the event
                if (appOwnerEmail.equals(bookOwner) || appOwnerEmail.equals("admin@iastate.edu")) {
                    //Map of parameters to send to the server
                    Map<String, String> eventMap = new HashMap<String, String>();
                    eventMap.put("type", "book");
                    eventMap.put("id", bookID);
                    //Send request to the server
                    ServerRequestUtility.asyncCall(queue, eventMap, responseListener, errorListener, "delete");
                }
            }
        });
    }
}