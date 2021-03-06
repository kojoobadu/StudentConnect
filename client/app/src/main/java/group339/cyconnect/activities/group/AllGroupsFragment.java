package group339.cyconnect.activities.group;

import android.content.Intent;
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
import group339.cyconnect.models.Group;
import group339.cyconnect.serverutil.ServerRequestUtility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * Fragment that contains ListView of all Groups in the database
 */
public class AllGroupsFragment extends Fragment {
    //adapter for ListView of Groups
    private ArrayAdapter<String> groupsAdapter;
    //List of Groups
    private ArrayList<String> groupStringArray= new ArrayList<>();
    //Tag to be used for logging while debugging
    private static final String LOG_TAG = "AllGroupsFragment";
    //Email of the current User (username)
    private String appOwnerEmail;
    public static AllGroupsFragment newInstance() {
        return new AllGroupsFragment();
    }
    /**
     * Required empty public constructor
     */
    public AllGroupsFragment() {}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_all_groups, container, false);
        //Get username from SharedPreferences for appOwnerEmail
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity().getApplicationContext());
        appOwnerEmail = prefs.getString("username", "");
        //Set the ArrayAdapter and link it to the ListView
        groupsAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_groups, R.id.list_items_groups_textview, groupStringArray);
        ListView groupsList = (ListView) rootView.findViewById(R.id.listviewgroups);
        groupsList.setAdapter(groupsAdapter);
        //Set Listeners for the items in the ListView
        groupsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String group = groupsAdapter.getItem(position);
                //Intent to leave AllGroupsActivity
                Intent groupIntent;
                //If this User owns this Group, go to MyGroupsActivity, sending Group text along
                if (group.contains(appOwnerEmail)) {
                    groupIntent = new Intent( getActivity(), MyGroupsActivity.class).putExtra("text",group);
                    startActivity(groupIntent);
                }
                //Otherwise go to ViewGroupActivity, sending Group text along
                else {
                    groupIntent = new Intent(getActivity(), ViewGroupActivity.class).putExtra("text", group);
                    startActivity(groupIntent);
                }
            }
        });
        return rootView;
    }

    public void onStart() {
        super.onStart();
        final RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        //Get all groups
        listAllGroups(queue);

    }

    /**
     * Sends request to the server for list of all Groups
     * @param queue
     */
    private void listAllGroups (final RequestQueue queue){
        //Map of parameters to send to the server
        Map<String, String> groupMap = new HashMap<String, String>();
        groupMap.put("type", "group");
        ServerRequestUtility.asyncCall(queue, groupMap, responseListener, errorListener, "get");
    }
    //Listener for response from server
    private Response.Listener responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            //Clear array of strings before adding respose to it
            groupStringArray.clear();
            //Array to store Group objects generated by response
            ArrayList<Group> groupsArray = new ArrayList<>();
            try {
                //Convert response into JSONArray
                JSONArray returnedArray = new JSONArray(response);
                //For each item in returnedArray, create a Group object and store it in groupsArray
                for (int i = 0; i < returnedArray.length(); i++) {
                    JSONObject jsonObject = returnedArray.getJSONObject(i);
                    Log.d("JSON OBJECT", jsonObject.toString());
                    String groupName = jsonObject.getString("GroupName");
                    String groupDescription = jsonObject.getString("Description");
                    String ownerEmail = jsonObject.getString("GroupOwnerEmail");
                    String groupID = jsonObject.getString("GroupID");
                    groupsArray.add(new Group(groupName,groupDescription, ownerEmail, groupID));
                }
                String groupString = "";
                //For each Group in groupsArray, add its String representation to groupStringArray
                for (Group g : groupsArray) {
                    groupString = g.toString();
                    groupStringArray.add(groupString);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Notify the groupsAdapter that groupStringArray has been altered
            groupsAdapter.notifyDataSetChanged();
        }
    };
    //Listener for error from server
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            //Log error
            Log.e("volleyError", error.toString());
        }
    };
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
}