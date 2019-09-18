package org.smartregister.anc.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.anc.contract.SiteCharacteristicsContract;
import org.smartregister.anc.util.Constants;

/**
 * Created by ndegwamartin on 30/08/2018.
 */
public class SiteCharacteristicsEnterActivityTest extends BaseActivityUnitTest {

    private SiteCharacteristicsEnterActivity activity;
    private ActivityController<SiteCharacteristicsEnterActivity> controller;

    @Mock
    private SiteCharacteristicsContract.Presenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Intent testIntent = new Intent();
        testIntent.putExtra(Constants.INTENT_KEY.IS_REMOTE_LOGIN, true);
        controller = Robolectric.buildActivity(SiteCharacteristicsEnterActivity.class, testIntent).create().start();
        activity = controller.get();
    }

    @Test
    public void testActivityIsInstantiatedCorrectly() {
        Assert.assertNotNull(activity);
        Assert.assertFalse(activity.isFinishing());
    }

    @Test
    public void testOnClickMethodInvokesLaunchSiteCharacteristicsFormOfPresenter() {
        SiteCharacteristicsEnterActivity spyActivity = Mockito.spy(activity);
        Whitebox.setInternalState(spyActivity, "presenter", presenter);

        spyActivity.onClick(new View(RuntimeEnvironment.application));

        Mockito.verify(presenter).launchSiteCharacteristicsForm();
    }

    @Override
    protected Activity getActivity() {
        return activity;
    }

    @Override
    protected ActivityController getActivityController() {
        return controller;
    }
}
