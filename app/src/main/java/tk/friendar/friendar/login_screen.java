package tk.friendar.friendar;

import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.ProgressDialog;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Simon on 8/23/2017.
 */

public class login_screen extends AppCompatActivity{

    Button login;
    EditText userPass, userName;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        userName = (EditText) findViewById(R.id.login_username);
        userPass = (EditText) findViewById(R.id.login_password);
        login = (Button) findViewById(R.id.login_button);

        pd = new ProgressDialog(login_screen.this);

        /* For sign up button */
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Does a GET request to authenticate the credentials of the user */
                pd.setMessage("Logging In . . .");
                pd.show();

                /* Attempt login */
                login();
            }
        });

    }

    /**
     * Switches the view to the home screen once the user's login is successful
     * @param view the current view of the application
     */
    public void submitLogin(View view){
        Intent intent_submit = new Intent(this, HomeScreen.class);
        startActivity(intent_submit);

    }

    /**
     * Attempts to login using the user's entered credentials
     */
    private void login(){
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, URLs.URL_SIGNUP,
            new Response.Listener<JSONObject>()
            {
                @Override
                public void onResponse(JSONObject response) {
                    pd.hide();
                    Log.d("JsonArray Response",response.toString());
                    System.out.println("BEGINNING SEARCH");
                    boolean res = processResponse(response);
                    if(res){
                        submitLogin(getCurrentFocus());
                    } else {
                        // TODO: reject the login
                    }
                }
            },
            new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    pd.hide();
                    Log.d("ErrorResponse", error.toString());
                }
            }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", "Basic amFtZXM6c3RvbmU=");
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        //req.setRetryPolicy(new DefaultRetryPolicy(30000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        req.setShouldCache(false);
        Volley.newRequestQueue(login_screen.this).add(req);
    }

    /**
     * Processes the response from the GET request from the server
     * We parse through the users to determine if the login credentials are legitimate
     * @param response is the JSON object that contains the JSON array of users
     */
    private boolean processResponse(JSONObject response){
        try
        {
            boolean found = false;
            JSONArray users_array = response.getJSONArray("users");
            /* Loop over all users in the database */
            for(int i = 0; i < users_array.length(); i++){
                JSONObject user = (JSONObject) users_array.get(i);
                System.out.printf("USER %d\n", i);
                String server_username = user.getString("username");
                System.out.println("USERNAME:" + server_username);
                String server_password = user.getString("usersPassword");
                System.out.println("PASSWORD:" + server_password);

                if(userName.equals(server_username) && userPass.equals(server_password)){
                    submitLogin(getCurrentFocus());
                    found = true;
                }
            }

            /* Found the user's correct credentials */
            if(found){
                return true;
            } else {
                System.out.println("NO USER FOUND");
                return false;
            }

        } catch (JSONException e){
            Log.d("Web Service Error",e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
