package org.smartregister.anc.library.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.Context;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.presenter.ProfilePresenter;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.util.PermissionUtils;

import java.util.HashMap;


/**
 * Created by ndegwamartin on 13/07/2018.
 */
public class ProfileActivityTest extends BaseActivityUnitTest {

    private ProfileActivity profileActivity;
    private ActivityController<ProfileActivity> controller;

    @Mock
    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Mock
    private ProfilePresenter presenter;

    @Mock
    private TextView textView;

    @Mock
    private AppBarLayout appBarLayout;

    @Mock
    private Intent intent;

    @Mock
    private ImageRenderHelper imageRenderHelper;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        Intent testIntent = new Intent();
        testIntent.putExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID, DUMMY_BASE_ENTITY_ID);
        HashMap<String, String> map = new HashMap<>();
        map.put(DBConstantsUtils.KeyUtils.CONTACT_STATUS, ConstantsUtils.AlertStatusUtils.ACTIVE);
        map.put(DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE, "10-12-2018");
        map.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT, "3");
        testIntent.putExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP, map);

        controller = Robolectric.buildActivity(ProfileActivity.class, testIntent).create().start().resume();

        profileActivity = controller.get();
        Whitebox.setInternalState(profileActivity, "presenter", presenter);
    }

    @Override
    protected Activity getActivity() {
        return profileActivity;
    }

    @Override
    protected ActivityController getActivityController() {
        return controller;
    }

    @After
    public void tearDown() {
        destroyController();

        //logout
        Context context = Context.getInstance();
        context.session().expire();
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

        Whitebox.setInternalState(spyActivity, "presenter", presenter);
        Mockito.doNothing().when(spyActivity).registerEventBus();
        spyActivity.onResumption();

        Mockito.verify(presenter).refreshProfileView(DUMMY_BASE_ENTITY_ID);
    }

    @Test
    public void testOnDestroyShouldInvokeOnDestroyMethodOfPresenter() {

        ProfileActivity spyActivity = Mockito.spy(profileActivity);

        Whitebox.setInternalState(spyActivity, "presenter", presenter);

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
    @Ignore
    public void testOnOffsetChangedShouldSetCorrectValuesForCollapsingBar() {
        ProfileActivity spyActivity = Mockito.spy(profileActivity);
        Mockito.doReturn(0).when(appBarLayout).getTotalScrollRange();
        Whitebox.setInternalState(spyActivity, "collapsingToolbarLayout", collapsingToolbarLayout);
        spyActivity.onOffsetChanged(appBarLayout, 0);
        Mockito.verify(appBarLayout).getTotalScrollRange();


//When app bar collapsed
        Whitebox.setInternalState(spyActivity, "patientName", TEST_STRING);
        spyActivity.onOffsetChanged(appBarLayout, 0);
        Mockito.verify(collapsingToolbarLayout).setTitle(TEST_STRING);


        //When app bar maximized
        Whitebox.setInternalState(spyActivity, "appBarTitleIsShown", true);
        spyActivity.onOffsetChanged(appBarLayout, 10);
        Mockito.verify(collapsingToolbarLayout).setTitle(null);

    }

    @Test
    public void testSetWomanPhoneNumberUpdatesFieldCorrectly() {

        ProfileActivity spyActivity = Mockito.spy(profileActivity);
        String phoneNumber = Whitebox.getInternalState(spyActivity, "phoneNumber");
        Assert.assertNull(phoneNumber);
        spyActivity.setPhoneNumber(TEST_STRING);
        phoneNumber = Whitebox.getInternalState(spyActivity, "phoneNumber");
        Assert.assertNotNull(phoneNumber);
        Assert.assertEquals(TEST_STRING, phoneNumber);
    }

    @Test
    public void testGetIntentStringInvokesGetStringExtraMethodOfIntentWithCorrectParameters() {
        ProfileActivity spyActivity = Mockito.spy(profileActivity);
        Mockito.doReturn(intent).when(spyActivity).getIntent();
        spyActivity.getIntentString(TEST_STRING);
        Mockito.verify(intent).getStringExtra(TEST_STRING);

    }

    @Test
    public void testOnRequestPermissionsResultInvokesLaunchPhoneDialerWhenCorrectPermissionsAreGranted() {

        profileActivity.setPhoneNumber(DUMMY_PHONE_NUMBER);
        ProfileActivity spyActivity = Mockito.spy(profileActivity);
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", DUMMY_PHONE_NUMBER, null));
        Mockito.doNothing().when(spyActivity).startActivity(intent);
        //With Non Existent Request Code
        spyActivity.onRequestPermissionsResult(3, new String[]{}, new int[]{});
        Mockito.verify(spyActivity, Mockito.times(0)).launchPhoneDialer(DUMMY_PHONE_NUMBER);
        //With No Permissions Granted
        spyActivity.onRequestPermissionsResult(PermissionUtils.PHONE_STATE_PERMISSION_REQUEST_CODE, new String[]{}, new int[]{});
        Mockito.verify(spyActivity, Mockito.times(0)).launchPhoneDialer(DUMMY_PHONE_NUMBER);
        Mockito.doReturn(true).when(spyActivity).isPermissionGranted();
        //With permissions Granted
        spyActivity.onRequestPermissionsResult(PermissionUtils.PHONE_STATE_PERMISSION_REQUEST_CODE, new String[]{}, new int[]{PackageManager.PERMISSION_GRANTED});

        Mockito.verify(spyActivity).launchPhoneDialer(DUMMY_PHONE_NUMBER);
    }

    @Test
    public void testLaunchPhoneDialerExceptionBlock() {

        ProfileActivity spyActivity = Mockito.spy(profileActivity);
        Mockito.doReturn(true).when(spyActivity).isPermissionGranted();
        Intent intent = null;
        Mockito.doReturn(intent).when(spyActivity).getTelephoneIntent(DUMMY_PHONE_NUMBER);

        spyActivity.launchPhoneDialer(DUMMY_PHONE_NUMBER);

        Mockito.verify(spyActivity, Mockito.times(0)).launchPhoneDialer(null);
    }

    @Test
    public void testSetProfileImageInvokesImageRenderHelperMethodsWithCorrectParameters() {

        ProfileActivity spyActivity = Mockito.spy(profileActivity);

        Whitebox.setInternalState(spyActivity, "imageRenderHelper", imageRenderHelper);

        spyActivity.setProfileImage(DUMMY_BASE_ENTITY_ID);

        ImageView imageView = Whitebox.getInternalState(spyActivity, "imageView");

        Mockito.verify(imageRenderHelper).refreshProfileImage(DUMMY_BASE_ENTITY_ID, imageView, R.drawable.avatar_woman);

    }
}
