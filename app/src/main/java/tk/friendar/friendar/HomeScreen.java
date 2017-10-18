package tk.friendar.friendar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import tk.friendar.friendar.chat.Chats;
import tk.friendar.friendar.chat.FriendAR_chat;
import tk.friendar.friendar.arscreen.VRActivity;
import tk.friendar.friendar.testing.DummyData;

public class HomeScreen extends AppCompatActivity {
    private GestureDetectorCompat gestureObject;

	private static final String TAG = "HomeScreen";
	public static final String EXTRA_MEETING_ID = "EXTRA_MEETING_ID";
	public static final String EXTRA_MEETING_NAME = "EXTRA_MEETING_NAME";

	private ArrayList<Meeting> meetings = new ArrayList<>();

	public void setMeetings(ArrayList<Meeting> meetings) {
		this.meetings = meetings;
		ArrayAdapter<Meeting> listAdapter = new ArrayAdapter<Meeting>(this,
				R.layout.home_screen_list_elem, meetings);
		ListView listView = (ListView) findViewById(R.id.home_screen_meeting_list);
		listView.setAdapter(listAdapter);
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

		// Meeting List
		//ArrayList<Meeting> meetings = DummyData.getMeetings();
		ArrayAdapter<Meeting> listAdapter = new ArrayAdapter<Meeting>(this,
				R.layout.home_screen_list_elem, meetings);
		ListView listView = (ListView) findViewById(R.id.home_screen_meeting_list);
		listView.setAdapter(listAdapter);

        // Swipe Gestures
        gestureObject = new GestureDetectorCompat(this, new HomeScreen.LearnGesture());
		listView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				return gestureObject.onTouchEvent(motionEvent);
			}
		});

		// Item Click
		listView.setOnItemClickListener(new ItemClickListener());

		// New Group floating button
		FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.home_screen_add_meeting_floating_button);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(HomeScreen.this, NewMeetingActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		try {
			getMeetings();
		} catch (JSONException e) {
			e.printStackTrace();
		}

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
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home_screen_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;

		switch (item.getItemId()) {
			case R.id.action_add_friend:
				intent = new Intent(this, AddFriendActivity.class);
				startActivity(intent);
				return true;
			case R.id.action_open_ar:
				intent = new Intent(this, VRActivity.class);
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	// Gesture Class
    class LearnGesture extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            if (event2.getX() < event1.getX()) {
                //swipe left to right open ar screen
				Intent intent = new Intent(HomeScreen.this, VRActivity.class);
				startActivity(intent);
				return true;
            }
            return false;
        }
    }

    // List item click listener
	class ItemClickListener implements AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Meeting m = (Meeting)parent.getItemAtPosition(position);
			Log.d(TAG, "Clicked: " + m.getName());

			Intent intent = new Intent(HomeScreen.this, FriendAR_chat.class);
			intent.putExtra(EXTRA_MEETING_ID, m.getId());
			intent.putExtra(EXTRA_MEETING_NAME, m.getName());
			startActivity(intent);
		}
	};

	public void newMeeting(View view){
		Intent new_meeting = new Intent(this,NewMeetingActivity.class);
		startActivity(new_meeting);

	}

	/**
	 * Does a GET request to get all the user's meetings/chats
	 */
	public void getMeetings() throws JSONException {
		final Context context = getApplicationContext();

		/* Does a GET request to authenticate the credentials of the user */
		StringRequest req = new StringRequest(Request.Method.GET, URLs.URL_MEETING,
			new Response.Listener<String>()
			{
				@Override
				public void onResponse(String response) {
					Log.d("JSON Response", response);
					try {
						JSONObject obj = new JSONObject(response);
						JSONArray resp = obj.has("meetings: ") ? obj.getJSONArray("meetings: ") : new JSONArray("[]");
						setMeetings(getAllMeetings(resp));
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
	 * Converts the JSON Array of user's meetings into an arraylist of meetings
	 * @param meetings User's meetings (JSON Array)
	 * @return The Arraylist of user's meetings
	 * @throws JSONException
	 */
	public ArrayList<Meeting> getAllMeetings(JSONArray meetings) throws JSONException {
		ArrayList<Meeting> allMeetings = new ArrayList<>();
		if(meetings.length() == 0){
			Toast.makeText(getApplicationContext(), "You have no meetings", Toast.LENGTH_SHORT);
			return allMeetings;
		}

		/* Iterate through the array of meetings */
		for(int i = 0; i < meetings.length(); i++){
			JSONObject meeting = meetings.getJSONObject(i);
			JSONArray meetingUsers = meeting.getJSONArray("meeting users");

			/* Within the array of meetings is an array of users attending the meeting */
			for(int j = 0; j < meetingUsers.length(); j++){

				/* Need to check infividually if the current user is included in the meetings */
				JSONObject meetingUser = meetingUsers.getJSONObject(j);
				String user = meetingUser.getString("user");
				user = user.replace("\\", "");
				JSONObject user_ = new JSONObject(user);

				if(user_.getInt("id") == VolleyHTTPRequest.id){
					String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            		/* Create a meeting object from the JSON data */
					Meeting meetingElem = new Meeting(meeting.getString("meetingName"),
							meeting.getInt("id"), timeStamp);

					allMeetings.add(meetingElem);
				}
			}
		}

		return allMeetings;
	}
}
