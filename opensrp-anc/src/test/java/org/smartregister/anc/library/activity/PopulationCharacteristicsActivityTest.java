package org.smartregister.anc.library.activity;

import android.app.Activity;
import android.widget.LinearLayout;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.anc.library.R;

/**
 * Created by ndegwamartin on 20/08/2018.
 */
public class PopulationCharacteristicsActivityTest extends BaseActivityUnitTest {

    private PopulationCharacteristicsActivity populationCharacteristicsActivity;
    private ActivityController<PopulationCharacteristicsActivity> controller;

    @Mock
    private LinearLayout view;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        controller = Robolectric.buildActivity(PopulationCharacteristicsActivity.class).create().start();
        populationCharacteristicsActivity = controller.get();
    }

    @Override
    protected Activity getActivity() {
        return populationCharacteristicsActivity;
    }

    @Override
    protected ActivityController getActivityController() {
        return controller;
    }

    @Test
    public void testActivityCreatedSuccesfully() {
        Assert.assertNotNull(populationCharacteristicsActivity);
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
}
