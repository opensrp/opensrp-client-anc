package org.smartregister.anc.library.activity;

import android.app.Activity;
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
import org.smartregister.anc.library.contract.BaseCharacteristicsContract;
import org.smartregister.anc.library.contract.SiteCharacteristicsContract;
import org.smartregister.anc.library.presenter.SiteCharacteristicsPresenter;

/**
 * Created by ndegwamartin on 04/08/2018.
 */
public class SiteCharacteristicsActivityTest extends BaseActivityUnitTest {

    private SiteCharacteristicsActivity activity;
    private ActivityController<SiteCharacteristicsActivity> controller;

    @Mock
    private SiteCharacteristicsContract.Presenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        controller = Robolectric.buildActivity(SiteCharacteristicsActivity.class).create().start();
        activity = controller.get();
    }

    @Test
    public void testActivityIsInstantiatedCorrectly() {
        Assert.assertNotNull(activity);
        Assert.assertFalse(activity.isFinishing());
    }

    @Test
    public void testGetToolbarTitleReturnsCorrectTitle() {
        String title = activity.getToolbarTitle();
        Assert.assertNotNull(title);
        Assert.assertEquals(RuntimeEnvironment.application.getString(R.string.site_characteristics), title);
    }

    @Test
    public void tetGetPresenterReturnsCorrectValidPresenterInstance() {
        BaseCharacteristicsContract.BasePresenter presenter = activity.getPresenter();
        Assert.assertNotNull(presenter);
        Assert.assertTrue(presenter instanceof SiteCharacteristicsPresenter);
    }

    @Test
    public void testOnClickMethodInvokesLaunchSiteCharacteristicsFormForEditOfPresenter() {
        SiteCharacteristicsActivity spyActivity = Mockito.spy(activity);
        Whitebox.setInternalState(spyActivity, "presenter", presenter);

        spyActivity.onClick(new View(RuntimeEnvironment.application));

        Mockito.verify(presenter).launchSiteCharacteristicsFormForEdit();
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
