package com.example.masa.modelViewGLES;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * Created by masa on 2017/03/05.
 */


public class ObjectModel {
    private final static String TAG = "ObjectModel";
    private static final int FIRST_INDEX = 0;

    public ArrayList<Object> objectArray = new ArrayList<>();

    private final ArrayList<Float>    verticesArray = new ArrayList<>();
    private final ArrayList<Float>    textCoordArray = new ArrayList<>();
    private final ArrayList<Integer>  faceVertexArray = new ArrayList<>();
    private final ArrayList<Integer>  faceTexCoordArray = new ArrayList<>();
    private final ArrayList<Material> materialArray = new ArrayList<>();
    private final ArrayList<Face>     faceArray = new ArrayList<>();
    private Float[] normalArray;

    public void loadObjFromFile(InputStream inputStream) throws IOException {
        Face face = new Face();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        while (reader.ready()) {
            String line = reader.readLine();
            if (line == null)
                break;

            //Log.v(TAG, line);
            String strAry[] = line.split(" ");
            if (strAry[0].equals("usemtl")){
                if (face.mtlFile != null) {
                    faceArray.add(face);
                    face = new Face();
                }
                face.mtlFile = strAry[1];
            } else if (strAry[0].equals("v")) { // vertex
                for (int i=1; i < strAry.length; i++) {
                    //Log.v(TAG, i + " : " + strAry[i]);
                    verticesArray.add(Float.parseFloat(strAry[i]));
                }
            } else if (strAry[0].equals("vt")) { // texture coordinate
                for (int i=1; i < strAry.length; i++) {
                    //Log.v(TAG, i + " : " + strAry[i]);
                    textCoordArray.add(Float.parseFloat(strAry[i]));
                }
            } else if (strAry[0].equals("f")) { // Polygonal face element
                for (int i=1; i < strAry.length; i++) {
                    String str[] = strAry[i].split("/");
                    if (str.length == 2) {
                        faceVertexArray.add(Integer.parseInt(str[0]));
                        faceTexCoordArray.add(Integer.parseInt(str[1]));

                        face.verIdxArray.add(Integer.parseInt(str[0]));
                        face.texIdxArray.add(Integer.parseInt(str[1]));
                    }
                }
            }
        }
        normalArray = new Float[verticesArray.size()];
        Arrays.fill(normalArray, 0.0f);
        Log.d(TAG, "Allocate " + verticesArray.size() + " elements for normalArray");

        faceArray.add(face);
        Log.d(TAG, "faceArray size : " + faceArray.size());
        if (Boolean.TRUE) {
            for (int i = 0; i < faceArray.size(); i++) {
                Log.d(TAG, "face " + i + " name        : " + faceArray.get(i).mtlFile);
                Log.d(TAG, "face " + i + " verIdx size : " + faceArray.get(i).verIdxArray.size());
            }
        }
    }

    public void loadMtlFromFile(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        Material material = new Material();

        while (reader.ready()) {
            String line = reader.readLine();
            if (line == null)
                break;

            //Log.d(TAG, line);
            String strAry[] = line.split(" ");
            if (strAry[0].equals("newmtl")) {
                //Log.d(TAG, "newmtl : " + strAry[1]);
                //Log.d(TAG, "material name : " + material.name);
                if (material.name != null) {
                    materialArray.add(material);
                    material = new Material();
                }
                material.name = strAry[1];
            } else if(strAry[0].equals("Ns")) {
                material.ns = Float.parseFloat(strAry[1]);
            } else if(strAry[0].equals("Ka")) {
                material.ka[0] = Float.parseFloat(strAry[1]);
                material.ka[1] = Float.parseFloat(strAry[2]);
                material.ka[2] = Float.parseFloat(strAry[3]);
            } else if(strAry[0].equals("Kd")) {
                material.kd[0] = Float.parseFloat(strAry[1]);
                material.kd[1] = Float.parseFloat(strAry[2]);
                material.kd[2] = Float.parseFloat(strAry[3]);
            } else if(strAry[0].equals("Ks")) {
                material.ks[0] = Float.parseFloat(strAry[1]);
                material.ks[1] = Float.parseFloat(strAry[2]);
                material.ks[2] = Float.parseFloat(strAry[3]);
            } else if(strAry[0].equals("d")) {
                material.d = Float.parseFloat(strAry[1]);
            } else if(strAry[0].equals("illum")) {
                material.illum = Integer.parseInt(strAry[1]);
            } else if(strAry[0].equals("map_Kd")) {
                material.map_Kd = strAry[1];
            }
        }
        materialArray.add(material);
        Log.d(TAG, "materialArray size : " + materialArray.size());
        if (Boolean.FALSE) {
            for (int i = 0; i < materialArray.size(); i++) {
                Log.d(TAG, "material " + i + " name : " + materialArray.get(i).name);
                Log.d(TAG, "material " + i + " ns   : " + materialArray.get(i).ns);
            }
        }
    }

