package tk.friendar.friendar;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
	private void requestLocationUpdates(final Activity activity) {
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
				putLocationToServer(lastLocation, activity);

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
	private void putLocationToServer(Location location, final Activity activity) {
		// TODO send location to server here!
		final JSONObject params = new JSONObject();
        /* Puts the information into the JSON Object */
		try {
            /* Temporary username, user can change it afterwards */
			params.put("latitude", location.getLatitude());
			params.put("logitude", location.getLongitude());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT, URLs.URL_SIGNUP, params,
			new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					Log.d("Response", response.toString());
					Toast.makeText(activity, "Location Sent", Toast.LENGTH_LONG).show();
				}
			},

			new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					NetworkResponse response = error.networkResponse;
					String msg = error.toString();
					Log.d("ErrorResponse", msg);
					Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
				}
			}) {

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> headers = new HashMap<>();
				headers.put("authorization",
						String.format("Basic %s", Base64.encodeToString(
								String.format("%s:%s", login_screen.getUser(),
										login_screen.getPass()).getBytes(), Base64.DEFAULT)));
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
		Volley.newRequestQueue(activity).add(req);
	}
}
