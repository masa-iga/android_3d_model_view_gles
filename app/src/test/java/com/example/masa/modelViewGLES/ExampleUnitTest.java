package com.example.masa.modelViewGLES;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void point3_test() throws Exception {
        Point3 point = new Point3(0.0f, 0.0f, 0.0f);
        point.set(1.0f, 2.0f, 3.0f);
        float delta = 0.0001f;
        assertEquals(1.0f, point.x, delta);
        assertEquals(2.0f, point.y, delta);
        assertEquals(3.0f, point.z, delta);
    }

}