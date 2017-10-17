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
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tk.friendar.friendar.arscreen.LocationHelper;

public class NewMeetingActivity extends AppCompatActivity implements OnMapReadyCallback {

	UserListAdapter listAdapter;

	GoogleMap map;
	Marker placeMaker = null;

	private static final String TAG = "NewMeetingActivity";

	private ArrayList<User> friends = new ArrayList<>();
	private int successfulMeetinguserPosts;

	public void setFriends(ArrayList<User> friends) {
		this.friends = friends;
		listAdapter = new UserListAdapter(friends);
		ListView listView = (ListView) findViewById(R.id.new_meeting_user_list);
		listView.setAdapter(listAdapter);
	}

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
		try {
			getFriends();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		listAdapter = new UserListAdapter(friends);
		ListView listView = (ListView) findViewById(R.id.new_meeting_user_list);
		listView.setAdapter(listAdapter);

		// Meeting name EditText listener + validator
		final Button submitButton = (Button)findViewById(R.id.new_meeting_submit);
		submitButton.setEnabled(false);

		final EditText meetingName = (EditText) findViewById(R.id.new_meeting_name);

		meetingName.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int i, int i1, int i2) { return; }
			@Override
			public void afterTextChanged(Editable editable)  { return; }

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				submitButton.setEnabled(FormValidator.isValidMeetingName(s.toString()));
			}
		});

		/* For complete new meeting up button */
		submitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				try {
					postNewMeeting(meetingName.getText().toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

		// Map
		MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.new_meeting_map);
		mapFragment.getMapAsync(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		//DeviceLocationService.getInstance().startLocationUpdates(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		//DeviceLocationService.getInstance().stopLocationUpdates(this);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		DeviceLocationService.getInstance().handlePermissionResults(this, requestCode, permissions, grantResults);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		map = googleMap;

		LatLng initPos = new LatLng(-37.7985, 144.9597);
		placeMaker = map.addMarker(new MarkerOptions().
				position(initPos).
				title("Destination").
				draggable(true));
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(initPos, 16.0f));

		// Need to do this to get lat-lon updates on drag
		map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
			@Override
			public void onMarkerDragStart(Marker marker) {}
			@Override
			public void onMarkerDrag(Marker marker) {}
			@Override
			public void onMarkerDragEnd(Marker marker) {}
		});
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
	 */
	public void getFriends() throws JSONException {
		final Context context = getApplicationContext();

		String url = URLs.URL_USERS + "/" + VolleyHTTPRequest.id;
		/* Does a GET request to authenticate the credentials of the user */
		StringRequest req = new StringRequest(Request.Method.GET, url,
			new Response.Listener<String>()
			{
				@Override
				public void onResponse(String response) {
					Log.d("JSON Response", response);
					try {
						JSONObject obj = new JSONObject(response);
						JSONArray resp = obj.has("friends") ? (obj.getJSONArray("friends")) : new JSONArray("[]") ;
						setFriends(getAllFriends(resp));
					} catch (JSONException e) {
						e.printStackTrace();
						Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
					} finally {
						//Toast.makeText(context, "GOT Friends", Toast.LENGTH_LONG).show();
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
		VolleyHTTPRequest.addRequest(req, context);
	}

	/**
	 * Converts the JSON Array of user's friends into an arraylist of users
	 * @param friends User's friends (JSON Array)
	 * @return The Arraylist of user's friends and their locations
	 * @throws JSONException
	 */
	public ArrayList<User> getAllFriends(JSONArray friends) throws JSONException {
		ArrayList<User> allFriends = new ArrayList<>();
		if(friends.length() == 0){
			Toast.makeText(getApplicationContext(), "You have no friends", Toast.LENGTH_SHORT).show();
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
			userFriend.setID(friend.getInt("id"));

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

	/**
	 * Posts a new meeting with selected users and info
	 * @param name
	 * @throws JSONException
	 */
	public void postNewMeeting(String name) throws JSONException{
		final Context context = getApplicationContext();

		if (getSelectedUsers().size() == 0) {
			errorPopup("No friends selected");
			return;
		}
		if (placeMaker == null) {
			errorPopup("Wait for map to load and select location");
			return;
		}

		// first post place

		final JSONObject params = new JSONObject();
        /* Puts the information into the JSON Object */
		params.put("meetingName", name);
		params.put("placeID", 1);
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		params.put("time", timeStamp);

		/* Does a POST request to create the new entry into the DB */
		StringRequest req = new StringRequest(Request.Method.POST, URLs.URL_MEETING,
				new Response.Listener<String>()
				{
					@Override
					public void onResponse(String response) {
						Log.d("JSON Response", response);
						int meetingID;
						try {
							JSONObject json = new JSONObject(response);
							meetingID = json.getInt("id");
							addFriendsToMeeting(meetingID);
						} catch (JSONException e) {
							e.printStackTrace();
							genericError();
							return;
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
			public byte[] getBody() throws AuthFailureError {
				return params.toString().getBytes();
			}

			@Override
			public String getBodyContentType() {
				return "application/json; charset=utf-8";
			}
		};

		req.setShouldCache(false);
		VolleyHTTPRequest.addRequest(req, context);
	}

	public void addFriendsToMeeting(int meetingID) throws JSONException {
		final Context context = getApplicationContext();
		successfulMeetinguserPosts = 0;
		final ArrayList<User> users = new ArrayList<User>(friends);
		User me = new User("", "", ""); // only need user id
		me.setID(VolleyHTTPRequest.id);
		users.add(me);

		for (final User user : users) {
			final JSONObject params = new JSONObject();
			/* Puts the information into the JSON Object */
			params.put("meetingID", meetingID);
			params.put("userID", user.getID());

			/* Does a POST request to create the new entry into the DB */
			StringRequest req = new StringRequest(Request.Method.POST, URLs.URL_MEETINGUSERS,
					new Response.Listener<String>() {
						@Override
						public void onResponse(String response) {
							Log.d("JSON Response", "Response " + successfulMeetinguserPosts + ": " + response);
							try {
								new JSONObject(response);
								successfulMeetinguserPosts++;  // assume valid jasson => success
							} catch (JSONException e) {
								e.printStackTrace();
							}

							if (successfulMeetinguserPosts == users.size()) {
								// All friends added to meeting! Return to home screen
								Toast.makeText(context, "Meeting created!", Toast.LENGTH_LONG).show();
								finish();
							}
						}
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							String msg = error.toString();
							Log.d("ErrorResponse", msg);
							genericError();
						}
					}
			) {
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

				@Override
				public String getBodyContentType() {
					return "application/json; charset=utf-8";
				}
			};

			req.setShouldCache(false);
			VolleyHTTPRequest.addRequest(req, context);
		}
	}

	private void genericError() {
		Toast.makeText(this, "Error making meeting", Toast.LENGTH_SHORT).show();
	}
}
