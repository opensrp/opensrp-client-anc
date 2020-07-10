package org.smartregister.anc.library.presenter;


import androidx.core.util.Pair;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.contract.RegisterContract;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.FetchStatus;
import org.smartregister.view.LocationPickerView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by keymanc on 11/07/2018.
 */
public class RegisterPresenterTest extends BaseUnitTest {
    @Mock
    private RegisterContract.View view;

    @Mock
    private RegisterContract.Interactor interactor;

    @Mock
    private RegisterContract.Model model;

    private RegisterContract.Presenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new RegisterPresenter(view);
    }

    @Test
    public void testRegisterViewConfiguration() {

        RegisterPresenter registerPresenter = (RegisterPresenter) presenter;
        registerPresenter.setModel(model);

        Mockito.doNothing().when(model).registerViewConfigurations(ArgumentMatchers.anyList());
        registerPresenter.registerViewConfigurations(null);
        Mockito.verify(model).registerViewConfigurations(null);

        List<String> list = new ArrayList<>();
        list.add("abc");
        list.add("def");
        list.add("ghi");

        registerPresenter.registerViewConfigurations(list);
        Mockito.verify(model).registerViewConfigurations(list);

    }

    @Test
    public void testUnRegisterViewConfiguration() {

        RegisterPresenter registerPresenter = (RegisterPresenter) presenter;
        registerPresenter.setModel(model);

        Mockito.doNothing().when(model).unregisterViewConfiguration(ArgumentMatchers.anyList());
        registerPresenter.unregisterViewConfiguration(null);
        Mockito.verify(model).unregisterViewConfiguration(null);

        List<String> list = new ArrayList<>();
        list.add("abc");
        list.add("def");
        list.add("ghi");

        registerPresenter.unregisterViewConfiguration(list);
        Mockito.verify(model).unregisterViewConfiguration(list);

    }

    @Test
    public void testSaveLanguage() {
        RegisterPresenter registerPresenter = (RegisterPresenter) presenter;
        registerPresenter.setModel(model);

        String language = "sheng";
        Mockito.doNothing().when(model).saveLanguage(ArgumentMatchers.anyString());
        Mockito.doNothing().when(view).displayToast(ArgumentMatchers.anyString());

        registerPresenter.saveLanguage(language);

        Mockito.verify(model).saveLanguage(language);
        Mockito.verify(view).displayToast(language + " selected");

    }

    @Test
    public void testStartFormWithNoLocation() throws Exception {
        String formName = "anc_registration";
        String entityId = "1";
        String metadata = "metadata";
        LocationPickerView locationPickerView = null;

        Mockito.doNothing().when(view).displayToast(ArgumentMatchers.anyInt());
        presenter.startForm(formName, entityId, metadata, locationPickerView);
        Mockito.verify(view, Mockito.times(1)).displayToast(R.string.no_location_picker);

        locationPickerView = Mockito.mock(LocationPickerView.class);
        Mockito.when(locationPickerView.getSelectedItem()).thenReturn("");
        presenter.startForm(formName, entityId, metadata, locationPickerView);
        Mockito.verify(view, Mockito.times(2)).displayToast(R.string.no_location_picker);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testStartFormWithNoEntityId() throws Exception {
        RegisterPresenter registerPresenter = (RegisterPresenter) presenter;
        registerPresenter.setModel(model);
        registerPresenter.setInteractor(interactor);

        LocationPickerView locationPickerView = Mockito.mock(LocationPickerView.class);

        String formName = "anc_registration";
        String entityId = "";
        String metadata = "metadata";
        String locationName = "Location Name";
        String locationID = "Location ID";

        Mockito.when(locationPickerView.getSelectedItem()).thenReturn(locationName);
        Mockito.doReturn(locationID).when(model).getLocationId(ArgumentMatchers.anyString());
        Mockito.doNothing().when(interactor).getNextUniqueId(ArgumentMatchers.any((Class<Triple<String, String, String>>) (Object) Triple.class),
                Mockito.any(RegisterContract.InteractorCallBack.class));

        presenter.startForm(formName, entityId, metadata, locationPickerView);

        Mockito.verify(locationPickerView, Mockito.times(2)).getSelectedItem();
        Mockito.verify(model).getLocationId(locationName);
        Mockito.verify(interactor).getNextUniqueId(Triple.of(formName, metadata, locationID), (RegisterContract.InteractorCallBack) presenter);

    }

    @Test
    public void testStartEditForm() throws Exception {
        RegisterPresenter registerPresenter = (RegisterPresenter) presenter;
        registerPresenter.setModel(model);

        LocationPickerView locationPickerView = Mockito.mock(LocationPickerView.class);
        JSONObject form = Mockito.mock(JSONObject.class);

        String formName = "anc_registration";
        String entityId = "1";
        String metadata = "metadata";
        String locationName = "Location Name";
        String locationID = "Location ID";

        Mockito.when(locationPickerView.getSelectedItem()).thenReturn(locationName);
        Mockito.doReturn(locationID).when(model).getLocationId(ArgumentMatchers.anyString());
        Mockito.doReturn(form).when(model).getFormAsJson(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());

        presenter.startForm(formName, entityId, metadata, locationPickerView);

        Mockito.verify(locationPickerView, Mockito.times(2)).getSelectedItem();
        Mockito.verify(model).getLocationId(locationName);
        Mockito.verify(model).getFormAsJson(formName, entityId, locationID);
        Mockito.verify(view).startFormActivity(form);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSaveForm() {
        RegisterPresenter registerPresenter = (RegisterPresenter) presenter;
        registerPresenter.setModel(model);
        registerPresenter.setInteractor(interactor);


        String jsonString = "{'json':'string'}";

        String baseEntityId = "112123";
        Client client = new Client(baseEntityId);
        Event event = new Event();
        Pair<Client, Event> pair = Pair.create(client, event);

        Mockito.doReturn(pair).when(model).processRegistration(ArgumentMatchers.anyString());
        Mockito.doNothing().when(interactor).saveRegistration(ArgumentMatchers.any((Class<Pair<Client, Event>>) (Object) Pair.class),
                ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(RegisterContract.InteractorCallBack.class));

        presenter.saveRegistrationForm(jsonString, false);

        Mockito.verify(view).showProgressDialog(R.string.saving_dialog_title);
        Mockito.verify(model).processRegistration(jsonString);
        Mockito.verify(interactor).saveRegistration(pair, jsonString, false, (RegisterContract.InteractorCallBack) presenter);
    }

    @Test
    public void testOnNoUniqueId() {
        RegisterPresenter registerPresenter = (RegisterPresenter) presenter;

        registerPresenter.onNoUniqueId();
        Mockito.verify(view).displayShortToast(R.string.no_openmrs_id);

    }

    @Test
    public void testOnUniqueIdFetched() throws Exception {
        RegisterPresenter registerPresenter = (RegisterPresenter) presenter;
        registerPresenter.setModel(model);

        JSONObject form = Mockito.mock(JSONObject.class);

        String formName = "anc_registration";
        String entityId = "123";
        String metadata = "metadata";
        String currentLocationId = "Location Id";

        Triple<String, String, String> triple = Triple.of(formName, metadata, currentLocationId);

        Mockito.doReturn(form).when(model).getFormAsJson(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        registerPresenter.onUniqueIdFetched(triple, entityId);

        Mockito.verify(model).getFormAsJson(formName, entityId, currentLocationId);
        Mockito.verify(view).startFormActivity(form);

    }

    @Test
    public void testOnRegistrationSaved() {
        RegisterPresenter registerPresenter = (RegisterPresenter) presenter;
        Mockito.doNothing().when(view).refreshList(ArgumentMatchers.any(FetchStatus.class));

        registerPresenter.onRegistrationSaved(false);

        Mockito.verify(view).refreshList(FetchStatus.fetched);
        Mockito.verify(view).hideProgressDialog();

    }

    @Test
    public void testOnDestroy() {
        RegisterPresenter registerPresenter = (RegisterPresenter) presenter;
        registerPresenter.setInteractor(interactor);

        Mockito.doNothing().when(interactor).onDestroy(ArgumentMatchers.anyBoolean());
        registerPresenter.onDestroy(true);
        Mockito.verify(interactor).onDestroy(true);
        registerPresenter.onDestroy(false);
        Mockito.verify(interactor).onDestroy(false);

    }

    @Test
    public void testUpdateInitials() {
        RegisterPresenter registerPresenter = (RegisterPresenter) presenter;
        registerPresenter.setModel(model);

        String initials = "EK";

        // Null initials
        Mockito.doReturn(null).when(model).getInitials();

        registerPresenter.updateInitials();
        Mockito.verify(view, Mockito.times(0)).updateInitialsText(initials);

        // Not null initials
        Mockito.doReturn(initials).when(model).getInitials();

        registerPresenter.updateInitials();
        Mockito.verify(view).updateInitialsText(initials);
    }
}
