package org.smartregister.anc.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.contract.ContactSummaryContract;
import org.smartregister.anc.model.ContactSummaryModel;
import org.smartregister.anc.model.ContactSummaryModelTest;
import org.smartregister.anc.util.DBConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ContactSummaryPresenterTest extends BaseUnitTest {

    private final String baseEntityId = UUID.randomUUID().toString();


    @Mock
    private ContactSummaryContract.Presenter presenter;

    @Mock
    private ContactSummaryContract.Interactor interactor;

    @Mock
    private ContactSummaryContract.View view;
    private ContactSummaryPresenter contactSummaryPresenter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        presenter = new ContactSummaryPresenter(interactor);
        presenter.attachView(view);
        contactSummaryPresenter = (ContactSummaryPresenter)presenter;
    }

    @Test
    public void testLoadWomanWithNormalInput() {
        presenter.loadWoman(baseEntityId);
        Mockito.verify(interactor).fetchWomanDetails(Mockito.eq(baseEntityId),
                Mockito.any(ContactSummaryContract.InteractorCallback.class));
    }

    @Test
    public void testLoadWomanWithNullInput() {
        presenter.loadWoman(null);
        Mockito.verify(interactor, Mockito.never()).fetchWomanDetails(Mockito.eq(baseEntityId),
                Mockito.any(ContactSummaryContract.InteractorCallback.class));
    }

    @Test
    public void testLoadWomanWithEmptyInput() {
        presenter.loadWoman("");
        Mockito.verify(interactor, Mockito.never()).fetchWomanDetails(Mockito.eq(baseEntityId),
                Mockito.any(ContactSummaryContract.InteractorCallback.class));
    }

    @Test
    public void testLoadContactsWithNormalInput() {
        presenter.loadUpcomingContacts(baseEntityId);
        Mockito.verify(interactor).fetchUpcomingContacts(Mockito.eq(baseEntityId),
                Mockito.any(ContactSummaryContract.InteractorCallback.class));
    }

    @Test
    public void testLoadContactsWithNullInput() {
        presenter.loadUpcomingContacts(null);
        Mockito.verify(interactor, Mockito.never()).fetchUpcomingContacts(Mockito.eq(baseEntityId),
                Mockito.any(ContactSummaryContract.InteractorCallback.class));
    }

    @Test
    public void testLoadContactsWithEmptyInput() {
        presenter.loadUpcomingContacts("");
        Mockito.verify(interactor, Mockito.never()).fetchUpcomingContacts(Mockito.eq(baseEntityId),
                Mockito.any(ContactSummaryContract.InteractorCallback.class));
    }


    @Test
    public void testOnWomanDetailsFetchedWithNullInput() {
        ContactSummaryPresenter contactSummaryPresenter = (ContactSummaryPresenter) presenter;
        contactSummaryPresenter.onWomanDetailsFetched(null);
        Assert.assertEquals(contactSummaryPresenter.getWomanDetails().isEmpty(),true);

    }

    @Test
    public void testOnWomanDetailsFetchedWitEmptyInput() {
        ContactSummaryPresenter contactSummaryPresenter = (ContactSummaryPresenter) presenter;
        contactSummaryPresenter.onWomanDetailsFetched(new HashMap<String, String>());
        Assert.assertEquals(contactSummaryPresenter.getWomanDetails().isEmpty(),true);

    }


    @Test
    public  void testOnWomanDetailsFetchWithNormalInput(){

        String firstName = "Elly";
        String lastName = "Smith";

        Map<String, String> details = new HashMap<>();
        details.put(DBConstants.KEY.FIRST_NAME, firstName);
        details.put(DBConstants.KEY.LAST_NAME, lastName);
        contactSummaryPresenter.onWomanDetailsFetched(details);
        Assert.assertEquals(contactSummaryPresenter.getWomanDetails().isEmpty(),false);


    }

    @Test
    public void testOnContactsFetchedWithNullInput() {
        ContactSummaryPresenter contactSummaryPresenter = (ContactSummaryPresenter) presenter;
        contactSummaryPresenter.onUpcomingContactsFetched(null);
        Assert.assertEquals(contactSummaryPresenter.getUpcomingContacts().isEmpty(),true);

    }

    @Test
    public void testOnContactsFetchedWitEmptyInput() {
        ContactSummaryPresenter contactSummaryPresenter = (ContactSummaryPresenter) presenter;
        contactSummaryPresenter.onUpcomingContactsFetched(new ArrayList<ContactSummaryModel>());
        Assert.assertEquals(contactSummaryPresenter.getUpcomingContacts().isEmpty(),true);


    }

    @Test
    public void testOnContactsFetchedWitNormalInput() {
         final List<ContactSummaryModel> contactDates = new ArrayList<>();
        contactDates.add(new ContactSummaryModel("Contact 2", "12 August 2018"));
        contactDates.add(new ContactSummaryModel("Contact 3", "16 September 2018"));
        contactDates.add(new ContactSummaryModel("Contact 4", "25 October 2018"));

        contactSummaryPresenter = (ContactSummaryPresenter) presenter;
        contactSummaryPresenter.onUpcomingContactsFetched(contactDates);
        Assert.assertEquals(contactSummaryPresenter.getUpcomingContacts().isEmpty(),false);

    }



}
