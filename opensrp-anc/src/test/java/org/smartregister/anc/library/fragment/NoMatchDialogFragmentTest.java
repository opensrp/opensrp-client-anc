package org.smartregister.anc.library.fragment;

import android.app.Activity;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.anc.library.activity.BaseActivityUnitTest;
import org.smartregister.anc.library.activity.BaseHomeRegisterActivity;

public class NoMatchDialogFragmentTest extends BaseActivityUnitTest {
    private BaseHomeRegisterActivity activity;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        ActivityController<BaseHomeRegisterActivity> controller = Robolectric.buildActivity(BaseHomeRegisterActivity.class).create().start();
        activity = controller.get();
    }

    @Override
    protected Activity getActivity() {
        return null;
    }

    @Override
    protected ActivityController getActivityController() {
        return null;
    }

    @Test
    public void testLaunchDialogShouldReturnNullIfActivityIsNotSet() {
        NoMatchDialogFragment noMatchDialogFragment = NoMatchDialogFragment.launchDialog(null, null, null);
        Assert.assertNull(noMatchDialogFragment);

    }

    // This test is not OK. An activity cannot be finishing after it was started
    @Ignore
    @Test
    public void testActivityIsInstantiatedCorrectly() {
        Assert.assertNotNull(activity);
        Assert.assertTrue(activity.isFinishing());
    }

    @Test
    public void testLaunchDialogShouldReturnNoMatchDialogFragmentIfActivityIsNotNull() {
        NoMatchDialogFragment noMatchDialogFragment = NoMatchDialogFragment.launchDialog(activity, "", "11234");
        Assert.assertNotNull(noMatchDialogFragment);

    }

    @After
    public void tearDown() {
        destroyController();
    }
}
