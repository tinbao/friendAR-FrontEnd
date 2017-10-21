package tk.friendar.friendar.testing;

import android.location.Location;
import android.util.Log;

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

	public static ArrayList<User> getManyFriends() {
		ArrayList<User> friends = new ArrayList<>();

		for (int i = 0; i < 20; i++) {
			User friend = new User("Friend " + (i + 1), "fr3nd" + i, "friend" + i + "@gmail.com");
			friend.setLocation(LocationHelper.fromLatLon(-37.7, 144.9));
			friends.add(friend);
		}

		return friends;
	}

	private static ArrayList<User> updatingFriends = null;
	private static int updatingFriendsTime = 0;
	public static ArrayList<User> getUpdatingFriends() {
		if (updatingFriends == null) {
			updatingFriends = getFriends();
			for (User friend : updatingFriends) {
				friend.setLocation(LocationHelper.fromLatLon(-40, 144.9)); // initially far away
			}
		}

		int n = updatingFriends.size();
		boolean moveInrange = (updatingFriendsTime % (n * 2)) / n == 0;
		int i = updatingFriendsTime % n;

		User friend = updatingFriends.get(i);
		if (moveInrange) {
			friend.setLocation(LocationHelper.fromLatLon(-37.78 + i * 0.001, 144.99));
		}
		else {
			friend.setLocation(LocationHelper.fromLatLon(-40, 144.9));
		}
		updatingFriends.set(i, friend);

		updatingFriendsTime++;
		return updatingFriends;
	}

	public static Location getDeviceLocation() {
		return LocationHelper.fromLatLon(-37.78277, 144.99436);
	}

	public static ArrayList<Meeting> getMeetings() {
		ArrayList<Meeting> meetings = new ArrayList<>();
		Meeting m;

		m = new Meeting("IT Project Devs", 0, "");
		meetings.add(m);

		m = new Meeting("Cool Guys Club", 1, "");
		meetings.add(m);

		// Test long group names
		m = new Meeting("Extremely Verbose Title Naming Appreciation Group Meeting", 2, "");
		meetings.add(m);

		m = new Meeting("Super Dooper Overly Long Titles Why Would Anyone Do This What Am I Doing With My Life", 3, "");
		meetings.add(m);

		return meetings;
	}

	public static ArrayList<Meeting> getManyMeetings() {
		ArrayList<Meeting> meetings = new ArrayList<>();

		for (int i = 0; i < 20; i++) {
			Meeting m = new Meeting("Meeting " + (i+1), i, "");
			meetings.add(m);
		}

		return meetings;
	}
}
