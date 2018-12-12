package org.smartregister.anc.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.widget.TextView;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.anc.R;
import org.smartregister.anc.contract.ProfileContract;
import org.smartregister.anc.util.Constants;

import static org.robolectric.Shadows.shadowOf;

public class ContactSummaryFinishActivityTest extends BaseActivityUnitTest {

    private ActivityController<ContactSummaryFinishActivity> activityController;
    private ContactSummaryFinishActivity activity;
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

        Intent contactSummaryActivityIntent = new Intent(RuntimeEnvironment.application,
                ContactSummaryFinishActivity.class);
        contactSummaryActivityIntent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, DUMMY_BASE_ENTITY_ID);
        activityController = Robolectric.buildActivity(ContactSummaryFinishActivity.class,
                contactSummaryActivityIntent);
        activity = activityController.create().get();
    }


    @Test
    public void testActivityIsNotNull() {
        Assert.assertNotNull(activity);
    }

    @Test
    public void testActivityStartedWithIntent() {
        Intent passedIntent = activity.getIntent();
        Assert.assertNotNull(passedIntent);

    }

    @Test
    public void testNameViewIsInitialized() {

        TextView nameView = Whitebox.getInternalState(activity, "nameView");
        Assert.assertNotNull(nameView);
    }

    @Test
    public void testAgeViewIsInitialized() {

        TextView ageView = Whitebox.getInternalState(activity, "ageView");
        Assert.assertNotNull(ageView);
    }


    @Test
    public void testGestationAgeViewIsInitialized() {

        TextView gestationAgeView = Whitebox.getInternalState(activity, "gestationAgeView");
        Assert.assertNotNull(gestationAgeView);
    }

    @Test
    public void testAncViewIsInitialized() {

        TextView ancIdView = Whitebox.getInternalState(activity, "ancIdView");
        Assert.assertNotNull(ancIdView);
    }

    @Test
    public void testOnResumeShouldInvokeRefreshViewMethodOfPresenter() {

        ContactSummaryFinishActivity spyActivity = Mockito.spy(activity);

        Whitebox.setInternalState(spyActivity, "mProfilePresenter", presenter);
        Mockito.doNothing().when(spyActivity).registerEventBus();
        spyActivity.onResume();

        Mockito.verify(presenter).refreshProfileView(DUMMY_BASE_ENTITY_ID);
    }


    @Test
    public void testOnDestroyShouldInvokeOnDestroyMethodOfPresenter() {

        ContactSummaryFinishActivity spyActivity = Mockito.spy(activity);

        Whitebox.setInternalState(spyActivity, "mProfilePresenter", presenter);

        spyActivity.onDestroy();

        Mockito.verify(presenter).onDestroy(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testSetProfileNameShouldSetTextViewWithCorrectContent() {

        ContactSummaryFinishActivity spyActivity = Mockito.spy(activity);

        Whitebox.setInternalState(spyActivity, "nameView", textView);

        spyActivity.setProfileName(TEST_STRING);

        Mockito.verify(textView).setText(TEST_STRING);
    }

    @Test
    public void testSetProfileAgeShouldSetAgeViewWithCorrectContent() {

        ContactSummaryFinishActivity spyActivity = Mockito.spy(activity);

        Whitebox.setInternalState(spyActivity, "ageView", textView);

        spyActivity.setProfileAge(TEST_STRING);

        Mockito.verify(textView).setText("AGE " + TEST_STRING);
    }

    @Test
    public void testSetProfileGestatonAgeShouldSetGestationAgeViewWithCorrectContent() {

        ContactSummaryFinishActivity spyActivity = Mockito.spy(activity);

        Whitebox.setInternalState(spyActivity, "gestationAgeView", textView);

        spyActivity.setProfileGestationAge(TEST_STRING);

        Mockito.verify(textView).setText("GA: " + TEST_STRING + " WEEKS");

        spyActivity.setProfileGestationAge(null);

        Mockito.verify(textView).setText("GA");
    }

    @Test
    public void testSetProfileIDShouldSetAncIdViewWithCorrectContent() {

        ContactSummaryFinishActivity spyActivity = Mockito.spy(activity);

        Whitebox.setInternalState(spyActivity, "ancIdView", textView);

        spyActivity.setProfileID(TEST_STRING);

        Mockito.verify(textView).setText("ID: " + TEST_STRING);
    }

    @Test
    public void testOnOffsetChangedShouldSetCorrectValuesForCollapsingBar() {


        ContactSummaryFinishActivity spyActivity = Mockito.spy(activity);

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

    @Test
    @Ignore
    public void testSaveAndFinishButtonClickedOpensContactSummarySendPage() {
        activity.findViewById(R.id.finalize_contact).performClick();
        Intent expectedIntent = new Intent(activity, ContactSummarySendActivity.class);
        Intent actual = shadowOf(RuntimeEnvironment.application).getNextStartedActivity();
        Assert.assertEquals(expectedIntent.getComponent(), actual.getComponent());
    }

    @After
    public void tearDown() {
        destroyController();
    }

    @Override
    protected Activity getActivity() {
        return activity;
    }

    @Override
    protected ActivityController getActivityController() {
        return activityController;
    }
}
