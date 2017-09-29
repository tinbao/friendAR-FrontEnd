package tk.friendar.friendar.testing;

import android.location.Location;

import java.util.ArrayList;

import tk.friendar.friendar.Meeting;
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
		james.setLocation(LocationHelper.fromLatLon(-37.78262, 144.99645)); // tram stop
		//james.setPicture(BitmapFactory.decodeResource(getResources(), R.drawable.james));
		friends.add(james);

		User tin = new User("Tin-Tin", "shintin", "tintin@hotmail.net.au");
		tin.setLocation(LocationHelper.fromLatLon(-37.78305, 144.99494)); // walker st
		//tin.setPicture(BitmapFactory.decodeResource(getResources(), R.drawable.tin));
		friends.add(tin);

		User simon = new User("Simon DiCicco", "kamonstone", "simonce@gmail.com");
		simon.setLocation(LocationHelper.fromLatLon(-37.7846, 145.0043)); // heidelberb road bridge
		//simon.setPicture(BitmapFactory.decodeResource(getResources(), R.drawable.simon));
		friends.add(simon);

		return friends;
	}

	public static Location getDeviceLocation() {
		return LocationHelper.fromLatLon(-37.78277, 144.99436);
	}

	public static ArrayList<Meeting> getMeetings() {
		ArrayList<Meeting> meetings = new ArrayList<>();
		Meeting m;

		m = new Meeting("IT Project Devs", 0);
		meetings.add(m);

		m = new Meeting("Cool Guys Club", 1);
		meetings.add(m);

		// Test long group names
		m = new Meeting("Extremely Verbose Title Naming Appreciation Group Meeting", 2);
		meetings.add(m);

		m = new Meeting("Super Dooper Overly Long Titles Why Would Anyone Do This What Am I Doing With My Life", 3);
		meetings.add(m);

		return meetings;
	}

	public static ArrayList<Meeting> getManyMeetings() {
		ArrayList<Meeting> meetings = new ArrayList<>();

		for (int i = 0; i < 20; i++) {
			Meeting m = new Meeting("Meeting " + (i+1), i);
			meetings.add(m);
		}

		return meetings;
	}
}
