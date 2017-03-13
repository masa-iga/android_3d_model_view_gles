package com.example.masa.modelViewGLES;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLException;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by masa on 2017/02/22.
 */

public final class GLES20Utils {
    private static final String TAG = "GLES20Utils";
    private static final int INVALID = 0;

    private GLES20Utils() {}

    private static final int FIRST_INDEX = 0;
    private static final int DEFAULT_OFFSET = 0;
    private static final int FLOAT_SIZE_BYTES = 4;

    public static FloatBuffer createBuffer(float[] array) {
        final FloatBuffer buffer =
                ByteBuffer.allocateDirect(array.length * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        buffer.put(array).position(FIRST_INDEX);
        return buffer;
    }

    public static int createProgram(final String vertexSource, final String fragmentSource) throws GLException {
        final int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == INVALID) {
            return INVALID;
        }
        final int pixcelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixcelShader == INVALID) {
            return INVALID;
        }

        int program = GLES20.glCreateProgram();
        if (program == INVALID) {
            Log.e(TAG, "glCreateProgram() failed");
            return program;
        }
        GLES20.glAttachShader(program, vertexShader);
        checkGlError("glAttachShader");
        GLES20.glAttachShader(program, pixcelShader);
        checkGlError("glAttachShader");

        GLES20.glLinkProgram(program);
        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, DEFAULT_OFFSET);
        if (linkStatus[FIRST_INDEX] != GLES20.GL_TRUE) {
            Log.e(TAG, "Could not link program: ");
            Log.e(TAG, GLES20.glGetProgramInfoLog(program));
            GLES20.glDeleteProgram(program);
            program = INVALID;
        }

        return program;
    }

    public static int loadShader(final int shaderType, final String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader == INVALID) {
            Log.e(TAG, "glCreateShader() error");
            return shader;
        }
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);
        final int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, DEFAULT_OFFSET);
        if (compiled[FIRST_INDEX] == INVALID) {
            Log.e(TAG, "Shader compile failed! " + shaderType + ":");
            Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
            Log.e(TAG, "source : " + source);
            GLES20.glDeleteShader(shader);
            shader = INVALID;
        }

        return shader;
    }

    public static void checkGlError(final String op) throws GLException {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            throw new GLException(error, op + ": glError " + error);
        }
    }

    public static int loadTexture(final Bitmap bitmap) {
        return loadTexture(bitmap, GLES20.GL_NEAREST, GLES20.GL_LINEAR);
    }

    public static int loadTexture(final Bitmap bitmap, final int min, final int mag) {
        final int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, DEFAULT_OFFSET);

        final int texture = textures[FIRST_INDEX];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, min);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, mag);

        return texture;
    }

}
