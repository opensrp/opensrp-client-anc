package org.smartregister.anc.library.activity;

import android.app.Activity;
import android.content.Context;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.common.collect.ImmutableMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.anc.library.R;
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
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.domain.FetchStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 24/07/2018.
 */
public class BaseHomeRegisterActivityTest extends BaseActivityUnitTest {

    private BaseHomeRegisterActivity baseHomeRegisterActivity;

    private ActivityController<BaseHomeRegisterActivity> controller;

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
    @Override
    public void setUp() {
        super.setUp();
        controller = Robolectric.buildActivity(BaseHomeRegisterActivity.class).create().start();
        baseHomeRegisterActivity = controller.get();
    }

    @Override
    protected Activity getActivity() {
        return baseHomeRegisterActivity;
    }

    @Override
    protected ActivityController getActivityController() {
        return controller;
    }

    @After
    public void tearDown() {
        destroyController();
    }

    @Test
    public void testActivityCreatedSuccesfully() {
        Assert.assertNotNull(baseHomeRegisterActivity);
    }

    @Test
    public void testGetRegisterFragmentShouldReturnAValidInstance() {
        Fragment fragment = baseHomeRegisterActivity.getRegisterFragment();
        Assert.assertNotNull(fragment);
        Assert.assertTrue(fragment instanceof HomeRegisterFragment);
    }

    @Test
    public void testGetOtherFragmentsShouldReturnCorrectInstances() {
        Fragment[] fragments = baseHomeRegisterActivity.getOtherFragments();
        Assert.assertNotNull(fragments);
        Assert.assertEquals(4, fragments.length);
        Assert.assertTrue(fragments[0] instanceof AdvancedSearchFragment);
        Assert.assertTrue(fragments[1] instanceof SortFilterFragment);
        Assert.assertTrue(fragments[2] instanceof MeFragment);
        Assert.assertTrue(fragments[3] instanceof LibraryFragment);
    }

    @Test
    public void testInitializePresenterInstantiatesPresenterCorrectly() {
        RegisterPresenter presenter = Whitebox.getInternalState(baseHomeRegisterActivity, "presenter");
        Assert.assertNotNull(presenter);
        baseHomeRegisterActivity.initializePresenter();
        Assert.assertNotNull(presenter);
    }

    @Test
    public void testGetViewIdentifiersReturnsCorrectIdentifierValues() {
        List<String> viewIdentifiers = baseHomeRegisterActivity.getViewIdentifiers();
        Assert.assertNotNull(viewIdentifiers);
        Assert.assertEquals(1, viewIdentifiers.size());
        Assert.assertEquals(ConstantsUtils.ConfigurationUtils.HOME_REGISTER, viewIdentifiers.get(0));

    }

    @Test
    public void testUpdateSortAndFilterShouldInvokeCorrectMethods() {
        Whitebox.setInternalState(baseHomeRegisterActivity, "mBaseFragment", homeRegisterFragment);
        BaseHomeRegisterActivity spyActivity = Mockito.spy(baseHomeRegisterActivity);
        spyActivity.updateSortAndFilter(filterList, sortField);
        Mockito.verify(homeRegisterFragment).updateSortAndFilter(filterList, sortField);
        Mockito.verify(spyActivity).switchToBaseFragment();
    }

    @Test
    public void testRemovePatientHandlerShouldInvokeCorrectMethods() {

        BaseHomeRegisterActivity baseHomeRegisterActivitySpy = Mockito.spy(baseHomeRegisterActivity);
        baseHomeRegisterActivitySpy.removePatientHandler(null);

        Mockito.verify(baseHomeRegisterActivitySpy, Mockito.times(0)).hideProgressDialog();
        Mockito.verify(baseHomeRegisterActivitySpy, Mockito.times(0)).refreshList(FetchStatus.fetched);

        baseHomeRegisterActivitySpy.removePatientHandler(new PatientRemovedEvent());

        Mockito.verify(baseHomeRegisterActivitySpy, Mockito.times(1)).hideProgressDialog();
        Mockito.verify(baseHomeRegisterActivitySpy, Mockito.times(1)).refreshList(FetchStatus.fetched);
    }

    @Test
    public void testShowProgressDialogHandlerInvokesShowProgressDialogWithCorrectTitle() {
        BaseHomeRegisterActivity baseHomeRegisterActivitySpy = Mockito.spy(baseHomeRegisterActivity);
        baseHomeRegisterActivitySpy.showProgressDialogHandler(null);

        Mockito.verify(baseHomeRegisterActivitySpy, Mockito.times(0)).showProgressDialog(ArgumentMatchers.anyInt());
        baseHomeRegisterActivitySpy.showProgressDialogHandler(new ShowProgressDialogEvent());

        Mockito.verify(baseHomeRegisterActivitySpy, Mockito.times(1)).showProgressDialog(R.string.saving_dialog_title);

    }

    @Test
    public void testShowRecordBirthPopUpInvokesMethodsOnRecordBirthAlertDialogsCorrectly() {

        BaseHomeRegisterActivity baseHomeRegisterActivitySpy = Mockito.spy(baseHomeRegisterActivity);
        Whitebox.setInternalState(baseHomeRegisterActivitySpy, "recordBirthAlertDialog", recordBirthAlertDialog);

        CommonPersonObjectClient client = new CommonPersonObjectClient(DUMMY_BASE_ENTITY_ID,
                ImmutableMap.of(DBConstantsUtils.KeyUtils.FIRST_NAME, DUMMY_USERNAME, DBConstantsUtils.KeyUtils.EDD, "2018-12-25"),
                DUMMY_USERNAME);
        Map<String, String> details = new HashMap<>(client.getDetails());
        client.setColumnmaps(details);
        baseHomeRegisterActivitySpy.showRecordBirthPopUp(client);
        Mockito.verify(recordBirthAlertDialog, Mockito.times(1)).setMessage(ArgumentMatchers.contains("25/12/2018"));
        Mockito.verify(recordBirthAlertDialog).show();
    }

