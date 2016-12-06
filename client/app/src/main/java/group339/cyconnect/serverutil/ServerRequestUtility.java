package group339.cyconnect.serverutil;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * This is a utility class to be used whenever we communicate with the server.
 * Very dynamic and allows developer to not have to worry if they are working in
 * group, event, or books; use this same utility.
 * Created by Group22 on 10/24/15.
 */
public class ServerRequestUtility {


    /**
     *  The method that handles building the URL as well as adding the final request
     *  to the queue to be sent and handled by the server
     * @param queue - queue for handling the response
     * @param objects - map that is used to build the url
     * @param responseListener - what to do when the response is a success
     * @param errorListener - what to do when the response is an error
     * @param action - what we are doing to the server, (Which CRUD method)
     */
    public static final void asyncCall(RequestQueue queue, Map<String, String> objects, Response.Listener responseListener, Response.ErrorListener errorListener, String action) {

        // Build the URL base, based on the action
        String url = "";
        String server = "http://10.26.14.213/";
        if (action.equals("get")) {
            url = server + "get.php?";
        }

        else if(action.equals("add")) {
            url = server + "add.php?";
        }

        else if(action.equals("edit")) {
            url = server + "edit.php?";
            Log.d("Editing URL: ", url);
        }
        else if(action.equals("addMulti")){
            url = server + "multipart.php?";
        }

        else if(action.equals("delete")) {
            url = server + "delete.php?";
            Log.d("Editing URL: ", url);
        }

        else if(action.equals("multiAdd")){
            url = server + "multipart.php?";
            Log.d("Editing URL: ", url);
        }

        // Use the map to build the URL.  Encode the obj in the set so that we can
        // handle symbols and spaces in our URL
        for (Map.Entry<String, String> obj : objects.entrySet()) {
            try {
                url += obj.getKey() + "=" + URLEncoder.encode(obj.getValue().replace("'", "\\'"), "UTF-8") + "&";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        // Build the final request to be added to the queue
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, responseListener, errorListener);
        queue.add(stringRequest);

    }

    /**
     * Method that was found online that worked really well for string parsing.
     * Example of usage:
     * String str = "Group 22 made the app CyConnect";
     * String newStr = substringBetween(str, "made ", " CyConnect"; -- newStr now equals "the app"
     * @param str - the full string to parse
     * @param open - where to start
     * @param close - where to stop
     * @return - everything that is in between open and close.
     */
    public static String substringBetween(String str, String open, String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        int start = str.indexOf(open);
        if (start != -1) {
            int end = str.indexOf(close, start + open.length());
            if (end != -1) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }
}
