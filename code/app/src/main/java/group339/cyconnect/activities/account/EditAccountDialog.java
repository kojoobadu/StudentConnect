package group339.cyconnect.activities.account;

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
import group339.cyconnect.activities.MainActivity;
import group339.cyconnect.serverutil.ServerRequestUtility;

import java.util.HashMap;
import java.util.Map;

public class EditAccountDialog extends DialogFragment {
    //EditText for inputting First Name
    private EditText editFirstName;
    //EditText for inputting Last Name
    private EditText editLastName;
    //EditText for inputting Bio
    private EditText editBio;
    /**
     * Required blank constructor
     */
    public EditAccountDialog(){}
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_edit_account, null);
        builder.setView(view);
        //Get the EditTexts of this dialog
        editFirstName = (EditText) view.findViewById(R.id.firstname);
        editLastName = (EditText) view.findViewById(R.id.lastname);
        editBio = (EditText) view.findViewById(R.id.user_bio);
        // Add action buttons: positive click updates account and negative click cancels dialog
        builder.setPositiveButton(R.string.save_user_info, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //save the data entered
                updateAccount();
                mListener.onDialogPositiveClick(EditAccountDialog.this);
            }
        })
                .setNegativeButton(R.string.cancel_editing, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditAccountDialog.this.getDialog().cancel();
                        mListener.onDialogNegativeClick(EditAccountDialog.this);
                    }
                });
        return builder.create();
    }

    /**
     * Updates User information in the database using inputs in dialog
     * @return
     */
    private boolean updateAccount(){
        //Build map of all parameters to pass to server
        Map<String, String> map = new HashMap<String, String>();
        Context context = MainActivity.getContextOfApplication();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String userName = prefs.getString("username", "");
        String password = prefs.getString("password", "");
        password = Password.encryptPassword(password);
        map.put("type", "account");
        map.put("id", userName);
        map.put("password", password);
        map.put("firstname", editFirstName.getText().toString());
        map.put("lastname", editLastName.getText().toString());
        map.put("desc", editBio.getText().toString());
        //Event joining never implemented, but event still required in the URL, so set as none
        map.put("event", "none");
        //Get the volley request queue
        final RequestQueue queue = Volley.newRequestQueue(getActivity());
        //Listener for response from server
        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //response is success or fail, but user is not notified
            }
        };
        //Listener for error from server
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log the error
                Log.e("volleyError", error.toString());
            }
        };
        //Call the server
        ServerRequestUtility.asyncCall(queue, map, responseListener, errorListener, "edit");
        return true;
    }
    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface EditAccountDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
    // Use this instance of the interface to deliver action events
    EditAccountDialogListener mListener;
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (EditAccountDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement EditAccountDialogListener");
        }
    }
}
