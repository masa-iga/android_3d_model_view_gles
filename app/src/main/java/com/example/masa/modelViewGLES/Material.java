package com.example.masa.modelViewGLES;

import java.sql.Array;
import java.util.Arrays;

/**
 * Created by masa on 2017/03/13.
 */

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
        ka = new Float[3];
        kd = new Float[3];
        ks = new Float[3];
        name = null;
        map_Kd = null;
    }
}
