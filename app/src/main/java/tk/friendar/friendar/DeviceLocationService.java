package tk.friendar.friendar;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

/**
 * Manages starting location services and enforcing permissions for activities.
 * To use:
 *   Call startLocationUpdates() from onResume()
 *   Call handlePermissionResults() from onRequestPermissionsResult()
 *   Call stopLocationUpdates() in onPause()
 */

public class DeviceLocationService {
	// Permissions
	private static final String[] PERMISSIONS = {
			Manifest.permission.ACCESS_FINE_LOCATION
	};
	private static final int REQUEST_LOCATION = 26;
	private boolean requestedPermissions = false;
	//Activity requestedPermissionsFrom = null;  // activity from which permissions were requested

	// Location services
	private FusedLocationProviderClient locationProviderClient;
	private LocationCallback locationCallback = null;
	private boolean locationUpdatesStarted = false;

	private static final long DEVICE_LOCATION_UPDATE_INTERVAl = 5000;
	private static final long DEVICE_LOCATION_UPDATE_FASTEST_INTERVAl = 1000;

	// Location cache
	private Location lastLocation = null;

	// Update listener
	UpdateListener updateListener = null;

	// Singleton stuff
	private static final DeviceLocationService instance = new DeviceLocationService();
	private DeviceLocationService() {}  // disallow instantiation

	private static final String TAG = "DeviceLocationService";

	public static DeviceLocationService getInstance() {
		return instance;
	}

	// Start location updates for the given activity
	public void startLocationUpdates(AppCompatActivity activity) {
		if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
				== PackageManager.PERMISSION_GRANTED) {
			// Have location permissions
			requestedPermissions = false;

			// Start updates
			requestLocationUpdates(activity);
		}
		else {
			if (requestedPermissions) {
				// Already requested permissions, but user denied
				requestedPermissions = false;

				// Minimize app
				Intent homeIntent = new Intent(Intent.ACTION_MAIN);
				homeIntent.addCategory(Intent.CATEGORY_HOME);
				homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				activity.startActivity(homeIntent);

				// Display error
				Toast.makeText(activity, "FriendAR requires location permissions.", Toast.LENGTH_LONG).show();
			}
			else {
				// don't have location permissions
				// try to get permissions and then try again
				requestedPermissions = true;
				ActivityCompat.requestPermissions(activity, PERMISSIONS, REQUEST_LOCATION);
			}
		}
	}

	@SuppressWarnings({"MissingPermission"})
	private void requestLocationUpdates(Activity activity) {
		Log.d(TAG, "Starting location updates for " + activity.toString());
		locationUpdatesStarted = true;

		// Get location provider
		locationProviderClient = LocationServices.getFusedLocationProviderClient(activity);

		// Build request
		final LocationRequest request = new LocationRequest();
		request.setInterval(DEVICE_LOCATION_UPDATE_INTERVAl);
		request.setFastestInterval(DEVICE_LOCATION_UPDATE_FASTEST_INTERVAl);
		request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		// Location callback
		locationCallback = new LocationCallback() {
			@Override
			public void onLocationResult(LocationResult locationResult) {
				lastLocation = locationResult.getLastLocation();
				notifyListeners(lastLocation);
				putLocationToServer(lastLocation);

				Log.d(TAG, "Location Update: " + lastLocation.toString());
			}
		};

		// Put request into settings
		LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
		builder.addLocationRequest(request);

		SettingsClient settingsClient = LocationServices.getSettingsClient(activity);
		settingsClient.checkLocationSettings(builder.build())
				.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
					@Override
					public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
						Log.d(TAG, "Successfully set location settings. Starting location updates.");

						// Start updates
						locationProviderClient.requestLocationUpdates(request, locationCallback, null);
						locationUpdatesStarted = true;
					}
				})
				.addOnFailureListener(activity, new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						Log.e(TAG, "Could not set location settings: " + e.getMessage());
					}
				});
	}

	// Stop location updates for the given activity
	public void stopLocationUpdates(Activity activity) {
		if (locationUpdatesStarted) {
			// stop updates
			Log.d(TAG, "Stopping location updates for " + activity.toString());
			locationUpdatesStarted = false;
			requestedPermissions = false;

			locationProviderClient.removeLocationUpdates(locationCallback);
			updateListener = null;
		}
	}

	public void handlePermissionResults(Activity activity, int code, String[] permissions, int[] grantResults) {
		if (code == REQUEST_LOCATION) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// We got the permissions!
				// Activity will start location updates from its onResume().
				Log.d(TAG, "Successfully requested location permissions for " + activity.toString());
			}
			else {
				Log.d(TAG, "User rejected requested location permissions for " + activity.toString());
			}
		}
	}

	public Location getLastLocation() {
		return lastLocation;
	}

	// Location update listener
	// Just store one reference since that will work for our cases
	public interface UpdateListener {
		void onLocationUpdate(Location location);
	}

	public void registerUpdateListener(UpdateListener listener) {
		updateListener = listener;
	}

	public void unregisterUpdateListener(UpdateListener listener) {
		updateListener = null;
	}

	private void notifyListeners(Location location) {
		// Consider changing to a thread if needed
		if (updateListener != null) {
			updateListener.onLocationUpdate(location);
		}
	}

	// Server updates
	private void putLocationToServer(Location location) {
		// TODO send location to server here!
	}
}