package group339.cyconnect.activities.account;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import group339.cyconnect.R;
import group339.cyconnect.activities.MainActivity;
import group339.cyconnect.models.Notification;
import group339.cyconnect.serverutil.ServerRequestUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * Fragment within AccountNotificationsActivity
 */
public class NotificationsFragment extends Fragment {
    // Array adapter for implementing notifications listview
    private ArrayAdapter<String> notificationsAdapter;
    // Arraylist to store notifications
    private ArrayList<String> notificationsStringArray;
    // Variable to store context of the application
    private Context context;
    // String to store the identifier of a notification
    private String notificationFromString;
    // String to store the sender of a notification
    private String sender;
    // String to store the tag of a notification
    private String tag;
    // String to store the text of a notification
    private String notificationTextString;
    // String to store the time a notification is sent
    private String notificationTimeString;
    //Tag for logging while debugging
    private static final String LOG_TAG = "NotificationsFragment";

    /**
     * Required empty public constructor
     */
    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = MainActivity.getContextOfApplication();
    }
    @Override
    // Creates a clickable list view of all notifications of the user

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notifications, container, false);
        notificationsStringArray = new ArrayList<>();
        notificationsAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_notifications, R.id.list_item_notifications_textview, notificationsStringArray);
        ListView notificationsList = (ListView) rootView.findViewById(R.id.listview_notifications);
        notificationsList.setAdapter(notificationsAdapter);
        notificationsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String fullNotificationString = notificationsAdapter.getItem(position);
                notificationAction(fullNotificationString);
            }
        });
        return rootView;
    }

    // Method to resume activity after the page has been idle for a while
    public void onStart() {
        super.onStart();
        final RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        getNotifications(queue);
    }

    // Method to update url for getting notifications and making a server call to get all
    // notifications of the user
    private void getNotifications(final RequestQueue queue){
        Map<String, String> notificationsMap = new HashMap<>();
        notificationsMap.put("type", "notification");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String username = sharedPref.getString("username", "");
        notificationsMap.put("id", username);
        ServerRequestUtility.asyncCall(queue, notificationsMap, responseListener, genericErrorListener, "get");

    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    // Response listener for making a server call to get all notifications.
    private Response.Listener responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            ArrayList<Notification> notificationsArray = new ArrayList<Notification>();
            notificationsArray.clear();
            notificationsStringArray.clear();
            try {
                JSONArray returnedArray = new JSONArray(response);
                for (int i = 0; i < returnedArray.length(); i++) {
                    JSONObject jsonObject = returnedArray.getJSONObject(i);
                    String notificationText = jsonObject.getString("NotificationText");
                    String tag = jsonObject.getString("Tag");
                    String identifier = jsonObject.getString("Identifier");
                    String sender = jsonObject.getString("Sender");
                    String time = jsonObject.getString("TimeSent");
                    Log.d("Printed", "from account activity");
                    notificationsArray.add(new Notification(notificationText, identifier, tag, sender, time));
                    String notificationString = "";
                    for (Notification n : notificationsArray) {
                        notificationString = n.toString();
                        notificationsStringArray.add(notificationString);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            notificationsAdapter.notifyDataSetChanged();

        }

    };

    // Method to show a notification alert for a group invite or any other notification alert
    private void notificationAction(String fullNotificationString){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());
        final RequestQueue queue = Volley.newRequestQueue(context);
        setNotificationStrings(fullNotificationString);
        alert.setTitle(notificationTextString);
        if (tag.equals("GroupInvite")) {
            alert.setMessage("You have been invited to join " + sender + "'s group!");
            setJoinAlert(alert, queue);
        } else {
            setElseAlert(alert, queue);
        }
    }

    // Method to create server call when user accepts to join a group or declines
    private void setJoinAlert(AlertDialog.Builder alert, final RequestQueue queue) {
        alert.setPositiveButton("Join", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int num) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String userName = prefs.getString("username", "");
            // Map to be used for telling the server you have joined the group
            Map<String, String> joinMap = joinMap(userName);
            // Map to be used for telling the sender you have accepted their notification
            Map<String, String> sendAccept = acceptMap(userName);
            // Map to be used for deleting the notification from the server
            Map<String, String> deleteNotification = deleteMap(userName);
            ServerRequestUtility.asyncCall(queue, joinMap, genericResponseListener, genericErrorListener, "addMulti");
            ServerRequestUtility.asyncCall(queue, sendAccept, genericResponseListener, genericErrorListener, "add");
            ServerRequestUtility.asyncCall(queue, deleteNotification, genericResponseListener, genericErrorListener, "delete");

            }
        });
        alert.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int num) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("action", "delete");
                ServerRequestUtility.asyncCall(queue, map, genericResponseListener, genericErrorListener, "delete");

            }
        });
        alert.create();
        alert.show();
    }

    // Method to breakdown a notification into its respective fields
    private void setNotificationStrings(String fullNotificationString) {
        notificationTextString = ServerRequestUtility.substringBetween(fullNotificationString, "Text: ", ", From");
        notificationFromString = ServerRequestUtility.substringBetween(fullNotificationString,"From: ", ", Tag");
        tag = ServerRequestUtility.substringBetween(fullNotificationString, "Tag: ", ", Time");
        notificationTimeString = ServerRequestUtility.substringBetween(fullNotificationString,"Time: ", ", Sender");
        sender = fullNotificationString.substring(fullNotificationString.indexOf("Sender: ") + 8);
    }

    // Method to show alert of notification if it is not a group invite
    private void setElseAlert(AlertDialog.Builder alert, final RequestQueue queue) {
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int num) {

                Response.Listener responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Success: ", "works");
                        // Open another dialog
                    }
                };
                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("volleyError", error.toString());
                    }
                };
            }
        });
        alert.create();
        alert.show();
    }

    // Almost like a constructor for building the map used for the Join call
    private Map<String, String> joinMap(String userName) {
        Map<String, String> joinMap = new HashMap<String, String>();
        joinMap.put("id", notificationFromString);
        joinMap.put("type", "members");
        joinMap.put("action", "add");
        joinMap.put("email", userName);
        return joinMap;
    }

    // Almost like a constructor for building the map used for the accept call
    private Map<String, String> acceptMap(String userName) {
        Map<String, String> sendAccept = new HashMap<>();
        sendAccept.put("id", sender);
        sendAccept.put("text", userName + " has accepted to join your group");
        sendAccept.put("type", "notification");
        sendAccept.put("tag", "GroupAccept");
        sendAccept.put("identifier", "accept");
        sendAccept.put("sender", userName);
        return sendAccept;
    }

    // Almost like a constructor for building the map used for the delete call
    private Map<String, String> deleteMap(String userName) {
        // Delete notification after joining

        Map<String, String> deleteNotification = new HashMap<>();
        deleteNotification.put("id",userName);
        deleteNotification.put("time",notificationTimeString);
        deleteNotification.put("text",notificationTextString);


        deleteNotification.put("action","delete");
        deleteNotification.put("type","notification");
        return deleteNotification;
    }

    // Generic Response Listener instantiation to be used all over
    private Response.Listener genericResponseListener = new Response.Listener() {
        @Override
        public void onResponse(Object response) {
            Log.d("Success: ", "works");
        }
    };

    // Generic ErrorResponse Listener instantiation to be used all over
    private Response.ErrorListener genericErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("volleyError", error.toString());
        }
    };

}


