package tk.friendar.friendar;

import android.content.Context;
import android.util.Base64;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by tinba on 16/10/2017.
 */

/**
 * Singleton class to store user credentials and authorise
 */
public class VolleyHTTPRequest {

    /* Single instance login username and password for authentication */
    static String username;
    static String password;
	public static int id;

    static Integer userID;

    private static VolleyHTTPRequest instance;
    private static RequestQueue reqQueue;

    public VolleyHTTPRequest(String username, String password) {
        this.username = username;
        this.password = password;

        instance = this;
    }

    /**
     * Generates the 64 bit encoded authorisation for required requests
     * @return The authorisation code
     */
    public static String makeAutho(){
        return String.format("Basic %s", Base64.encodeToString(
                String.format("%s:%s", username, password).getBytes(), Base64.DEFAULT));
    }

    /**
     * Gets the request queue from the instance
     * @return
     */
    public static RequestQueue getRequestQueue(Context context){
        if(reqQueue == null){
            reqQueue = Volley.newRequestQueue(context);
        }

        return reqQueue;
    }

    /**
     * Adds a JSON Object Request to the request queue
     * @param req
     */
    public static void addRequest(JsonObjectRequest req, Context context){
        VolleyLog.d("Adding request to queue");
        getRequestQueue(context).add(req);
    }

    /**
     * Adds a JSON Array Request to the request queue
     * @param req
     */
    public static void addRequest(JsonArrayRequest req, Context context){
        VolleyLog.d("Adding request to queue");
        getRequestQueue(context).add(req);
    }

    /**
     * Adds a string request to the queue
     * @param req
     * @param context
     */
    public static void addRequest(StringRequest req, Context context){
        VolleyLog.d("Adding string request to queue");
        getRequestQueue(context).add(req);
    }

    /**
     * Removes all pending requests from the queue
     * @param obj
     */
    public void cancelPendingRequests(JSONObject obj) {
        if (reqQueue != null) {
            reqQueue.cancelAll(obj);
        }
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    public static Integer getUserID() {
        return userID;
    }

    public static void setUsername(String username) {
        VolleyHTTPRequest.username = username;
    }

    public static void setPassword(String password) {
        VolleyHTTPRequest.password = password;
    }

    public static void setUserID(Integer userID) {
        VolleyHTTPRequest.userID = userID;
    }

    public static VolleyHTTPRequest getInstance() {
        return instance;
    }

}
