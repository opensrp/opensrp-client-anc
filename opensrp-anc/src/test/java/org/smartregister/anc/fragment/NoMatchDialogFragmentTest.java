package org.smartregister.anc.fragment;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.smartregister.anc.activity.BaseUnitTest;

public class NoMatchDialogFragmentTest extends BaseUnitTest {
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testLaunchDialogShouldReturnNullIfActivityIsNotSet() {
		NoMatchDialogFragment noMatchDialogFragment = NoMatchDialogFragment.launchDialog(null,null,null);
		Assert.assertNull(noMatchDialogFragment);
		
	}
}
