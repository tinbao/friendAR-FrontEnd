package tk.friendar.friendar.arscreen;


import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;

import tk.friendar.friendar.User;
import tk.friendar.friendar.testing.DummyData;

/**
 * Created by lucah on 30/8/17.
 * Activity used for the VR screen.
 * For renderering of this screen, see OverlayRenderer.
 *
 * FIXME Sync position sensor to magnetic north properly
 */

public class VRActivity extends AppCompatActivity implements SensorEventListener {
	// OpenGL renderer overlay
	private OverlayRenderer vrOverlay;

	// Camera
	private CameraPreview cameraPreview;
	CameraHelper camera = new CameraHelper();

	// Sensors
	private SensorManager sensorManager;
	private Sensor rotationSensor;
	private final float[] rotationMatrix = new float[16];

	// Nearby friends
	ArrayList<User> nearbyFriends;

	private static final String TAG = "VRActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// actionbar
		ActionBar ab = getSupportActionBar();
		ab.setTitle("");

		// Camera
		vrOverlay = new OverlayRenderer(this);
		setContentView(vrOverlay);

		// Renderer
		cameraPreview = new CameraPreview(this);
		addContentView(cameraPreview, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

		// Sensors
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
	}

	@Override
	protected void onStart() {
		super.onStart();

		// Ensure we have permissions for the camera
		if (CameraHelper.havePermissions(this)) {
			Log.d(TAG, "Already have camera permissions");
			startCamera();
		} else {
			Log.d(TAG, "Requesting camera permissions");
			CameraHelper.requestPermissions(this);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		vrOverlay.onResume();

		// Listen to rotation sensor
		sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_FASTEST);
	}

	@Override
	protected void onPause() {
		super.onPause();
		vrOverlay.onPause();

		// Unlisten to sensors
		sensorManager.unregisterListener(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		stopCamera();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case CameraHelper.PERMISSION_REQUEST_CAMERA:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// Got permissions
					startCamera();
				}
				else {
					// Did not get permissions, try again...
					CameraHelper.requestPermissions(this);
				}
				break;
		}
	}

	// Camera management
	private void startCamera() {
		camera.open();
		cameraPreview.setAndStartCamera(camera.getCamera());
	}

	private void stopCamera() {
		camera.stopPreview();
		camera.close();
	}

	// Sensors
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
			// Convert rotation vector to a rotation matrix and copy into mRotationMatrix
			SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
			// Update camera transformations in overlay
			vrOverlay.onRotationMatrixChange(rotationMatrix);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {
	}
}
