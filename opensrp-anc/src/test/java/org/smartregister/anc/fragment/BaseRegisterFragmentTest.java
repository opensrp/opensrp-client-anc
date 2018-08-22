package org.smartregister.anc.fragment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.activity.HomeRegisterActivity;

public class BaseRegisterFragmentTest extends BaseUnitTest {
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testAdvancedSearchFragmentInstance() {
		HomeRegisterActivity homeRegisterActivity = Mockito.mock(HomeRegisterActivity.class);
		AdvancedSearchFragment advancedSearchFragment = Mockito.mock(AdvancedSearchFragment.class);
		Mockito.doReturn(advancedSearchFragment).when(homeRegisterActivity).findFragmentByPosition(1);
		Assert.assertNotNull(advancedSearchFragment);
		Assert.assertTrue(advancedSearchFragment instanceof  AdvancedSearchFragment);
	}
}
