package org.smartregister.anc.library.fragment;

import static org.robolectric.shadows.ShadowInstrumentation.getInstrumentation;

import android.app.Activity;
import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.anc.library.activity.BaseActivityUnitTest;
import org.smartregister.anc.library.activity.ProfileActivity;
import org.smartregister.anc.library.presenter.ProfilePresenter;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;

import java.util.HashMap;

public class ProfileContactsFragmentTest extends BaseActivityUnitTest {
    private ProfileActivity profileActivity;
    private ActivityController<ProfileActivity> controller;
    private ProfileContactsFragment profileContactsFragment;
    private ProfileActivity spyActivity;

    @Mock
    private ProfilePresenter presenter;

    @Before
    public void setUp() {
        super.setUp();
    }

    @Override
    protected Activity getActivity() {
        return profileActivity;
    }

    @Override
    protected ActivityController getActivityController() {
        return controller;
    }

    @Test
    public void testFragmentInstance() {
        Intent testIntent = new Intent();
        testIntent.putExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID, DUMMY_BASE_ENTITY_ID);
        HashMap<String, String> map = new HashMap<>();
        map.put(DBConstantsUtils.KeyUtils.CONTACT_STATUS, ConstantsUtils.AlertStatusUtils.ACTIVE);
        map.put(DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE, "10-12-2018");
        map.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT, "3");
        testIntent.putExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP, map);

        controller = Robolectric.buildActivity(ProfileActivity.class, testIntent).create().start().resume();
        profileActivity = controller.get();

        spyActivity = Mockito.spy(profileActivity);
        Whitebox.setInternalState(profileActivity, "presenter", presenter);
        profileContactsFragment = ProfileContactsFragment.newInstance(spyActivity.getIntent().getExtras());
        startFragment(profileContactsFragment);
        Assert.assertNotNull(profileContactsFragment);
    }

    private void startFragment(Fragment fragment) {
        FragmentManager fragmentManager = spyActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(fragment, null);
        fragmentTransaction.commit();

        getActivity().runOnUiThread(() -> spyActivity.getSupportFragmentManager().executePendingTransactions());

        getInstrumentation().waitForIdleSync();
    }

    @Test
    public void testFragmentInstanceWithNullBundle() {
        Intent testIntent = new Intent();
        testIntent.putExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID, DUMMY_BASE_ENTITY_ID);
        HashMap<String, String> map = new HashMap<>();
        map.put(DBConstantsUtils.KeyUtils.CONTACT_STATUS, ConstantsUtils.AlertStatusUtils.NOT_DUE);
        map.put(DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE, "10-12-2018");
        map.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT, "3");
        testIntent.putExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP, map);

        controller = Robolectric.buildActivity(ProfileActivity.class, testIntent).create().start().resume();
        profileActivity = controller.get();

        spyActivity = Mockito.spy(profileActivity);
        Whitebox.setInternalState(profileActivity, "presenter", presenter);
        profileContactsFragment = ProfileContactsFragment.newInstance(null);
        startFragment(profileContactsFragment);
        Assert.assertNotNull(profileContactsFragment);
    }

    @After
    public void tearDown() {
        destroyController();
    }
}
