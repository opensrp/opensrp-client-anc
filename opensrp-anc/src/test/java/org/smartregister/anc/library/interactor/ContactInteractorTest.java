package org.smartregister.anc.library.interactor;

import android.util.Pair;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.Context;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.contract.ContactContract;
import org.smartregister.anc.library.helper.AncRulesEngineHelper;
import org.smartregister.anc.library.helper.ECSyncHelper;
import org.smartregister.anc.library.model.PartialContact;
import org.smartregister.anc.library.repository.PartialContactRepository;
import org.smartregister.anc.library.repository.PatientRepository;
import org.smartregister.anc.library.repository.PreviousContactRepository;
import org.smartregister.anc.library.repository.RegisterQueryProvider;
import org.smartregister.anc.library.rule.ContactRule;
import org.smartregister.anc.library.util.AppExecutors;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.service.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

import timber.log.Timber;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PatientRepository.class, AncLibrary.class, PreviousContactRepository.class, PartialContactRepository.class, EventClientRepository.class, LocationHelper.class, Pair.class})
@PowerMockIgnore({"org.powermock.*", "org.mockito.*",})
public class ContactInteractorTest extends BaseUnitTest {

    private ContactContract.Interactor interactor;

    @Captor
    private ArgumentCaptor<Map<String, String>> detailsArgumentCaptor;

    @Mock
    private AncLibrary ancLibrary;

    @Mock
    private LocationHelper locationHelper;

    @Mock
    private AncRulesEngineHelper ancRulesEngineHelper;

    @Mock
    private DetailsRepository detailsRepository;

    @Mock
    private PreviousContactRepository previousContactRepository;

    @Mock
    private EventClientRepository eventClientRepository;

    @Mock
    private PartialContactRepository partialContactRepository;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private Context context;

    @Mock
    private ECSyncHelper ecSyncHelper;

    @Mock
    private UserService userService;

