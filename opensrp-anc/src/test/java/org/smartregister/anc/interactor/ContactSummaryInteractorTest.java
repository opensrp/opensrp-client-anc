package org.smartregister.anc.interactor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.contract.ContactSummaryContract;
import org.smartregister.anc.model.ContactSummaryModel;
import org.smartregister.anc.repository.PatientRepository;
import org.smartregister.anc.util.AppExecutors;
import org.smartregister.anc.util.DBConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

@RunWith(PowerMockRunner.class)
public class ContactSummaryInteractorTest extends BaseUnitTest {

    private ContactSummaryContract.Interactor summaryInteractor;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Captor
    private ArgumentCaptor<Map<String, String>> detailsArgumentCaptor;

    @Captor
    private ArgumentCaptor<List<ContactSummaryModel>> upcomingContactsCaptor;

    private final String baseEntityId = UUID.randomUUID().toString();


    @Before
    public void setUp() {
        summaryInteractor = new ContactSummaryInteractor(new AppExecutors(
                Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor(),
                Executors.newSingleThreadExecutor()));
    }

    @PrepareForTest({PatientRepository.class})
    @Test
    public void testFetchWomanDetails() {

        ContactSummaryContract.InteractorCallback callBack = Mockito.mock(
                ContactSummaryContract.InteractorCallback.class);

        String firstName = "Elly";
        String lastName = "Smith";

        Map<String, String> details = new HashMap<>();
        details.put(DBConstants.KEY.FIRST_NAME, firstName);
        details.put(DBConstants.KEY.LAST_NAME, lastName);

        PowerMockito.mockStatic(PatientRepository.class);

        PowerMockito.when(PatientRepository.getWomanProfileDetails(baseEntityId))
                .thenReturn(details);

        summaryInteractor.fetchWomanDetails(baseEntityId, callBack);

        Mockito.verify(callBack, Mockito.timeout(ASYNC_TIMEOUT)).onWomanDetailsFetched(detailsArgumentCaptor.capture());

        Assert.assertEquals(details, detailsArgumentCaptor.getValue());
    }

    @Test
    @Ignore
    public void testFetchUpcomingContacts() {

        ContactSummaryContract.InteractorCallback callBack = PowerMockito.mock(
                ContactSummaryContract.InteractorCallback.class);

        final List<ContactSummaryModel> contactDates = new ArrayList<>();
        contactDates.add(new ContactSummaryModel("Contact 2", "12 August 2018"));
        contactDates.add(new ContactSummaryModel("Contact 3", "16 September 2018"));
        contactDates.add(new ContactSummaryModel("Contact 4", "25 October 2018"));

        summaryInteractor.fetchUpcomingContacts(baseEntityId, callBack);

        Mockito.verify(callBack, Mockito.timeout(ASYNC_TIMEOUT)).onUpcomingContactsFetched(upcomingContactsCaptor.capture(), 1);

        Assert.assertEquals(contactDates.size(), upcomingContactsCaptor.getValue().size());
    }


}
