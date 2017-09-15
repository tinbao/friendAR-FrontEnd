package tk.friendar.friendar.arscreen;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by lucah on 15/8/17.
 * View used to draw the camera preview.
 *
 * FIXME adjust surface dimensions to match camera dimensions (currently squashed vertically)
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera camera = null;
    private static final String TAG = "CameraPreview";

	private boolean surfaceCreated = false;
	private boolean startPreviewOnCreated = false;

    public CameraPreview(Context context) {
        super(context);

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
    }

    public void setAndStartCamera(Camera camera) {
		this.camera = camera;
		startPreview();
	}

	private void startPreview() {
		// If the surface hasn't been created yet, try again when it has
		if (!surfaceCreated) {
			startPreviewOnCreated = true;
			return;
		}

		// Otherwise try to start
		try {
			camera.setPreviewDisplay(mHolder);
			camera.setDisplayOrientation(90);
			camera.startPreview();
		} catch (IOException e) {
			throw new RuntimeException("ERROR starting camera preview");
		}
	}

    public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG,"surface created");
		surfaceCreated = true;

		// Start preview if it hasn't been already
		if (startPreviewOnCreated) {
			startPreview();
			startPreviewOnCreated = false;
		}
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG,"surface destroyed");
		surfaceCreated = false;
		// release the Camera in activity
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		Log.d(TAG,"surface changed");
		// preview surface or camera does not exist yet
		if (mHolder.getSurface() == null || camera == null){
            return;
        }

        // Stop preview before making changes
		camera.stopPreview();

		// Paramater changes
		Camera.Parameters params = camera.getParameters();

		// Auto focus
		if (params.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
			params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
		}
		else {
			Log.d(TAG, "Device does not support CONTINUOUS_VIDEO auto-focus");
		}

		// Finalize changes and restart camera
        camera.setParameters(params);
		startPreview();
    }
}
