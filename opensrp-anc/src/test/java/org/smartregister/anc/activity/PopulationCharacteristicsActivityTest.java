package org.smartregister.anc.activity;

import android.app.Activity;
import android.widget.LinearLayout;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.anc.R;
import org.smartregister.anc.custom.SettingsTestMenuItem;

/**
 * Created by ndegwamartin on 20/08/2018.
 */
public class PopulationCharacteristicsActivityTest extends BaseActivityUnitTest {

    private PopulationCharacteristicsActivity populationCharacteristicsActivity;
    private ActivityController<PopulationCharacteristicsActivity> controller;

    @Mock
    private SettingsTestMenuItem menuItem;

    @Mock
    private LinearLayout view;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        controller = Robolectric.buildActivity(PopulationCharacteristicsActivity.class).create().start();
        populationCharacteristicsActivity = controller.get();
    }

    @Test
    public void testActivityCreatedSuccesfully() {
        Assert.assertNotNull(populationCharacteristicsActivity);
    }

    @Test
    public void testOnOptionsItemSelectedInvokesSuperWithCorrectParams() {
        PopulationCharacteristicsActivity spyActivity = Mockito.spy(populationCharacteristicsActivity);

        spyActivity.onOptionsItemSelected(menuItem);

        Mockito.verify(spyActivity).onBackPressed();

    }

    @Test
    public void testOnItemClickInvokesRenderSubInfoAlertDialog() {
        PopulationCharacteristicsActivity spyActivity = Mockito.spy(populationCharacteristicsActivity);

        Mockito.doReturn(view).when(view).findViewById(ArgumentMatchers.anyInt());

        Mockito.doReturn(TEST_STRING).when(view).getTag(R.id.CHARACTERISTIC_DESC);

        spyActivity.onItemClick(view, 0);

        Mockito.verify(spyActivity).renderSubInfoAlertDialog(ArgumentMatchers.anyString());

    }

    @After
    public void tearDown() {
        destroyController();
    }


    @Override
    protected Activity getActivity() {
        return populationCharacteristicsActivity;
    }

    @Override
    protected ActivityController getActivityController() {
        return controller;
    }
}
