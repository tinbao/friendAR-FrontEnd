package tk.friendar.friendar.testing;

import android.location.Location;

import java.util.ArrayList;

import tk.friendar.friendar.User;
import tk.friendar.friendar.arscreen.LocationHelper;

/**
 * Created by lucah on 11/9/17.
 * Use to generate dummy data for testing.
 */

public class DummyData {
	public static ArrayList<User> getFriends() {
		ArrayList<User> friends = new ArrayList<>();

		User james = new User("James Stone", "JamesTone", "jamess@yahoo.com.au");
		// james.setLocation(LocationHelper.fromLatLon(-37.78262, 144.99645)); // tram stop
		james.setLocation(LocationHelper.fromLatLon(-37.79859, 144.96048)); // south lawn
		//james.setPicture(BitmapFactory.decodeResource(getResources(), R.drawable.james));
		friends.add(james);

		User tin = new User("Tin-Tin", "shintin", "tintin@hotmail.net.au");
		//tin.setLocation(LocationHelper.fromLatLon(-37.78305, 144.99494)); // walker st
		tin.setLocation(LocationHelper.fromLatLon(-37.79777, 144.96024)); // old arts
		//tin.setPicture(BitmapFactory.decodeResource(getResources(), R.drawable.tin));
		friends.add(tin);

		User simon = new User("Simon DiCicco", "kamonstone", "simonce@gmail.com");
		//simon.setLocation(LocationHelper.fromLatLon(-37.7846, 145.0043)); // heidelberb road bridge
		simon.setLocation(LocationHelper.fromLatLon(-37.79852, 144.95938)); // baillieu library
		//simon.setPicture(BitmapFactory.decodeResource(getResources(), R.drawable.simon));
		friends.add(simon);

		return friends;
	}

	public static Location getDeviceLocation() {
		return LocationHelper.fromLatLon(-37.78277, 144.99436);
	}
}
