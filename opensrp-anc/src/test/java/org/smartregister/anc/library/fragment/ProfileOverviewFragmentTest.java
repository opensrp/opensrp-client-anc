package org.smartregister.anc.library.fragment;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

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

import static org.robolectric.shadows.ShadowInstrumentation.getInstrumentation;

public class ProfileOverviewFragmentTest extends BaseActivityUnitTest {
    private ProfileActivity profileActivity;
    private ActivityController<ProfileActivity> controller;
    private ProfileOverviewFragment profileOverviewFragment;
    private ProfileActivity spyActivity;

    @Mock
    private ProfilePresenter presenter;

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

        spyActivity = Mockito.spy(profileActivity);
        profileOverviewFragment = ProfileOverviewFragment.newInstance(spyActivity.getIntent().getExtras());

        startFragment(profileOverviewFragment);
        Whitebox.setInternalState(profileActivity, "presenter", presenter);
    }

    private void startFragment(Fragment fragment) {
        FragmentManager fragmentManager = spyActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(fragment, null);
        fragmentTransaction.commit();

        getActivity().runOnUiThread(() -> spyActivity.getSupportFragmentManager().executePendingTransactions());

        getInstrumentation().waitForIdleSync();
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
        Assert.assertNotNull(profileOverviewFragment);
    }
}
