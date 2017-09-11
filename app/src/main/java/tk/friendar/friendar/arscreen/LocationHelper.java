package tk.friendar.friendar.arscreen;

import android.location.Location;

/**
 * Created by lucah on 6/9/17.
 * Helper class for locations and dealing with android Locations.
 */

public class LocationHelper {
	// Create a location from latitude and longitude values
	public static Location fromLatLon(double lat, double lon) {
		Location loc = new Location("LocationHelper");
		loc.setLatitude(lat);
		loc.setLongitude(lon);
		return loc;
	}
}
