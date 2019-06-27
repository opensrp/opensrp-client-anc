package org.smartregister.anc.fragment;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.activity.HomeRegisterActivity;

public class NoMatchDialogFragmentTest extends BaseUnitTest {
    private HomeRegisterActivity activity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ActivityController<HomeRegisterActivity> controller = Robolectric.buildActivity(HomeRegisterActivity.class).create()
                .start();
        activity = controller.get();
    }

    @Test
    public void testLaunchDialogShouldReturnNullIfActivityIsNotSet() {
        NoMatchDialogFragment noMatchDialogFragment = NoMatchDialogFragment.launchDialog(null, null, null);
        Assert.assertNull(noMatchDialogFragment);

    }

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
}
