package group339.cyconnect.activities.event;

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
 * View a single event, if you are the admin have the option to edit and delete, all other users can only vote on the event
 */
public class ViewEventActivity extends AppCompatActivity {

    // instance variables
    private String thisEventString = "";
    private String eventName = "";
    private String eventDescription = "";
    private String eventOwner = "";
    private String eventRanking = "";
    private String oldName = "";
    private Button editButton;
    private Button deleteButton;
    private Button upVoteButton;
    private Button downVoteButton;
    private String appOwnerEmail;
    private EditText eventNameText;
    private EditText eventDescriptionText;
    private TextView eventRankingText;
    private TextView eventOwnerText;


    // create the view
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);
        Context context = MainActivity.getContextOfApplication();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        appOwnerEmail = prefs.getString("username", "");
        editButton = (Button) findViewById(R.id.edit_event_button);
        // hide the edit and delete buttons by default
        editButton.setVisibility(View.GONE);
        deleteButton = (Button) findViewById(R.id.delete_event_button);
        deleteButton.setVisibility(View.GONE);
        // if the appOwner is the admin or the eventOwner show the edit and delete options
        if (appOwnerEmail.equals(eventOwner) || appOwnerEmail.equals("admin@iastate.edu")) {
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
        }
        final RequestQueue queue = Volley.newRequestQueue(this);
        if(this.getIntent().hasExtra("text")){
            thisEventString = this.getIntent().getStringExtra("text");
            readEventString(thisEventString);
        }
        oldName = eventName;
        // Listener for the edit and delete, will only be used if the logged in user is the admin
        edit(editButton, queue);
        delete(deleteButton, queue);
        upVoteButton = (Button) findViewById(R.id.up_vote_button);
        downVoteButton = (Button) findViewById(R.id.down_vote_button);
        // create the listeners for the two different types of voting
        vote(downVoteButton, queue);
        vote(upVoteButton, queue);
        // Set the UI fields
        eventNameText = (EditText) findViewById(R.id.event_name_text);
        eventNameText.setText(eventName);
        eventDescriptionText = (EditText) findViewById(R.id.event_description_text);
        eventDescriptionText.setText(eventDescription);
        eventOwnerText = (TextView) findViewById(R.id.event_owner_text);
        eventOwnerText.setText(eventOwner);
        eventRankingText = (TextView) findViewById(R.id.event_ranking_text);
        eventRankingText.setText(eventRanking);

    }

    /**
     * Set the menu bar
      * @param menu - menu to pass in
     * @return that the menu works and exists
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_general, menu);
        return true;
    }

    /**
     * listens for which menu item has been clicked
     * @param item - the item that has been clicked on and what to do with it
     * @return true that everything worked
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent = MenuUtil.getMenuIntent(ViewEventActivity.this, id);

        if(intent != null) {
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * a method that is used to setup the edit call and button listener all together
     * @param edit - button that the listener is added to
     * @param queue - queue to be used for calling the server
     */
    private void edit(Button edit, final RequestQueue queue) {
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // only edit if the appoOwner is the admin or the owner of the event
                if (appOwnerEmail.equals(eventOwner) || appOwnerEmail.equals("admin@iastate.edu")) {
                    Map<String, String> eventMap = new HashMap<String, String>();
                    eventMap.put("oldname", oldName);
                    eventMap.put("type", "event");
                    eventMap.put("name", eventNameText.getText().toString());
                    eventMap.put("desc", eventDescriptionText.getText().toString());
                    eventMap.put("owner", eventOwner);
                    ServerRequestUtility.asyncCall(queue, eventMap, responseListener, errorListener, "edit");
                }
            }
        });

    }

    // If the response is a success, go back to the list of all events, lets the user know what they did worked.
    private Response.Listener responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            if (response.toString().equals("success")) {
                Intent eventIntent = new Intent(ViewEventActivity.this, AllEventsActivity.class);
                startActivity(eventIntent);
            }
            Log.d("Response: ", response);
        }
    };

    // If the response contains an error log the volley error
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("VolleyError", error.toString());
        }
    };

    /**
     * sets all the private string variables to their necessary data based on what came from the
     * read.
     * @param eventString the event that we are currently viewing represented as a string.
     */
    private void readEventString(String eventString) {
        eventName = ServerRequestUtility.substringBetween(eventString, "Name: ", ", Description");
        eventDescription = ServerRequestUtility.substringBetween(eventString, "Description: ", ", Owner");
        eventOwner = ServerRequestUtility.substringBetween(eventString, "Owner: ", ", Ranking");
        eventRanking = eventString.substring(eventString.indexOf("Ranking") + 9);
    }

    /**
     * Method for handling the type of vote that we are doing.  Increase/decreases the ranking by 1 based on which button is passed in
     * Anyone can vote on an event
     * @param vote - the button that is passed in
     * @param queue - the queue to be used for the server call
     */
    private void vote(final Button vote, final RequestQueue queue) {
        vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> eventMap = new HashMap<String, String>();
                eventMap.put("type", "event");
                eventMap.put("name", eventName);
                eventMap.put("desc", eventDescription);
                eventMap.put("owner", eventOwner);
                int ranking = Integer.parseInt(eventRanking);
                if (vote.getId() == R.id.up_vote_button) {
                    ranking++;
                } else if (vote.getId() == R.id.down_vote_button) {
                    ranking--;
                }

                eventMap.put("voting", ranking + "");
                ServerRequestUtility.asyncCall(queue, eventMap, responseListener, errorListener, "edit");
            }
        });
    }

    /**
     * Method for handling the deleting of an event, can only be done as the admin or eventOwner
     * @param delete - button to handle the deleting of the event, set the listener.
     * @param queue - the queue to be used for the server call
     */
    private void delete(final Button delete, final RequestQueue queue) {
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // only delete if the logged in user owns the event
                if (appOwnerEmail.equals(eventOwner) || appOwnerEmail.equals("admin@iastate.edu")) {
                    Map<String, String> eventMap = new HashMap<String, String>();
                    eventMap.put("type", "event");
                    eventMap.put("name", eventName);
                    eventMap.put("owner", eventOwner);
                    ServerRequestUtility.asyncCall(queue, eventMap, responseListener, errorListener, "delete");
                }
            }
        });
    }
}
