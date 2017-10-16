package tk.friendar.friendar.arscreen;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.hitlabnz.sensor_fusion.orientationProvider.ImprovedOrientationSensor1Provider;
import org.hitlabnz.sensor_fusion.orientationProvider.OrientationProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tk.friendar.friendar.DeviceLocationService;
import tk.friendar.friendar.HomeScreen;
import tk.friendar.friendar.URLs;
import tk.friendar.friendar.User;
import tk.friendar.friendar.VolleyHTTPRequest;
import tk.friendar.friendar.testing.DummyData;

/**
 * Created by lucah on 30/8/17.
 * Activity used for the VR screen.
 * For renderering of this screen, see OverlayRenderer.
 */

public class VRActivity extends AppCompatActivity {
	// OpenGL renderer overlay
	private OverlayRenderer vrOverlay;

	// Camera
	private CameraPreview cameraPreview;
	CameraHelper camera = new CameraHelper();
	boolean cameraStarted = false;

	// Sensors
	private OrientationProvider orientationProvider;
	private final float[] rotationMatrix = new float[16];

	// Server requests
	private static final long FRIEND_LOCATION_GET_INTERVAL = 10000;
	Handler friendLocationGet;
	Runnable friendLocationGetRunnable;

	// Permissions
	private static final String[] PERMISSIONS = {
			Manifest.permission.CAMERA,
			Manifest.permission.ACCESS_FINE_LOCATION
	};
	private static final int REQUEST_ALL_PERMISSIONS = 25;

	// Nearby friends
	ArrayList<User> allFriends = new ArrayList<>();

	// Swipe listener
	private GestureDetectorCompat gestureObject;

	private static final String TAG = "VRActivity";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// actionbar
		ActionBar ab = getSupportActionBar();
		ab.setTitle("");

		// Camera
		vrOverlay = new OverlayRenderer(this);
		setContentView(vrOverlay);

		// Renderer
		cameraPreview = new CameraPreview(this);
		addContentView(cameraPreview, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

		// Ensure overlay is on top of camera
		vrOverlay.bringToFront();

		// Sensors
		SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		orientationProvider = new ImprovedOrientationSensor1Provider(sensorManager);
		vrOverlay.setOrientationProvider(orientationProvider);

		// Server requests
		friendLocationGet = new Handler();
		friendLocationGetRunnable = new Runnable() {
			@Override
			public void run() {
				Log.d(TAG, "Requesting friend locations from server");
                try {
                    updateFriendLocations();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                friendLocationGet.postDelayed(this, FRIEND_LOCATION_GET_INTERVAL);  // loop
			}
		};

		// Swipe gesture
		gestureObject = new GestureDetectorCompat(this, new LearnGesture());
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		vrOverlay.onResume();

		// Check permissions before starting camera and location
		if (haveAllPermissions()) {
			Log.d(TAG, "Resuming, have all permissions.");
			startCamera();
			DeviceLocationService.getInstance().startLocationUpdates(this);
			DeviceLocationService.getInstance().registerUpdateListener(vrOverlay);

			// Start server post/get loop
			friendLocationGet.postDelayed(friendLocationGetRunnable, 100);
		}
		else {
			Log.d(TAG, "Resuming, requesting permissions.");
			ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_ALL_PERMISSIONS);
		}

		// Start rotation sensor
		orientationProvider.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		vrOverlay.onPause();

		// Stop camera and location services
		if (cameraStarted) stopCamera();
		DeviceLocationService.getInstance().unregisterUpdateListener(vrOverlay);
		DeviceLocationService.getInstance().stopLocationUpdates(this);

		// Stop server post/get loops
		friendLocationGet.removeCallbacks(friendLocationGetRunnable);

		// Stop rotation sensore
		orientationProvider.stop();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	private boolean haveAllPermissions() {
		for (String permission : PERMISSIONS) {
			if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case REQUEST_ALL_PERMISSIONS:
				if (grantResults.length == 0) {
					Toast.makeText(this, "The AR screen requires camera and location permissions.", Toast.LENGTH_LONG).show();
					finish();
					return;
				}
				for (int i = 0; i < permissions.length; i++) {
					String pName = "";
					if (permissions[i].equals(Manifest.permission.CAMERA)) pName = "camera";
					if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) pName = "location";

					if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
						String response = "The AR screen requires " + pName + " permissions.";
						Toast.makeText(this, response, Toast.LENGTH_LONG).show();
						Log.d(TAG, "Could not get " + pName + " permissions");
						finish();
						return;
					}
					else {
						Log.d(TAG, "Got " + pName + " permissions");
					}
				}

				// Got all permissions! Camera/location services will be started in onResume()
				Log.d(TAG, "Have all permissions");
				break;
		}
	}

	// Camera management
	private void startCamera() {
		camera.open();
		cameraPreview.setAndStartCamera(camera.getCamera());
		cameraStarted = true;
	}

	private void stopCamera() {
		camera.stopPreview();
		camera.close();
		cameraStarted = false;
	}


	// Gestures
	class LearnGesture extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent event1, MotionEvent event2,
							   float velocityX, float velocityY) {
			if (event1.getX() < event2.getX()) {
				//swipe right to left to return to home screen
				finish();
				return true;
			}
			return false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		this.gestureObject.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	// Location updates
	// Pull all locations from the server
	void updateFriendLocations() throws JSONException {
		allFriends = new ArrayList<>();
        int updatingFriendsTime = 0;
		//allFriends = DummyData.getUpdatingFriends();

        // TODO request all friend locations and fill 'allFriends' with the data
        allFriends = getAllFriends(getFriends());

		vrOverlay.onFriendLocationUpdates(allFriends);
	}


    /**
     * Does a GET request to get all the user's friends
     * @return The JSON Array of all the friends (JSON Objects)
     */
	public JSONArray getFriends(){
		final JSONArray[] resp = {null};
        final Context context = getApplicationContext();

		/* Does a GET request to authenticate the credentials of the user */
		JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, URLs.URL_SIGNUP,
			new Response.Listener<JSONArray>()
			{
				@Override
				public void onResponse(JSONArray response) {
					Log.d("JSON Response",response.toString());
                    Toast.makeText(context, "GOT Friends", Toast.LENGTH_LONG).show();
					resp[0] = response;
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

		return resp[0];
	}

    /**
     * Converts the JSON Array of user's friends into an arraylist of users
     * @param friends User's friends (JSON Array)
     * @return The Arraylist of user's friends and their locations
     * @throws JSONException
     */
	public ArrayList<User> getAllFriends(JSONArray friends) throws JSONException {
		allFriends = new ArrayList<User>();
		if(friends == null){
			Toast.makeText(getApplicationContext(), "You have no friends", Toast.LENGTH_SHORT);
            return allFriends;
		}

		/* Iterate through the array of users */
        for(int i = 0; i < friends.length(); i++){
            JSONObject friend = friends.getJSONObject(i);

            /* Create a user object from the JSON data */
			User userFriend = new User(friend.getString("fullName"), friend.getString("username"),
                    friend.getString("email"));
            userFriend.setLocation(LocationHelper.fromLatLon(friend.getDouble("latitude"),
                    friend.getDouble("longitude")));

            allFriends.add(userFriend);
		}

		return allFriends;
	}
}
