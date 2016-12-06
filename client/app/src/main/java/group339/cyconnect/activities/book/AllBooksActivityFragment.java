package group339.cyconnect.activities.book;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
import group339.cyconnect.models.Book;
import group339.cyconnect.serverutil.ServerRequestUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class AllBooksActivityFragment extends Fragment {
    //Adapter for array of Books
    private ArrayAdapter<String> booksAdapter;
    //String array of Books
    private ArrayList<String> booksStringArray = new ArrayList<String>();
    /**
     * Required blank constructor
     */
     public AllBooksActivityFragment() {}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate layout file
        View rootView = inflater.inflate(R.layout.fragment_all_books, container, false);
        //Initialize adapter and set it to match up with the list
        booksAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_book, R.id.list_items_books_textview, booksStringArray);
        ListView booksList = (ListView) rootView.findViewById(R.id.listview_all_books);
        booksList.setAdapter(booksAdapter);
        booksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //If an item (Book) in the list is selected, go to its ViewBookActivity
                String book = booksAdapter.getItem(position);
                Intent bookIntent = new Intent(getActivity(), ViewBookActivity.class).putExtra("text", book);
                startActivity(bookIntent);
            }
        });
        return rootView;
    }
    //Set queue on start and get the list of Books
    public void onStart() {
        super.onStart();
        final RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        listAllBooks(queue);
    }

    /**
     * Sends a request to the server for all books, and populates the ListView with the returned JSONArray
     * @param queue
     */
    private void listAllBooks (final RequestQueue queue){
        Map<String, String> bookMap = new HashMap<String, String>();
        bookMap.put("type", "book");
        bookMap.put("action", "get");
        ServerRequestUtility.asyncCall(queue, bookMap, responseListener, errorListener, "get");
    }
    //Listener for request to get all books from the server
    private Response.Listener responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            //Clear the array to prepare to repopulate it with new results
            booksStringArray.clear();
            ArrayList<Book> booksArray = new ArrayList<Book>();
            try {
                //Convert response into JSONArray
                JSONArray returnedArray = new JSONArray(response);
                for (int i = 0; i < returnedArray.length(); i++) {
                    JSONObject jsonObject = returnedArray.getJSONObject(i);
                    String name = jsonObject.getString("BookName");
                    String ISBN = jsonObject.getString("ISBN");
                    String desc = jsonObject.getString("Description");
                    String owner = jsonObject.getString("OwnerEmail");
                    String id = jsonObject.getString("BookID");
                    booksArray.add(new Book(name, ISBN, desc, owner, id));
                }
                String bookString = "";
                //Populate the booksStringArray with string representations of the books in booksArray
                for (Book x : booksArray){
                    bookString = x.toString();
                    booksStringArray.add(bookString);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Notify the adapter that the backing array has changed
            booksAdapter.notifyDataSetChanged();
        }
    };

    //Listener for error from server
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("volleyError", error.toString());
        }
    };
}