    public void testShowAttentionFlagsAlertDialogPopUpInvokesMethodsOnAttentionFlagsAlertDialogsCorrectly() {
        BaseHomeRegisterActivity baseHomeRegisterActivitySpy = Mockito.spy(baseHomeRegisterActivity);
        Whitebox.setInternalState(baseHomeRegisterActivitySpy, "attentionFlagAlertDialog", attentionFlagsAlertDialog);

        List<AttentionFlag> testAttentionFlags = Arrays.asList(new AttentionFlag("Red Flag 1", true), new AttentionFlag("Red Flag 2",
                true), new AttentionFlag("Yellow Flag 1", false), new AttentionFlag("Yellow Flag 2", false));

        baseHomeRegisterActivitySpy.showAttentionFlagsDialog(testAttentionFlags);
        Mockito.verify(attentionFlagsAlertDialog).show();
    }

    @Test
    public void testStartFormActivityInvokesPresenterStartFormMethodWithCorrectParameters() throws Exception {
        BaseHomeRegisterActivity baseHomeRegisterActivitySpy = Mockito.spy(baseHomeRegisterActivity);
        // Removing this makes the tests flaky and dependent on other tests
        Whitebox.setInternalState(baseHomeRegisterActivitySpy, "mBaseFragment", homeRegisterFragment);
        Whitebox.setInternalState(baseHomeRegisterActivitySpy, "presenter", registerPresenter);

        baseHomeRegisterActivitySpy.startFormActivity(TEST_STRING, TEST_STRING, TEST_STRING);
        /* LocationPickerView locationPickerView = null;
        Mockito.verify(registerPresenter).startForm(ArgumentMatchers.eq(TEST_STRING), ArgumentMatchers.eq(TEST_STRING),
                ArgumentMatchers.eq(TEST_STRING), ArgumentMatchers.eq(locationPickerView));*/
        // Todo Use thhe location picker functionality is added on the me page
        Mockito.verify(registerPresenter).startForm(TEST_STRING, TEST_STRING,
                TEST_STRING, (String) null);
    }

    @Test
    public void testStartRegistrationFormActivityInvokesPresenterStartFormMethodWithCorrectParameters() throws Exception {
        BaseHomeRegisterActivity baseHomeRegisterActivitySpy = Mockito.spy(baseHomeRegisterActivity);
        // Removing this makes the tests flaky and dependent on other tests
        Whitebox.setInternalState(baseHomeRegisterActivitySpy, "mBaseFragment", homeRegisterFragment);
        Whitebox.setInternalState(baseHomeRegisterActivitySpy, "presenter", registerPresenter);
        baseHomeRegisterActivitySpy.startRegistration();
        Mockito.verify(registerPresenter).startForm("anc_register", null, "", (String) null);
    }

    @Test()
    public void testStartFormActivityDisplaysErrorMessageToastWhenExceptionThrown() {
        BaseHomeRegisterActivity baseHomeRegisterActivitySpy = Mockito.spy(baseHomeRegisterActivity);
        RegisterContract.Presenter presenter = null;
        Whitebox.setInternalState(baseHomeRegisterActivitySpy, "presenter", presenter);
        baseHomeRegisterActivitySpy.startFormActivity(TEST_STRING, TEST_STRING, TEST_STRING);
        Mockito.verify(baseHomeRegisterActivitySpy).displayToast(RuntimeEnvironment.application.getString(R.string.error_unable_to_start_form));
    }

    @Test
    public void testGetContextReturnsCorrectActivityInstance() {
        Context context = baseHomeRegisterActivity.getContext();
        Assert.assertNotNull(context);
        Assert.assertTrue(context instanceof BaseHomeRegisterActivity);
    }

    @Test
    public void testHomeRegisterActivityOnBackPressedCalled() {
        BaseHomeRegisterActivity spyActivity = Mockito.spy(baseHomeRegisterActivity);
        spyActivity.onBackPressed();
        Mockito.verify(spyActivity).onBackPressed();
    }

    @Test
    public void testSwitchToAdvancedSearchFromBarcode() {
        BaseHomeRegisterActivity baseHomeRegisterActivitySpy = Mockito.spy(baseHomeRegisterActivity);
        Whitebox.setInternalState(baseHomeRegisterActivitySpy, "isAdvancedSearch", true);
        try {
            Whitebox.invokeMethod(baseHomeRegisterActivitySpy, BaseHomeRegisterActivity.class, "switchToAdvancedSearchFromBarcode");
        } catch (Exception e) {
            Timber.e(e, "BaseHomeRegisterActivityTest --> testSwitchToAdvancedSearchFromBarcode");
        }

        Assert.assertEquals(Whitebox.getInternalState(baseHomeRegisterActivitySpy, "advancedSearchQrText"), "");
        Assert.assertFalse(Whitebox.getInternalState(baseHomeRegisterActivitySpy, "isAdvancedSearch"));
        Assert.assertEquals(Whitebox.getInternalState(baseHomeRegisterActivitySpy, "advancedSearchFormData"), new HashMap<>());
    }
}
