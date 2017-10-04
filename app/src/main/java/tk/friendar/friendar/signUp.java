package tk.friendar.friendar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.app.Dialog;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Calendar;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Simon on 8/22/2017.
 */

public class signUp extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

//    EditText userEmail, userPassword, confirmPassword;
//    ProgressBar progressBar;
//    Button complete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
//
//        progressBar = (ProgressBar) findViewById(R.id.progressBar);
//        complete = (Button) findViewById(R.id.complete_signup);
//
//        //if the user is already logged in we will directly start the profile activity
//        /*if (SharedPrefManager.getInstance(this).isLoggedIn()) {
//            finish();
//            startActivity(new Intent(this, ProfileActivity.class));
//            return;
//        }*/
//
//        userEmail = (EditText) findViewById(R.id.email);
//        userPassword = (EditText) findViewById(R.id.password);
//        confirmPassword = (EditText) findViewById(R.id.password2);
//
//        /* For sign up button */
//        complete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //if user pressed on button register
//                //here we will register the user to server
//                //registerUser();
//            }
//        });
//
//        /* For login button */
//        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //if user pressed on login
//                //we will open the login screen
//                finish();
//                startActivity(new Intent(signUp.this, LoginActivity.class));
//            }
//        });
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


//    private void registerUser() {
//        final String email = userEmail.getText().toString().trim();
//        final String password = userPassword.getText().toString().trim();
//        final String password_confirm = confirmPassword.getText().toString().trim();
//
//        if (TextUtils.isEmpty(email)) {
//            userEmail.setError("Please enter your email");
//            userEmail.requestFocus();
//            return;
//        }
//
//        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            userEmail.setError("Enter a valid email");
//            userEmail.requestFocus();
//            return;
//        }
//
//        if (TextUtils.isEmpty(password)) {
//            userPassword.setError("Enter a password");
//            userPassword.requestFocus();
//            return;
//        }
//
//        if (TextUtils.isEmpty(password_confirm)) {
//            confirmPassword.setError("Confirm a password");
//            confirmPassword.requestFocus();
//            return;
//        }
//
//        if (!password.equals(password_confirm)){
//            userPassword.setError("Passwords do not match");
//            userPassword.requestFocus();
//            confirmPassword.setError("Passwords do not match");
//            confirmPassword.requestFocus();
//            return;
//        }
//
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_SIGNUP, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                progressBar.setVisibility(View.GONE);
//
//                try {
//                    //converting response to json object
//                    JSONObject obj = new JSONObject(response);
//
//                    //if no error in response
//                    if (!obj.getBoolean("error")) {
//                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
//
//                        //getting the user from the response
//                        JSONObject userJson = obj.getJSONObject("user");
//
//                        //creating a new user object
//                        User user = new User(
//                                userJson.getString("name"),
//                                userJson.getString("username"),
//                                userJson.getString("email")
//                        );
//
//                        //storing the user in shared preferences
//                        //SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
//
//                        //starting the profile activity
//                        finish();
//                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
//                    } else {
//                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("email", email);
//                params.put("password", password);
//                return params;
//            }
//        };
//
//        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
//    }

}
