package org.smartregister.anc.library.activity;

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
import org.smartregister.anc.R;
import org.smartregister.anc.library.contract.SiteCharacteristicsContract;
import org.smartregister.anc.library.util.Constants;

/**
 * Created by ndegwamartin on 30/08/2018.
 */
public class SiteCharacteristicsExitActivityTest extends BaseActivityUnitTest {

    private SiteCharacteristicsExitActivity activity;
    private ActivityController<SiteCharacteristicsExitActivity> controller;

    @Mock
    private SiteCharacteristicsContract.Presenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Intent testIntent = new Intent();
        testIntent.putExtra(Constants.INTENT_KEY.IS_REMOTE_LOGIN, true);
        controller = Robolectric.buildActivity(SiteCharacteristicsExitActivity.class, testIntent).create().start();
        activity = controller.get();
    }

    @Test
    public void testActivityIsInstantiatedCorrectly() {
        Assert.assertNotNull(activity);
        Assert.assertFalse(activity.isFinishing());
    }

    @Test
    public void testOnClickMethodInvokesLaunchSiteCharacteristicsFormForEditOfPresenterWhenViewClickedIsBackHomeButton() {
        SiteCharacteristicsExitActivity spyActivity = Mockito.spy(activity);
        Whitebox.setInternalState(spyActivity, "presenter", presenter);
        View view = new View(RuntimeEnvironment.application);
        view.setId(R.id.btn_back_to_home);
        spyActivity.onClick(view);

        Mockito.verify(presenter).launchSiteCharacteristicsFormForEdit();
    }

    @Test
    public void testOnClickMethodInvokesGoToHomeRegisterPageWhenViewClickedIsHomeRegisterButton() {
        SiteCharacteristicsExitActivity spyActivity = Mockito.spy(activity);

        View view = new View(RuntimeEnvironment.application);
        view.setId(R.id.btn_site_characteristics_home_register);

        spyActivity.onClick(view);

        Mockito.verify(spyActivity).goToHomeRegisterPage();
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
