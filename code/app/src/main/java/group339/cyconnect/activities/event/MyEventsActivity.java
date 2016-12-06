package group339.cyconnect.activities.event;

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
import group339.cyconnect.activities.MenuUtil;
import group339.cyconnect.serverutil.ServerRequestUtility;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a class to be used for handling all of your events.  It is the activity that displays your individual event after you have clicked on it.
 * Here is where the owner will edit and delete his or her events.
 */
public class MyEventsActivity extends AppCompatActivity {

    private String thisEventString = "";
    private String eventName = "";
    private String eventDescription = "";
    private String eventOwner = "";
    private String eventRanking = "";
    private String oldName = "";
    private Button editButton;
    private Button deleteButton;
    private String appOwnerEmail;
    private EditText eventNameText;
    private EditText eventDescriptionText;
    private TextView eventRankingText;
    private TextView eventOwnerText;

    // Create the screen
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // Get the current logged in user, also the owner of the event.
        appOwnerEmail = prefs.getString("username", "");
        // instantiate the button to be clicked when you want to edit.
        editButton = (Button) findViewById(R.id.edit_my_event_button);
        // instantiate the button to be clicked when you want to delete.
        deleteButton = (Button) findViewById(R.id.delete_my_event_button);
        // This fills the screen with the current event after having clicked on it in the listView of MyEvents
        if(this.getIntent().hasExtra("text")){
            thisEventString = this.getIntent().getStringExtra("text");
            readEventString(thisEventString);
        }
        // Current name of the app, required for sending during the edit call
        oldName = eventName;
        // UI fields to hold the event data as well as tell the user what they are reading.
        eventNameText = (EditText) findViewById(R.id.edit_my_event_name);
        eventNameText.setText(eventName);
        eventDescriptionText = (EditText) findViewById(R.id.my_event_description_text);
        eventDescriptionText.setText(eventDescription);
        eventOwnerText = (TextView) findViewById(R.id.my_event_owner_text);
        eventOwnerText.setText(eventOwner);
        eventRankingText = (TextView) findViewById(R.id.my_event_ranking_text);
        eventRankingText.setText(eventRanking);
    }

    // When the screen is created set up the server calls here,
    // this helps with lagginess.
    public void onStart() {
        super.onStart();
        final RequestQueue queue = Volley.newRequestQueue(this);
        edit(editButton, queue);
        delete(deleteButton, queue);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Use the general menu button for better movement
        getMenuInflater().inflate(R.menu.menu_general, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        // Setup the button listeners in the menu item.
        Intent intent = MenuUtil.getMenuIntent(MyEventsActivity.this, id);

        if(intent != null) {
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A method used to set all the necessary instance variables for this particular event
     * @param eventString - full event object as a string
     */
    private void readEventString(String eventString) {
        eventName = ServerRequestUtility.substringBetween(eventString, "Name: ", ", Description");
        eventDescription = ServerRequestUtility.substringBetween(eventString, "Description: ", ", Owner");
        eventOwner = ServerRequestUtility.substringBetween(eventString, "Owner: ", ", Ranking");
        eventRanking = thisEventString.substring(eventString.indexOf("Ranking") + 9);
    }

    /**
     * Method to handle the editing
     * @param edit - button to apply the listener to
     * @param queue - queue that is used for the server request
     */
    private void edit(Button edit, final RequestQueue queue) {
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // only edit if the logged in user owns the event
                if (appOwnerEmail.equals(eventOwner) || appOwnerEmail.equals("admin@iastate.edu")) {
                    // Map to be sent to the server to tell it what to edit
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

    // take you to the event page if the response is a success, better flow for the user
    private Response.Listener responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            if (response.toString().equals("success")) {
                Intent eventIntent = new Intent(MyEventsActivity.this, EventActivity.class);
                startActivity(eventIntent);
            }
            Log.d("Response: ", response);
        }
    };

    // Log the volley error if we get one.
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("VolleyError", error.toString());
        }
    };

    /**
     * Method to handle the deleting
     * @param delete - button to apply the listener to
     * @param queue - queue that is used for the server request
     */
    private void delete(final Button delete, final RequestQueue queue) {
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // only delete if the logged in user owns the event
                if (appOwnerEmail.equals(eventOwner)) {
                    // Map to be sent to the server to tell it what to delete
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
