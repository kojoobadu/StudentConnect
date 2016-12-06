package group339.cyconnect.activities.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import group339.cyconnect.Password;
import group339.cyconnect.R;
import group339.cyconnect.activities.MainActivity;
import group339.cyconnect.activities.MenuUtil;
import group339.cyconnect.serverutil.ServerRequestUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends AppCompatActivity implements EditAccountDialog.EditAccountDialogListener {
    //Button to get jump to notification list
    private Button notificationsButton;
    //Button to open edit dialog
    private Button editButton;
    //Button to log out of app
    private Button logoutButton;
    //Name of this User
    private TextView nameTextView;
    //Bio of this User
    private TextView bioTextView;
    //Queue for making volley requests
    private RequestQueue mQueue;
    //Context of application
    public static Context applicationContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        //get the volley request queue
        mQueue = Volley.newRequestQueue(this);
        //Get the button for notifications and set its OnClickListener
        notificationsButton = (Button) findViewById(R.id.notification_button);
        notificationsButton.setOnClickListener(viewNotifications);
        //Get context of application
        applicationContext = getApplicationContext();
        //Get the button for logging out and set its OnClickListener
        logoutButton = (Button) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get this device's SharedPreferences and set them to None
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
                SharedPreferences.Editor editPrefs = prefs.edit();
                editPrefs.putString("username", "None").commit();
                editPrefs.putString("password", "None").commit();
                //After clearing SharedPreferences, jump to login screen
                Intent logoutIntent = new Intent(AccountActivity.this, MainActivity.class);
                startActivity(logoutIntent);
            }
        });
        //on creation, get information about this account
        setAccountData(mQueue);
        //Get button to edit account and add OnClickListener
        editButton = (Button) findViewById(R.id.account_edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open EditAccountDialog
                DialogFragment editDialog = new EditAccountDialog();
                editDialog.show(getSupportFragmentManager(), "EditingAccount");
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
        //Get id of the item clicked in the menu
        int id = item.getItemId();
        //Use MenuUtil.getMenuIntent to get the appropriate intent for the selected id
        Intent intent = MenuUtil.getMenuIntent(AccountActivity.this, id);
        //If the intent is not null, start the intent
        if (intent != null) {
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets all the account info fields based on the username and password in SharedPreferences
     *
     * @param queue
     */
    public void setAccountData(RequestQueue queue) {
        //Map for storing parameters of request to the server
        Map<String, String> map = new HashMap<String, String>();
        //Get username and password from SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        String userName = prefs.getString("username", "");
        String password = prefs.getString("password", "");
        //Encrypt the password before sending it to the server
        password = Password.encryptPassword(password);
        //Populate the map with the parameters type, userName, and password
        map.put("type", "account");
        map.put("userName", userName);
        map.put("password", password);
        //Listener for response from server after making request
        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response);
                //Get TextViews of AccountActivity
                nameTextView = (TextView) findViewById(R.id.nameTextView);
                bioTextView = (TextView) findViewById(R.id.bioTextField);
                try {
                    //Convert String response into JSONArray
                    JSONArray returnedArray = new JSONArray(response);
                    //Get the first element of the array
                    JSONObject jsonObject = returnedArray.getJSONObject(0);
                    //Get the fields from the jsonObject
                    String fname = jsonObject.getString("FirstName");
                    String lname = jsonObject.getString("LastName");
                    String bio = jsonObject.getString("Description");
                    String eventName = jsonObject.getString("EventName");
                    //set fields in UI to fetched data
                    nameTextView.setText(fname + " " + lname);
                    bioTextView.setText(bio);
                } catch (JSONException e) {
                    e.printStackTrace();
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
        //Send get request to server
        ServerRequestUtility.asyncCall(queue, map, responseListener, errorListener, "get");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        //Get the updated account information
        setAccountData(mQueue);
    }

    //Do nothing on negative click (cancel)
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
    }

    //Listener for notifications button
    private View.OnClickListener viewNotifications = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Get intent for notifications page, then jump to it
            Intent notificationsIntent = new Intent(AccountActivity.this, AccountNotificationsActivity.class);
            startActivity(notificationsIntent);
        }
    };
}
