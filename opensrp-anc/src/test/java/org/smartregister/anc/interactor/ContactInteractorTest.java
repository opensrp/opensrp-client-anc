package org.smartregister.anc.interactor;

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
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.ContactContract;
import org.smartregister.anc.helper.RulesEngineHelper;
import org.smartregister.anc.repository.PatientRepository;
import org.smartregister.anc.rule.ContactRule;
import org.smartregister.anc.util.AppExecutors;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.repository.DetailsRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

@RunWith(PowerMockRunner.class)
public class ContactInteractorTest extends BaseUnitTest {

    private ContactContract.Interactor interactor;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Captor
    private ArgumentCaptor<Map<String, String>> detailsArgumentCaptor;

    @Mock
    private AncApplication ancApplication;

    @Mock
    private RulesEngineHelper rulesEngineHelper;

    @Mock
    private DetailsRepository detailsRepository;

    @Before
    public void setUp() {
        interactor = new ContactInteractor(new AppExecutors(Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor()));
    }

    @PrepareForTest({PatientRepository.class})
    @Test
    public void testFetchWomanDetails() {

        String baseEntityId = UUID.randomUUID().toString();
        ContactContract.InteractorCallback callBack = Mockito.mock(ContactContract.InteractorCallback.class);

        String firstName = "First Name";
        String lastName = "Last Name";

        Map<String, String> details = new HashMap<>();
        details.put(DBConstants.KEY.FIRST_NAME, firstName);
        details.put(DBConstants.KEY.LAST_NAME, lastName);

        PowerMockito.mockStatic(PatientRepository.class);

        PowerMockito.when(PatientRepository.getWomanProfileDetails(baseEntityId))
                .thenReturn(details);


        interactor.fetchWomanDetails(baseEntityId, callBack);

        Mockito.verify(callBack, Mockito.timeout(ASYNC_TIMEOUT)).onWomanDetailsFetched(detailsArgumentCaptor.capture());

        Assert.assertEquals(details, detailsArgumentCaptor.getValue());
    }

    @Test
    @PrepareForTest({PatientRepository.class, AncApplication.class})
    public void testFinalizeContactFormInvokesUpdatesPatientRepositoryWithCorrectParameters() throws Exception {

        String firstName = "First Name";
        String lastName = "Last Name";

        Map<String, String> details = new HashMap<>();
        details.put(DBConstants.KEY.FIRST_NAME, firstName);
        details.put(DBConstants.KEY.LAST_NAME, lastName);
        details.put(DBConstants.KEY.EDD, "2018-10-19");
        details.put(DBConstants.KEY.NEXT_CONTACT_DATE, "2018-08-09");
        details.put(DBConstants.KEY.BASE_ENTITY_ID, DUMMY_BASE_ENTITY_ID);

        final List<String> contactDates = new ArrayList<>();
        contactDates.add("10");
        contactDates.add("20");
        contactDates.add("30");
        contactDates.add("40");


        PowerMockito.mockStatic(AncApplication.class);
        PowerMockito.mockStatic(PatientRepository.class);


        PowerMockito.when(PatientRepository.getWomanProfileDetails(DUMMY_BASE_ENTITY_ID)).thenReturn(details);
        PowerMockito.when(AncApplication.getInstance()).thenReturn(ancApplication);
        PowerMockito.when(ancApplication.getRulesEngineHelper()).thenReturn(rulesEngineHelper);
        PowerMockito.when(ancApplication.getDetailsRepository()).thenReturn(detailsRepository);

        Mockito.doNothing().when(detailsRepository).add(ArgumentMatchers.eq(details.get(DBConstants.KEY.BASE_ENTITY_ID)), ArgumentMatchers.eq(Constants.DETAILS_KEY.CONTACT_SHEDULE), ArgumentMatchers.anyString(), ArgumentMatchers.anyLong());

        List<Integer> integerList = Arrays.asList(new Integer[]{10, 20, 30, 40,});

        PowerMockito.when(rulesEngineHelper.getContactVisitSchedule(ArgumentMatchers.any(ContactRule.class), ArgumentMatchers.eq("contact-rules.yml"))).thenReturn(integerList);

        PowerMockito.mockStatic(PatientRepository.class);

        PowerMockito.doNothing().when(PatientRepository.class, "updateContactVisitDetails", ArgumentMatchers.eq(details.get(DBConstants.KEY.BASE_ENTITY_ID)), ArgumentMatchers.anyInt(), ArgumentMatchers.anyString());

        interactor.finalizeContactForm(details);

        PowerMockito.verifyStatic(PatientRepository.class);
        PatientRepository.updateContactVisitDetails(ArgumentMatchers.eq(details.get(DBConstants.KEY.BASE_ENTITY_ID)), ArgumentMatchers.anyInt(), ArgumentMatchers.anyString());

        Assert.assertNotNull(interactor);
    }

}
