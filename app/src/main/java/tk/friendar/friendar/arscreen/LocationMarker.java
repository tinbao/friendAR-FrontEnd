package tk.friendar.friendar.arscreen;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import tk.friendar.friendar.User;

/**
 * Created by lucah on 5/9/17.
 * Marker for showing each user's location on the screen.
 * Manages loading data and generating an appropriate marker for each user.
 */

public class LocationMarker {
	private int iconTexture = 0;
	public User user;

	// Icon texture loading
	Bitmap iconBitmap = null;
	public boolean shouldUpload = false;

	private static final int ICON_TEXTURE_WIDTH = 256;
	private static final int ICON_TEXTURE_HEIGHT = 256;
	private static int vertexAttrib = 0;
	private static int uvAttrib = 0;

	// vertex and texture coordinate data for quad
	private static final float[] quadData = {
			// vertices         // tex coords (flipped on y-axis)
			-1.0f,  1.0f, 0.0f, 0.0f, 0.0f,
			 1.0f, -1.0f, 0.0f, 1.0f, 1.0f,
			-1.0f, -1.0f, 0.0f, 0.0f, 1.0f,
			-1.0f,  1.0f, 0.0f, 0.0f, 0.0f,
			 1.0f,  1.0f, 0.0f, 1.0f, 0.0f,
			 1.0f, -1.0f, 0.0f, 1.0f, 1.0f
	};

	public LocationMarker(User user) {
		this.user = user;
	}

	// Load and buffer data for the texture quad
	public static void loadQuadData(Shader shader, String vertexAttribName, String texAttribName) {
		int floatSize = 4;
		int stride = 5 * floatSize;

		// Buffer data
		ByteBuffer bb = ByteBuffer.allocateDirect(floatSize * quadData.length);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(quadData);

		// Vertices
		fb.position(0);
		vertexAttrib = GLES20.glGetAttribLocation(shader.getProgram(), vertexAttribName);
		GLES20.glVertexAttribPointer(vertexAttrib, 3, GLES20.GL_FLOAT, false, stride, fb);
		GLES20.glEnableVertexAttribArray(vertexAttrib);

		// Texture coords
		fb.position(3);
		uvAttrib = GLES20.glGetAttribLocation(shader.getProgram(), texAttribName);
		GLES20.glVertexAttribPointer(uvAttrib, 2, GLES20.GL_FLOAT, false, stride, fb);
		GLES20.glEnableVertexAttribArray(uvAttrib);
	}

	// Draw the icon and load it into an OpenGL texture
	public void generateIconTexture() {
		iconBitmap = Bitmap.createBitmap(ICON_TEXTURE_WIDTH, ICON_TEXTURE_HEIGHT, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(iconBitmap);

		// Draw the icon to the canvas/bitmap
		iconBitmap.eraseColor(Color.TRANSPARENT); // clear
		float w = ICON_TEXTURE_WIDTH;
		float h = ICON_TEXTURE_HEIGHT;
		float strokeWidth = 9.0f;
		float radius = w / 2 - strokeWidth / 2;
		float fontSize = 40.0f;
		int color = user.colourFromUsername();

		// Background
		float k = 0.8f;
		int bgColor = Color.argb(255, (int)(Color.red(color) * k), (int)(Color.green(color) * k), (int)(Color.blue(color) * k));
		Paint bgPaint = new Paint();
		bgPaint.setColor(bgColor);
		bgPaint.setStyle(Paint.Style.FILL);
		canvas.drawCircle(w / 2, h / 2, radius, bgPaint);

		// Profile picture
		// If not available, draw their usernme
		Bitmap pic = user.getPicture();
		if (pic != null) {
			Paint picPaint = new Paint();
			android.graphics.Shader picShader = new BitmapShader(pic,
					android.graphics.Shader.TileMode.CLAMP, android.graphics.Shader.TileMode.CLAMP);
			Matrix picMatr = new Matrix();
			picMatr.postScale((float) ICON_TEXTURE_WIDTH / pic.getWidth(), (float) ICON_TEXTURE_HEIGHT / pic.getWidth());
			picShader.setLocalMatrix(picMatr);
			picPaint.setShader(picShader);
			canvas.drawCircle(w / 2, h / 2, radius, picPaint);
		}
		else {
			Paint textPaint = new Paint();
			textPaint.setColor(Color.argb(255, 0, 0, 0));
			textPaint.setTextSize(fontSize);
			String name = user.getName();
			float textWidth = textPaint.measureText(name);
			canvas.drawText(name, w / 2 - textWidth / 2, h / 2 + fontSize / 4, textPaint);
		}

		// Outline
		bgPaint.setColor(color);
		bgPaint.setStyle(Paint.Style.STROKE);
		bgPaint.setStrokeWidth(strokeWidth);
		canvas.drawCircle(w / 2, h / 2, radius, bgPaint);

		// We will likely call this method from outside the GL thread, so mark to to upload later
		shouldUpload = true;
	}

	public void uploadIconTexture() {
		// Create and bind gl texture
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		iconTexture = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, iconTexture);

		// Set texture parameters
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

		// Load image data
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, iconBitmap, 0);

		// Free bitmap and cleanup
		iconBitmap.recycle();
		iconBitmap = null;
		shouldUpload = false;
	}

	// Draw this marker
	public void draw(Shader shader, String samplerUniformName) {
		if (iconTexture != 0) {
			shader.uniformTexture(samplerUniformName, iconTexture, 0);
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
		}
	}
}
