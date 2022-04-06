package org.smartregister.anc.library.fragment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.anc.library.activity.BaseHomeRegisterActivity;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.view.activity.BaseRegisterActivity;

public class BaseRegisterFragmentTest extends BaseUnitTest {
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAdvancedSearchFragmentInstance() {
        BaseHomeRegisterActivity baseHomeRegisterActivity = Mockito.mock(BaseHomeRegisterActivity.class);
        AdvancedSearchFragment advancedSearchFragment = Mockito.mock(AdvancedSearchFragment.class);
        Mockito.doReturn(advancedSearchFragment).when(baseHomeRegisterActivity).findFragmentByPosition(BaseRegisterActivity.ADVANCED_SEARCH_POSITION);
        Assert.assertNotNull(advancedSearchFragment);
        Assert.assertTrue(advancedSearchFragment instanceof AdvancedSearchFragment);
    }
}
