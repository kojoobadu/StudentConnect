package group339.cyconnect.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
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
import group339.cyconnect.Password;
import group339.cyconnect.R;
import group339.cyconnect.activities.account.AccountActivity;
import group339.cyconnect.serverutil.ServerRequestUtility;

import org.json.JSONArray;
import org.json.JSONException;


import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CreateAccountDialog.CreateAccountDialogListener{

    // Context of application
    public static Context applicationContext;
    // Edit text view to enter username
    private EditText userNameField;
    // Edit text view to enter password
    private EditText passwordField;
    // Boolean to determine if logged in or not
    private boolean connected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applicationContext = getApplicationContext();
        // Grab username from shared preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        String userName = prefs.getString("username", "");
        // if statement to check if user is logged in
        if( userName.equals("None")) {


            setContentView(R.layout.activity_main);


            final RequestQueue queue = Volley.newRequestQueue(this);

            userNameField = (EditText) findViewById(R.id.login_username);
            passwordField = (EditText) findViewById(R.id.login_password);
            connected = false;

            //button for creating a new account
            Button createButton = (Button) findViewById(R.id.create_account_button);
            createButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogFragment createDialog = new CreateAccountDialog();
                    createDialog.show(getSupportFragmentManager(), "CreatingAccount");
                }
            });

            //button for logging in
            Button loginButton = (Button) findViewById(R.id.login_button);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //if username or password are blank, alert the user
                    if (userNameField.getText().toString().length() == 0 || passwordField.getText().toString().length() == 0) {
                        ((TextView) findViewById(R.id.login_status)).setText("Username or Password are blank");
                    } else {
                        // HashMap to build URL
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("type", "account");
                        map.put("userName", userNameField.getText().toString());
                        String encryptedPassword = Password.encryptPassword(passwordField.getText().toString());
                        map.put("password", encryptedPassword);
                        Response.Listener responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Response", response);
                                connected = true;
                                try {
                                    JSONArray returnedArray = new JSONArray(response);
                                    if (returnedArray.length() > 0) {
                                        // Update shared preferences with username and password
                                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
                                        SharedPreferences.Editor editPrefs = prefs.edit();
                                        editPrefs.putString("username", userNameField.getText().toString()).commit();
                                        editPrefs.putString("password", passwordField.getText().toString()).commit();
                                        //jump to AccountActivity
                                        Intent accountIntent = new Intent(MainActivity.this, AccountActivity.class);
                                        startActivity(accountIntent);
                                    } else {
                                        //display wrong username/password
                                        ((TextView) findViewById(R.id.login_status)).setText("Username/Password are incorrect");
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                        };
                        Response.ErrorListener errorListener = new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("volleyError", error.toString());
                            }
                        };
                        // Make a server request with ayncCall method from the ServerRequestUtility class
                        ServerRequestUtility.asyncCall(queue, map, responseListener, errorListener, "get");
                    }
                }
            });

            PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        }
        // Skip login page if user hasn't logged out
        else{
            Intent accountIntent = new Intent(MainActivity.this, AccountActivity.class);
            startActivity(accountIntent);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(isVisible(connected)) {
            getMenuInflater().inflate(R.menu.menu_general, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent = MenuUtil.getMenuIntent(MainActivity.this, id);

        if(intent != null) {
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static Context getContextOfApplication() {
        return applicationContext;
    }

    //methods to handle return from creating an account
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
    }

    @Override
    // Method to go to activity intent upon successful json response or prompt user of already existing account.
    public void onReturnValue(String response){
        if(response.equals("success")){
            Intent accountIntent = new Intent(MainActivity.this, AccountActivity.class);
            startActivity(accountIntent);
        }
        else{
            ((TextView)findViewById(R.id.login_status)).setText("This Username already has an account");
        }
    }
    // Method to determine whether to show menu button or not.
    private static boolean isVisible(boolean connected){
        return connected;
    }

}
