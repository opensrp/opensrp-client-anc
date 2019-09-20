package org.smartregister.anc.library.interactor;

import org.jeasy.rules.api.Facts;
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
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.contract.ContactSummarySendContract;
import org.smartregister.anc.library.model.ContactSummaryModel;
import org.smartregister.anc.library.repository.PatientRepository;
import org.smartregister.anc.library.repository.PreviousContactRepository;
import org.smartregister.anc.library.util.AppExecutors;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.repository.DetailsRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

@RunWith (PowerMockRunner.class)
@PrepareForTest ({PatientRepository.class, AncLibrary.class, Utils.class})
public class YamlConfigInteractorTest extends BaseUnitTest {

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
    private AncLibrary AncLibrary;

    @Mock
    private android.content.Context context;

    @Mock
    private DetailsRepository detailsRepository;

    @Mock
    private PreviousContactRepository previousContactRepository;


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
        details.put(DBConstantsUtils.KEY_UTILS.FIRST_NAME, firstName);
        details.put(DBConstantsUtils.KEY_UTILS.LAST_NAME, lastName);

        PowerMockito.mockStatic(PatientRepository.class);

        PowerMockito.when(PatientRepository.getWomanProfileDetails(baseEntityId))
                .thenReturn(details);

        summaryInteractor.fetchWomanDetails(baseEntityId, callBack);

        Mockito.verify(callBack, Mockito.timeout(ASYNC_TIMEOUT)).onWomanDetailsFetched(detailsArgumentCaptor.capture());

