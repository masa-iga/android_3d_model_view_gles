package com.example.masa.modelViewGLES;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by masa on 2017/03/02.
 */

public final class SimpleRenderer implements GLSurfaceView.Renderer {
    private final String TAG = "SimpleRenderer";
    private final Context mContext;

    private int frame;
    private ObjectModel mModel;
    private int mProgram;
    private int mPosition;
    private int mTexcoord;
    private int mNormal;
    private int mModelview;
    private int mProjection;
    private int mTexture;
    private int mHasTexture;
    private int mKdcolor;
    private int mKscolor;
    private int mNscolor;
    private int mTextureId;

    private final FloatBuffer mVertexBuffer = GLES20Utils.createBuffer(vertexs);
    private final FloatBuffer mTexcoordBuffer = GLES20Utils.createBuffer(texcoords);

    private static final float vertexs[] = {
            -1.0f,  1.0f, 0.0f,
            -1.0f, -1.0f, 0.0f,
             1.0f,  1.0f, 0.0f,
             1.0f, -1.0f, 0.0f
    };
    private static final float texcoords[] = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f
    };

    private static final String vertex_shader =
            "attribute vec3 position;" +
            "attribute vec2 texcoord;" +
            "attribute vec3 normal;" +
            "uniform mat4 u_modelview;" +
            "uniform mat4 u_projection;" +
            "varying vec2 v_texcoord;" +
            "varying vec3 v_normal;" +
            "varying vec3 v_eye;" +
            "void main() {" +
            "  v_normal = vec3(u_modelview * vec4(normal, 0.0));" +
            "  v_eye = (u_modelview * vec4(position, 1.0)).xyz;" +
            "  v_texcoord = texcoord;" +
            "  gl_Position = u_projection * u_modelview * vec4(position, 1.0);" +
            "}";

    private static final String fragment_shader =
            "precision mediump float;" +
            "varying vec2 v_texcoord;" +
            "varying vec3 v_normal;" +
            "varying vec3 v_eye;" +
            "uniform vec3 u_kdcolor;" +
            "uniform vec3 u_kscolor;" +
            "uniform float u_nscolor;" +
            "uniform float u_hasTexture;" +
            "uniform sampler2D s_texture;" +
            "void main() {" +
            "  vec3 n = normalize(v_normal);" +
            "  vec3 lightPoint = vec3(1.0, 1.0, 1.0);" +
            "  vec3 light_v = normalize(lightPoint);" +
            "  float diffuse = max(dot(light_v, n), 0.0);" +
            "  float specular = pow(max(dot(-normalize(v_eye), reflect(-light_v, n)), 0.0), u_nscolor);" +
            "  float l = 0.5;" +
            "  if (u_hasTexture > 0.5) {" +
            "    vec4 texcolor = texture2D(s_texture, v_texcoord);" +
            "    gl_FragColor.rgb = (u_kdcolor * texcolor.rgb) * (diffuse + l) + u_kscolor * specular;" +
            "    gl_FragColor.a   = texcolor.a;" +
            "  } else {" +
            "    gl_FragColor = vec4(u_kdcolor * diffuse + u_kscolor * specular, 1.0) + vec4(u_kdcolor, 1.0) * vec4(l, l, l, 1.0);" +
            "  }" +
            "}";

    public SimpleRenderer(final Context context) {
        frame = 0;
        mContext = context;
        try {
            mModel = new ObjectModel();
            mModel.loadObjFromFile(context.getResources().openRawResource(R.raw.miku_obj));
            mModel.loadMtlFromFile(context.getResources().openRawResource(R.raw.miku_mtl));
            mModel.setupModel();
        } catch (java.io.IOException e) {
            Log.v("SimpleRenderer", "loading model: "+e);
        }
    }
    // The system calls this method once, when creating the GLSurfaceView.
    // Use this method to perform actions that need to happen only once,
    // such as setting OpenGL environment parameters or initializing OpenGL graphic objects.
    @Override
    public void onSurfaceCreated(final GL10 gl, final EGLConfig config) {
        mProgram = GLES20Utils.createProgram(vertex_shader, fragment_shader);
        if (mProgram == 0) {
            throw new IllegalStateException();
        }
        GLES20.glUseProgram(mProgram);
        GLES20Utils.checkGlError("glUserProgram");

        mPosition = GLES20.glGetAttribLocation(mProgram, "position");
        GLES20Utils.checkGlError("glGetAttribLocation position");
        if (mPosition == -1) {
            throw new IllegalStateException("Could not get attrib location for position");
        }
        GLES20.glEnableVertexAttribArray(mPosition);

        mTexcoord = GLES20.glGetAttribLocation(mProgram, "texcoord");
        GLES20Utils.checkGlError("glGetAttribLocation texcoord");
        if (mTexcoord == -1) {
            throw new IllegalStateException("Could not get texcoord");
        }
        GLES20.glEnableVertexAttribArray(mTexcoord);

        mNormal = GLES20.glGetAttribLocation(mProgram, "normal");
        GLES20Utils.checkGlError("glGetAttribLocation normal");
        if (mNormal == -1) {
            throw new IllegalStateException("Could not get normal");
        }
        GLES20.glEnableVertexAttribArray(mNormal);

        mModelview = GLES20.glGetUniformLocation(mProgram, "u_modelview");
        GLES20Utils.checkGlError("glGetAttribLocation u_modelview");
        if (mModelview == -1) {
            throw new IllegalStateException("Could not get u_modelview");
        }
        GLES20.glEnableVertexAttribArray(mModelview);

        mProjection = GLES20.glGetUniformLocation(mProgram, "u_projection");
        GLES20Utils.checkGlError("glGetAttribLocation u_projection");
        if (mProjection == -1) {
            throw new IllegalStateException("Could not get u_projection");
        }
        GLES20.glEnableVertexAttribArray(mProjection);

        mKdcolor = GLES20.glGetUniformLocation(mProgram, "u_kdcolor");
        GLES20Utils.checkGlError("glGetUniformLocation u_kdcolor");
        if (mKdcolor == -1) {
            throw new IllegalStateException("Could not get uniform location for u_kdcolor");
        }

        mKscolor = GLES20.glGetUniformLocation(mProgram, "u_kscolor");
        GLES20Utils.checkGlError("glGetUniformLocation u_kscolor");
        if (mKscolor == -1) {
            throw new IllegalStateException("Could not get uniform location for u_kscolor");
        }

        mNscolor = GLES20.glGetUniformLocation(mProgram, "u_nscolor");
        GLES20Utils.checkGlError("glGetUniformLocation u_nscolor");
        if (mNscolor == -1) {
            throw new IllegalStateException("Could not get uniform location for u_nscolor");
        }

        mHasTexture = GLES20.glGetUniformLocation(mProgram, "u_hasTexture");
        GLES20Utils.checkGlError("glGetUniformLocation u_hasTexture");
        if (mHasTexture == -1) {
            throw new IllegalStateException("Could not get uniform location for u_hasTexture");
        }

        mTexture = GLES20.glGetUniformLocation(mProgram, "s_texture");
        GLES20Utils.checkGlError("glGetUniformLocation s_texture");
        if (mTexture == -1) {
            throw new IllegalStateException("Could not get uniform location for s_texture");
        }

        final Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.eyem2);
        mTextureId = GLES20Utils.loadTexture(bitmap);
        bitmap.recycle();

        if (Boolean.FALSE) {
            Log.d(TAG, "--- mVertexBuffer ---");
            for (int i = 0; i < 4; i++) {
                Log.d(TAG, i + " : (" + mVertexBuffer.get(3 * i) + ", " + mVertexBuffer.get(3 * i + 1) + ", " + mVertexBuffer.get(3 * i + 2) + ")");
            }
            Log.d(TAG, "--- mTexcoordBuffer ---");
            for (int i = 0; i < 4; i++) {
                Log.d(TAG, i + " : (" + mTexcoordBuffer.get(2 * i) + ", " + mTexcoordBuffer.get(2 * i + 1) + ")");
            }
        }
    }

    // The system calls this method on each redraw of the GLSurfaceView.
    // Use this method as the primary execution point for drawing (and re-drawing) graphic objects.
    @Override
    public void onSurfaceChanged(final GL10 gl, final int width, final int height) {
        final int x = 0, y = 0;
        GLES20.glViewport(x, y, width, height);
        GLES20Utils.checkGlError("glViewport");
    }

    // The system calls this method when the GLSurfaceView geometry changes,
    // including changes in size of the GLSurfaceView or orientation of the device screen.
    // For example, the system calls this method when the device changes from portrait to
    // landscape orientation. Use this method to respond to changes in the GLSurfaceView container.
    @Override
    public void onDrawFrame(final GL10 gl) {
        float mvMat[] = new float[16];
        float pjMat[] = new float[16];

        Matrix.setIdentityM(mvMat, 0);
        Matrix.translateM(mvMat, 0, 0, -2, -7);
        Matrix.rotateM(mvMat, 0, -frame * 1.0f, 0, 1, 0);
        Matrix.frustumM(pjMat, 0, -1, 1, -1, 1, 3, 10);

        GLES20.glUniformMatrix4fv(mModelview, 1, false, mvMat, 0);
        GLES20Utils.checkGlError("glUniformMatrix4fv mvMat");
        GLES20.glUniformMatrix4fv(mProjection, 1, false, pjMat, 0);
        GLES20Utils.checkGlError("glUniformMatrix4fv pjMat");

        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        GLES20Utils.checkGlError("glClearColor");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20Utils.checkGlError("glClear");

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20Utils.checkGlError("glEnable GL_BLEND");
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20Utils.checkGlError("glBlendFunc");

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20Utils.checkGlError("glEnable GL_DEPTH_TEST");

        if (Boolean.TRUE) {
            for (int i=0; i < mModel.objectArray.size(); i++) {
                ObjectModel.Object obj     = mModel.objectArray.get(i);
                FloatBuffer vertexBuffer   = obj.mVerticesBuffer;
                FloatBuffer texCoordBuffer = obj.mTexCoordBuffer;
                FloatBuffer normalBuffer   = obj.mNormalBuffer;
                FloatBuffer nsBuffer       = obj.ns;
                FloatBuffer kdBuffer       = obj.kd;
                FloatBuffer ksBuffer       = obj.ks;

                if (obj.hasTexture) {
                    if (frame == 0) {
                        Log.d(TAG, "texture material : " + obj.name);
                        Log.d(TAG, "texture file     : " + obj.map_Kd);
                    }
                    GLES20.glUniform1f(mHasTexture, 1.0f);
                    GLES20Utils.checkGlError("glUniform1f mHasTexture");

                    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                    GLES20Utils.checkGlError("glActiveTexture");
                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
                    GLES20Utils.checkGlError("glBindTexture");
                    GLES20.glUniform1i(mTexture, 0);
                    GLES20Utils.checkGlError("glUniform1i mTexture");
                } else {
                    GLES20.glUniform1f(mHasTexture, 0.0f);
                    GLES20Utils.checkGlError("glUniform1f mHasTexture");
                }

                GLES20.glUniform1fv(mNscolor, 1, nsBuffer);
                GLES20Utils.checkGlError("glUniform1fv nsBuffer");
                GLES20.glUniform3fv(mKdcolor, 1, kdBuffer);
                GLES20Utils.checkGlError("glUniform3fv kdBuffer");
                GLES20.glUniform3fv(mKscolor, 1, ksBuffer);
                GLES20Utils.checkGlError("glUniform3fv ksBuffer");
                GLES20.glVertexAttribPointer(mPosition, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
                GLES20Utils.checkGlError("glVertexAttribPointer vertexBuffer");
                GLES20.glVertexAttribPointer(mTexcoord, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer);
                GLES20Utils.checkGlError("glVertexAttribPointer texCoordBuffer");
                GLES20.glVertexAttribPointer(mNormal, 3, GLES20.GL_FLOAT, false, 0, normalBuffer);
                GLES20Utils.checkGlError("glVertexAttribPointer normalBuffer");

                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mModel.objectArray.get(i).triangleNum);
                GLES20Utils.checkGlError("glVertexAttribPointer glDrawArrays");
            }
        }
        GLES20.glDisable(GLES20.GL_BLEND);
        frame++;
    }
}
