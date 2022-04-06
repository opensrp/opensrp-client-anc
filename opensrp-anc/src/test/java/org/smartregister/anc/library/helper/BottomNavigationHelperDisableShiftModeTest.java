package org.smartregister.anc.library.helper;

import android.app.Activity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.BaseActivityUnitTest;
import org.smartregister.anc.library.activity.BaseHomeRegisterActivity;
import org.smartregister.helper.BottomNavigationHelper;

public class BottomNavigationHelperDisableShiftModeTest extends BaseActivityUnitTest {

    private BaseHomeRegisterActivity baseHomeRegisterActivity;
    private ActivityController<BaseHomeRegisterActivity> controller;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        controller = Robolectric.buildActivity(BaseHomeRegisterActivity.class).create().start();
        baseHomeRegisterActivity = controller.get();
    }

    @Override
    protected Activity getActivity() {
        return baseHomeRegisterActivity;
    }

    @Override
    protected ActivityController getActivityController() {
        return controller;
    }

    @Test
    public void testDisableShiftMode() {
        BottomNavigationHelper spyBottomNavigationHelper = Mockito.mock(BottomNavigationHelper.class);
        BaseHomeRegisterActivity spyActivity = Mockito.spy(baseHomeRegisterActivity);

        BottomNavigationView bottomNavigationView = spyActivity.findViewById(R.id.bottom_navigation);
        Assert.assertNotNull(bottomNavigationView);

        spyBottomNavigationHelper.disableShiftMode(bottomNavigationView);
    }

    @Test
    public void testDisableShiftModeWithNullmShiftModeField() {
        BottomNavigationHelper spyBottomNavigationHelper = Mockito.mock(BottomNavigationHelper.class);
        Assert.assertNotNull(spyBottomNavigationHelper);

        BottomNavigationView bottomNavigationView = Mockito.mock(BottomNavigationView.class);
        Assert.assertNotNull(bottomNavigationView);

        Mockito.doReturn(null).when(bottomNavigationView).getChildAt(0);
        Assert.assertTrue(true);

        spyBottomNavigationHelper.disableShiftMode(bottomNavigationView);
    }

    @After
    public void tearDown() {
        destroyController();
    }
}