        Assert.assertEquals(details, detailsArgumentCaptor.getValue());
    }

    @Test
    public void testFetchUpcomingContactsWithReferralContacts() {
        ContactSummarySendContract.InteractorCallback callBack = PowerMockito.mock(
                ContactSummarySendContract.InteractorCallback.class);
        ContactSummarySendContract.Interactor summaryInteractorSpy = Mockito.spy(summaryInteractor);

        final List<String> contactSchedule = new ArrayList<>();
        contactSchedule.add("10");
        contactSchedule.add("20");
        contactSchedule.add("30");
        contactSchedule.add("40");

        String contactScheduleList = "[10, 20, 30, 40]";

        Map<String, String> details = new HashMap<>();
        details.put(DBConstantsUtils.KEY_UTILS.NEXT_CONTACT, "2");
        details.put(DBConstantsUtils.KEY_UTILS.NEXT_CONTACT_DATE, "2017-04-09");
        details.put(DBConstantsUtils.KEY_UTILS.EDD, "2017-04-10");
        details.put(ConstantsUtils.DetailsKeyUtils.CONTACT_SCHEDULE, "{ contact_schedule : \"" + contactSchedule.toString() + "\" }");

        Facts facts = new Facts();
        facts.put(ConstantsUtils.DetailsKeyUtils.CONTACT_SCHEDULE, contactScheduleList);

        PowerMockito.mockStatic(AncLibrary.class);
        PowerMockito.mockStatic(PatientRepository.class);
        PowerMockito.mockStatic(Utils.class);

        PowerMockito.when(PatientRepository.getWomanProfileDetails(baseEntityId)).thenReturn(details);
        PowerMockito.when(AncLibrary.getInstance()).thenReturn(AncLibrary);
        PowerMockito.when(AncLibrary.getApplicationContext()).thenReturn(context);
        PowerMockito.when(context.getString(R.string.contact_number)).thenReturn("Contact %s number");
        PowerMockito.when(AncLibrary.getDetailsRepository()).thenReturn(detailsRepository);
        PowerMockito.when(detailsRepository.getAllDetailsForClient(baseEntityId)).thenReturn(details);

        PowerMockito.when(AncLibrary.getPreviousContactRepository()).thenReturn(previousContactRepository);
        PowerMockito.when(previousContactRepository.getImmediatePreviousSchedule(baseEntityId, "1")).thenReturn(facts);
        PowerMockito.when(Utils.getListFromString(contactScheduleList)).thenReturn(contactSchedule);

        summaryInteractorSpy.fetchUpcomingContacts(baseEntityId, "-2", callBack);

        Mockito.verify(callBack, Mockito.timeout(ASYNC_TIMEOUT))
                .onUpcomingContactsFetched(upcomingContactsCaptor.capture(), integerArgumentCaptor.capture());

        Assert.assertNotNull(upcomingContactsCaptor.getValue());
        Assert.assertEquals(contactSchedule.size(), upcomingContactsCaptor.getValue().size());

        Assert.assertNotNull(integerArgumentCaptor.getValue());
        Assert.assertEquals(-2, integerArgumentCaptor.getValue().intValue());

        Assert.assertEquals("Contact 2 number", upcomingContactsCaptor.getValue().get(0).getContactName());
        Assert.assertEquals("Contact 5 number",
                upcomingContactsCaptor.getValue().get(contactSchedule.size() - 1).getContactName());
    }

    @Test
    public void testFetchUpcomingContactsWithNoReferralContacts() {
        ContactSummarySendContract.InteractorCallback callBack = PowerMockito.mock(
                ContactSummarySendContract.InteractorCallback.class);
        ContactSummarySendContract.Interactor summaryInteractorSpy = Mockito.spy(summaryInteractor);

        final List<String> contactSchedule = new ArrayList<>();
        contactSchedule.add("10");
        contactSchedule.add("20");
        contactSchedule.add("30");
        contactSchedule.add("40");

        String contactScheduleList = "[10, 20, 30, 40]";

        Map<String, String> details = new HashMap<>();
        details.put(DBConstantsUtils.KEY_UTILS.NEXT_CONTACT, "2");
        details.put(DBConstantsUtils.KEY_UTILS.NEXT_CONTACT_DATE, "2017-04-09");
        details.put(DBConstantsUtils.KEY_UTILS.EDD, "2017-04-10");
        details.put(ConstantsUtils.DetailsKeyUtils.CONTACT_SCHEDULE, "{ contact_schedule : \"" + contactSchedule.toString() + "\" }");

        Facts facts = new Facts();
        facts.put(ConstantsUtils.DetailsKeyUtils.CONTACT_SCHEDULE, contactScheduleList);

        PowerMockito.mockStatic(AncLibrary.class);
        PowerMockito.mockStatic(PatientRepository.class);
        PowerMockito.mockStatic(Utils.class);

        PowerMockito.when(PatientRepository.getWomanProfileDetails(baseEntityId)).thenReturn(details);
        PowerMockito.when(AncLibrary.getInstance()).thenReturn(AncLibrary);
        PowerMockito.when(AncLibrary.getApplicationContext()).thenReturn(context);
        PowerMockito.when(context.getString(R.string.contact_number)).thenReturn("Contact %s number");
        PowerMockito.when(AncLibrary.getDetailsRepository()).thenReturn(detailsRepository);
        PowerMockito.when(detailsRepository.getAllDetailsForClient(baseEntityId)).thenReturn(details);
        PowerMockito.when(Utils.getListFromString(contactScheduleList)).thenReturn(contactSchedule);

        summaryInteractorSpy.fetchUpcomingContacts(baseEntityId, "", callBack);

        Mockito.verify(callBack, Mockito.timeout(ASYNC_TIMEOUT))
                .onUpcomingContactsFetched(upcomingContactsCaptor.capture(), integerArgumentCaptor.capture());

        Assert.assertNotNull(upcomingContactsCaptor.getValue());
        Assert.assertEquals(contactSchedule.size(), upcomingContactsCaptor.getValue().size());

        Assert.assertNotNull(integerArgumentCaptor.getValue());
        Assert.assertEquals(1, integerArgumentCaptor.getValue().intValue());

        Assert.assertEquals("Contact 2 number", upcomingContactsCaptor.getValue().get(0).getContactName());
        Assert.assertEquals("Contact 5 number",
                upcomingContactsCaptor.getValue().get(contactSchedule.size() - 1).getContactName());
    }

    @Test
    public void testFetchUpcomingContactsWithNoReferralContactsAndContactSchedule() {
        ContactSummarySendContract.InteractorCallback callBack = PowerMockito.mock(
                ContactSummarySendContract.InteractorCallback.class);
        ContactSummarySendContract.Interactor summaryInteractorSpy = Mockito.spy(summaryInteractor);

        final List<String> contactSchedule = new ArrayList<>();
        String contactScheduleList = "[10, 20, 30, 40]";

        Map<String, String> details = new HashMap<>();
        details.put(DBConstantsUtils.KEY_UTILS.NEXT_CONTACT, "2");
        details.put(DBConstantsUtils.KEY_UTILS.NEXT_CONTACT_DATE, "2017-04-09");
        details.put(DBConstantsUtils.KEY_UTILS.EDD, "2017-04-10");

        Facts facts = new Facts();
        facts.put(ConstantsUtils.DetailsKeyUtils.CONTACT_SCHEDULE, contactScheduleList);

        PowerMockito.mockStatic(AncLibrary.class);
        PowerMockito.mockStatic(PatientRepository.class);
        PowerMockito.mockStatic(Utils.class);

        PowerMockito.when(PatientRepository.getWomanProfileDetails(baseEntityId)).thenReturn(details);
        PowerMockito.when(AncLibrary.getInstance()).thenReturn(AncLibrary);
        PowerMockito.when(AncLibrary.getDetailsRepository()).thenReturn(detailsRepository);
        PowerMockito.when(detailsRepository.getAllDetailsForClient(baseEntityId)).thenReturn(details);
        PowerMockito.when(Utils.getListFromString(contactScheduleList)).thenReturn(contactSchedule);

        summaryInteractorSpy.fetchUpcomingContacts(baseEntityId, "", callBack);

        Mockito.verify(callBack, Mockito.timeout(ASYNC_TIMEOUT))
                .onUpcomingContactsFetched(upcomingContactsCaptor.capture(), integerArgumentCaptor.capture());

        Assert.assertNotNull(upcomingContactsCaptor.getValue());
        Assert.assertEquals(contactSchedule.size(), upcomingContactsCaptor.getValue().size());

        Assert.assertNotNull(integerArgumentCaptor.getValue());
        Assert.assertEquals(1, integerArgumentCaptor.getValue().intValue());

        Assert.assertEquals(0, upcomingContactsCaptor.getValue().size());
    }

}
