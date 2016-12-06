package group339.cyconnect.activities.group;

/**
 * Created by BORISNDOUTOUME on 11/4/15.
 */

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewGroupActivity extends AppCompatActivity {

    // String to store the information of the group selected
    private String thisGroupString = "";
    // String to store the group name
    private String groupName = "";
    // String to store the group description
    private String groupDescription = "";
    // String to store the group owner
    private String groupOwner = "";
    // String to store the group tag
    private String groupTag = "";
    // Button to edit a selected group
    private Button editButton;
    // Button to delete a selected group
    private Button deleteButton;
    // Edit text field to change the name of a group
    private EditText groupNameText;
    // Edit text field to change the description of a group
    private EditText groupDescriptionText;
    // TextView to display the owner of a group
    private TextView groupOwnerText;
    // String to store the username of the current user
    private String appOwnerEmail;
    // String to store the group id
    private String groupID;
    // Variable to store the request queue
    private RequestQueue queue;

    @Override
    // Method to setup the layout of this activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group);
        Context context = MainActivity.getContextOfApplication();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        appOwnerEmail = prefs.getString("username", "");
        queue = Volley.newRequestQueue(this);

        if (this.getIntent().hasExtra("text")) {
            thisGroupString = this.getIntent().getStringExtra("text");
            Log.d("GROUP STRING", thisGroupString);
            readGroupString(thisGroupString);
        }

        deleteButton = (Button) findViewById(R.id.delete_group_button);
        delete(deleteButton, queue);
        editButton = (Button) findViewById(R.id.edit_group_button);
        edit(editButton, queue);

        groupNameText = (EditText) findViewById(R.id.group_name_text);
        groupNameText.setText(groupName);

        groupDescriptionText = (EditText) findViewById(R.id.group_description_text);
        groupDescriptionText.setText(groupDescription);

        groupOwnerText = (TextView) findViewById(R.id.group_owner_text);
        groupOwnerText.setText(groupOwner);

        Button chatButton = (Button) findViewById(R.id.chat_button);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChat();
            }
        });
    }

    //Send request to server for list of members in group and check if current user is one.
    private void goToChat(){
        Map<String, String> memberMap = new HashMap<String, String>();
        memberMap.put("type", "members");
        memberMap.put("action", "get");
        memberMap.put("id", groupID);
        ServerRequestUtility.asyncCall(queue, memberMap,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ArrayList<String> memberArray = new ArrayList<String>();
                        try {
                            JSONArray returnedArray = new JSONArray(response);
                            for (int i = 0; i < returnedArray.length(); i++) {
                                JSONObject jsonObject = returnedArray.getJSONObject(i);
                                Log.d("JSON OBJECT", jsonObject.toString());
                                memberArray.add(jsonObject.getString("MemberEmail"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(memberArray.contains(appOwnerEmail)){
                            Intent chatIntent = new Intent(MainActivity.getContextOfApplication(), ChatActivity.class).putExtra("id", groupID)
                                    .putExtra("name", groupName);
                            startActivity(chatIntent);
                        }
                    }
                },
                errorListener, "multiAdd");
    }

    @Override
    // Inflate the menu; this adds items to the action bar if it is present.
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_general, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent = MenuUtil.getMenuIntent(ViewGroupActivity.this, id);

        if(intent != null) {
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Method to derive the various fields of a group
    private void readGroupString(String groupString) {
        Log.d("GroupString: ", groupString);
        groupName = ServerRequestUtility.substringBetween(groupString, "GroupName: ", ", Description");
        groupOwner = ServerRequestUtility.substringBetween(groupString, "Owner: ", ", GroupTag");
        groupTag = ServerRequestUtility.substringBetween(groupString, "GroupTag: ", ", GroupID");
        groupID = groupString.substring(groupString.indexOf("GroupID")+ 9);
        groupDescription = ServerRequestUtility.substringBetween(groupString, "Description: ", ", Owner");
    }

    //Response listener for making server calls
    private Response.Listener responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            if (response.toString().equals("success")) {
                Intent groupIntent = new Intent(ViewGroupActivity.this, GroupActivity.class);
                startActivity(groupIntent);
            }
        }
    };

    //Error listener for making request calls
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("VolleyError", error.toString());
        }
    };

    // Method to delete a group
    private void delete(final Button delete, final RequestQueue queue) {
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appOwnerEmail.equals(groupOwner) || appOwnerEmail.equals("admin@iastate.edu")) {
                    Map<String, String> groupMap = new HashMap<String, String>();
                    groupMap.put("type", "group");
                    groupMap.put("id", groupID);
                    ServerRequestUtility.asyncCall(queue, groupMap, responseListener, errorListener, "delete");
                }
            }
        });
    }

    // Method to delete a group
    private void edit(Button edit, final RequestQueue queue) {
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appOwnerEmail.equals(groupOwner) || groupOwner.equals("admin@iastate.edu")) {
                    Map<String, String> groupMap = new HashMap<String, String>();
                    groupMap.put("type", "group");
                    groupMap.put("id", groupID);
                    groupMap.put("name", groupNameText.getText().toString());
                    groupMap.put("desc", groupDescriptionText.getText().toString());
                    groupMap.put("tag", groupTag);
                    ServerRequestUtility.asyncCall(queue, groupMap, responseListener, errorListener, "edit");
                }
            }
        });
    }
}
