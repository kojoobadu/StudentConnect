package group339.cyconnect.activities.book;

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
 * Lists all comments on a book and allows a User to add a comment
 */
public class CommentActivityFragment extends Fragment {
    //Adapter for list of comments
    private ArrayAdapter<String> chatAdapter;
    //Array of comments
    private ArrayList<String> chatStringArray= new ArrayList<String>();
    //Volley RequestQueue
    private RequestQueue queue;
    /**
     * Required blank constructor
     */
    public CommentActivityFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Create the view for the UI.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate layout file
        final View rootView = inflater.inflate(R.layout.fragment_comment, container, false);
        //Set adapter and ListView
        chatAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_comment_book, R.id.list_items_chat_textview_book, chatStringArray);
        ListView chatList = (ListView) rootView.findViewById(R.id.listviewcomment);
        chatList.setAdapter(chatAdapter);
        //Add listeners to buttons
        Button commentButton = (Button) rootView.findViewById(R.id.comment_button_book);
        setCommentButtonListener(commentButton);
        //Button for refreshing comments
        Button refreshButton = (Button) rootView.findViewById(R.id.refresh_button_book);
        setRefreshListener(refreshButton);
        return rootView;
    }

    /**
     * Method to send the comment to the server
     * @param msg - string to be added to the server as a comment
     */
    private void addMessage(String msg) {
        Context context = MainActivity.getContextOfApplication();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String userName = prefs.getString("username", "");
        Map<String, String> messageMap = new HashMap<String, String>();
        messageMap.put("type", "bookcomment");
        messageMap.put("text", msg);
        messageMap.put("poster", userName);
        messageMap.put("id", getActivity().getIntent().getStringExtra("id"));
        ServerRequestUtility.asyncCall(queue, messageMap, notificationsResponse, errorListener, "add");
    }

    /**
     * When the screen starts populate the view with the current comments
     */
    public void onStart() {
        super.onStart();
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        listChatMessages(queue);
    }

    /**
     * list all of the current comments for this book
     * @param queue - to be used for the server call
     */
    private void listChatMessages (final RequestQueue queue){
        Map<String, String> chatMap = new HashMap<String, String>();
        chatMap.put("type", "bookcomment");
        chatMap.put("id", getActivity().getIntent().getStringExtra("id"));
        ServerRequestUtility.asyncCall(queue, chatMap, responseListener, errorListener, "get");
    }

    /**
     * response listener to handle the returned array of comments. Populate the listview with all the current comments
     */
    private Response.Listener responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            chatStringArray.clear();
            try {
                // JSON reponse from the server.
                JSONArray returnedArray = new JSONArray(response);
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
            // Tell the adapter it needs to update it's data
            chatAdapter.notifyDataSetChanged();

        }
    };

    /**
     * Standard error response, log the VolleyError
     */
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("volleyError", error.toString());
        }
    };

    /**
     * Standard successful response to log that the notfication has been sent to the
     * book owner
     */
    private Response.Listener genericResponseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.d("Notification sent", response);
        }
    };

    /**
     * Specific response to make sure to send a notification to the owner of the book that someone has commented
     * on their book
     */
    private Response.Listener notificationsResponse = new Response.Listener<String>() {

        @Override
        public void onResponse(String response) {
            //send notifications
            Map<String, String> notifyMap = new HashMap<String, String>();
            notifyMap.put("type", "notification");
            Context context = MainActivity.getContextOfApplication();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String userName = prefs.getString("username", "");
            notifyMap.put("text", userName + " commented on your book " + getActivity().getIntent().getStringExtra("name"));
            notifyMap.put("identifier", getActivity().getIntent().getStringExtra("id"));
            notifyMap.put("tag", "GroupComment");
            notifyMap.put("sender", userName);
            notifyMap.put("id", getActivity().getIntent().getStringExtra("ownerEmail"));
            ServerRequestUtility.asyncCall(queue, notifyMap, genericResponseListener, errorListener, "add");
            listChatMessages(queue);
        }
    };

    /**
     * Alert dialogue builder with the popup to enter the message
     * @param comment - button to be used to say "comment"
     */
    private void setCommentButtonListener(Button comment) {
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog to send comment to server
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Comment");
                final EditText message = new EditText(getActivity());
                message.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(message);
                //On an OK, send comment to server
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String messageString = message.getText().toString();
                        addMessage(messageString);
                    }
                });
                //On cancel, return to comment list
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    /**
     * Method to handle the refreshing of the listView. Allows the user to
     * See if other users have commented while they were on the screen.
     * @param refresh - button to apply the listener to
     */
    private void setRefreshListener(Button refresh) {
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listChatMessages(queue);
            }
        });
    }
}
