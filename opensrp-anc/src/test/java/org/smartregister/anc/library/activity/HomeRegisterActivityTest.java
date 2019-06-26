package org.smartregister.anc.library.activity;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.google.common.collect.ImmutableMap;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.anc.R;
import org.smartregister.anc.library.contract.RegisterContract;
import org.smartregister.anc.library.domain.AttentionFlag;
import org.smartregister.anc.library.event.PatientRemovedEvent;
import org.smartregister.anc.library.event.ShowProgressDialogEvent;
import org.smartregister.anc.library.fragment.AdvancedSearchFragment;
import org.smartregister.anc.library.fragment.HomeRegisterFragment;
import org.smartregister.anc.library.fragment.LibraryFragment;
import org.smartregister.anc.library.fragment.MeFragment;
import org.smartregister.anc.library.fragment.SortFilterFragment;
import org.smartregister.anc.library.presenter.RegisterPresenter;
import org.smartregister.anc.library.util.Constants;
import org.smartregister.anc.library.util.DBConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.domain.FetchStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ndegwamartin on 24/07/2018.
 */
public class HomeRegisterActivityTest extends BaseActivityUnitTest {

    private HomeRegisterActivity homeRegisterActivity;

    private ActivityController<HomeRegisterActivity> controller;

    @Mock
    private HomeRegisterFragment homeRegisterFragment;

    @Mock
    private List<Field> filterList;
    @Mock
    private Field sortField;

    @Mock
    private AlertDialog attentionFlagsAlertDialog;

    @Mock
    private AlertDialog recordBirthAlertDialog;

    @Mock
    private RegisterPresenter registerPresenter;

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
        Assert.assertTrue(fragments.length == 4);
        Assert.assertTrue(fragments[0] instanceof AdvancedSearchFragment);
        Assert.assertTrue(fragments[1] instanceof SortFilterFragment);
        Assert.assertTrue(fragments[2] instanceof MeFragment);
        Assert.assertTrue(fragments[3] instanceof LibraryFragment);
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

        Whitebox.setInternalState(homeRegisterActivity, "mBaseFragment", homeRegisterFragment);

        HomeRegisterActivity spyActivity = Mockito.spy(homeRegisterActivity);

        spyActivity.updateSortAndFilter(filterList, sortField);

        Mockito.verify(homeRegisterFragment).updateSortAndFilter(filterList, sortField);

