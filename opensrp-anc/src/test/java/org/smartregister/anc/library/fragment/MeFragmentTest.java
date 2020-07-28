package org.smartregister.anc.library.fragment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.anc.library.activity.BaseHomeRegisterActivity;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.view.activity.BaseRegisterActivity;

public class MeFragmentTest extends BaseUnitTest {
    private BaseHomeRegisterActivity activity;
    private MeFragment meFragment;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        activity = Mockito.mock(BaseHomeRegisterActivity.class);
        meFragment = Mockito.mock(MeFragment.class);
    }

    @Test
    public void testActivityIsInstantiatedCorrectly() {
        Mockito.doReturn(meFragment).when(activity).findFragmentByPosition(BaseRegisterActivity.ADVANCED_SEARCH_POSITION);
        Assert.assertNotNull(meFragment);
        Assert.assertTrue(meFragment instanceof MeFragment);
    }

}
