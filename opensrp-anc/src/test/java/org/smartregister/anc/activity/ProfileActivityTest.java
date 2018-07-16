package org.smartregister.anc.activity;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.widget.TextView;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.anc.contract.ProfileContract;
import org.smartregister.anc.util.Constants;


/**
 * Created by ndegwamartin on 13/07/2018.
 */
public class ProfileActivityTest extends BaseUnitTest {

    private ProfileActivity profileActivity;
    private ActivityController<ProfileActivity> controller;

    @Mock
    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Mock
    private ProfileContract.Presenter presenter;

    @Mock
    private TextView textView;

    @Mock
    private AppBarLayout appBarLayout;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Intent testIntent = new Intent();
        testIntent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, DUMMY_BASE_ENTITY_ID);
        controller = Robolectric.buildActivity(ProfileActivity.class, testIntent).create().start();
        profileActivity = controller.get();
    }

    @After
    public void tearDown() {
        destroyController();
    }


    @Test
    public void testActivityCreatedSuccesfully() {
        Assert.assertNotNull(profileActivity);
    }

    @Test
    public void testNameViewIsInitialized() {

        TextView nameView = Whitebox.getInternalState(profileActivity, "nameView");
        Assert.assertNotNull(nameView);
    }

    @Test
    public void testAgeViewIsInitialized() {

        TextView ageView = Whitebox.getInternalState(profileActivity, "ageView");
        Assert.assertNotNull(ageView);
    }


    @Test
    public void testGestationAgeViewIsInitialized() {

        TextView gestationAgeView = Whitebox.getInternalState(profileActivity, "gestationAgeView");
        Assert.assertNotNull(gestationAgeView);
    }

    @Test
    public void testAncViewIsInitialized() {

        TextView ancIdView = Whitebox.getInternalState(profileActivity, "ancIdView");
        Assert.assertNotNull(ancIdView);
    }

    @Test
    public void testOnResumeShouldInvokeRefreshViewMethodOfPresenter() {

        ProfileActivity spyActivity = Mockito.spy(profileActivity);

        Whitebox.setInternalState(spyActivity, "mProfilePresenter", presenter);

        spyActivity.onResume();

        Mockito.verify(presenter).refreshProfileView(DUMMY_BASE_ENTITY_ID);
    }


    @Test
    public void testOnDestroyShouldInvokeOnDestroyMethodOfPresenter() {

        ProfileActivity spyActivity = Mockito.spy(profileActivity);

        Whitebox.setInternalState(spyActivity, "mProfilePresenter", presenter);

        spyActivity.onDestroy();

        Mockito.verify(presenter).onDestroy(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testSetProfileNameShouldSetTextViewWithCorrectContent() {

        ProfileActivity spyActivity = Mockito.spy(profileActivity);

        Whitebox.setInternalState(spyActivity, "nameView", textView);

        spyActivity.setProfileName(TEST_STRING);

        Mockito.verify(textView).setText(TEST_STRING);
    }

    @Test
    public void testSetProfileAgeShouldSetAgeViewWithCorrectContent() {

        ProfileActivity spyActivity = Mockito.spy(profileActivity);

        Whitebox.setInternalState(spyActivity, "ageView", textView);

        spyActivity.setProfileAge(TEST_STRING);

        Mockito.verify(textView).setText("AGE " + TEST_STRING);
    }

    @Test
    public void testSetProfileGestatonAgeShouldSetGestationAgeViewWithCorrectContent() {

        ProfileActivity spyActivity = Mockito.spy(profileActivity);

        Whitebox.setInternalState(spyActivity, "gestationAgeView", textView);

        spyActivity.setProfileGestationAge(TEST_STRING);

        Mockito.verify(textView).setText("GA: " + TEST_STRING + " WEEKS");

        spyActivity.setProfileGestationAge(null);

        Mockito.verify(textView).setText("GA");
    }

    @Test
    public void testSetProfileIDShouldSetAncIdViewWithCorrectContent() {

        ProfileActivity spyActivity = Mockito.spy(profileActivity);

        Whitebox.setInternalState(spyActivity, "ancIdView", textView);

        spyActivity.setProfileID(TEST_STRING);

        Mockito.verify(textView).setText("ID: " + TEST_STRING);
    }

    @Test
    public void testOnOffsetChangedShouldSetCorrectValuesForCollapsingBar() {


        ProfileActivity spyActivity = Mockito.spy(profileActivity);

        Mockito.doReturn(0).when(appBarLayout).getTotalScrollRange();

        Whitebox.setInternalState(spyActivity, "collapsingToolbarLayout", collapsingToolbarLayout);


        spyActivity.onOffsetChanged(appBarLayout, 0);

        Mockito.verify(appBarLayout).getTotalScrollRange();


//When app bar collapsed
        Whitebox.setInternalState(spyActivity, "womanName", TEST_STRING);

        spyActivity.onOffsetChanged(appBarLayout, 0);

        Mockito.verify(collapsingToolbarLayout).setTitle(TEST_STRING);


//When app bar maximized
        Whitebox.setInternalState(spyActivity, "appBarTitleIsShown", true);

        spyActivity.onOffsetChanged(appBarLayout, 10);

        Mockito.verify(collapsingToolbarLayout).setTitle(null);

    }

    private void destroyController() {
        try {
            profileActivity.finish();
            controller.pause().stop().destroy(); //destroy controller if we can

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.gc();
    }
}
