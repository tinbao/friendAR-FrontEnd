package tk.friendar.friendar;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddFriendActivity extends AppCompatActivity {
	private ProgressDialog pd;

	private static final String TAG = "AddFriendActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_friend);

		// Action bar
		ActionBar ab = getSupportActionBar();
		ab.setTitle("Add Friend");
		ab.setDisplayHomeAsUpEnabled(true);

		// Field listeners + validators
		((Button)findViewById(R.id.add_friend_submit)).setEnabled(false);

		((EditText)findViewById(R.id.add_friend_username)).addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int i, int i1, int i2) { return; }
			@Override
			public void afterTextChanged(Editable editable)  { return; }

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				((Button)findViewById(R.id.add_friend_submit))
						.setEnabled(FormValidator.isValidUsername(s.toString()));
			}
		});

		// Progress window
		pd = new ProgressDialog(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		DeviceLocationService.getInstance().startLocationUpdates(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		DeviceLocationService.getInstance().stopLocationUpdates(this);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		DeviceLocationService.getInstance().handlePermissionResults(this, requestCode, permissions, grantResults);
	}

	public void submitFriendRequest(View view) {
		final String username = ((EditText)findViewById(R.id.add_friend_username)).getText().toString();

		if (username.equals(VolleyHTTPRequest.getUsername())) {
			errorPopup("You can't friend yourself.");
			return;
		}

		// First try to find if user exists
		String url = URLs.URL_USERS;
		StringRequest req = new StringRequest(Request.Method.GET, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.d(TAG, "RESPONSE: " + response.toString());

						try {
							JSONObject json = new JSONObject(response);
							JSONArray users = json.getJSONArray("users: ");
							for (int i = 0; i < users.length(); i++) {
								JSONObject user = users.getJSONObject(i);
								int id = user.getInt("id");
								if (user.getString("username").equals(username)) {
									// User found, check if new then friend them
									Log.d(TAG, "Found user: " + user.toString());

									checkNewFriends(username, id);
									return;
								}
							}
							// No user found
							errorPopup("Could not find user");
							pd.hide();
						} catch (JSONException e) {
							e.printStackTrace();
							genericServerError();
							pd.hide();
						}
					}
				},

				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						NetworkResponse response = error.networkResponse;
						String msg = error.toString();
						Log.d(TAG, "ERROR: " + msg);
						genericServerError();
						pd.hide();
					}
				}) {

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> headers = new HashMap<>();
				headers.put("authorization", VolleyHTTPRequest.makeAutho());
				return headers;
			}

			/** Defines the body type of the data being posted */
			@Override
			public String getBodyContentType() {
				return "application/json; charset=utf-8";
			}
		};

		// Show progress box
		pd.setMessage("Finding user...");
		pd.show();
		pd.setCancelable(false);
		pd.setCanceledOnTouchOutside(false);

		/* Requests are posted and executed in a queue */
		req.setShouldCache(false);
		Volley.newRequestQueue(this).add(req);
	}

	void errorPopup(String error) {
		Snackbar.make(findViewById(R.id.add_friend_layout),
				error, Snackbar.LENGTH_LONG)
				.show();
	}

	void genericServerError() {
		Toast.makeText(AddFriendActivity.this, "Error sending friend request", Toast.LENGTH_SHORT).show();
	}

	void successPopup(String s) {
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}

	void checkNewFriends(final String username, final int friendID) {
		String url = URLs.URL_USERS + "/" + VolleyHTTPRequest.id;
		StringRequest req = new StringRequest(Request.Method.GET, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.d(TAG, "RESPONSE: " + response.toString());

						try {
							JSONObject thisUser = new JSONObject(response);
							if (thisUser.has("friends")) {
								JSONArray friends = thisUser.getJSONArray("friends");
								for (int i = 0; i < friends.length(); i++) {
									JSONObject friend = friends.getJSONObject(i);
									int id = friend.getInt("id");
									if (id == friendID) {
										// Already friended user!
										errorPopup("Already friended this user.");
										pd.hide();
										return;
									}
								}
							}
							// new user!
							sendFriendRequest(username, friendID);
						} catch (JSONException e) {
							e.printStackTrace();
							genericServerError();
							pd.hide();
						}
					}
				},

				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						NetworkResponse response = error.networkResponse;
						Log.d(TAG, "ERROR: " + error.toString());
						genericServerError();
						pd.hide();
					}
				}) {

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> headers = new HashMap<>();
				headers.put("authorization", VolleyHTTPRequest.makeAutho());
				return headers;
			}

			/** Defines the body type of the data being posted */
			@Override
			public String getBodyContentType() {
				return "application/json; charset=utf-8";
			}
		};

		/* Requests are posted and executed in a queue */
		req.setShouldCache(false);
		Volley.newRequestQueue(this).add(req);
	}

	void sendFriendRequest(final String username, int friendID) {
		final JSONObject params = new JSONObject();
		try {
            /* Temporary username, user can change it afterwards */
			params.put("userA_ID", VolleyHTTPRequest.id);
			params.put("userB_ID", friendID);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String url = URLs.URL_FRIENDSHIPS;
		StringRequest req = new StringRequest(Request.Method.POST, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.d(TAG, "RESPONSE: " + response.toString());

						successPopup("Friended " + username + "!");
						finish();
					}
				},

				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						NetworkResponse response = error.networkResponse;
						Log.d(TAG, "ERROR: " + error.toString());
						genericServerError();
						pd.hide();
					}
				}) {

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> headers = new HashMap<>();
				headers.put("authorization", VolleyHTTPRequest.makeAutho());
				return headers;
			}

			@Override
			public byte[] getBody() throws AuthFailureError {
				return params.toString().getBytes();
			}

			/** Defines the body type of the data being posted */
			@Override
			public String getBodyContentType() {
				return "application/json; charset=utf-8";
			}
		};

		/* Requests are posted and executed in a queue */
		req.setShouldCache(false);
		Volley.newRequestQueue(this).add(req);
	}
}