    private List<PartialContact> partialContactList = new ArrayList<>();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        interactor = new ContactInteractor(new AppExecutors(Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor()));
    }

    @Test
    public void testFetchWomanDetails() {
        String baseEntityId = UUID.randomUUID().toString();
        ContactContract.InteractorCallback callBack = Mockito.mock(ContactContract.InteractorCallback.class);

        String firstName = "First Name";
        String lastName = "Last Name";

        Map<String, String> details = new HashMap<>();
        details.put(DBConstantsUtils.KeyUtils.FIRST_NAME, firstName);
        details.put(DBConstantsUtils.KeyUtils.LAST_NAME, lastName);

        PowerMockito.mockStatic(AncLibrary.class);

        PowerMockito.when(AncLibrary.getInstance()).thenReturn(ancLibrary);

        PowerMockito.when(ancLibrary.getRegisterQueryProvider()).thenReturn(new RegisterQueryProvider());

        PowerMockito.mockStatic(PatientRepository.class);
        PowerMockito.when(PatientRepository.getWomanProfileDetails(baseEntityId)).thenReturn(details);

        interactor.fetchWomanDetails(baseEntityId, callBack);
        Mockito.verify(callBack, Mockito.timeout(ASYNC_TIMEOUT)).onWomanDetailsFetched(detailsArgumentCaptor.capture());
        Assert.assertEquals(details, detailsArgumentCaptor.getValue());
    }

    @Test
    public void testFinalizeContactFormInvokesUpdatesPatientRepositoryWithCorrectParameters() {
        String firstName = "First Name";
        String lastName = "Last Name";

        Map<String, String> details = new HashMap<>();
        details.put(DBConstantsUtils.KeyUtils.FIRST_NAME, firstName);
        details.put(DBConstantsUtils.KeyUtils.LAST_NAME, lastName);
        details.put(DBConstantsUtils.KeyUtils.EDD, "2018-10-19");
        details.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE, "2018-08-09");
        details.put(DBConstantsUtils.KeyUtils.BASE_ENTITY_ID, DUMMY_BASE_ENTITY_ID);
        details.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT, "1");

        PowerMockito.mockStatic(AncLibrary.class);

        PowerMockito.when(AncLibrary.getInstance()).thenReturn(ancLibrary);
        PowerMockito.when(ancLibrary.getRegisterQueryProvider()).thenReturn(new RegisterQueryProvider());
        PowerMockito.when(ancLibrary.getAncRulesEngineHelper()).thenReturn(ancRulesEngineHelper);
        PowerMockito.when(ancLibrary.getDetailsRepository()).thenReturn(detailsRepository);
        PowerMockito.when(ancLibrary.getPreviousContactRepository()).thenReturn(previousContactRepository);

        Mockito.doNothing().when(detailsRepository).add(ArgumentMatchers.eq(details.get(DBConstantsUtils.KeyUtils.BASE_ENTITY_ID)), ArgumentMatchers.eq(ConstantsUtils.DetailsKeyUtils.CONTACT_SCHEDULE), ArgumentMatchers.anyString(), ArgumentMatchers.anyLong());

        List<Integer> integerList = Arrays.asList(10, 20, 30, 40);

        PowerMockito.when(
                ancRulesEngineHelper.getContactVisitSchedule(ArgumentMatchers.any(ContactRule.class), ArgumentMatchers.eq(ConstantsUtils.RulesFileUtils.CONTACT_RULES))).thenReturn(integerList);

        ContactInteractor contactInteractor = (ContactInteractor) interactor;
        contactInteractor.finalizeContactForm(details, RuntimeEnvironment.application);

        Assert.assertNotNull(contactInteractor);
    }

    @Test
    public void testFinalizeContactFormInvokesUpdatesPatientRepositoryWithCorrectParametersWithReferralNotNull() {
        try {
            String firstName = "First Name";
            String lastName = "Last Name";

            Map<String, String> details = getStringStringMap(firstName, lastName);

            PartialContact partialContact = new PartialContact();
            partialContact.setFormJsonDraft("{\"validate_on_submit\":true,\"display_scroll_bars\":true,\"count\":\"1\",\"encounter_type\":\"Quick Check\",\"entity_id\":\"\",\"relational_id\":\"\",\"form_version\":\"0.0.1\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"encounter_location\":\"\",\"look_up\":{\"entity_id\":\"\",\"value\":\"\"}},\"step1\":{\"title\":\"Quick Check\",\"fields\":[{\"key\":\"contact_reason\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160288AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"native_radio\",\"label\":\"Reason for coming to facility\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"first_contact\",\"text\":\"First contact\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165269AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"scheduled_contact\",\"text\":\"Scheduled contact\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1246AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"specific_complaint\",\"text\":\"Specific complaint\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5219AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":\"true\",\"err\":\"Reason for coming to facility is required\"},\"value\":\"first_contact\"},{\"key\":\"specific_complaint\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5219AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"check_box\",\"label\":\"Specific complaint(s)\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"exclusive\":[\"dont_know\",\"none\"],\"value\":[\"abnormal_discharge\",\"altered_skin_color\",\"cough\"],\"options\":[{\"key\":\"abnormal_discharge\",\"text\":\"Abnormal vaginal discharge\",\"value\":true,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"123395AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"altered_skin_color\",\"text\":\"Jaundice\",\"value\":true,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"136443AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"changes_in_bp\",\"text\":\"Changes in blood pressure\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"155052AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"constipation\",\"text\":\"Constipation\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"996AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"contractions\",\"text\":\"Contractions\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163750AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"cough\",\"text\":\"Cough\",\"value\":true,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"143264AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"depression\",\"text\":\"Depression\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"119537AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"anxiety\",\"text\":\"Anxiety\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"121543AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"dizziness\",\"text\":\"Dizziness\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"156046AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"domestic_violence\",\"text\":\"Domestic violence\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"141814AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"extreme_pelvic_pain\",\"text\":\"Extreme pelvic pain - can't walk (symphysis pubis dysfunction)\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165270AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"fever\",\"text\":\"Fever\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"140238AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"full_abdominal_pain\",\"text\":\"Full abdominal pain\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"139547AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"flu_symptoms\",\"text\":\"Flu symptoms\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"137162AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"fluid_loss\",\"text\":\"Fluid loss\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"148968AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"headache\",\"text\":\"Headache\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"139084AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"heartburn\",\"text\":\"Heartburn\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"139059AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"leg_cramps\",\"text\":\"Leg cramps\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"135969AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"leg_pain\",\"text\":\"Leg pain\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"114395AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"leg_redness\",\"text\":\"Leg redness\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165215AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"low_back_pain\",\"text\":\"Low back pain\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"116225AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"pelvic_pain\",\"text\":\"Pelvic pain\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"131034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"nausea_vomiting_diarrhea\",\"text\":\"Nausea / vomiting / diarrhea\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"157892AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"no_fetal_movement\",\"text\":\"No fetal movement\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1452AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"oedema\",\"text\":\"Oedema\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"460AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"other_bleeding\",\"text\":\"Other bleeding\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"147241AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"other_pain\",\"text\":\"Other pain\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"114403AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"other_psychological_symptoms\",\"text\":\"Other psychological symptoms\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160198AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"other_skin_disorder\",\"text\":\"Other skin disorder\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"119022AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"other_types_of_violence\",\"text\":\"Other types of violence\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"158358AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"dysuria\",\"text\":\"Pain during urination (dysuria)\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"118771AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"pruritus\",\"text\":\"Pruritus\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"879AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"reduced_fetal_movement\",\"text\":\"Reduced or poor fetal movement\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"113377AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"shortness_of_breath\",\"text\":\"Shortness of breath\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"141600AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"tiredness\",\"text\":\"Tiredness\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"124628AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"trauma\",\"text\":\"Trauma\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"124193AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"bleeding\",\"text\":\"Vaginal bleeding\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"147232AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"visual_disturbance\",\"text\":\"Visual disturbance\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"123074AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"other_specify\",\"text\":\"Other (specify)\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":\"true\",\"err\":\"Specific complain is required\"},\"relevance\":{\"step1:contact_reason\":{\"type\":\"string\",\"ex\":\"equalTo(.,\\\"specific_complaint\\\")\"}}},{\"key\":\"specific_complaint_other\",\"openmrs_entity_parent\":\"5219AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160632AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"normal_edit_text\",\"edit_text_style\":\"bordered\",\"hint\":\"Specify\",\"v_regex\":{\"value\":\"[A-Za-z\\\\s\\\\.\\\\-]*\",\"err\":\"Please enter valid content\"},\"relevance\":{\"step1:specific_complaint\":{\"ex-checkbox\":[{\"or\":[\"other_specify\"]}]}}},{\"key\":\"danger_signs\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160939AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"check_box\",\"label\":\"Danger signs\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"exclusive\":[\"danger_none\"],\"value\":[\"danger_none\",\"danger_bleeding\"],\"options\":[{\"key\":\"danger_none\",\"text\":\"None\",\"value\":true,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"danger_bleeding\",\"text\":\"Bleeding vaginally\",\"value\":true,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"150802AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"central_cyanosis\",\"text\":\"Central cyanosis\",\"label_info_text\":\"Bluish discolouration around the mucous membranes in the mouth, lips and tongue\",\"label_info_title\":\"Central cyanosis\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165216AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"convulsing\",\"text\":\"Convulsing\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"164483AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"danger_fever\",\"text\":\"Fever\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"140238AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"severe_headache\",\"text\":\"Severe headache\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"139081AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"visual_disturbance\",\"text\":\"Visual disturbance\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"123074AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"imminent_delivery\",\"text\":\"Imminent delivery\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"162818AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"labour\",\"text\":\"Labour\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"145AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"looks_very_ill\",\"text\":\"Looks very ill\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163293AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"severe_vomiting\",\"text\":\"Severe vomiting\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"118477AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"severe_pain\",\"text\":\"Severe pain\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163477AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"severe_abdominal_pain\",\"text\":\"Severe abdominal pain\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165271AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"unconscious\",\"text\":\"Unconscious\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"123818AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":\"true\",\"err\":\"Danger signs is required\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"quick_check_relevance_rules.yml\"}}}}]}}");
            partialContact.setFinalized(false);
            partialContact.setSortOrder(1);
            partialContact.setType("anc_quick_check");
            partialContactList.add(partialContact);


            PowerMockito.mockStatic(AncLibrary.class);
            PowerMockito.mockStatic(PreviousContactRepository.class);
            PowerMockito.mockStatic(PartialContactRepository.class);
            PowerMockito.mockStatic(EventClientRepository.class);
            PowerMockito.mockStatic(LocationHelper.class);
            PowerMockito.mockStatic(Pair.class);

            PowerMockito.when(AncLibrary.getInstance()).thenReturn(ancLibrary);
            PowerMockito.when(ancLibrary.getRegisterQueryProvider()).thenReturn(new RegisterQueryProvider());
            PowerMockito.when(ancLibrary.getDetailsRepository()).thenReturn(detailsRepository);
            PowerMockito.when(ancLibrary.getPreviousContactRepository()).thenReturn(previousContactRepository);
            PowerMockito.when(ancLibrary.getEventClientRepository()).thenReturn(eventClientRepository);
            PowerMockito.when(ancLibrary.getPartialContactRepository()).thenReturn(partialContactRepository);
            PowerMockito.when(partialContactRepository.getPartialContacts(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(partialContactList);
            PowerMockito.when(ancLibrary.getContext()).thenReturn(context);
            PowerMockito.when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
            PowerMockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn("demo");
            PowerMockito.when(LocationHelper.getInstance()).thenReturn(locationHelper);
            PowerMockito.when(locationHelper.getChildLocationId()).thenReturn("7465-fdhlkfjdk-45kjsd8465-4567");
            PowerMockito.when(allSharedPreferences.fetchDefaultLocalityId("demo")).thenReturn("732632-32468736-jsdfg-34");
            PowerMockito.when(allSharedPreferences.fetchDefaultTeam("demo")).thenReturn("Bukesa Demo");
            PowerMockito.when(allSharedPreferences.fetchDefaultTeamId("demo")).thenReturn("demo-3872shf-43658-7hds-jkf63-54");
            PowerMockito.when(ancLibrary.getEcSyncHelper()).thenReturn(ecSyncHelper);
            PowerMockito.when(context.userService()).thenReturn(userService);
            PowerMockito.when(userService.getAllSharedPreferences()).thenReturn(allSharedPreferences);
            PowerMockito.when(eventClientRepository.getClientByBaseEntityId(DUMMY_BASE_ENTITY_ID)).thenReturn(new JSONObject("{\"attributes\":{\"age\":\"19\",\"contact_status\":\"today\",\"edd\":\"0\",\"last_contact_record_date\":\"2019-10-24\",\"next_contact\":\"2\",\"next_contact_date\":\"-0001-05-22\",\"red_flag_count\":\"15\",\"yellow_flag_count\":\"10\"}}"));

            ContactInteractor contactInteractor = (ContactInteractor) interactor;
            HashMap<String, String> contactDetails = contactInteractor.finalizeContactForm(details, RuntimeEnvironment.application);

            Assert.assertNotNull(contactInteractor);
            Assert.assertNotNull(contactDetails);
            Assert.assertEquals("First Name", contactDetails.get(DBConstantsUtils.KeyUtils.FIRST_NAME));
        } catch (JSONException e) {
            Timber.e(e, this.getClass().getCanonicalName() + " --> testFinalizeContactFormInvokesUpdatesPatientRepositoryWithCorrectParametersWithReferralNotNull()");
        }
    }

    @NotNull
    private Map<String, String> getStringStringMap(String firstName, String lastName) {
        Map<String, String> details = new HashMap<>();
        details.put(DBConstantsUtils.KeyUtils.FIRST_NAME, firstName);
        details.put(DBConstantsUtils.KeyUtils.LAST_NAME, lastName);
        details.put(DBConstantsUtils.KeyUtils.EDD, "2018-10-19");
        details.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE, "2018-08-09");
        details.put(DBConstantsUtils.KeyUtils.BASE_ENTITY_ID, DUMMY_BASE_ENTITY_ID);
        details.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT, "1");
        details.put(ConstantsUtils.REFERRAL, "-1");
        return details;
    }
}
