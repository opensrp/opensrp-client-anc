package org.smartregister.anc.presenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.smartregister.anc.activity.ContactSummaryActivity;
import org.smartregister.anc.contract.BaseContactContract;
import org.smartregister.anc.contract.ContactSummaryContract;
import org.smartregister.anc.model.ContactSummaryModel;

import java.util.UUID;

import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ContactSummaryPresenterTest {

    private final String baseEntityId =  UUID.randomUUID().toString();

    @Mock
    private ContactSummaryContract.Presenter presenter;

    @Mock
    private ContactSummaryContract.Interactor interactor;

    @Mock
    private ContactSummaryContract.View view;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        presenter = new ContactSummaryPresenter(interactor);
        presenter.attachView(view);
    }

    @Test
    public void testLoadWomanWithNormalInput() {
        presenter.loadWoman(baseEntityId);
        Mockito.verify(interactor).fetchWomanDetails(Mockito.eq(baseEntityId),Mockito.any(ContactSummaryContract.InteractorCallback.class));
    }
    @Test
    public void testLoadWomanWithNullInput() {
        presenter.loadWoman(null);
        Mockito.verify(interactor,Mockito.never()).fetchWomanDetails(Mockito.eq(baseEntityId),Mockito.any(ContactSummaryContract.InteractorCallback.class));
    }
    @Test
    public void testLoadWomanWithEmptyInput() {
        presenter.loadWoman("");
        Mockito.verify(interactor, Mockito.never()).fetchWomanDetails(Mockito.eq(baseEntityId),Mockito.any(ContactSummaryContract.InteractorCallback.class));
    }
    @Test
    public void testLoadContactsWithNormalInput() {
        presenter.loadUpcomingContacts(baseEntityId);
        Mockito.verify(interactor).fetchUpcomingContacts(Mockito.eq(baseEntityId),Mockito.any(ContactSummaryContract.InteractorCallback.class));
    }
    @Test
    public void testLoadContactsWithNullInput() {
        presenter.loadUpcomingContacts(null);
        Mockito.verify(interactor,Mockito.never()).fetchUpcomingContacts(Mockito.eq(baseEntityId),Mockito.any(ContactSummaryContract.InteractorCallback.class));
    }
    @Test
    public void testLoadContactsWithEmptyInput() {
        presenter.loadUpcomingContacts("");
        Mockito.verify(interactor, Mockito.never()).fetchUpcomingContacts(Mockito.eq(baseEntityId),Mockito.any(ContactSummaryContract.InteractorCallback.class));
    }

    @Test
    public void testDisplayWomansName(){
        ((ContactSummaryPresenter)presenter).onUpcomingContactsFetched(Mockito.<ContactSummaryModel>anyList());
        Mockito.verify(view).displayWomansName(Mockito.anyString());
    }

}
