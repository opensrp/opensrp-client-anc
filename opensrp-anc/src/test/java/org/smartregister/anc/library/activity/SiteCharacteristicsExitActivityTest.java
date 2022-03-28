package org.smartregister.anc.library.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.contract.SiteCharacteristicsContract;
import org.smartregister.anc.library.util.ConstantsUtils;

/**
 * Created by ndegwamartin on 30/08/2018.
 */
public class SiteCharacteristicsExitActivityTest extends BaseActivityUnitTest {

    private SiteCharacteristicsExitActivity activity;
    private ActivityController<SiteCharacteristicsExitActivity> controller;

    @Mock
    private SiteCharacteristicsContract.Presenter presenter;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        Intent testIntent = new Intent();
        testIntent.putExtra(ConstantsUtils.IntentKeyUtils.IS_REMOTE_LOGIN, true);

        controller = Robolectric.buildActivity(SiteCharacteristicsExitActivity.class, testIntent).create().start();
        activity = controller.get();
    }

    @Override
    protected Activity getActivity() {
        return activity;
    }

    @Override
    protected ActivityController getActivityController() {
        return controller;
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

        Mockito.doNothing()
                .when(spyActivity)
                .goToHomeRegisterPage();

        spyActivity.onClick(view);

        Mockito.verify(spyActivity).goToHomeRegisterPage();
    }

    @After
    public void tearDown() {
        destroyController();
    }
}
