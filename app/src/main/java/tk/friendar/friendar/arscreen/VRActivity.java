package tk.friendar.friendar.arscreen;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

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

import java.util.ArrayList;

import tk.friendar.friendar.User;

/**
 * Created by lucah on 30/8/17.
 * Activity used for the VR screen.
 * For renderering of this screen, see OverlayRenderer.
 *
 * FIXME Sync position sensor to magnetic north properly
 */

public class VRActivity extends AppCompatActivity implements SensorEventListener {
	// OpenGL renderer overlay
	private OverlayRenderer vrOverlay;

	// Camera
	private CameraPreview cameraPreview;
	CameraHelper camera = new CameraHelper();
	boolean cameraStarted = false;

	// Sensors
	private SensorManager sensorManager;
	private Sensor rotationSensor;
	private final float[] rotationMatrix = new float[16];

	// Location service
	FusedLocationProviderClient locationProviderClient;
	LocationCallback locationCallback = null;
	boolean locationUpdatesStarted = false;
	private static final long DEVICE_LOCATION_UPDATE_INTERVAl = 5000;
	private static final long DEVICE_LOCATION_UPDATE_FASTEST_INTERVAl = 1000;

	// Server requests
	private static final long DEVICE_LOCATION_POST_INTERVAL = 6000;
	private static final long FRIEND_LOCATION_GET_INTERVAL = 10000;
	Handler deviceLocationPost;
	Handler friendLocationGet;
	Runnable deviceLocationPostRunnable;
	Runnable friendLocationGetRunnable;

	// Permissions
	private static final String[] PERMISSIONS = {
			Manifest.permission.CAMERA,
			Manifest.permission.ACCESS_FINE_LOCATION
	};
	private static final int REQUEST_ALL_PERMISSIONS = 25;

	// Nearby friends
	ArrayList<User> nearbyFriends;

	private static final String TAG = "VRActivity";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide action bar
		getSupportActionBar().hide();

		// Camera
		vrOverlay = new OverlayRenderer(this);
		setContentView(vrOverlay);

		// Renderer
		cameraPreview = new CameraPreview(this);
		addContentView(cameraPreview, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

		// Ensure overlay is on top of camera
		vrOverlay.bringToFront();

		// Sensors
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

		// Location
		locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

		// Server requests
		deviceLocationPost = new Handler();
		deviceLocationPostRunnable = new Runnable() {
			@Override
			public void run() {
				Log.d(TAG, "Sending device location to server");
				// TODO send location to server here

				deviceLocationPost.postDelayed(this, DEVICE_LOCATION_POST_INTERVAL);  // loop
			}
		};

		friendLocationGet = new Handler();
		friendLocationGetRunnable = new Runnable() {
			@Override
			public void run() {
				Log.d(TAG, "Requesting friend locations from server");
				// TODO request locations here

				friendLocationGet.postDelayed(this, FRIEND_LOCATION_GET_INTERVAL);  // loop
			}
		};
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
			startDeviceLocationUpdates();

			// Start server post/get loop
			deviceLocationPost.postDelayed(deviceLocationPostRunnable, DEVICE_LOCATION_POST_INTERVAL);
			friendLocationGet.postDelayed(friendLocationGetRunnable, FRIEND_LOCATION_GET_INTERVAL);
		}
		else {
			Log.d(TAG, "Resuming, requesting permissions.");
			ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_ALL_PERMISSIONS);
		}

		// Listen to rotation sensor
		sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_FASTEST);
	}

	@Override
	protected void onPause() {
		super.onPause();
		vrOverlay.onPause();

		// Stop camera and location services
		if (cameraStarted) stopCamera();
		if (locationUpdatesStarted) stopDeviceLocationUpdates();

		// Stop server post/get loops
		deviceLocationPost.removeCallbacks(deviceLocationPostRunnable);
		friendLocationGet.removeCallbacks(friendLocationGetRunnable);

		// Unlisten to sensors
		sensorManager.unregisterListener(this);
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

	// Sensors
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
			// Convert rotation vector to a rotation matrix and copy into mRotationMatrix
			SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
			// Update camera transformations in overlay
			vrOverlay.onRotationMatrixChange(rotationMatrix);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {
	}

	// Device location updates
	@SuppressWarnings({"MissingPermission"})
	private void startDeviceLocationUpdates() {
		// Build request
		final LocationRequest request = new LocationRequest();
		request.setInterval(DEVICE_LOCATION_UPDATE_INTERVAl);
		request.setFastestInterval(DEVICE_LOCATION_UPDATE_FASTEST_INTERVAl);
		request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		// Location callback
		locationCallback = new LocationCallback() {
			@Override
			public void onLocationResult(LocationResult locationResult) {
				Location loc = locationResult.getLastLocation();
				vrOverlay.onDeviceLocationUpdate(loc);
				Log.d(TAG, "New Loc: " + loc.toString());
			}
		};

		// Put request into settings
		LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
		builder.addLocationRequest(request);

		SettingsClient settingsClient = LocationServices.getSettingsClient(this);
		settingsClient.checkLocationSettings(builder.build())
				.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
					@Override
					public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
						Log.d(TAG, "Successfully set location settings.");

						// Start updates
						locationProviderClient.requestLocationUpdates(request, locationCallback, null);
						locationUpdatesStarted = true;
					}
				})
				.addOnFailureListener(this, new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						Log.e(TAG, "Could not set location settings: " + e.getMessage());
					}
				});
	}

	private void stopDeviceLocationUpdates() {
		if (locationCallback != null) {
			locationProviderClient.removeLocationUpdates(locationCallback);
			locationUpdatesStarted = false;
		}
	}
}
