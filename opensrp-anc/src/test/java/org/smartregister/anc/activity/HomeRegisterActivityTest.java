package org.smartregister.anc.activity;

import android.app.Activity;
import android.support.v4.app.Fragment;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.anc.custom.SettingsTestMenuItem;
import org.smartregister.anc.fragment.AdvancedSearchFragment;
import org.smartregister.anc.fragment.BaseRegisterFragment;
import org.smartregister.anc.fragment.HomeRegisterFragment;
import org.smartregister.anc.fragment.SortFilterFragment;
import org.smartregister.anc.presenter.RegisterPresenter;
import org.smartregister.anc.util.Constants;
import org.smartregister.configurableviews.model.Field;

import java.util.List;

/**
 * Created by ndegwamartin on 24/07/2018.
 */
public class HomeRegisterActivityTest extends BaseActivityUnitTest {
    private HomeRegisterActivity homeRegisterActivity;
    private ActivityController<HomeRegisterActivity> controller;


    @Mock
    private BaseRegisterFragment baseRegisterFragment;

    @Mock
    private SettingsTestMenuItem menuItem;

    @Mock
    private List<Field> filterList;
    @Mock
    private Field sortField;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        controller = Robolectric.buildActivity(HomeRegisterActivity.class).create().start();
        homeRegisterActivity = controller.get();
    }

    @After
    public void tearDown() {
        destroyController();
    }


    @Test
    public void testActivityCreatedSuccesfully() {
        Assert.assertNotNull(homeRegisterActivity);
    }

    @Test
    public void testGetRegisterFragmentShouldReturnAValidInstance() {

        Fragment fragment = homeRegisterActivity.getRegisterFragment();
        Assert.assertNotNull(fragment);
        Assert.assertTrue(fragment instanceof HomeRegisterFragment);
    }

    @Test
    public void testGetOtherFragmentsShouldReturnCorrectInstances() {

        Fragment[] fragments = homeRegisterActivity.getOtherFragments();
        Assert.assertNotNull(fragments);
        Assert.assertTrue(fragments.length == 2);
        Assert.assertTrue(fragments[0] instanceof AdvancedSearchFragment);
        Assert.assertTrue(fragments[1] instanceof SortFilterFragment);
    }

    @Test
    public void testOnOptionsItemSelectedInvokesSuperWithCorrectParams() {
        HomeRegisterActivity spyActivity = Mockito.spy(homeRegisterActivity);

        spyActivity.onOptionsItemSelected(menuItem);

        Mockito.verify(spyActivity).superOnOptionsItemsSelected(menuItem);

    }

    @Test
    public void testInitializePresenterInstantiatesPresenterCorrectly() {


        RegisterPresenter presenter = Whitebox.getInternalState(homeRegisterActivity, "presenter");

        Assert.assertNotNull(presenter);

        homeRegisterActivity.initializePresenter();

        Assert.assertNotNull(presenter);

    }

    @Test
    public void testGetViewIdentifiersReturnsCorrectIdentifierValues() {
        List<String> viewIdentifiers = homeRegisterActivity.getViewIdentifiers();
        Assert.assertNotNull(viewIdentifiers);
        Assert.assertTrue(viewIdentifiers.size() == 1);
        Assert.assertEquals(Constants.CONFIGURATION.HOME_REGISTER, viewIdentifiers.get(0));

    }

    @Test
    public void testUpdateSortAndFilterShouldInvokeCorrectMethods() {

        Whitebox.setInternalState(homeRegisterActivity, "mBaseFragment", baseRegisterFragment);

        HomeRegisterActivity spyActivity = Mockito.spy(homeRegisterActivity);

        spyActivity.updateSortAndFilter(filterList, sortField);

        Mockito.verify(baseRegisterFragment).updateSortAndFilter(filterList, sortField);

        Mockito.verify(spyActivity).switchToBaseFragment();
    }

    @Override
    protected Activity getActivity() {
        return homeRegisterActivity;
    }

    @Override
    protected ActivityController getActivityController() {
        return controller;
    }
}
