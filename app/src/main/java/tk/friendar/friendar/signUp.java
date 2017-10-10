package tk.friendar.friendar;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.app.Dialog;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.support.v4.app.DialogFragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Calendar;

import java.util.GregorianCalendar;

/**
 * Created by Simon on 8/22/2017.
 */

public class signUp extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    EditText userEmail, userPassword, confirmPassword;
    ProgressBar progressBar;
    Button complete;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        complete = (Button) findViewById(R.id.complete_signup);

        userEmail = (EditText) findViewById(R.id.email);
        userPassword = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.password2);

        pd = new ProgressDialog(signUp.this);

        /* For sign up button */
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.setMessage("Signing Up . . .");
                pd.show();
                /* Creates JSON Object (String) and POSTs to server via HTTP for new users */
                register();
            }
        });
    }

    public void datePicker(View view){
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(),"date");
    }

    private void setDate(final Calendar calendar){
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        ((TextView) findViewById(R.id.showDate)).setText(dateFormat.format(calendar.getTime()));

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar cal = new GregorianCalendar(year,month, dayOfMonth);
        setDate(cal);

    }

    public static class DatePickerFragment extends DialogFragment{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(),
            (DatePickerDialog.OnDateSetListener) getActivity(),year,month,day);
        }

    }

    public void signUp(View view){
        Intent intent_signup = new Intent(this,signUp.class);
        startActivity(intent_signup);
    }

    public void login(View view){
        Intent intent_login = new Intent(this,login_screen.class);
        startActivity(intent_login);
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
     * Registers a new user to the database using volley's POST method through a REST API
     * Will also confirm that the user enters in correctly formatted inputs.
     */
    private void register() {
        final String email = userEmail.getText().toString().trim();
        final String password = userPassword.getText().toString().trim();
        final String password_confirm = confirmPassword.getText().toString().trim();
        /* Temporary Username: taking the string before the @ in the email */
        final String userName = email.split("@")[0];

        if (TextUtils.isEmpty(email)) {
            userEmail.setError("Please enter your email");
            userEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            userEmail.setError("Enter a valid email");
            userEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            userPassword.setError("Enter a password");
            userPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password_confirm)) {
            confirmPassword.setError("Confirm a password");
            confirmPassword.requestFocus();
            return;
        }

        if (!password.equals(password_confirm)) {
            userPassword.setError("Passwords do not match");
            userPassword.requestFocus();
            confirmPassword.setError("Passwords do not match");
            confirmPassword.requestFocus();
            return;
        }

        final JSONObject params = new JSONObject();
        /* Puts the information into the JSON Object */
        try {
            /* Temporary username, user can change it afterwards */
            params.put("fullName", userName);
            params.put("username", userName);
            params.put("usersPassword", password);
            params.put("email", email);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        /* Makes the request that will be processed by the server */
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URLs.URL_SIGNUP, params,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    pd.hide();
                    Log.d("Response", response.toString());
                    submitLogin(getCurrentFocus());
                }
            },

            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pd.hide();
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        try {
                            String res = new String(response.data,
                                    HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                            /* Tries to make a JSON Object and see if it will incur an error */
                            JSONObject obj = new JSONObject(res);

                        } catch (UnsupportedEncodingException e1) {
                            /* Couldn't properly decode data to string */
                            e1.printStackTrace();
                        } catch (JSONException e2) {
                            /* returned data is not JSONObject */
                            e2.printStackTrace();
                        }
                    }
                }
            }) {

                /** Defines the body type of the data being posted */
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
            };

        /* Requests are posted and executed in a queue */
        req.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        req.setShouldCache(false);
        Volley.newRequestQueue(signUp.this).add(req);

    }
}