package tk.friendar.friendar;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, DeviceLocationService.UpdateListener {

    private GoogleMap mMap;
    private Marker current_loc;
    private MarkerOptions mOptions;
    private LatLng loc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mOptions = new MarkerOptions();




    }

    @Override
    protected void onResume() {
        super.onResume();
        DeviceLocationService.getInstance().startLocationUpdates(this);
        DeviceLocationService.getInstance().registerUpdateListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        DeviceLocationService.getInstance().stopLocationUpdates(this);
        DeviceLocationService.getInstance().unregisterUpdateListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        DeviceLocationService.getInstance().handlePermissionResults(this,requestCode,permissions,grantResults);
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

        // Add a marker in Sydney and move the camera
        //LatLng Unimelb = new LatLng(-37.7963690, 144.9611740);
        //mMap.addMarker(new MarkerOptions().position(this.loc).title("Your current location"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(this.loc,15.0f));
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);



    }

    @Override
    public void onLocationUpdate(Location location) {
        Log.d("MapsActivity", "locationUpdate called");

        /*
        mOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
        mMap.addMarker(mOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mOptions.getPosition(),15.0f));
        */

        if(current_loc == null){
            mOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
            current_loc = mMap.addMarker(mOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mOptions.getPosition(),15.0f));
        }
        else {
            mOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
            current_loc.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));

        }

    }
}