        Mockito.verify(spyActivity).switchToBaseFragment();
    }

    @Test
    public void testRemovePatientHandlerShouldInvokeCorrectMethods() {

        HomeRegisterActivity homeRegisterActivitySpy = Mockito.spy(homeRegisterActivity);
        homeRegisterActivitySpy.removePatientHandler(null);

        Mockito.verify(homeRegisterActivitySpy, Mockito.times(0)).hideProgressDialog();
        Mockito.verify(homeRegisterActivitySpy, Mockito.times(0)).refreshList(FetchStatus.fetched);

        homeRegisterActivitySpy.removePatientHandler(new PatientRemovedEvent());

        Mockito.verify(homeRegisterActivitySpy, Mockito.times(1)).hideProgressDialog();
        Mockito.verify(homeRegisterActivitySpy, Mockito.times(1)).refreshList(FetchStatus.fetched);
    }

    @Test
    public void testShowProgressDialogHandlerInvokesShowProgressDialogWithCorrectTitle() {

        HomeRegisterActivity homeRegisterActivitySpy = Mockito.spy(homeRegisterActivity);
        homeRegisterActivitySpy.showProgressDialogHandler(null);

        Mockito.verify(homeRegisterActivitySpy, Mockito.times(0)).showProgressDialog(ArgumentMatchers.anyInt());
        homeRegisterActivitySpy.showProgressDialogHandler(new ShowProgressDialogEvent());

        Mockito.verify(homeRegisterActivitySpy, Mockito.times(1)).showProgressDialog(R.string.saving_dialog_title);

    }

    @Test
    public void testShowRecordBirthPopUpInvokesMethodsOnRecordBirthAlertDialogsCorrectly() {

        HomeRegisterActivity homeRegisterActivitySpy = Mockito.spy(homeRegisterActivity);
        Whitebox.setInternalState(homeRegisterActivitySpy, "recordBirthAlertDialog", recordBirthAlertDialog);

        CommonPersonObjectClient client = new CommonPersonObjectClient(DUMMY_BASE_ENTITY_ID,
                ImmutableMap.of(DBConstants.KEY.FIRST_NAME, DUMMY_USERNAME, DBConstants.KEY.EDD, "2018-12-25"),
                DUMMY_USERNAME);
        Map<String, String> details = new HashMap<>();
        details.putAll(client.getDetails());
        client.setColumnmaps(details);
        homeRegisterActivitySpy.showRecordBirthPopUp(client);
        Mockito.verify(recordBirthAlertDialog, Mockito.times(1)).setMessage(ArgumentMatchers.contains("25/12/2018"));
        Mockito.verify(recordBirthAlertDialog).show();
    }

    public void testShowAttentionFlagsAlertDialogPopUpInvokesMethodsOnAttentionFlagsAlertDialogsCorrectly() {
        HomeRegisterActivity homeRegisterActivitySpy = Mockito.spy(homeRegisterActivity);
        Whitebox.setInternalState(homeRegisterActivitySpy, "attentionFlagAlertDialog", attentionFlagsAlertDialog);

        List<AttentionFlag> testAttentionFlags = Arrays
                .asList(new AttentionFlag[]{new AttentionFlag("Red Flag 1", true), new AttentionFlag("Red Flag 2",
                        true), new AttentionFlag("Yellow Flag 1", false), new AttentionFlag("Yellow Flag 2", false)});

        homeRegisterActivitySpy.showAttentionFlagsDialog(testAttentionFlags);
        Mockito.verify(attentionFlagsAlertDialog).show();
    }

    @Test
    public void testStartFormActivityInvokesPresenterStartFormMethodWithCorrectParameters() throws Exception {
        HomeRegisterActivity homeRegisterActivitySpy = Mockito.spy(homeRegisterActivity);
        Whitebox.setInternalState(homeRegisterActivitySpy, "presenter", registerPresenter);

        homeRegisterActivitySpy.startFormActivity(TEST_STRING, TEST_STRING, TEST_STRING);
        /* LocationPickerView locationPickerView = null;
        Mockito.verify(registerPresenter).startForm(ArgumentMatchers.eq(TEST_STRING), ArgumentMatchers.eq(TEST_STRING),
                ArgumentMatchers.eq(TEST_STRING), ArgumentMatchers.eq(locationPickerView));*/
        // Todo Use thhe location picker functionality is added on the me page
        Mockito.verify(registerPresenter).startForm(ArgumentMatchers.eq(TEST_STRING), ArgumentMatchers.eq(TEST_STRING),
                ArgumentMatchers.eq(TEST_STRING), ArgumentMatchers.eq(""));
    }

    @Test
    public void testStartRegistrationFormActivityInvokesPresenterStartFormMethodWithCorrectParameters() throws Exception {
        HomeRegisterActivity homeRegisterActivitySpy = Mockito.spy(homeRegisterActivity);
        Whitebox.setInternalState(homeRegisterActivitySpy, "presenter", registerPresenter);

        homeRegisterActivitySpy.startRegistration();
        Mockito.verify(registerPresenter).startForm("anc_register", null,
                null, "");
    }

    @Test ()
    public void testStartFormActivityDisplaysErrorMessageToastWhenExceptionThrown() {
        HomeRegisterActivity homeRegisterActivitySpy = Mockito.spy(homeRegisterActivity);
        RegisterContract.Presenter presenter = null;
        Whitebox.setInternalState(homeRegisterActivitySpy, "presenter", presenter);

        homeRegisterActivitySpy.startFormActivity(TEST_STRING, TEST_STRING, TEST_STRING);
        Mockito.verify(homeRegisterActivitySpy)
                .displayToast(RuntimeEnvironment.application.getString(R.string.error_unable_to_start_form));
    }

    @Test
    public void testGetContextReturnsCorrectActivityInstance() {

        Context context = homeRegisterActivity.getContext();

        Assert.assertNotNull(context);
        Assert.assertTrue(context instanceof HomeRegisterActivity);

    }

    @Test
    public void testHomeRegisterActivityOnBackPressedCalled() {
        HomeRegisterActivity spyActivity = Mockito.spy(homeRegisterActivity);
        spyActivity.onBackPressed();
        Mockito.verify(spyActivity).onBackPressed();
    }

    @Test
    public void testSwitchToAdvancedSearchFromBarcode() {
        HomeRegisterActivity homeRegisterActivitySpy = Mockito.spy(homeRegisterActivity);
        Whitebox.setInternalState(homeRegisterActivitySpy, "isAdvancedSearch", true);
        try {
            Whitebox.invokeMethod(homeRegisterActivitySpy, HomeRegisterActivity.class, "switchToAdvancedSearchFromBarcode");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertEquals(Whitebox.getInternalState(homeRegisterActivitySpy, "advancedSearchQrText"), "");
        Assert.assertEquals(Whitebox.getInternalState(homeRegisterActivitySpy, "isAdvancedSearch"), false);
        Assert.assertEquals(Whitebox.getInternalState(homeRegisterActivitySpy, "advancedSearchFormData"), new HashMap<>());
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
