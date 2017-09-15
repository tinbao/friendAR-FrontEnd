package tk.friendar.friendar.arscreen;

import android.opengl.GLES20;
import android.util.Log;

/**
 * Created by lucah on 4/9/17.
 * General helper for compiling and using OpenGL shaders and programs
 */

public class Shader {
	private int mProgram = 0;
	private static final String TAG = "Shader";

	boolean loadProgram(String vertexShaderCode, String fragmentShaderCode) {
		// Load shaders
		int vs = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int fs = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

		if (vs < 0 || fs < 0) {
			return false;
		}

		// Crete program and attatch shaders
		int program = GLES20.glCreateProgram();
		GLES20.glAttachShader(program, vs);
		GLES20.glAttachShader(program, fs);

		// Link program!
		GLES20.glLinkProgram(program);

		// Flag shaders for deletion (for when the program is deleted)
		GLES20.glDeleteShader(vs);
		GLES20.glDeleteShader(fs);

		// Check for compilation errors
		int[] success = new int[1];
		GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, success, 0);
		if (success[0] == GLES20.GL_FALSE) {
			String error = GLES20.glGetProgramInfoLog(program);
			Log.e(TAG, "Program linking failed: " + error);

			return false;
		}

		mProgram = program;
		return true;
	}

	public void use() {
		if (mProgram == 0) {
			Log.e(TAG, "Program not loaded");
			return;
		}
		GLES20.glUseProgram(mProgram);
	}

	public void delete() {
		GLES20.glDeleteProgram(mProgram);
	}

	public int getProgram() {
		return mProgram;
	}


	// Uniforms

	public void uniformFloat4(String name, float[] values) {
		GLES20.glUniform4fv(uniformLocation(name), 1, values, 0);
	}

	public void uniformMat4(String name, float[] matrix) {
		GLES20.glUniformMatrix4fv(uniformLocation(name), 1, false, matrix, 0);
	}

	public void uniformTexture(String name, int texture, int textureUnitNumber) {
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + textureUnitNumber);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
		GLES20.glUniform1i(uniformLocation(name), textureUnitNumber);
	}

	private int uniformLocation(String name) {
		int location = GLES20.glGetUniformLocation(mProgram, name);
		if (location == -1) Log.e(TAG, "Uniform '" + name + "' not found");
		return location;
	}


	// General helper functions

	// Load and compile and shader
	public static int loadShader(int type, String shaderCode) {
		// Create shader
		int shader = GLES20.glCreateShader(type);

		// add source code and compile, then check for compilation errors
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);

		int[] success = new int[1];
		GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, success, 0);
		if (success[0] == GLES20.GL_FALSE) {
			String error = GLES20.glGetShaderInfoLog(shader);
			String shaderName = (type == GLES20.GL_VERTEX_SHADER) ? "Vertex" : "Fragment";
			Log.e(TAG, shaderName + " shader compilation failed: " + error);

			GLES20.glDeleteShader(shader);
			return -1;
		}

		return shader;
	}
}
