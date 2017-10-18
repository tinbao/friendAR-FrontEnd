package tk.friendar.friendar.arscreen;

import android.location.Location;

/**
 * By Luca Harris
 * Provides gentle transitions between infrequent location updates
 */

public class LiveLocationSmoother {
	private long timeStart;
	private Location curr;
	private Location prev;
	private double smoothDuration = 1.0; // seconds
	private double timeBetweenUpdates;

	public LiveLocationSmoother() {
		curr = null;
		prev = null;
		timeStart = System.nanoTime();
	}

	public LiveLocationSmoother(double smoothDuration) {
		this();
		this.smoothDuration = smoothDuration;
	}

	public void newLocation(Location location) {
		timeBetweenUpdates = (System.nanoTime() - timeStart) / 1e9;
		timeStart = System.nanoTime();

		prev = curr;
		curr = location;
	}

	public Location getSmoothedLocation() {
		if (prev == null) {
			return curr;
		}
		double elapsedTime = (System.nanoTime() - timeStart) / 1e9;
		double t = smoothStep(elapsedTime / smoothDuration);
		Location smoothed = new Location("LiveLocationSmoother");
		smoothed.setLatitude(prev.getLatitude() * (1 - t) + curr.getLatitude() * t);
		smoothed.setLongitude(prev.getLongitude() * (1 - t) + curr.getLongitude() * t);
		return smoothed;
	}

	private double smoothStep(double t) {
		t = Math.min(Math.max(0, t), 1);
		return t * t * (3 - 2 * t);
	}
}
