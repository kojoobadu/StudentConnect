package group339.cyconnect.activities.group;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import group339.cyconnect.R;
import group339.cyconnect.activities.MainActivity;
import group339.cyconnect.serverutil.ServerRequestUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * A placeholder fragment containing a ListView of Comments on a Group
 */
public class ChatActivityFragment extends Fragment {
    //Adapter for ListView of GroupComments for this Group
    private ArrayAdapter<String> chatAdapter;
    //List of GroupComments for this Group
    private ArrayList<String> chatStringArray= new ArrayList<String>();
    //Volley RequestQueue
    private RequestQueue queue;
    /**
     * Required blank constructor
     */
    public ChatActivityFragment() {}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        //Set adapter and tie it to the ListView
        chatAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_chat, R.id.list_items_chat_textview, chatStringArray);
        ListView chatList = (ListView) rootView.findViewById(R.id.listviewchat);
        chatList.setAdapter(chatAdapter);
        //Get Comment button and add OnClickListener
        Button commentButton = (Button) rootView.findViewById(R.id.comment_button);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog to send comment to server
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Message");
                //Set up EditText for inputting message and add it to the dialog
                final EditText message = new EditText(getActivity());
                message.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(message);
                //On OK, send comment to the server
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String messageString = message.getText().toString();
                        //Send comment to the server and notify other Group members
                        addMessage(messageString);
                    }
                });
                //On Cancel, return to ChatActivity
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                //Show the dialog
                builder.show();
            }
        });
        //Get Refresh button and set OnClickListener
        Button refreshButton = (Button) rootView.findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listChatMessages(queue);
            }
        });
        return rootView;
    }
    /**
     * Sends a GroupComment to the server, and then sends Notifications to everyone in the Group commented on
     * @param msg
     */
    private void addMessage(String msg) {
        //Map of parameters to send to the server
        Map<String, String> messageMap = new HashMap<String, String>();
        messageMap.put("type", "groupcomment");
        messageMap.put("text", msg);
        Context context = MainActivity.getContextOfApplication();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String userName = prefs.getString("username", "");
        messageMap.put("poster", userName);
        messageMap.put("id", getActivity().getIntent().getStringExtra("id"));
        //Send request to add GroupComment to the server
        ServerRequestUtility.asyncCall(queue, messageMap,
                //Listener for response from server
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If the comment was added successfully, send notifications to all users in group
                        if(response.equals("success")){
                            sendNotifications();
                        }
                        //Refresh the list of GroupComments
                        listChatMessages(queue);
                    }
                },
                //Listener for error from server
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log error
                        Log.e("volleyError", error.toString());
                    }
                },
                "add");
    }
    public void onStart() {
        super.onStart();
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        //Get the list of GroupComments
        listChatMessages(queue);
    }
    /**
     * Gets the list of GroupComments for this Group from the server
     * @param queue
     */
    private void listChatMessages (final RequestQueue queue){
        //Map of parameters to be sent to the server
        Map<String, String> chatMap = new HashMap<String, String>();
        chatMap.put("type", "groupcomment");
        chatMap.put("id", getActivity().getIntent().getStringExtra("id"));
        //Send request to the server
        ServerRequestUtility.asyncCall(queue, chatMap, responseListener, errorListener, "get");
    }
    //Listener for response from server when getting GroupComments (listChatMessages)
    private Response.Listener responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            //Clear the array before adding to it
            chatStringArray.clear();
            try {
                //Convert response into a JSONArray
                JSONArray returnedArray = new JSONArray(response);
                //For every item in returnedArray, add it to the chatStringArray
                for (int i = 0; i < returnedArray.length(); i++) {
                    JSONObject jsonObject = returnedArray.getJSONObject(i);
                    Log.d("JSON OBJECT", jsonObject.toString());
                    String text = jsonObject.getString("CommentText");
                    String username = jsonObject.getString("Poster");
                    chatStringArray.add(username + ":\n" + text);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Notify the chatAdapter that chatStringArray has been altered
            chatAdapter.notifyDataSetChanged();
        }
    };
    /**
     * Sends notifications to everyone in this Group that a new comment has been posted
     */
    private void sendNotifications(){
        //Map of parameters to be sent to server
        Map<String, String> memberMap = new HashMap<String, String>();
        memberMap.put("type", "members");
        memberMap.put("action", "get");
        memberMap.put("id", getActivity().getIntent().getStringExtra("id"));
        //Send request to server for list of members in this Group
        ServerRequestUtility.asyncCall(queue, memberMap,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //List of usernames of every member of this Group
                        ArrayList<String> memberArray = new ArrayList<String>();
                        try {
                            //Convert response to a JSONArray
                            JSONArray returnedArray = new JSONArray(response);
                            //Add users to the memberArray
                            for (int i = 0; i < returnedArray.length(); i++) {
                                JSONObject jsonObject = returnedArray.getJSONObject(i);
                                Log.d("JSON OBJECT", jsonObject.toString());
                                memberArray.add(jsonObject.getString("MemberEmail"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //send notifications
                        Map<String, String> notifyMap = new HashMap<String, String>();
                        notifyMap.put("type", "notification");
                        Context context = MainActivity.getContextOfApplication();
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                        String userName = prefs.getString("username", "");
                        notifyMap.put("text", userName + " commented in group " + getActivity().getIntent().getStringExtra("name"));
                        notifyMap.put("identifier", getActivity().getIntent().getStringExtra("id"));
                        notifyMap.put("tag", "GroupComment");
                        notifyMap.put("sender", userName);
                        //For every member in this Group excluding the current User, send a request to the server to add a notification for them
                        for(String user : memberArray){
                            if(!user.equals(userName)) {
                                notifyMap.put("id", user);
                                ServerRequestUtility.asyncCall(queue, notifyMap,
                                        //Listener for response from the server
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {}
                                        },
                                        errorListener, "add");
                            }
                        }
                    }
                },
                errorListener, "multiAdd");
    }
    //Listener for error from the server when getting GroupComments, members in a group, and sending notifications to Group members
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            //Log the error
            Log.e("volleyError", error.toString());
        }
    };
}