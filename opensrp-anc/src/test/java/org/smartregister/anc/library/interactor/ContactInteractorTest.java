package org.smartregister.anc.library.interactor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.contract.ContactContract;
import org.smartregister.anc.library.domain.WomanDetail;
import org.smartregister.anc.library.helper.AncRulesEngineHelper;
import org.smartregister.anc.library.repository.PatientRepositoryHelper;
import org.smartregister.anc.library.repository.PreviousContactRepositoryHelper;
import org.smartregister.anc.library.rule.ContactRule;
import org.smartregister.anc.library.util.AppExecutors;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.repository.DetailsRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"org.powermock.*", "org.mockito.*",})
public class ContactInteractorTest extends BaseUnitTest {

    private ContactContract.Interactor interactor;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Captor
    private ArgumentCaptor<Map<String, String>> detailsArgumentCaptor;

    @Mock
    private AncLibrary AncLibrary;

    @Mock
    private AncRulesEngineHelper ancRulesEngineHelper;

    @Mock
    private DetailsRepository detailsRepository;

    @Mock
    private PreviousContactRepositoryHelper previousContactRepositoryHelper;

    @Before
    public void setUp() {
        interactor = new ContactInteractor(new AppExecutors(Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor()));
    }

    @PrepareForTest({PatientRepositoryHelper.class})
    @Test
    public void testFetchWomanDetails() {

        String baseEntityId = UUID.randomUUID().toString();
        ContactContract.InteractorCallback callBack = Mockito.mock(ContactContract.InteractorCallback.class);

        String firstName = "First Name";
        String lastName = "Last Name";

        Map<String, String> details = new HashMap<>();
        details.put(DBConstantsUtils.KeyUtils.FIRST_NAME, firstName);
        details.put(DBConstantsUtils.KeyUtils.LAST_NAME, lastName);

        PowerMockito.mockStatic(PatientRepositoryHelper.class);

        PowerMockito.when(PatientRepositoryHelper.getWomanProfileDetails(baseEntityId))
                .thenReturn(details);


        interactor.fetchWomanDetails(baseEntityId, callBack);

        Mockito.verify(callBack, Mockito.timeout(ASYNC_TIMEOUT)).onWomanDetailsFetched(detailsArgumentCaptor.capture());

        Assert.assertEquals(details, detailsArgumentCaptor.getValue());
    }

    @Test
    @PrepareForTest({PatientRepositoryHelper.class, AncLibrary.class})
    public void testFinalizeContactFormInvokesUpdatesPatientRepositoryWithCorrectParameters() throws Exception {

        String firstName = "First Name";
        String lastName = "Last Name";

        Map<String, String> details = new HashMap<>();
        details.put(DBConstantsUtils.KeyUtils.FIRST_NAME, firstName);
        details.put(DBConstantsUtils.KeyUtils.LAST_NAME, lastName);
        details.put(DBConstantsUtils.KeyUtils.EDD, "2018-10-19");
        details.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE, "2018-08-09");
        details.put(DBConstantsUtils.KeyUtils.BASE_ENTITY_ID, DUMMY_BASE_ENTITY_ID);
        details.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT, "1");

        final List<String> contactDates = new ArrayList<>();
        contactDates.add("10");
        contactDates.add("20");
        contactDates.add("30");
        contactDates.add("40");


        PowerMockito.mockStatic(AncLibrary.class);
        PowerMockito.mockStatic(PatientRepositoryHelper.class);


        PowerMockito.when(PatientRepositoryHelper.getWomanProfileDetails(DUMMY_BASE_ENTITY_ID)).thenReturn(details);
        PowerMockito.when(AncLibrary.getInstance()).thenReturn(AncLibrary);
        PowerMockito.when(AncLibrary.getAncRulesEngineHelper()).thenReturn(ancRulesEngineHelper);
        PowerMockito.when(AncLibrary.getDetailsRepository()).thenReturn(detailsRepository);
        PowerMockito.when(AncLibrary.getPreviousContactRepositoryHelper()).thenReturn(previousContactRepositoryHelper);

        Mockito.doNothing().when(detailsRepository).add(ArgumentMatchers.eq(details.get(DBConstantsUtils.KeyUtils.BASE_ENTITY_ID)), ArgumentMatchers.eq(ConstantsUtils.DetailsKeyUtils.CONTACT_SCHEDULE), ArgumentMatchers.anyString(), ArgumentMatchers.anyLong());

        List<Integer> integerList = Arrays.asList(new Integer[]{10, 20, 30, 40});

        PowerMockito.when(
                ancRulesEngineHelper.getContactVisitSchedule(ArgumentMatchers.any(ContactRule.class), ArgumentMatchers.eq(ConstantsUtils.RulesFileUtils.CONTACT_RULES))).thenReturn(integerList);

        PowerMockito.mockStatic(PatientRepositoryHelper.class);
        ContactInteractor contactInteractor = (ContactInteractor) interactor;
        contactInteractor.finalizeContactForm(details);

        PowerMockito.verifyStatic(PatientRepositoryHelper.class);

        PatientRepositoryHelper.updateContactVisitDetails(ArgumentMatchers.any(WomanDetail.class), ArgumentMatchers.anyBoolean());
        Assert.assertNotNull(contactInteractor);
    }

}
