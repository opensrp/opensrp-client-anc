package org.smartregister.anc.library.fragment;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.anc.library.activity.BaseHomeRegisterActivity;
import org.smartregister.anc.library.activity.BaseUnitTest;

public class NoMatchDialogFragmentTest extends BaseUnitTest {
    private BaseHomeRegisterActivity activity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ActivityController<BaseHomeRegisterActivity> controller = Robolectric.buildActivity(BaseHomeRegisterActivity.class).create()
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
