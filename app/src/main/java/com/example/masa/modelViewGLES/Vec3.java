package com.example.masa.modelViewGLES;

//import android.util.FloatMath;

/**
    GNU GENERAL PUBLIC LICENSE
        Version 3, 29 June 2007

        Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
        Everyone is permitted to copy and distribute verbatim copies
        of this license document, but changing it is not allowed.
 */

import android.util.Log;

/**
 * Simple 3D vector class
 * @author william
 *
 */

public class Vec3 {
    public float x;
    public float y;
    public float z;

    public Vec3() { }
    public Vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3(float[] vals) {
        this.x = vals[0];
        this.y = vals[1];
        this.z = vals[2];
    }

    public Vec3 getUnitVector() {
        float m = magnitude();
        return new Vec3(x / m, y / m, z / m);
    }

    public void normalize() {
        float m = magnitude();
        if (m == 0.0f)
            m = 1.0f;
        set(this.x/m, this.y/m, this.z/m);
    }

    public float magnitude() {
        //return FloatMath.sqrt(x*x + y*y + z*z);
        return (float) Math.sqrt((double)(x*x + y*y + z*z));
    }

    public float dot(Vec3 other) {
        return x * other.x +
                y * other.y +
                z * other.z;
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    /**
     * @param data Must be a float array of 3 elements
     */
    public void set(float[] data) {
        this.x = data[0];
        this.y = data[1];
        this.z = data[2];
    }

    public Vec3 mulLocal(float scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;
        return this;
    }
    public Vec3 mul(float scalar) {
        Vec3 ret = new Vec3();
        ret.x = x * scalar;
        ret.y = y * scalar;
        ret.z = z * scalar;
        return ret;
    }

    public Vec3 addLocal(Vec3 r) {
        x += r.x;
        y += r.y;
        z += r.z;
        return this;
    }

    public Vec3 minusLocal(float x, float y, float z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public float[] toFloats() {
        return new float[] {x, y, z};
    }

    public Vec3 cross(Vec3 v) {
        return new Vec3(
                this.y * v.z - v.y * this.z,
                this.z * v.x - v.z * this.x,
                this.x * v.y - v.x * this.y
        );
//        float x, y, z;
//        x = this.y * v.z - v.y * this.z;
//        y = this.z * v.x - v.z * this.x;
//        z = this.x * v.y - v.x * this.y;
//        Log.d("Vec3", x + " " + y + " " + z);
//        return new Vec3(x, y, z);
    }
}
