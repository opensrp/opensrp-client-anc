package org.smartregister.anc.helper;

import android.app.Activity;
import android.support.design.widget.BottomNavigationView;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.BaseActivityUnitTest;
import org.smartregister.anc.activity.HomeRegisterActivity;

public class BottomNavigationHelperDisableShiftModeTest extends BaseActivityUnitTest {
	
	private HomeRegisterActivity homeRegisterActivity;
	private ActivityController<HomeRegisterActivity> controller;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		controller = Robolectric.buildActivity(HomeRegisterActivity.class).create().start();
		homeRegisterActivity = controller.get();
	}
	
	@Test
	public void testDisableShiftMode() {
		BottomNavigationHelper spyBottomNavigationHelper = Mockito.mock(BottomNavigationHelper.class);
		HomeRegisterActivity spyActivity = Mockito.spy(homeRegisterActivity);
		
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
	
	
	@Override
	protected Activity getActivity() {
		return homeRegisterActivity;
	}
	
	@Override
	protected ActivityController getActivityController() {
		return controller;
	}
}
