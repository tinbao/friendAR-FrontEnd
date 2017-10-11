package tk.friendar.friendar;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;

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
	Activity requestedPermissionsFrom = null;  // activity from which permissions were requested

	// Location services
	FusedLocationProviderClient locationProviderClient;
	LocationCallback locationCallback = null;
	boolean locationUpdatesStarted = false;

	private static final long DEVICE_LOCATION_UPDATE_INTERVAl = 5000;
	private static final long DEVICE_LOCATION_UPDATE_FASTEST_INTERVAl = 1000;

	// Location cache
	private Location lastLocation = null;

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
			// start updates
			requestLocationUpdates(activity);
		}
		else {
			if (requestedPermissionsFrom == null) {
				// don't have location permissions
				// try to get permissions and then try again
				requestedPermissionsFrom = activity;
				ActivityCompat.requestPermissions(activity, PERMISSIONS, REQUEST_LOCATION);
			}
			else if (requestedPermissionsFrom == activity) {
				// Already requested permissions, but user denied
				requestedPermissionsFrom = null;

				// Minimize app
				Intent homeIntent = new Intent(Intent.ACTION_MAIN);
				homeIntent.addCategory(Intent.CATEGORY_HOME);
				homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				activity.startActivity(homeIntent);

				// Display error
				Toast.makeText(activity, "FriendAR requires location permissions.", Toast.LENGTH_LONG).show();
			}
		}
	}

	private void requestLocationUpdates(Activity activity) {
		Log.d(TAG, "Starting location updates for " + activity.toString());
		locationUpdatesStarted = true;
	}

	// Stop location updates for the given activity
	public void stopLocationUpdates(Activity activity) {
		if (locationUpdatesStarted) {
			// stop updates
			Log.d(TAG, "Stopping location updates for " + activity.toString());
			requestedPermissionsFrom = null;
			locationUpdatesStarted = false;
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
}