    public final void setupModel() {
        for (int i=0; i < faceArray.size(); i++) {
            Material material = null;
            Face face = faceArray.get(i);

            for (int j=0; j < materialArray.size(); j++) {
                if (face.mtlFile.equals(materialArray.get(j).name)) {
                    material = materialArray.get(j);
                    break;
                }
            }
            if (material == null) {
                Log.e(TAG, face.mtlFile + " not found");
                break;
            }
            Log.d(TAG, "material name : " + material.name);

            Object obj = new Object();
            obj.name = material.name;

            obj.ns.put(material.ns);
            obj.ns.position(FIRST_INDEX);
            for (int j=0; j < 3; j++) {
                obj.ka.put(material.ka[j]);
            }
            obj.ka.position(FIRST_INDEX);
            for (int j=0; j < 3; j++) {
                obj.kd.put(material.kd[j]);
            }
            obj.kd.position(FIRST_INDEX);
            for (int j=0; j < 3; j++) {
                obj.ks.put(material.ks[j]);
            }
            obj.ks.position(FIRST_INDEX);

            obj.d  = material.d;
            obj.illum = material.illum;
            if (material.map_Kd != null) {
                obj.hasTexture = Boolean.TRUE;
                obj.map_Kd = material.map_Kd;
            }
            obj.triangleNum = face.verIdxArray.size();

            int size = face.verIdxArray.size() * (Float.SIZE * 3);
            obj.mVerticesBuffer = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder()).asFloatBuffer();
            obj.mNormalBuffer   = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder()).asFloatBuffer();

            // Set up vertex and vertex index
            for (int j=0; j < face.verIdxArray.size(); j+=3) {
                int idx0 = 3 * (face.verIdxArray.get(j+0) - 1); // idx v0 x, y, z
                int idx1 = 3 * (face.verIdxArray.get(j+1) - 1); // idx v1 x, y, z
                int idx2 = 3 * (face.verIdxArray.get(j+2) - 1); // idx v2 x, y, z
                Vec3 v0 = new Vec3(verticesArray.get(idx0+0), verticesArray.get(idx0+1), verticesArray.get(idx0+2));
                Vec3 v1 = new Vec3(verticesArray.get(idx1+0), verticesArray.get(idx1+1), verticesArray.get(idx1+2));
                Vec3 v2 = new Vec3(verticesArray.get(idx2+0), verticesArray.get(idx2+1), verticesArray.get(idx2+2));
                obj.mVerticesBuffer.put(v0.x);
                obj.mVerticesBuffer.put(v0.y);
                obj.mVerticesBuffer.put(v0.z);
                obj.mVerticesBuffer.put(v1.x);
                obj.mVerticesBuffer.put(v1.y);
                obj.mVerticesBuffer.put(v1.z);
                obj.mVerticesBuffer.put(v2.x);
                obj.mVerticesBuffer.put(v2.y);
                obj.mVerticesBuffer.put(v2.z);

                // Generate normal vector
//                Log.d(TAG, "v0: " + v0.x + " " + v0.y + " " + v0.z);
//                Log.d(TAG, "v1: " + v1.x + " " + v1.y + " " + v1.z);
//                Log.d(TAG, "v2: " + v2.x + " " + v2.y + " " + v2.z);
                Vec3 v3 = v1.minusLocal(v0.x, v0.y, v0.z);
                Vec3 v4 = v2.minusLocal(v0.x, v0.y, v0.z);
//                Log.d(TAG, "v3: " + v3.x + " " + v3.y + " " + v3.z);
//                Log.d(TAG, "v4: " + v4.x + " " + v4.y + " " + v4.z);
                Vec3 cross = v3.cross(v4);
//                Log.d(TAG, "cross: " + cross.x + " " + cross.y + " " + cross.z);
                cross.normalize();
//                Log.d(TAG, "cross: " + cross.x + " " + cross.y + " " + cross.z);

                normalArray[idx0+0] += cross.x;
                normalArray[idx0+1] += cross.y;
                normalArray[idx0+2] += cross.z;
                normalArray[idx1+0] += cross.x;
                normalArray[idx1+1] += cross.y;
                normalArray[idx1+2] += cross.z;
                normalArray[idx2+0] += cross.x;
                normalArray[idx2+1] += cross.y;
                normalArray[idx2+2] += cross.z;
            }
            obj.mVerticesBuffer.position(FIRST_INDEX);

