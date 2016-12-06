package group339.cyconnect.activities.event;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import group339.cyconnect.models.Event;
import group339.cyconnect.serverutil.ServerRequestUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AllEventsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AllEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllEventsFragment extends Fragment {
    private ArrayAdapter<String> eventsAdapter;
    private ArrayList<String> eventStringArray = new ArrayList<String>();

    private static final String LOG_TAG = "AllEventsFragment";

    public static AllEventsFragment newInstance() {
        return new AllEventsFragment();
    }

    public AllEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_events, container, false);


        eventsAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_events, R.id.list_items_events_textview, eventStringArray);
        ListView eventsList = (ListView) rootView.findViewById(R.id.listview_all_events);
        eventsList.setAdapter(eventsAdapter);
        eventsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String event = eventsAdapter.getItem(position);
                Intent eventIntent = new Intent(getActivity(), ViewEventActivity.class).putExtra("text", event);
                startActivity(eventIntent);
            }
        });
        return rootView;
    }

    public void onStart() {
        super.onStart();
        final RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        listAllEvents(queue);

    }

    private void listAllEvents (final RequestQueue queue){
        Map<String, String> eventMap = new HashMap<String, String>();
        eventMap.put("type", "event");
        eventMap.put("action", "get");
        ServerRequestUtility.asyncCall(queue, eventMap, responseListener, errorListener, "get");
    }

    private Response.Listener responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            ArrayList<Event> eventsArray = new ArrayList<Event>();
            eventStringArray.clear();
            try {
                JSONArray returnedArray = new JSONArray(response);
                for (int i = 0; i < returnedArray.length(); i++) {
                    JSONObject jsonObject = returnedArray.getJSONObject(i);
                    String eventName = jsonObject.getString("EventName");
                    String eventDescription = jsonObject.getString("EventDescription");
                    String ownerEmail = jsonObject.getString("OwnerEmail");
                    String ranking = jsonObject.getString("Ranking");
                    eventsArray.add(new Event(eventName, eventDescription, ownerEmail, ranking));
                }
                String eventString = "";
                for (Event e : eventsArray) {
                    eventString = e.toString();
                    eventStringArray.add(eventString);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            eventsAdapter.notifyDataSetChanged();

        }
    };

    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
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
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }



}
