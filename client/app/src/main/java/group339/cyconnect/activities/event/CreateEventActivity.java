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
 * Class to handle the creating of a new Event.
 */
public class CreateEventActivity extends AppCompatActivity {
    //Field for entering new Event name
    EditText eventName;
    //Field for entering new Event description
    EditText eventDescription;
    //Button to send request to create new Event
    Button create;
    // Button to clear the fields
    Button cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        //Get volley RequestQueue
        final RequestQueue queue = Volley.newRequestQueue(this);
        //Get EditTexts and button
        eventName = (EditText) findViewById(R.id.event_title);
        eventDescription = (EditText) findViewById(R.id.event_description_text);
        create = (Button) findViewById(R.id.event_create);
        cancel = (Button) findViewById(R.id.event_cancel);
        cancel.setOnClickListener(cancelCreate);
        //Add OnClickListener to create button
        confirmCreate(create, queue);
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
        Intent intent = MenuUtil.getMenuIntent(CreateEventActivity.this, id);
        if(intent != null) {
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * Attaches OnClickListener to create button
     * @param create
     * @param queue
     */
    private void confirmCreate(Button create, final RequestQueue queue) {
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Map of parameters to be sent to the server
                Map<String, String> eventMap = createEventMap();
                //Call the server
                ServerRequestUtility.asyncCall(queue, eventMap, responseListener, errorListener, "add");
            }
        });
    }

    private View.OnClickListener cancelCreate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            eventName.setText("");
            eventDescription.setText("");
        }
    };

    /**
     * Response listener to take you to the EventActivity
     */
    private Response.Listener responseListener = new Response.Listener() {
        @Override
        public void onResponse(Object response) {
            //If creation is successful, go to EventActivity
            if (response.toString().equals("success")) {
                Intent eventIntent = new Intent(CreateEventActivity.this, EventActivity.class);
                startActivity(eventIntent);
            }
        }
    };

    /**
     * Standard error Listener that logs the error
     */
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("volleyError", error.toString());
        }
    };

    /**
     *
     * @return - the map that holds the data of all the event to create.
     */
    private Map<String, String> createEventMap() {
        Map<String, String> eventMap = new HashMap<String, String>();
        eventMap.put("type", "event");
        eventMap.put("action", "get");
        eventMap.put("name", eventName.getText().toString());
        eventMap.put("desc", eventDescription.getText().toString());
        Context context = MainActivity.getContextOfApplication();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String userName = prefs.getString("username", "");
        eventMap.put("owner", userName);
        eventMap.put("voting", "0");
        return eventMap;
    }
}