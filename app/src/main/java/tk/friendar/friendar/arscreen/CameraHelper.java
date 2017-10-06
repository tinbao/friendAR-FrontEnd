package tk.friendar.friendar.arscreen;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by lucah on 29/8/17.
 * Helper class for android Camera.
 * Helps open/close and get permissions.
 */

public class CameraHelper {
	private Camera mCamera = null;
	private static final String TAG = "CameraHelper";

	public Camera getCamera() {
		return mCamera;
	}

	public boolean open() {
		boolean success;

		try {
			mCamera = Camera.open(0);
			success = true;
		} catch (RuntimeException e) {
			// Camera open in another app
			success = false;
			Log.d(TAG, "Can't open camera");
		}

		return success;
	}

	public void close() {
		mCamera.release();
		mCamera = null;
	}

	public void startPreview() {
		mCamera.startPreview();
	}

	public void stopPreview() {
		mCamera.stopPreview();
	}
}
