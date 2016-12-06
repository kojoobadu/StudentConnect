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

public class MyGroupsActivity extends AppCompatActivity {

    // Text view to display information about the group
    TextView longGroupString;
    // String to store information about the group
    private String thisGroupString;
    // Button to invite other users to the group
    Button inviteButton;
    // Edit text field to enter username of the user to invite
    EditText invitedUserText;
    // Edit text field to enter invite message
    EditText inviteMessage;
    // String to store group id
    String groupID = "";
    //String to store username from shared preferences
    private String appOwnerEmail;
    // String to store the group name
    private String groupName;
    // String to store the request queue
    private RequestQueue queue;


    @Override
    // Method to set layout of MyGroupsActivity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);
        Context context = MainActivity.getContextOfApplication();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        appOwnerEmail = prefs.getString("username", "");
        if (this.getIntent().hasExtra("text")) {
            thisGroupString = this.getIntent().getStringExtra("text");
            groupID = thisGroupString.substring(thisGroupString.indexOf("GroupID: ") + 9);
        }

        longGroupString = (TextView) findViewById(R.id.long_group_string);
        longGroupString.setText(thisGroupString);
        inviteButton = (Button) findViewById(R.id.invite_user_button);
        invitedUserText = (EditText) findViewById(R.id.invited_user_result_2);
        inviteMessage = (EditText) findViewById(R.id.invite_message);
        queue = Volley.newRequestQueue(this);
        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invite(queue);
            }
        });
        groupName = ServerRequestUtility.substringBetween(thisGroupString, "GroupName: ", ", Description");
        Button chatButton = (Button) findViewById(R.id.my_chat_button);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChat();
            }
        });
    }

    private void goToChat(){
        //Send request to server for list of members in group and check if current user is one.
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
                        if (memberArray.contains(appOwnerEmail)) {
                            Intent chatIntent = new Intent(MainActivity.getContextOfApplication(), ChatActivity.class).putExtra("id", groupID)
                                    .putExtra("name", groupName);
                            startActivity(chatIntent);
                        }
                    }
                },
                errorListener, "multiAdd");
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

        Intent intent = MenuUtil.getMenuIntent(MyGroupsActivity.this, id);

        if(intent != null) {
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Method to invite user to a group
    private void invite(final RequestQueue queue) {
        Map<String, String> inviteMap = new HashMap<String, String>();
        inviteMap.put("id", invitedUserText.getText().toString());
        inviteMap.put("text", inviteMessage.getText().toString());
        inviteMap.put("type", "notification");
        inviteMap.put("tag", "GroupInvite");
        inviteMap.put("identifier", groupID); // Identifier is extra information so for group it's groupID
        inviteMap.put("sender",appOwnerEmail);
        ServerRequestUtility.asyncCall(queue, inviteMap, responseListener, errorListener, "add");
    }

    // Response listener for server calls
    private Response.Listener responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.d("Response: ", response);
        }
    };

    // Error listener for server calls
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("VolleyError", error.toString());
        }
    };

}

