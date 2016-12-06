package group339.cyconnect.activities.group;

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
 * Class to be used for creating a group. Handles sending the created object to the server.
 */
public class CreateGroupActivity extends AppCompatActivity {
    // UI Elements for the class
    EditText groupName;
    EditText groupDescription;
    Button create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_);
        final RequestQueue queue = Volley.newRequestQueue(this);
        groupName = (EditText) findViewById(R.id.enter_group_name);
        groupDescription = (EditText) findViewById(R.id.group_description);
        create = (Button) findViewById(R.id.saveButton);
        createListener(create, queue);
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

        Intent intent = MenuUtil.getMenuIntent(CreateGroupActivity.this, id);

        if(intent != null) {
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * The listener that is used for setting the button listener on the
     * create button as well as making the add call
     * @param create - button to set the listener on
     * @param queue - queue to add the call to
     */
    private void createListener(Button create, final RequestQueue queue) {
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> groupMap = buildGroupMap();
                ServerRequestUtility.asyncCall(queue, groupMap, responseListener, errorListener, "add");
            }
        });
    }

    /**
     * Build the map for the createdGroup
     * @return - the createdGroup
     */
    private Map<String, String> buildGroupMap() {
        Map<String, String> groupMap = new HashMap<String, String>();
        groupMap.put("type", "group");
        groupMap.put("name", groupName.getText().toString());
        groupMap.put("desc", groupDescription.getText().toString());
        groupMap.put("tag", "some tag");
        Context context = MainActivity.getContextOfApplication();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String userName = prefs.getString("username", "");
        groupMap.put("owner", userName);
        return groupMap;
    }

    /**
     * If the response is a success, take user back to the GroupActivity screen
     */
    private Response.Listener responseListener = new Response.Listener() {
        @Override
        public void onResponse(Object response) {
            if (response.toString().equals("success")) {
                Intent groupIntent = new Intent(CreateGroupActivity.this, GroupActivity.class);
                startActivity(groupIntent);
            }
        }
    };

    /**
     * Generic errorListener, if log the error.
     */
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("volleyError", error.toString());
        }
    };
}
