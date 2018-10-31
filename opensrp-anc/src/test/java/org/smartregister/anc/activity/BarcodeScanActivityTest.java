package org.smartregister.anc.activity;

import android.app.Activity;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;

public class BarcodeScanActivityTest extends BaseActivityUnitTest {
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

    @Override
    protected Activity getActivity() {
        return barcodeScanActivity;
    }

    @Override
    protected ActivityController getActivityController() {
        return controller;
    }
}
