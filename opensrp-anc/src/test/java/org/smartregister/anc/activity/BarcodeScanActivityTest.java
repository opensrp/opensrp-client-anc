package org.smartregister.anc.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.SurfaceHolder;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.anc.R;
import org.smartregister.anc.barcode.CameraSourcePreview;

public class BarcodeScanActivityTest extends BaseActivityUnitTest {
    @Mock
    private SparseArray<Barcode> barcodeSparseArray;

    @Mock
    private CameraSource cameraSource;

    @Mock
    private SurfaceHolder surfaceHolder;

    @Mock
    private CameraSourcePreview cameraSourcePreview;

    @Mock
    private Detector.Detections<Barcode> detections;

    @Mock
    private Vibrator vibrator;

    @Mock
    private Context context;

    private BarcodeScanActivity barcodeScanActivity;
    private ActivityController<BarcodeScanActivity> controller;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        controller = Robolectric.buildActivity(BarcodeScanActivity.class).create().start();
        barcodeScanActivity = controller.get();
        context = RuntimeEnvironment.application;
        cameraSourcePreview = barcodeScanActivity.findViewById(R.id.preview);
    }

    @After
    public void tearDown() {
        destroyController();
    }

    @Test
    public void testActivityCreatedSuccessfully() {
        Assert.assertNotNull(barcodeScanActivity);
    }

    @Test
    public void testCloseActivitySuccessfully() {
        barcodeScanActivity.closeBarcodeActivity(barcodeSparseArray);
        Assert.assertTrue(barcodeScanActivity.isFinishing());
    }

    @Test
    public void testReceiveDetections() {
        Assert.assertNotNull(detections);
        Mockito.doReturn(barcodeSparseArray).when(detections).getDetectedItems();
        Assert.assertNotNull(barcodeSparseArray);
        Assert.assertEquals(0, barcodeSparseArray.size());
        Whitebox.setInternalState(barcodeSparseArray.size(), 2);
        Assert.assertEquals(2, barcodeSparseArray.size());

        barcodeScanActivity.receiveDetections(detections);
    }

    @Override
    protected Activity getActivity() {
        return barcodeScanActivity;
    }

    @Override
    protected ActivityController getActivityController() {
        return controller;
    }
}
