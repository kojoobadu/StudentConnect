package group339.cyconnect.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import group339.cyconnect.Password;
import group339.cyconnect.R;
import group339.cyconnect.serverutil.ServerRequestUtility;

import java.util.HashMap;
import java.util.Map;
/**
 * Dialog for creation of a new User account
 * Created by Jacob on 11/19/2015.
 */
public class CreateAccountDialog extends DialogFragment {
    //Text field to input username (email address)
    private EditText userName;
    //Text field to input password
    private EditText password;
    //Text field to input first name
    private EditText firstName;
    //Text field to input last name
    private EditText lastName;
    //Text field to input User bio
    private EditText bio;
    /**
     * Required blank constructor
     */
    public CreateAccountDialog(){}
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_create_account, null);
        builder.setView(view);
        //Get all the EditText fields
        userName = (EditText) view.findViewById(R.id.username);
        password = (EditText) view.findViewById(R.id.password);
        firstName = (EditText) view.findViewById(R.id.firstname);
        lastName = (EditText) view.findViewById(R.id.lastname);
        bio = (EditText) view.findViewById(R.id.user_bio);
        // Add action buttons
        builder.setPositiveButton(R.string.save_user_info, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //Send a request to create this account to the server if the User clicks OK
                createAccount();
                //Do the action associated with a click of OK
                mListener.onDialogPositiveClick(CreateAccountDialog.this);
            }
        })
                .setNegativeButton(R.string.cancel_editing, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Cancel this dialog and do the appropriate actions on a click of Cancel
                        CreateAccountDialog.this.getDialog().cancel();
                        mListener.onDialogNegativeClick(CreateAccountDialog.this);
                    }
                });
        return builder.create();
    }
    /**
     * Sends a request to the server to create a new User account
     * @return
     */
    private boolean createAccount(){
        //Map of parameters to pass to the server
        Map<String, String> map = new HashMap<String, String>();
        map.put("type", "account");
        map.put("id", userName.getText().toString());
        map.put("password", Password.encryptPassword((password.getText().toString())));
        map.put("firstname", firstName.getText().toString());
        map.put("lastname", lastName.getText().toString());
        map.put("desc", bio.getText().toString());
        //Get volley RequestQueue
        final RequestQueue queue = Volley.newRequestQueue(getActivity());
        //Listener for response from the server
        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response);
                //response is success or failure
                if(response.equals("success")){
                    //set username and password in SharedPreferences if account creation is successful
                    Context context = MainActivity.getContextOfApplication();
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editPrefs = prefs.edit();
                    editPrefs.putString("username", userName.getText().toString()).commit();
                    editPrefs.putString("password", password.getText().toString()).commit();
                }
                //Do the action associated with positive response from server
                mListener.onReturnValue(response);
            }

        };
        //Listener for error from the server
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log the error
                Log.e("volleyError", error.toString());
            }
        };
        //Make call to the server
        ServerRequestUtility.asyncCall(queue, map, responseListener, errorListener, "add");
        return true;
    }
    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface CreateAccountDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
        public void onReturnValue(String response);
    }
    // Use this instance of the interface to deliver action events
    CreateAccountDialogListener mListener;
    // Overrides the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (CreateAccountDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement CreateAccountDialogListener");
        }
    }
}