            // Set up normal
            for (int j=0; j < face.verIdxArray.size(); j+=3) {
                int idx0 = 3 * (face.verIdxArray.get(j+0) - 1); // idx v0 x, y, z
                int idx1 = 3 * (face.verIdxArray.get(j+1) - 1); // idx v1 x, y, z
                int idx2 = 3 * (face.verIdxArray.get(j+2) - 1); // idx v2 x, y, z

                Vec3 n0 = new Vec3(normalArray[idx0+0], normalArray[idx0+1], normalArray[idx0+2]);
                Vec3 n1 = new Vec3(normalArray[idx1+0], normalArray[idx1+1], normalArray[idx1+2]);
                Vec3 n2 = new Vec3(normalArray[idx2+0], normalArray[idx2+1], normalArray[idx2+2]);
                n0.normalize();
                n1.normalize();
                n2.normalize();

                obj.mNormalBuffer.put(n0.x);
                obj.mNormalBuffer.put(n0.y);
                obj.mNormalBuffer.put(n0.z);
                obj.mNormalBuffer.put(n1.x);
                obj.mNormalBuffer.put(n1.y);
                obj.mNormalBuffer.put(n1.z);
                obj.mNormalBuffer.put(n2.x);
                obj.mNormalBuffer.put(n2.y);
                obj.mNormalBuffer.put(n2.z);
            }
            obj.mNormalBuffer.position(FIRST_INDEX);

            // Texture
            size = face.texIdxArray.size() * (Float.SIZE * 2);
            obj.mTexCoordBuffer = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder()).asFloatBuffer();
            for (int j=0; j < face.texIdxArray.size(); j++) {
                int idx = 2 * (face.texIdxArray.get(j) - 1);
                obj.mTexCoordBuffer.put(textCoordArray.get(idx+0)); // tex x
                obj.mTexCoordBuffer.put(textCoordArray.get(idx+1)); // tex y
            }
            obj.mTexCoordBuffer.position(FIRST_INDEX);

            objectArray.add(obj);
        }
    }

    public class Face {
        String mtlFile;
        public ArrayList<Integer> verIdxArray;
        public ArrayList<Integer> texIdxArray;
        Face() {
            verIdxArray = new ArrayList<>();
            texIdxArray = new ArrayList<>();
        }
    }

    public class Object {
        public String name;
        public FloatBuffer mVerticesBuffer;
        public FloatBuffer mTexCoordBuffer;
        public FloatBuffer mNormalBuffer;
        public FloatBuffer ns;
        public FloatBuffer ka;
        public FloatBuffer kd;
        public FloatBuffer ks;
        public Float d;
        public int illum;
        public String map_Kd;
        public boolean hasTexture;
        public int triangleNum;

        Object() {
            // About how to create buffers in Java, refer to
            // http://librastudio.hatenablog.com/entry/2013/08/19/010615
            int size = Float.SIZE * 1;
            ns = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder()).asFloatBuffer();
            size = Float.SIZE * 3;
            ka = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder()).asFloatBuffer();
            kd = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder()).asFloatBuffer();
            ks = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder()).asFloatBuffer();
            hasTexture = Boolean.FALSE;
        }
    }
}
