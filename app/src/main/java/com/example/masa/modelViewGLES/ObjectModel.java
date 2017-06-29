package com.example.masa.modelViewGLES;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class ObjectModel {
    private static final String TAG = ObjectModel.class.getName();
    private static final int FIRST_INDEX = 0;

    // todo: get it private
    public ArrayList<MyObject> objectArray;
    private ObjectData objectData;
    private MaterialData materialData;

    public class MyObject {
        public String name;
        public FloatBuffer vtxBuffer;
        public FloatBuffer texBuffer;
        public FloatBuffer normalBuffer;
        public FloatBuffer ns;
        public FloatBuffer ka;
        public FloatBuffer kd;
        public FloatBuffer ks;
        public Float d;
        public int illum;
        public String map_Kd;
        public boolean hasTexture;
        public int triangleNum;

        MyObject() {
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

    ObjectModel() {
        objectArray = new ArrayList<>();
        objectData = new ObjectData();
        materialData = new MaterialData();
    }

    public void loadObjectData(InputStream inputStream) {
        try {
            objectData.loadObjectData(inputStream);
        } catch (IOException e) {
            Log.e(TAG, "file not found : " + inputStream);
            e.printStackTrace();
        }
    }

    public void loadMaterialData(InputStream inputStream) {
        try {
            materialData.loadMaterialData(inputStream);
        } catch (IOException e) {
            Log.e(TAG, "file not found : " + inputStream);
            e.printStackTrace();
        }
    }

    public final void setupMaterialName() {
        for (int i=0; i < objectData.getFaceSize(); i++) {
            ObjectData.FaceData faceData = objectData.getFaceData(i);
            MyObject myObject = new MyObject();
            myObject.name = faceData.materialFile;
            objectArray.add(myObject);
        }
    }

    public final void setupMaterial() {
        for (int i=0; i < objectArray.size(); i++) {
            MyObject myObject = objectArray.get(i);
            for (int j = 0; j < materialData.getMaterialDataSize(); j++) {
                MaterialData.Material material = materialData.getMaterial(j);
                if (!myObject.name.equals(material.name)) {
                    continue;
                }
                myObject.ns.put(material.ns);   myObject.ns.position(FIRST_INDEX);
                for (int k=0; k < 3; k++) { myObject.ka.put(material.ka[k]); }
                myObject.ka.position(FIRST_INDEX);
                for (int k=0; k < 3; k++) { myObject.kd.put(material.kd[k]); }
                myObject.kd.position(FIRST_INDEX);
                for (int k=0; k < 3; k++) { myObject.ks.put(material.ks[k]); }
                myObject.ks.position(FIRST_INDEX);
                myObject.d = material.d;
                myObject.illum = material.illum;
                if (material.map_Kd != null) {
                    myObject.hasTexture = Boolean.TRUE;
                    myObject.map_Kd = material.map_Kd;
                }
                break;
            }
        }
    }

    public final void setupVtxCoord() {
        for (int i=0; i < objectArray.size(); i++) {
            MyObject myObject = objectArray.get(i);
            for (int j=0; j < objectData.getFaceSize(); j++) {
                ObjectData.FaceData faceData = objectData.getFaceData(j);
                if (!myObject.name.equals(faceData.materialFile)) {
                    continue;
                }
                int bufferSize = 3 * faceData.vtxIdxArray.size() * Float.SIZE;
                myObject.vtxBuffer = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder()).asFloatBuffer();
                for (int k=0; k < faceData.vtxIdxArray.size(); k++) {
                    int idx = 3 * (faceData.vtxIdxArray.get(k) - 1); // '-1' means array index starts from index 0, '3' means each index composed of triangle
                    Vec3 vec3 = new Vec3(objectData.getVtxCoord(idx+0), objectData.getVtxCoord(idx+1), objectData.getVtxCoord(idx+2));
                    myObject.vtxBuffer.put(vec3.x);
                    myObject.vtxBuffer.put(vec3.y);
                    myObject.vtxBuffer.put(vec3.z);
                }
                myObject.vtxBuffer.position(FIRST_INDEX);
                myObject.triangleNum = faceData.vtxIdxArray.size();
                break;
            }
        }
    }

    public final void setupNormal() {
        for (int i=0; i < objectArray.size(); i++) {
            MyObject myObject = objectArray.get(i);
            for (int j=0; j < objectData.getFaceSize(); j++) {
                ObjectData.FaceData faceData = objectData.getFaceData(j);
                if (!myObject.name.equals(faceData.materialFile)) {
                    continue;
                }
                int bufferSize = 3 * faceData.vtxIdxArray.size() * Float.SIZE;
                myObject.normalBuffer = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder()).asFloatBuffer();
                for (int k=0; k < faceData.vtxIdxArray.size(); k++) {
                    int idx = 3 * (faceData.vtxIdxArray.get(k) - 1); // '-1' means array index starts from index 0, '3' means each index composed of triangle
                    Vec3 vec3 = new Vec3(objectData.getNormalCoord(idx+0), objectData.getNormalCoord(idx+1), objectData.getNormalCoord(idx+2));
                    myObject.normalBuffer.put(vec3.x);
                    myObject.normalBuffer.put(vec3.y);
                    myObject.normalBuffer.put(vec3.z);
                }
                myObject.normalBuffer.position(FIRST_INDEX);
                break;
            }
        }
    }

    public final void setupTexCoord() {
        for (int i=0; i < objectArray.size(); i++) {
            MyObject myObject = objectArray.get(i);
            for (int j=0; j < objectData.getFaceSize(); j++) {
                ObjectData.FaceData faceData = objectData.getFaceData(j);
                if (!myObject.name.equals(faceData.materialFile)) {
                    continue;
                }
                int bufferSize = 2 * faceData.texIdxArray.size() * Float.SIZE;
                myObject.texBuffer = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder()).asFloatBuffer();
                for (int k=0; k < faceData.texIdxArray.size(); k++) {
                    int idx = 2 * (faceData.texIdxArray.get(k) - 1); // subtract offset "1"
                    Vec3 vec3 = new Vec3(objectData.getTexCoord(idx+0), objectData.getTexCoord(idx+1), 0.0f);
                    myObject.texBuffer.put(vec3.x);
                    myObject.texBuffer.put(vec3.y);
                }
                myObject.texBuffer.position(FIRST_INDEX);
                break;
            }
        }
    }
}
