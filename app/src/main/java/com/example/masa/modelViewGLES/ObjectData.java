package com.example.masa.modelViewGLES;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class ObjectData {
    private static final String TAG = ObjectData.class.getName();
    private final ArrayList<Float>      vtxCoordArray;
    private final ArrayList<Float>      texCoordArray;
    private final ArrayList<FaceData>   faceDataArray;
    private Float[]                     normalCoordArray;

    private final String PARSE_MATERIAL       = "usemtl";
    private final String PARSE_VERTEX_COORD   = "v";
    private final String PARSE_TEX_COORD      = "vt";
    private final String PARSE_POLYGONAL_FACE = "f";

    public class FaceData {
        String materialFile;
        public ArrayList<Integer> vtxIdxArray;
        public ArrayList<Integer> texIdxArray;

        FaceData() {
            vtxIdxArray = new ArrayList<>();
            texIdxArray = new ArrayList<>();
        }
    }

    ObjectData() {
        vtxCoordArray = new ArrayList<>();
        texCoordArray = new ArrayList<>();
        faceDataArray = new ArrayList<>();
    }

    public int getVertexSize()  { return vtxCoordArray.size(); }
    public int getTextureSize() { return texCoordArray.size(); }
    public int getFaceSize()    { return faceDataArray.size(); }
    public int getNormalSize()  { return normalCoordArray.length; }

    public Float getVtxCoord(int idx) {
        try {
            return vtxCoordArray.get(idx);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return Float.MIN_VALUE;
        }
    }

    public Float getTexCoord(int idx) {
        try {
            return texCoordArray.get(idx);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return Float.MIN_VALUE;
        }
    }

    public FaceData getFaceData(int idx) {
        try {
            return faceDataArray.get(idx);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return new FaceData();
        }
    }

    public Float getNormalCoord(int idx) {
        try {
            return normalCoordArray[idx];
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return Float.MIN_VALUE;
        }
    }

    public void loadObjectData(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        FaceData faceData = new FaceData();

        while (reader.ready()) {
            String line = reader.readLine();
            if (line == null)
                break;

            Log.v(TAG, line);
            String strAry[] = line.split(" ");
            switch (strAry[0]) {
                case PARSE_MATERIAL :
                    if (faceData.materialFile != null) {
                        faceDataArray.add(faceData);
                        faceData = new FaceData();
                    }
                    faceData.materialFile = strAry[1];
                    break;
                case PARSE_VERTEX_COORD :
                    for (int i=1; i < strAry.length; i++)
                        vtxCoordArray.add(Float.parseFloat(strAry[i]));
                    break;
                case PARSE_TEX_COORD :
                    for (int i=1; i < strAry.length; i++)
                        texCoordArray.add(Float.parseFloat(strAry[i]));
                    break;
                case PARSE_POLYGONAL_FACE :
                    for (int i=1; i < strAry.length; i++) {
                        String str[] = strAry[i].split("/");
                        if (str.length == 2) {
                            faceData.vtxIdxArray.add(Integer.parseInt(str[0]));
                            faceData.texIdxArray.add(Integer.parseInt(str[1]));
                        }
                    }
                    break;
                default :
                    Log.v(TAG, "unknown case : " + strAry[0]);
                    break;
            }
        }
        faceDataArray.add(faceData);

        Log.i(TAG, "vertex coord array size  : " + getVertexSize());
        Log.i(TAG, "texture coord array size : " + getTextureSize());
        Log.i(TAG, "face data array size     : " + getFaceSize());
        setupNormal();
    }

    public void setupNormal() {
        if (normalCoordArray != null) {
            Log.w(TAG, "normalCoordArray is already set");
            return ;
        }
        normalCoordArray = new Float[getVertexSize()];
        Arrays.fill(normalCoordArray, 0.0f);
        for (int i=0; i < getFaceSize(); i++) {
            FaceData faceData = getFaceData(i);
            for (int j=0; j < faceData.vtxIdxArray.size(); j += 3) {
                int idx0 = 3 * (faceData.vtxIdxArray.get(j+0) - 1); // idx v0 x, y, z
                int idx1 = 3 * (faceData.vtxIdxArray.get(j+1) - 1); // idx v1 x, y, z
                int idx2 = 3 * (faceData.vtxIdxArray.get(j+2) - 1); // idx v2 x, y, z
                Vec3 p0 = new Vec3(vtxCoordArray.get(idx0+0), vtxCoordArray.get(idx0+1), vtxCoordArray.get(idx0+2));
                Vec3 p1 = new Vec3(vtxCoordArray.get(idx1+0), vtxCoordArray.get(idx1+1), vtxCoordArray.get(idx1+2));
                Vec3 p2 = new Vec3(vtxCoordArray.get(idx2+0), vtxCoordArray.get(idx2+1), vtxCoordArray.get(idx2+2));
                Vec3 v0 = p1.minusLocal(p0.x, p0.y, p0.z);
                Vec3 v1 = p2.minusLocal(p0.x, p0.y, p0.z);
                Vec3 cross = v0.cross(v1);
                cross.normalize();
                normalCoordArray[idx0+0] += cross.x;
                normalCoordArray[idx0+1] += cross.y;
                normalCoordArray[idx0+2] += cross.z;
                normalCoordArray[idx1+0] += cross.x;
                normalCoordArray[idx1+1] += cross.y;
                normalCoordArray[idx1+2] += cross.z;
                normalCoordArray[idx2+0] += cross.x;
                normalCoordArray[idx2+1] += cross.y;
                normalCoordArray[idx2+2] += cross.z;
            }
        }
        normalizeNormal();
    }

    public void normalizeNormal() {
        for (int i=0; i < getNormalSize(); i += 3) {
            Vec3 v = new Vec3(normalCoordArray[i+0], normalCoordArray[i+1], normalCoordArray[i+2]);
            v.normalize();
            normalCoordArray[i+0] = v.x;
            normalCoordArray[i+1] = v.y;
            normalCoordArray[i+2] = v.z;
        }
    }
}
