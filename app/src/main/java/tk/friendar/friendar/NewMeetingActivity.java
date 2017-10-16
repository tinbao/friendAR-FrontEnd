package tk.friendar.friendar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tk.friendar.friendar.arscreen.LocationHelper;
import tk.friendar.friendar.testing.DummyData;

public class NewMeetingActivity extends AppCompatActivity {

	UserListAdapter listAdapter;

	private static final String TAG = "NewMeetingActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_meeting);

		// Action bar
		ActionBar ab = getSupportActionBar();
		ab.setTitle("New Meeting");
		ab.setDisplayHomeAsUpEnabled(true);

		// Friend list
		//ArrayList<User> friends = DummyData.getFriends();
		ArrayList<User> friends = new ArrayList<>();
		try {
			friends = getFriends();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		listAdapter = new UserListAdapter(friends);
		ListView listView = (ListView) findViewById(R.id.new_meeting_user_list);
		listView.setAdapter(listAdapter);

		// Meeting name EditText listener + validator
		final Button submitButton = (Button)findViewById(R.id.new_meeting_submit);
		submitButton.setEnabled(false);

		((EditText)findViewById(R.id.new_meeting_name)).addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int i, int i1, int i2) { return; }
			@Override
			public void afterTextChanged(Editable editable)  { return; }

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				submitButton.setEnabled(FormValidator.isValidMeetingName(s.toString()));
			}
		});
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

	public void submitRequest(View view) {
		String name = ((EditText)findViewById(R.id.new_meeting_name)).getText().toString();
		ArrayList<User> users = getSelectedUsers();

		if (users.size() == 0) {
			errorPopup("No friends selected");
		}
		else {
			Log.d(TAG, "Created: " + name);
			for (User user : users) {
				Log.d(TAG, "includes: " + user.getName());
			}
			finish();
		}
	}

	ArrayList<User> getSelectedUsers() {
		ArrayList<User> users = new ArrayList<>();
		ArrayList<Boolean> checkboxes = listAdapter.checkboxStates;
		ArrayList<User> friends = listAdapter.users;
		for (int i = 0; i < checkboxes.size(); i++) {
			if (checkboxes.get(i)) {
				users.add(friends.get(i));
			}
		}
		return users;
	}

	void errorPopup(String error) {
		Snackbar.make(findViewById(R.id.new_meeting_layout),
				error, Snackbar.LENGTH_LONG)
				.show();
	}

	/**
	 * Does a GET request to get all the user's friends
	 * @return The JSON Array of all the friends (JSON Objects)
	 */
	public ArrayList<User> getFriends() throws JSONException {
		final JSONArray[] resp = {new JSONArray()};
		final Context context = getApplicationContext();

		/* Does a GET request to authenticate the credentials of the user */
		JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, URLs.URL_SIGNUP,
				new Response.Listener<JSONObject>()
				{
					@Override
					public void onResponse(JSONObject response) {

						Log.d("JSON Response",response.toString());
						Toast.makeText(context, "GOT Friends", Toast.LENGTH_LONG).show();
						try {
							resp[0] = response.getJSONArray("users: ");
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				},
				new Response.ErrorListener(){
					@Override
					public void onErrorResponse(VolleyError error) {
						String msg = error.toString();
						Log.d("ErrorResponse", msg);
						Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
					}
				}
		){
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> headers = new HashMap<>();
				headers.put("authorization", VolleyHTTPRequest.makeAutho());
				return headers;
			}

			@Override
			public String getBodyContentType() {
				return "application/json; charset=utf-8";
			}
		};

		req.setShouldCache(false);
		VolleyHTTPRequest.addRequest(req, getApplicationContext());
		return getAllFriends(resp[0]);
	}

	/**
	 * Converts the JSON Array of user's friends into an arraylist of users
	 * @param friends User's friends (JSON Array)
	 * @return The Arraylist of user's friends and their locations
	 * @throws JSONException
	 */
	public ArrayList<User> getAllFriends(JSONArray friends) throws JSONException {
		ArrayList<User> allFriends = new ArrayList<>();
		if(friends == null){
			Toast.makeText(getApplicationContext(), "You have no friends", Toast.LENGTH_SHORT);
			return allFriends;
		}

		/* Iterate through the array of users */
		for(int i = 0; i < friends.length(); i++){
			JSONObject friend = friends.getJSONObject(i);
			Log.d("JSON User", friend.toString());

            /* Create a user object from the JSON data */
			User userFriend = new User(friend.getString("fullName"), friend.getString("username"),
					friend.getString("email"));
			userFriend.setLocation(LocationHelper.fromLatLon(friend.getDouble("latitude"),
					friend.getDouble("longitude")));

			allFriends.add(userFriend);
		}

		return allFriends;
	}


	// Custom list adapter for the user list
	class UserListAdapter extends BaseAdapter {
		ArrayList<User> users;
		ArrayList<Boolean> checkboxStates;

		public UserListAdapter(ArrayList<User> users) {
			this.users = users;
			checkboxStates = new ArrayList<>(users.size());
			for (int i = 0; i < users.size(); i++) checkboxStates.add(false);
		}

		@Override
		public int getCount() { return users.size(); }
		@Override
		public User getItem(int position) { return users.get(position);	}
		@Override
		public long getItemId(int position) { return position; }

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final int position2 = position;

			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.activity_new_meeting_list_elem, parent, false);
			}

			// Create listener for checkbox, update state array on click
			((CheckBox)convertView.findViewById(R.id.new_meeting_user_item_checkbox)).
					setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton cb, boolean b) {
							checkboxStates.set(position2, b);
						}
					});

			// Insert user data and checkbox state
			((TextView)convertView.findViewById(R.id.new_meeting_user_item_name))
					.setText(getItem(position).getName());
			((CheckBox)convertView.findViewById(R.id.new_meeting_user_item_checkbox))
					.setChecked(checkboxStates.get(position));

			return convertView;
		}
	}
}
