package com.example.masa.modelViewGLES;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.core.deps.guava.collect.ArrayListMultimap;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private static final String TAG = ExampleInstrumentedTest.class.getName();

    // Context of the app under test.
    private Context appContext;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setup() {
        appContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void useAppContext() throws Exception {
        assertEquals("com.example.masa.android_3d_model_view_gles", appContext.getPackageName());
    }

    @Test
    public void objectData_test() throws Exception {
        ObjectData objectData = new ObjectData();
        objectData.loadObjectData(appContext.getResources().openRawResource(R.raw.test_object));
        assertEquals(12, objectData.getVertexSize());
        assertEquals(8,  objectData.getTextureSize());
        assertEquals(2,  objectData.getFaceSize());

        objectData.setupNormal();
        assertEquals(12,  objectData.getNormalSize());
        Float[] expectedNormal = {
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f
        };
        for (int i=0; i < 12; i++) {
            assertEquals(expectedNormal[i], objectData.getNormalCoord(i));
        }
    }

    @Test
    public void materialData_test() throws Exception {
        MaterialData materialData = new MaterialData();
        materialData.loadMaterialData(appContext.getResources().openRawResource(R.raw.test_material));

        assertEquals(2, materialData.getMaterialDataSize());

        float delta = 0.000001f;
        assertEquals("material0", materialData.getMaterial(0).name);
        assertEquals(0.123456, materialData.getMaterial(0).ns,    delta);
        assertEquals(1.111111, materialData.getMaterial(0).ka[0], delta);
        assertEquals(1.222222, materialData.getMaterial(0).ka[1], delta);
        assertEquals(1.333333, materialData.getMaterial(0).ka[2], delta);
        assertEquals(2.111111, materialData.getMaterial(0).kd[0], delta);
        assertEquals(2.222222, materialData.getMaterial(0).kd[1], delta);
        assertEquals(2.333333, materialData.getMaterial(0).kd[2], delta);
        assertEquals(3.111111, materialData.getMaterial(0).ks[0], delta);
        assertEquals(3.222222, materialData.getMaterial(0).ks[1], delta);
        assertEquals(3.333333, materialData.getMaterial(0).ks[2], delta);
        assertEquals(5.555555, materialData.getMaterial(0).d,     delta);
        assertEquals(0, materialData.getMaterial(0).illum);

        assertEquals(3.033333, materialData.getMaterial(1).ks[2], delta);
    }

    @Test
    public void objectModel_test() throws Exception {
        ObjectModel objectModel = new ObjectModel();
        objectModel.loadObjectData(appContext.getResources().openRawResource(R.raw.test_object));
        objectModel.loadMaterialData(appContext.getResources().openRawResource(R.raw.test_material));

        objectModel.setupMaterialName();
        objectModel.setupVtxCoord();
        objectModel.setupTexCoord();
        objectModel.setupNormal();
        objectModel.setupMaterial();

        assertEquals(2, objectModel.objectArray.size());
    }
}
