/**
 * Created by lucah on 8/9/17.
 * A user of the app. Can represent the user of the app or another user (eg a friend).
 */

package tk.friendar.friendar;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
	private String name;
	private String username;
	private String email;
	private Location location = null;
	private Bitmap picture = null;
	private int id = 0;

	public User(String name, String username, String email) {
		this.name = name;
		this.username = username;
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Bitmap getPicture() {
		return picture;
	}

	public void setID(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}

	/**
	 * Creates a new JSON User to send to server
	 * @return JSON user
	 * @throws JSONException
	 */
	public JSONObject createJSONUser() throws JSONException {
		final JSONObject newObj = new JSONObject();
		newObj.put("username", username);
		newObj.put("email", email);
		newObj.put("latitude", location.getLatitude());
		newObj.put("longitude", location.getLongitude());
		newObj.put("fullName", name);

		return newObj;
	}

	public void setPicture(Bitmap picture) {
		this.picture = picture;
	}

	// Generate a pretty color based on the user's username
	public int colourFromUsername() {
		int hash = username.hashCode();
		int n = Math.abs(hash) % 12;
		int min = 80;
		int mid = 180;

		switch (n) {
			//
			case 0:
				return Color.rgb(255, min, min);
			case 1:
				return Color.rgb(min, 255, min);
			case 2:
				return Color.rgb(min, min, 255);
			//
			case 3:
				return Color.rgb(255, min, mid);
			case 4:
				return Color.rgb(255, mid, min);
			case 5:
				return Color.rgb(min, 255, mid);
			case 6:
				return Color.rgb(mid, 255, min);
			case 7:
				return Color.rgb(min, mid, 255);
			case 8:
				return Color.rgb(mid, min, 255);
			//
			case 9:
				return Color.rgb(min, 255, 255);
			case 10:
				return Color.rgb(255, min, 255);
			case 11:
				return Color.rgb(255, 255, min);
			default:
				return 0;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || o.getClass() != this.getClass()) return false;
		if (o == this) return true;

		User user = (User)o;
		// Consider storing user ID and using for equality testing
		return (user.username.equals(this.username) || user.email.equals(this.email));
	}
}
