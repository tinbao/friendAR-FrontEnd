package tk.friendar.friendar;

import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.ProgressDialog;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
                pd.setMessage("Logging In . . .");
                pd.show();
                pd.setCancelable(false);
                pd.setCanceledOnTouchOutside(false);

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
        /* Does a GET request to authenticate the credentials of the user */
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, URLs.URL_SIGNUP,
            new Response.Listener<JSONObject>()
            {
                @Override
                public void onResponse(JSONObject response) {
                    pd.hide();
                    Log.d("JSON Response",response.toString());

                    /* Switch to HOME screen */
                    submitLogin(getCurrentFocus());
                }
            },
            new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    pd.hide();
                    String msg = error.toString();
                    Log.d("ErrorResponse", msg);
                    Context context = getApplicationContext();

                    if(msg.equals("com.android.volley.AuthFailureError")) {
                        /* Incorrect credentials case */
                        CharSequence text = "Incorrect Username or Password";

                        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        ){


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                VolleyHTTPRequest.setUsername(userName.getText().toString());
                VolleyHTTPRequest.setPassword(userPass.getText().toString());
                headers.put("authorization", VolleyHTTPRequest.makeAutho());
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        req.setShouldCache(false);
        Volley.newRequestQueue(login_screen.this).add(req);
    }

}
