package org.smartregister.anc.interactor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.Context;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.ContactSummarySendContract;
import org.smartregister.anc.model.ContactSummaryModel;
import org.smartregister.anc.repository.PatientRepository;
import org.smartregister.anc.util.AppExecutors;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.repository.DetailsRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PatientRepository.class, AncApplication.class})
public class ContactSummaryInteractorTest extends BaseUnitTest {

    private ContactSummarySendContract.Interactor summaryInteractor;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Captor
    private ArgumentCaptor<Map<String, String>> detailsArgumentCaptor;

    @Captor
    private ArgumentCaptor<List<ContactSummaryModel>> upcomingContactsCaptor;

    @Captor
    private ArgumentCaptor<Integer> integerArgumentCaptor;

    private final String baseEntityId = UUID.randomUUID().toString();

    @Mock
    private AncApplication ancApplication;

    @Mock
    private Context opensrpContext;

    @Mock
    private android.content.Context context;

    @Mock
    private DetailsRepository detailsRepository;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        summaryInteractor = new ContactSummaryInteractor(new AppExecutors(
                Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor(),
                Executors.newSingleThreadExecutor()));
    }

    @Test
    public void testFetchWomanDetails() {

        ContactSummarySendContract.InteractorCallback callBack = Mockito.mock(
                ContactSummarySendContract.InteractorCallback.class);

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
    public void testFetchUpcomingContacts() {

        ContactSummarySendContract.InteractorCallback callBack = PowerMockito.mock(
                ContactSummarySendContract.InteractorCallback.class);

        final List<String> contactDates = new ArrayList<>();
        contactDates.add("10");
        contactDates.add("20");
        contactDates.add("30");
        contactDates.add("40");


        Map<String, String> details = new HashMap<>();
        details.put(DBConstants.KEY.NEXT_CONTACT, "2");
        details.put(DBConstants.KEY.NEXT_CONTACT_DATE, "2017-04-09");
        details.put(DBConstants.KEY.EDD, "2017-04-10");
        details.put(Constants.DETAILS_KEY.CONTACT_SHEDULE, "{ contact_schedule : \"" + contactDates.toString() + "\" }");

        PowerMockito.mockStatic(AncApplication.class);
        PowerMockito.mockStatic(PatientRepository.class);


        PowerMockito.when(PatientRepository.getWomanProfileDetails(baseEntityId)).thenReturn(details);
        PowerMockito.when(AncApplication.getInstance()).thenReturn(ancApplication);
        PowerMockito.when(ancApplication.getContext()).thenReturn(opensrpContext);
        PowerMockito.when(ancApplication.getApplicationContext()).thenReturn(context);
        PowerMockito.when(context.getString(R.string.contact_number)).thenReturn("Contact %s number");
        PowerMockito.when(ancApplication.getDetailsRepository()).thenReturn(detailsRepository);
        PowerMockito.when(detailsRepository.getAllDetailsForClient(baseEntityId)).thenReturn(details);

        ContactSummarySendContract.Interactor summaryInteractorSpy = Mockito.spy(summaryInteractor);
        summaryInteractorSpy.fetchUpcomingContacts(baseEntityId, callBack);

        Mockito.verify(callBack, Mockito.timeout(ASYNC_TIMEOUT)).onUpcomingContactsFetched(upcomingContactsCaptor.capture(), integerArgumentCaptor.capture());

        Assert.assertNotNull(upcomingContactsCaptor.getValue());
        Assert.assertEquals(contactDates.size(), upcomingContactsCaptor.getValue().size());

        Assert.assertNotNull(integerArgumentCaptor.getValue());
        Assert.assertEquals(1, integerArgumentCaptor.getValue().intValue());

        Assert.assertEquals("Contact 2 number", upcomingContactsCaptor.getValue().get(0).getContactName());
        Assert.assertEquals("Contact 5 number", upcomingContactsCaptor.getValue().get(contactDates.size() - 1).getContactName());
    }


}
