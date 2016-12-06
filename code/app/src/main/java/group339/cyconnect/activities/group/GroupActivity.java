package group339.cyconnect.activities.group;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import group339.cyconnect.models.Group;
import group339.cyconnect.serverutil.ServerRequestUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupActivity extends AppCompatActivity {

    Button createGroup;
    Button listAllGroups;
    ListView myGroupsList;
    private String appOwnerEmail;
    private ArrayAdapter<String> myGroupsAdapter;
    private ArrayList<String> myGroupStringArray;

    /**
     * Set all the instance variables on the creation as well as the button listeners, all UI stuff here
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        appOwnerEmail = prefs.getString("username", "");
        final RequestQueue queue = Volley.newRequestQueue(this);
        createGroup = (Button) findViewById(R.id.create_group);
        createGroup.setOnClickListener(createScreen);
        listAllGroups = (Button) findViewById(R.id.list_all_groups);
        listAllGroups.setOnClickListener(viewScreen);
        myGroupStringArray = new ArrayList<String>();
        myGroupsAdapter = new ArrayAdapter<String>(this, R.layout.list_item_groups, R.id.list_items_groups_textview, myGroupStringArray);
        myGroupsList = (ListView) findViewById(R.id.listview_my_groups);
        myGroupsList.setAdapter(myGroupsAdapter);
        myGroupsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String myGroup = myGroupsAdapter.getItem(position);
                Intent myGroupIntent = new Intent(getApplicationContext(), MyGroupsActivity.class).putExtra("text", myGroup);
                startActivity(myGroupIntent);
            }
        });
    }

    /**
     * At the start of the view grab all the groups that I a member of.
     */
    public void onStart() {
        super.onStart();
        final RequestQueue queue = Volley.newRequestQueue(this.getApplicationContext());
        getMyGroups(queue);
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
     * Handles where to go on each menu item click
     * @param item - item that has been clicked
     * @return - that the menu click worked and took to new screen
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Intent intent = MenuUtil.getMenuIntent(GroupActivity.this, id);

        if(intent != null) {
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Pseudo constructor for the viewScreen listener. Takes you to
     * the view all Groups screen
     */
    private View.OnClickListener viewScreen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent listGroupsActivityIntent = new Intent(GroupActivity.this, AllGroupsActivity.class)    ;
            startActivity(listGroupsActivityIntent);
        }
    };

    /**
     * Pseudo constructor for the CreateScreen listener. Takes you to
     * the Create a Group screen
     */
    private View.OnClickListener createScreen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent createGroupIntent = new Intent(GroupActivity.this,
                    CreateGroupActivity.class);
            startActivity(createGroupIntent);
        }
    };

    /**
     * Builds the hashMap to get the groups the logged in user is a member of
     * @param queue - what the call is added to
     */
    private void getMyGroups(final RequestQueue queue) {
        Map<String, String> myGroupMap = new HashMap<String, String>();
        myGroupMap.put("type", "members");
        myGroupMap.put("action", "get");
        myGroupMap.put("email", appOwnerEmail);
        ServerRequestUtility.asyncCall(queue, myGroupMap, responseListener, errorListener, "addMulti");
    }

    /**
     * Fill the array with all the groups you are a member of.  Handles the response after calling to get all the
     * groups you are a member of.
     */
    private Response.Listener responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            ArrayList<Group> groupsArray = new ArrayList<Group>();
            try {
                JSONArray returnedArrary = new JSONArray(response);
                for (int i = 0; i < returnedArrary.length(); i++) {
                    JSONObject jsonObject = returnedArrary.getJSONObject(i);
                    String groupName = jsonObject.getString("GroupName");
                    String groupDescription = jsonObject.getString("Description");
                    String ownerEmail = jsonObject.getString("GroupOwnerEmail");
                    String groupID = jsonObject.getString("GroupID");

                    groupsArray.add(new Group(groupName,groupDescription, ownerEmail, groupID));

                    String groupString = "";
                    for (Group g : groupsArray) {
                        groupString = g.toString();
                        myGroupStringArray.add(groupString);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            myGroupsAdapter.notifyDataSetChanged();
        }
    };

    /**
     * Standard error listener
     */
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("volleyError", error.toString());
        }
    };



}
