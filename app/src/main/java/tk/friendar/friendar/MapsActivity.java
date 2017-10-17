package tk.friendar.friendar;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
	int meetingID;

	private static final String TAG = "MapsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		meetingID = getIntent().getIntExtra("id", 0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

		String url = URLs.URL_MEETING + "/" + meetingID;
		StringRequest req = new StringRequest(Request.Method.GET, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.d(TAG, "GET meeting:" + response);

						try {
							JSONObject meeting = new JSONObject(response);

							// Place marker
							JSONObject place = new JSONObject(meeting.getString("place").replace("\\", ""));
							LatLng placePos = new LatLng(place.getDouble("latitude"), place.getDouble("longitude"));
							mMap.addMarker(new MarkerOptions().position(placePos).title("Meeting Point"));
							mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placePos, 16.0f));

							// Friends
							JSONArray users = meeting.getJSONArray("meeting users");
							for (int i = 0; i < users.length(); i++) {
								JSONObject user = new JSONObject(users.getJSONObject(i).getString("user").replace("\\", ""));
								LatLng userPos = new LatLng(user.getDouble("latitude"), user.getDouble("longitude"));
								String userName = user.getString("username");
								mMap.addMarker(new MarkerOptions().position(userPos).title(userName)
										.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
							}

						} catch (JSONException e) {
							e.printStackTrace();
							errorAndFinish();
							return;
						}
					}
				},
				new Response.ErrorListener(){
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d(TAG, "ERROR: " + error.toString());
						errorAndFinish();
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
    }

    private void errorAndFinish() {
		Toast.makeText(this, "Error loading map.", Toast.LENGTH_SHORT).show();
		finish();
	}
}
