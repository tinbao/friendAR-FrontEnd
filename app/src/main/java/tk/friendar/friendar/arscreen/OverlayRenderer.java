package tk.friendar.friendar.arscreen;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.location.Location;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.Iterator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import tk.friendar.friendar.User;
import tk.friendar.friendar.testing.DummyData;

/**
 * Created by lucah on 30/8/17.
 * OpenGL view and renderer used for the VR screen.
 */

public class OverlayRenderer extends GLSurfaceView implements GLSurfaceView.Renderer {
	// Transformations
	private float[] modelMatrix = new float[16];
	private float[] viewMatrix = new float[16];
	private float[] objectToWorld = new float[16];  // view * model
	private float[] projectionMatrix = new float[16];
	private float[] mvpMatrix = new float[16];

	// Locations
	private Location deviceLocation;
	private ArrayList<LocationMarker> nearbyFriends;

	// Shaders
	private Shader markerShader = new Shader();

	private final String vsCode =
			"attribute vec3 vPosition;" +
			"attribute vec2 vTexCoord;" +
			"varying vec2 texCoord;" +
			"uniform mat4 uMVPMatrix;" +
			"void main() {" +
			"  texCoord = vTexCoord;" +
			"  gl_Position = uMVPMatrix * vec4(vPosition, 1.0);" +
			"}";

	private final String fsCode =
			"precision mediump float;" +
			"varying vec2 texCoord;" +
			"uniform sampler2D iconTex;" +
			"void main() {" +
			"  vec4 col = texture2D(iconTex, texCoord);" +
			"  if (col.a <= 0.0) discard;" +
			"  gl_FragColor = col;" +
			"}";

	private static final String TAG = "OverlayRenderer";

	public OverlayRenderer(Context context) {
		super(context);

		// Init OpenGL
		setEGLContextClientVersion(2);

		// Make surface translucent
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);  // 8bits for r,g,b,a and 16 for depth
		getHolder().setFormat(PixelFormat.TRANSLUCENT);

		// Finalize renderer
		setRenderer(this);

		// Locations
		nearbyFriends = new ArrayList<>();
		deviceLocation = DummyData.getDeviceLocation();  // TODO real location
	}

	@Override
	public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
		GLES20.glClearColor(0, 0, 0, 0);

		// Enable depth testing
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);

		// Load markerShader
		if (!markerShader.loadProgram(vsCode, fsCode)) {
			throw new RuntimeException("Couldn't compile AR Shader");
		}
		markerShader.use();

		// Load shape
		LocationMarker.loadQuadData(markerShader, "vPosition", "vTexCoord");

		// TODO get actualy friends from activity
		ArrayList<User> friends = DummyData.getFriends();
		for (User friend : friends) {
			onFriendInRange(friend);
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl10, int width, int height) {
		GLES20.glViewport(0, 0, width, height);

		calculateProjectionMatrix(width, height);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		super.surfaceDestroyed(holder);
	}

	@Override
	public void onDrawFrame(GL10 gl10) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		markerShader.use();

		// Draw all markers
		// Calculate the model matrix and then the MVP
		for (LocationMarker marker : nearbyFriends) {
			float distance = deviceLocation.distanceTo(marker.user.getLocation());
			float bearing = deviceLocation.bearingTo(marker.user.getLocation());

			calculateModelMatrix(bearing, distance);
			calculateMVP();
			markerShader.uniformMat4("uMVPMatrix", mvpMatrix);

			marker.draw(markerShader, "iconTex");
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}


	// Friend connect/disconnect events
	public void onFriendInRange(User friend) {
		LocationMarker marker = new LocationMarker(friend);
		marker.generateIconTexture();
		nearbyFriends.add(marker);
	}

	public void onFriendOutOfRange(User friend) {
		Iterator<LocationMarker> iter = nearbyFriends.iterator();
		while (iter.hasNext()) {
			LocationMarker marker = iter.next();
			if (marker.user == friend) {
				iter.remove();
			}
		}
	}


	// Calculate model matrix from position relative to camera
	private void calculateModelMatrix(float bearing, float distance) {
		// distance and scale to draw
		// modelDistance is linear for small distances, but asymptotes at 1km
		// scale counter acts distance (inverse square law), but not fully
		// combination gives appearance of distance without limiting visibility
		float modelDistance = 0.7f * distance / (1.0f + distance / 999.0f);
		float scale = (float)Math.pow(modelDistance, 0.5f);

		double rBearing = Math.toRadians(bearing);
		float counterAngle = -bearing;// + (float)Math.toRadians(180);  // counter rotation to face viewer
		float distanceNorth = (float)Math.cos(rBearing) * modelDistance;
		float distanceEast = (float)Math.sin(rBearing) * modelDistance;

		// Transformations (use reverse order for transformations: translate -> rotate -> scale)
		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.translateM(modelMatrix, 0, distanceEast, 0, -distanceNorth);
		Matrix.rotateM(modelMatrix, 0, counterAngle, 0.0f, 1.0f, 0.0f); // face toward viewer
		Matrix.scaleM(modelMatrix, 0, scale, scale, scale);
	}

	// Recalculate transformations on device rotation change.
	// Only need to update view matrix (our perspective changed).
	public void onRotationMatrixChange(float[] rotationMatrix) {
		// Rotation
		// rotate the rotation matrix forward to match intuitive rotation
		Matrix.rotateM(viewMatrix, 0, rotationMatrix, 0, 90.0f, 1.0f, 0.0f, 0.0f);

		// treat camera as 0,0,0 so no translation needed
		// Matrix.translateM(viewMatrix, x, y, z);
	}

	// calculate projection matrix from display dimensions
	private void calculateProjectionMatrix(float screenWidth, float screenHeight) {
		float ratio = screenWidth / screenHeight;
		Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 1.0f, 1000.0f);
	}

	// recalculate MVP matrix
	private void calculateMVP() {
		Matrix.multiplyMM(objectToWorld, 0, viewMatrix, 0, modelMatrix, 0);
		Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, objectToWorld, 0);
	}
}