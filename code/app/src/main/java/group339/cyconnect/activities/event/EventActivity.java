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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import group339.cyconnect.R;

import group339.cyconnect.activities.MenuUtil;
import group339.cyconnect.models.Event;
import group339.cyconnect.serverutil.ServerRequestUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * Main Activity for Events; displays list of this User's events and buttons to see all Events and to create a new Event
 */
public class EventActivity extends AppCompatActivity {
    //Button to get to CreateEventActivity
    Button createEvent;
    //Button to get to AllEventsActivity
    Button listAllEvents;
    //ListView of this User's Events
    ListView myEventsList;
    //Adapter for myEventsList
    private ArrayAdapter<String> myEventsAdapter;
    //List of this User's Events
    private ArrayList<String> myEventStringArray = new ArrayList<String>();
    //This User's email (username)
    private String appOwnerEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        //Set appOwnerEmail from SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        appOwnerEmail = prefs.getString("username", "");
        //Get buttons and set their OnClickListeners
        createEvent = (Button) findViewById(R.id.create_event);
        createEvent.setOnClickListener(createScreen);
        listAllEvents = (Button) findViewById(R.id.list_all_events);
        listAllEvents.setOnClickListener(viewScreen);
        //Set adapter and ListView for Events list
        myEventsAdapter = new ArrayAdapter<String>(this, R.layout.list_item_events, R.id.list_items_events_textview, myEventStringArray);
        myEventsList = (ListView) this.findViewById(R.id.listview_my_events);
        myEventsList.setAdapter(myEventsAdapter);
        //Set Listeners for items in the list of Events
        myEventsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String myEvent = myEventsAdapter.getItem(position);
                //Go to MyEventsActivity for the selected Event
                Intent myEventIntent = new Intent(getApplicationContext(), MyEventsActivity.class).putExtra("text", myEvent);
                startActivity(myEventIntent);
            }
        });
    }
    public void onStart() {
        super.onStart();
        final RequestQueue queue = Volley.newRequestQueue(this.getApplicationContext());
        //Get list of this User's events
        getMyEvents(queue);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_general, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get id of menu item selected and jump to its associated activity if it exists
        int id = item.getItemId();
        Intent intent = MenuUtil.getMenuIntent(EventActivity.this, id);
        if(intent != null) {
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //Listener for create button
    private OnClickListener createScreen= new OnClickListener() {
        @Override
        public void onClick(View v) {
            //Go to CreateEventActivity
            Intent createActivityIntent = new Intent(EventActivity.this,
                    CreateEventActivity.class);
            startActivity(createActivityIntent);
        }
    };
    //Listener for List Events button
    private OnClickListener viewScreen= new OnClickListener() {
        @Override
        public void onClick(View v) {
            //Go to AllEventsActivity
            Intent listEventsActivityIntent = new Intent(EventActivity.this,
                    AllEventsActivity.class);
            startActivity(listEventsActivityIntent);
        }
    };
    /**
     * Sends request for list of this User's Events
     * @param queue
     */
    private void getMyEvents(final RequestQueue queue) {
        //Map of parameters to send to the server
        Map<String, String> myEventMap = new HashMap<String, String>();
        myEventMap.put("type", "event");
        myEventMap.put("action", "get");
        myEventMap.put("id", appOwnerEmail);
        //Send a request to the server
        ServerRequestUtility.asyncCall(queue, myEventMap, responseListener, errorListener, "get");
    }
    //Listener for reaponse from the server when getting this User's events
    private Response.Listener responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            //Clear array before beginning
            myEventStringArray.clear();
            ArrayList<Event> eventsArray = new ArrayList<Event>();
            myEventStringArray.clear();
            try {
                //Convert response into JSONArray
                JSONArray returnedArray = new JSONArray(response);
                //For every Event in returnedArray, add an Event object to eventsArray
                for (int i = 0; i < returnedArray.length(); i++) {
                    JSONObject jsonObject = returnedArray.getJSONObject(i);
                    String eventName = jsonObject.getString("EventName");
                    String eventDescription = jsonObject.getString("EventDescription");
                    String ownerEmail = jsonObject.getString("OwnerEmail");
                    String ranking = jsonObject.getString("Ranking");
                    eventsArray.add(new Event(eventName, eventDescription, ownerEmail, ranking));
                }
                String eventString = "";
                //For every Event in eventsArray, place its String representation in myEventStringArray
                for (Event e : eventsArray) {
                    eventString = e.toString();
                    myEventStringArray.add(eventString);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Notify the adapter that myEventsStringArray has changed
            myEventsAdapter.notifyDataSetChanged();

        }
    };
    //Listener for error from server
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("volleyError", error.toString());
        }
    };
}