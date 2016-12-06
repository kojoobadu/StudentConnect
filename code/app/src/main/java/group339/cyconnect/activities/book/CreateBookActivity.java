package group339.cyconnect.activities.book;

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
 * Activity for creating a new Book
 */
public class CreateBookActivity extends AppCompatActivity {
    //Field for name of new Book
    EditText enterName;
    //Field for description of new Book
    EditText enterDescription;
    //Button to send new Book to server
    Button send;
    // Field for the ISBN of the book
    EditText enterISBN;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_book);
        //get volley RequestQueue
        final RequestQueue queue = Volley.newRequestQueue(this);
        //Get EditTexts and Button
        enterName = (EditText)findViewById(R.id.type_bookName);
        enterDescription = (EditText)findViewById(R.id.type_bookDescription);
        enterISBN = (EditText) findViewById(R.id.book_isbn);
        send = (Button)findViewById(R.id.addBook_button);
        //Set listener for send button
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Map for params to send to server
                Map<String,String> eventMap = new HashMap<String, String>();
                eventMap.put("type", "book");
                eventMap.put("action", "add");
                eventMap.put("desc", enterDescription.getText().toString());
                eventMap.put("name", enterName.getText().toString());
                eventMap.put("isbn", enterISBN.getText().toString());
                Context context = MainActivity.getContextOfApplication();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                String userName = prefs.getString("username", "");
                eventMap.put("owner", userName);
                Log.d("New Username", userName);
                Log.d("EventMap", eventMap.toString());
                //Listener for response from server
                Response.Listener responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.toString().equals("success")) {
                            //Return to main Book screen (BookActivity)
                            Intent bookIntent = new Intent(CreateBookActivity.this, BookActivity.class);
                            startActivity(bookIntent);
                        }
                    }
                };
                //Listener for error from server
                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("volleyError", error.toString());
                    }
                };
                //Send request to server
                ServerRequestUtility.asyncCall(queue, eventMap, responseListener, errorListener, "add");
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_general, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get id of menu item selected and jump to associated activity if it exists
        int id = item.getItemId();
        Intent intent = MenuUtil.getMenuIntent(CreateBookActivity.this, id);
        if(intent != null) {
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}