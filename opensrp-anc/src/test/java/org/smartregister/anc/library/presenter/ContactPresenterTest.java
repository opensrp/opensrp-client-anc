package org.smartregister.anc.library.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.Context;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.contract.ContactContract;
import org.smartregister.anc.library.repository.PartialContactRepository;
import org.smartregister.anc.library.util.DBConstantsUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ContactPresenterTest extends BaseUnitTest {

    @Mock
    private ContactContract.View view;

    @Mock
    private ContactContract.Interactor interactor;

    @Mock
    private ContactContract.Model model;

    @Mock
    private PartialContactRepository partialContactRepository;

    @Mock
    private Context context;

    private AncLibrary ancLibrary;

    private ContactContract.Presenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        AncLibrary.init(context, 1);
        ancLibrary = Mockito.spy(AncLibrary.getInstance());

        presenter = new ContactPresenter(view);
    }

    @Test
    public void testSetBaseEntityId() {

        Assert.assertFalse(presenter.baseEntityIdExists());

        String baseEntityId = UUID.randomUUID().toString();

        ((ContactPresenter) presenter).setInteractor(interactor);
        ((ContactPresenter) presenter).setModel(model);


        presenter.setBaseEntityId(baseEntityId);

        Mockito.verify(interactor).fetchWomanDetails(Mockito.eq(baseEntityId), Mockito.any(ContactContract.InteractorCallback.class));

        Assert.assertTrue(presenter.baseEntityIdExists());

    }

    @Test
    public void testOnWomanDetailsFetched() {

        String firstName = "First Name Test";
        String lastName = "Last Name Test";

        Map<String, String> details = new HashMap<>();
        details.put(DBConstantsUtils.KeyUtils.FIRST_NAME, firstName);
        details.put(DBConstantsUtils.KeyUtils.LAST_NAME, lastName);

        ContactPresenter contactPresenter = (ContactPresenter) presenter;
        contactPresenter.setModel(model);

        String patientName = firstName + " " + lastName;

        Mockito.doReturn(patientName).when(model).extractPatientName(details);

        contactPresenter.onWomanDetailsFetched(details);

        Assert.assertEquals(details, contactPresenter.getDetails());

        Mockito.verify(model).extractPatientName(Mockito.anyMap());
        Mockito.verify(view).displayPatientName(patientName);

    }

    @Test
    public void testOnNullWomanDetailsFetched() {
        ContactPresenter contactPresenter = (ContactPresenter) presenter;
        contactPresenter.setModel(model);

        contactPresenter.onWomanDetailsFetched(null);

        Assert.assertNull(contactPresenter.getDetails());


        Mockito.verify(model, Mockito.times(0)).extractPatientName(Mockito.anyMap());
        Mockito.verify(view, Mockito.times(0)).displayPatientName(Mockito.anyString());
    }

    @Test
    public void testOnEmptyWomanDetailsFetched() {

        ContactPresenter contactPresenter = (ContactPresenter) presenter;
        contactPresenter.setModel(model);

        contactPresenter.onWomanDetailsFetched(new HashMap<>());

        Assert.assertNull(contactPresenter.getDetails());

        Mockito.verify(model, Mockito.times(0)).extractPatientName(Mockito.anyMap());
        Mockito.verify(view, Mockito.times(0)).displayPatientName(Mockito.anyString());

    }

    @Test
    public void testGetPatientName() {

        ((ContactPresenter) presenter).setModel(model);
        ((ContactPresenter) presenter).setDetails(Mockito.mock(Map.class));

        String patientName = "Patient Name";
        Mockito.doReturn(patientName).when(model).extractPatientName(Mockito.anyMap());

        String actualPatientName = presenter.getPatientName();

        Mockito.verify(model).extractPatientName(Mockito.anyMap());

        Assert.assertEquals(patientName, actualPatientName);

    }

    @Test
    public void testGetPatientNameWhenDetailsIsNull() {

        ((ContactPresenter) presenter).setDetails(null);

        String actualPatientName = presenter.getPatientName();

        Mockito.verify(model, Mockito.times(0)).extractPatientName(Mockito.anyMap());

        Assert.assertEquals("", actualPatientName);

    }

    @Test
    public void testGetPatientNameWhenDetailsIsEmpty() {

        ((ContactPresenter) presenter).setDetails(new HashMap<String, String>());

        String actualPatientName = presenter.getPatientName();

        Mockito.verify(model, Mockito.times(0)).extractPatientName(Mockito.anyMap());

        Assert.assertEquals("", actualPatientName);

    }

    @Test
    public void testOnDestroy() {

        ContactPresenter contactPresenter = (ContactPresenter) presenter;
        Assert.assertNotNull(contactPresenter.getViewReference());
        Assert.assertNotNull(contactPresenter.getInteractor());

        contactPresenter.onDestroy(true);

        Assert.assertNull(contactPresenter.getViewReference());
        Assert.assertNotNull(contactPresenter.getInteractor());

        contactPresenter.onDestroy(false);

        Assert.assertNull(contactPresenter.getViewReference());
        Assert.assertNull(contactPresenter.getInteractor());

    }

    @Test
    public void testDeleteDraftInvokesDeleteJsonDraftOfPatientRepositoryWithCorrectParamters() {
        ContactPresenter contactPresenter = Mockito.spy((ContactPresenter) presenter);

        Mockito.doReturn(ancLibrary).when(contactPresenter).getAncLibrary();
        Mockito.doReturn(partialContactRepository).when(ancLibrary).getPartialContactRepository();
        Mockito.doNothing().when(partialContactRepository).deleteDraftJson(DUMMY_BASE_ENTITY_ID);

        contactPresenter.deleteDraft(DUMMY_BASE_ENTITY_ID);

        Mockito.verify(partialContactRepository, Mockito.times(1)).deleteDraftJson(DUMMY_BASE_ENTITY_ID);
    }


    @Test
    public void testSaveFinalJsonInvokesSaveFinalJsonOfPatientRepositoryWithCorrectParameters() {

        ContactPresenter contactPresenter = Mockito.spy((ContactPresenter) presenter);

        Mockito.doReturn(ancLibrary).when(contactPresenter).getAncLibrary();
        Mockito.doReturn(partialContactRepository).when(ancLibrary).getPartialContactRepository();
        Mockito.doNothing().when(partialContactRepository).saveFinalJson(DUMMY_BASE_ENTITY_ID);


        contactPresenter.saveFinalJson(DUMMY_BASE_ENTITY_ID);

        Mockito.verify(partialContactRepository, Mockito.times(1)).saveFinalJson(DUMMY_BASE_ENTITY_ID);

    }

    @Test
    public void testFinalizeContactFormInvokesFinalizeContactFormOfInteractorWithCorrectParameters() {
        Whitebox.setInternalState(presenter, "interactor", interactor);
        ContactPresenter contactPresenter = Mockito.spy((ContactPresenter) presenter);

        Map<String, String> details = new HashMap<>();
        details.put(DUMMY_USERNAME, DUMMY_PASSWORD);

        contactPresenter.finalizeContactForm(details, RuntimeEnvironment.application);

        Mockito.verify(interactor, Mockito.times(1)).finalizeContactForm(details, RuntimeEnvironment.application);

    }
}
