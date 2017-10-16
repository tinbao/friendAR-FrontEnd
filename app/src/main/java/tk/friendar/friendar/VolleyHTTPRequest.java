package tk.friendar.friendar;

import android.util.Base64;

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

    public VolleyHTTPRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Generates the 64 bit encoded authorisation for required requests
     * @return The authorisation code
     */
    public static String makeAutho(){
        return String.format("Basic %s", Base64.encodeToString(
                String.format("%s:%s", username, password).getBytes(), Base64.DEFAULT));
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setUsername(String username) {
        VolleyHTTPRequest.username = username;
    }

    public static void setPassword(String password) {
        VolleyHTTPRequest.password = password;
    }
}
