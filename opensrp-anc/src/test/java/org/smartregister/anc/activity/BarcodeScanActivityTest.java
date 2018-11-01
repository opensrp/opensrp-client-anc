package org.smartregister.anc.activity;

import android.app.Activity;
import android.util.SparseArray;

import com.google.android.gms.vision.barcode.Barcode;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;

public class BarcodeScanActivityTest extends BaseActivityUnitTest {
    @Mock
    private SparseArray<Barcode> barcodeSparseArray;

    private BarcodeScanActivity barcodeScanActivity;
    private ActivityController<BarcodeScanActivity> controller;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        controller = Robolectric.buildActivity(BarcodeScanActivity.class).create().start();
        barcodeScanActivity = controller.get();
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


    @Override
    protected Activity getActivity() {
        return barcodeScanActivity;
    }

    @Override
    protected ActivityController getActivityController() {
        return controller;
    }
}
