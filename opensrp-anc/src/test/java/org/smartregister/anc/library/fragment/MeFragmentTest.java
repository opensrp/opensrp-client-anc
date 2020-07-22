package org.smartregister.anc.library.fragment;

import android.app.Activity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.activity.BaseActivityUnitTest;
import org.smartregister.anc.library.activity.BaseHomeRegisterActivity;

public class MeFragmentTest extends BaseActivityUnitTest {
    @Mock
    private AncLibrary ancLibrary;
    private BaseHomeRegisterActivity activity;
    private MeFragment meFragment;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        meFragment = Mockito.spy(MeFragment.class);
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
    public void testActivityIsInstantiatedCorrectly() {
        Assert.assertNotNull(activity);
    }

}
