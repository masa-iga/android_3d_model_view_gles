package com.example.masa.modelViewGLES;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MaterialData {
    private static final String TAG = MaterialData.class.getName();
    private final ArrayList<Material> materialArray;

    public class Material {
        public String name;
        public Float ns;
        public Float[] ka;
        public Float[] kd;
        public Float[] ks;
        public Float d;
        public int illum;
        public String map_Kd;

        Material() {
            name = null;
            ka = new Float[3];
            kd = new Float[3];
            ks = new Float[3];
            map_Kd = null;
        }
    }

    MaterialData() {
        materialArray = new ArrayList<>();
    }

    public int getMaterialDataSize() { return materialArray.size(); }

    public Material getMaterial(int idx) {
        try {
            return materialArray.get(idx);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return new Material();
        }
    }

    public void loadMaterialData(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        Material material = new Material();

        while (reader.ready()) {
            String line = reader.readLine();
            if (line == null)
                break;

            Log.v(TAG, line);
            String strAry[] = line.split(" ");

            switch (strAry[0]) {
                case "newmtl" :
                    if (material.name != null) {
                        materialArray.add(material);
                        material = new Material();
                    }
                    material.name = strAry[1];
                    break;
                case "Ns" :
                    material.ns = Float.parseFloat(strAry[1]);
                    break;
                case "Ka" :
                    material.ka[0] = Float.parseFloat(strAry[1]);
                    material.ka[1] = Float.parseFloat(strAry[2]);
                    material.ka[2] = Float.parseFloat(strAry[3]);
                    break;
                case "Kd" :
                    material.kd[0] = Float.parseFloat(strAry[1]);
                    material.kd[1] = Float.parseFloat(strAry[2]);
                    material.kd[2] = Float.parseFloat(strAry[3]);
                    break;
                case "Ks" :
                    material.ks[0] = Float.parseFloat(strAry[1]);
                    material.ks[1] = Float.parseFloat(strAry[2]);
                    material.ks[2] = Float.parseFloat(strAry[3]);
                    break;
                case "d" :
                    material.d = Float.parseFloat(strAry[1]);
                    break;
                case "illum" :
                    material.illum = Integer.parseInt(strAry[1]);
                    break;
                case "map_Kd" :
                    material.map_Kd = strAry[1];
                    break;
                default :
                    Log.v(TAG, "unknown case : " + strAry[0]);
                    break;
            }
        }
        materialArray.add(material);
        Log.i(TAG, "materialArray size : " + getMaterialDataSize());
    }
}
