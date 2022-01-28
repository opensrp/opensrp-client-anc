package org.smartregister.anc.library.interactor;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.timeout;

import androidx.core.util.Pair;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.contract.RegisterContract;
import org.smartregister.anc.library.helper.ECSyncHelper;
import org.smartregister.anc.library.repository.RegisterQueryProvider;
import org.smartregister.anc.library.sync.BaseAncClientProcessorForJava;
import org.smartregister.anc.library.util.ANCJsonFormUtils;
import org.smartregister.anc.library.util.AppExecutors;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.UniqueId;
import org.smartregister.domain.db.EventClient;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class RegisterInteractorTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    private RegisterContract.Interactor interactor;
    @Captor
    private ArgumentCaptor<Triple<String, String, String>> tripleArgumentCaptor;

    @Captor
    private ArgumentCaptor<JSONObject> jsonArgumentCaptor;

    @Captor
    private ArgumentCaptor<List<EventClient>> eventClientArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Captor
    private ArgumentCaptor<Long> longArgumentCaptor;

    @Mock
    private AncLibrary ancLibrary;
    @Mock
    private DrishtiApplication drishtiApplication;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        interactor = new RegisterInteractor(new AppExecutors(Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor()));
    }

    @Test
    public void testGetNextUniqueId() {
        UniqueIdRepository uniqueIdRepository = Mockito.mock(UniqueIdRepository.class);
        RegisterContract.InteractorCallBack callBack = Mockito.mock(RegisterContract.InteractorCallBack.class);

        RegisterInteractor registerInteractor = (RegisterInteractor) interactor;
        registerInteractor.setUniqueIdRepository(uniqueIdRepository);

        String formName = "anc_registration";
        String metadata = "metadata";
        String currentLocationId = "Location Id";
        String openmrsId = "openmrs id";

        UniqueId uniqueId = new UniqueId();
        uniqueId.setOpenmrsId(openmrsId);

        Mockito.when(uniqueIdRepository.getNextUniqueId()).thenReturn(uniqueId);
        registerInteractor.getNextUniqueId(Triple.of(formName, metadata, currentLocationId), callBack);
        Mockito.verify(uniqueIdRepository, timeout(ASYNC_TIMEOUT)).getNextUniqueId();

        Mockito.verify(callBack, timeout(ASYNC_TIMEOUT)).onUniqueIdFetched(tripleArgumentCaptor.capture(), stringArgumentCaptor.capture());

        Triple<String, String, String> triple = tripleArgumentCaptor.getValue();
        assertEquals(formName, triple.getLeft());
        assertEquals(metadata, triple.getMiddle());
        assertEquals(currentLocationId, triple.getRight());
        assertEquals(openmrsId, stringArgumentCaptor.getValue());

    }

    @Test
    public void testFailedToGetNextUniqueId() {
        UniqueIdRepository uniqueIdRepository = Mockito.mock(UniqueIdRepository.class);
        RegisterContract.InteractorCallBack callBack = Mockito.mock(RegisterContract.InteractorCallBack.class);

        RegisterInteractor registerInteractor = (RegisterInteractor) interactor;
        registerInteractor.setUniqueIdRepository(uniqueIdRepository);

        String formName = "anc_registration";
        String metadata = "metadata";
        String currentLocationId = "Location Id";

        Triple<String, String, String> triple = Triple.of(formName, metadata, currentLocationId);

        Mockito.when(uniqueIdRepository.getNextUniqueId()).thenReturn(null);
        registerInteractor.getNextUniqueId(triple, callBack);
        Mockito.verify(uniqueIdRepository, timeout(ASYNC_TIMEOUT)).getNextUniqueId();

        Mockito.verify(callBack, timeout(ASYNC_TIMEOUT)).onNoUniqueId();
    }


    @Test
    public void testSaveNewRegistration() throws Exception {
        UniqueIdRepository uniqueIdRepository = Mockito.mock(UniqueIdRepository.class);
        Repository repositoryMock = Mockito.mock(Repository.class);
        SQLiteDatabase sqLiteDatabaseMock = Mockito.mock(SQLiteDatabase.class);
        Mockito.doReturn(sqLiteDatabaseMock).when(repositoryMock).getWritableDatabase();
        ECSyncHelper syncHelper = Mockito.mock(ECSyncHelper.class);
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        BaseAncClientProcessorForJava baseAncClientProcessorForJava = Mockito.mock(BaseAncClientProcessorForJava.class);
        RegisterContract.InteractorCallBack callBack = Mockito.mock(RegisterContract.InteractorCallBack.class);
        RegisterInteractor registerInteractor = (RegisterInteractor) interactor;
        registerInteractor.setUniqueIdRepository(uniqueIdRepository);
        registerInteractor.setSyncHelper(syncHelper);
        registerInteractor.setAllSharedPreferences(allSharedPreferences);
        registerInteractor.setClientProcessorForJava(baseAncClientProcessorForJava);
        String baseEntityId = "112123";
        String ancId = "1324354";
        String formSubmissionId = "132vb-sdsd-we";
        Client client = new Client(baseEntityId);
        Map<String, String> identifiers = new HashMap<>();
        identifiers.put(ConstantsUtils.JsonFormKeyUtils.ANC_ID, ancId);
        client.setIdentifiers(identifiers);
        Event event = new Event();
        event.setBaseEntityId(baseEntityId);
        event.setFormSubmissionId(formSubmissionId);
        Pair<Client, Event> pair = Pair.create(client, event);
        JSONObject clientObject = new JSONObject(ANCJsonFormUtils.gson.toJson(client));
        JSONObject eventObject = new JSONObject(ANCJsonFormUtils.gson.toJson(event));
        String jsonString = "{\"count\":\"1\",\"encounter_type\":\"ANCRegistration\",\"entity_id\":\"\",\"relational_id\":\"\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"encounter_location\":\"\",\"look_up\":{\"entity_id\":\"\",\"value\":\"\"}},\"step1\":{\"title\":\"{{anc_register.step1.title}}\",\"fields\":[{\"key\":\"wom_image\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"choose_image\",\"uploadButtonText\":\"Takeapictureofthewoman\"},{\"key\":\"anc_id\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_identifier\",\"openmrs_entity_id\":\"ANC_ID\",\"type\":\"barcode\",\"barcode_type\":\"qrcode\",\"hint\":\"{{anc_register.step1.anc_id.hint}}\",\"value\":\"0\",\"scanButtonText\":\"ScanQRCode\",\"v_numeric\":{\"value\":\"true\",\"err\":\"{{anc_register.step1.anc_id.v_numeric.err}}\"},\"v_required\":{\"value\":\"true\",\"err\":\"{{anc_register.step1.anc_id.v_required.err}}\"}},{\"key\":\"first_name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"first_name\",\"type\":\"edit_text\",\"hint\":\"{{anc_register.step1.first_name.hint}}\",\"edit_type\":\"name\",\"v_required\":{\"value\":\"true\",\"err\":\"{{anc_register.step1.first_name.v_required.err}}\"},\"v_regex\":{\"value\":\"[A-Za-z\\\\s\\\\.\\\\-]*\",\"err\":\"{{anc_register.step1.first_name.v_regex.err}}\"}},{\"key\":\"last_name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"last_name\",\"type\":\"edit_text\",\"hint\":\"{{anc_register.step1.last_name.hint}}\",\"edit_type\":\"name\",\"v_required\":{\"value\":\"true\",\"err\":\"{{anc_register.step1.last_name.v_required.err}}\"},\"v_regex\":{\"value\":\"[A-Za-z\\\\s\\\\.\\\\-]*\",\"err\":\"{{anc_register.step1.last_name.v_regex.err}}\"}},{\"key\":\"gender\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"gender\",\"type\":\"hidden\",\"value\":\"F\"},{\"key\":\"dob\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdate\",\"type\":\"hidden\",\"value\":\"\",\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"registration_calculation_rules.yml\"}}}},{\"key\":\"dob_calculated\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"hidden\",\"value\":\"\",\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"registration_calculation_rules.yml\"}}}},{\"key\":\"dob_entered\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"date_picker\",\"hint\":\"{{anc_register.step1.dob_entered.hint}}\",\"expanded\":false,\"duration\":{\"label\":\"{{anc_register.step1.dob_entered.duration.label}}\"},\"min_date\":\"today-49y\",\"max_date\":\"today-10y\",\"v_required\":{\"value\":\"true\",\"err\":\"{{anc_register.step1.dob_entered.v_required.err}}\"},\"relevance\":{\"step1:dob_unknown\":{\"ex-checkbox\":[{\"not\":[\"dob_unknown\"]}]}}},{\"key\":\"dob_unknown\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdate_estimated\",\"type\":\"check_box\",\"options\":[{\"key\":\"dob_unknown\",\"text\":\"{{anc_register.step1.dob_unknown.options.dob_unknown.text}}\",\"text_size\":\"18px\",\"value\":\"false\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}]},{\"key\":\"age\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"age\",\"type\":\"hidden\",\"value\":\"\",\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"registration_calculation_rules.yml\"}}}},{\"key\":\"age_calculated\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"hidden\",\"value\":\"\",\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"registration_calculation_rules.yml\"}}}},{\"key\":\"age_entered\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"age\",\"type\":\"edit_text\",\"hint\":\"{{anc_register.step1.age_entered.hint}}\",\"v_numeric\":{\"value\":\"true\",\"err\":\"{{anc_register.step1.age_entered.v_numeric.err}}\"},\"v_min\":{\"value\":\"10\",\"err\":\"Agemustbeequaltoorgreaterthan10\"},\"v_max\":{\"value\":\"49\",\"err\":\"Agemustbeequalorlessthan49\"},\"relevance\":{\"step1:dob_unknown\":{\"ex-checkbox\":[{\"and\":[\"dob_unknown\"]}]}},\"v_required\":{\"value\":\"true\",\"err\":\"{{anc_register.step1.age_entered.v_required.err}}\"}},{\"key\":\"home_address\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"address2\",\"type\":\"edit_text\",\"hint\":\"{{anc_register.step1.home_address.hint}}\",\"edit_type\":\"name\",\"v_required\":{\"value\":\"true\",\"err\":\"{{anc_register.step1.home_address.v_required.err}}\"}},{\"key\":\"phone_number\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"edit_text\",\"hint\":\"{{anc_register.step1.phone_number.hint}}\",\"v_numeric\":{\"value\":\"true\",\"err\":\"{{anc_register.step1.phone_number.v_numeric.err}}\"},\"v_required\":{\"value\":\"true\",\"err\":\"{{anc_register.step1.phone_number.v_required.err}}\"}},{\"key\":\"reminders\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163164AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"native_radio\",\"label\":\"{{anc_register.step1.reminders.label}}\",\"label_info_text\":\"{{anc_register.step1.reminders.label_info_text}}\",\"label_text_style\":\"normal\",\"text_color\":\"#000000\",\"options\":[{\"key\":\"yes\",\"text\":\"{{anc_register.step1.reminders.options.yes.text}}\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"no\",\"text\":\"{{anc_register.step1.reminders.options.no.text}}\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true,\"err\":\"{{anc_register.step1.reminders.v_required.err}}\"}},{\"key\":\"alt_name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163258AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"edit_text\",\"hint\":\"{{anc_register.step1.alt_name.hint}}\",\"edit_type\":\"name\",\"look_up\":\"true\",\"entity_id\":\"\",\"v_regex\":{\"value\":\"[A-Za-z\\\\s\\\\.\\\\-]*\",\"err\":\"{{anc_register.step1.alt_name.v_regex.err}}\"}},{\"key\":\"alt_phone_number\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"alt_phone_number\",\"type\":\"edit_text\",\"hint\":\"{{anc_register.step1.alt_phone_number.hint}}\",\"v_numeric\":{\"value\":\"true\",\"err\":\"{{anc_register.step1.alt_phone_number.v_numeric.err}}\"}},{\"key\":\"cohabitants\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"cohabitants\",\"label\":\"{{anc_register.step1.cohabitants.label}}\",\"label_info_text\":\"{{anc_register.step1.cohabitants.label_info_text}}\",\"type\":\"check_box\",\"exclusive\":[\"no_one\"],\"options\":[{\"key\":\"parents\",\"text\":\"{{anc_register.step1.cohabitants.options.parents.text}}\",\"text_size\":\"18px\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"siblings\",\"text\":\"{{anc_register.step1.cohabitants.options.siblings.text}}\",\"text_size\":\"18px\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"extended_family\",\"text\":\"{{anc_register.step1.cohabitants.options.extended_family.text}}\",\"text_size\":\"18px\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"partner\",\"text\":\"{{anc_register.step1.cohabitants.options.partner.text}}\",\"text_size\":\"18px\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"friends\",\"text\":\"{{anc_register.step1.cohabitants.options.friends.text}}\",\"text_size\":\"18px\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"no_one\",\"text\":\"{{anc_register.step1.cohabitants.options.no_one.text}}\",\"text_size\":\"18px\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}]},{\"key\":\"next_contact\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"next_contact\",\"type\":\"hidden\",\"value\":\"\"},{\"key\":\"edd\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"edd\",\"type\":\"hidden\",\"value\":\"\"},{\"key\":\"next_contact_date\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"next_contact_date\",\"type\":\"hidden\",\"value\":\"\"},{\"key\":\"contact_status\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"contact_status\",\"type\":\"hidden\",\"value\":\"\"},{\"key\":\"previous_contact_status\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"contact_status\",\"type\":\"hidden\",\"value\":\"\"},{\"key\":\"red_flag_count\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"red_flag_count\",\"type\":\"hidden\",\"value\":\"\"},{\"key\":\"yellow_flag_count\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"yellow_flag_count\",\"type\":\"hidden\",\"value\":\"\"},{\"key\":\"last_contact_record_date\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"last_contact_record_date\",\"type\":\"hidden\",\"value\":\"\"}]},\"properties_file_name\":\"anc_register\"}";
        long timestamp = new Date().getTime();
        List<EventClient> eventClients = new ArrayList<>();
        EventClient eventClient = new EventClient(ANCJsonFormUtils.gson.fromJson(eventObject.toString(), org.smartregister.domain.Event.class),
                ANCJsonFormUtils.gson.fromJson(clientObject.toString(), org.smartregister.domain.Client.class));
        eventClients.add(eventClient);
        Mockito.doReturn(timestamp).when(allSharedPreferences).fetchLastUpdatedAtDate(0);
        Mockito.doReturn(eventClients).when(syncHelper).getEvents(Arrays.asList(formSubmissionId));
        RegisterQueryProvider registerQueryProvider = new RegisterQueryProvider();
        Mockito.doReturn(registerQueryProvider).when(ancLibrary).getRegisterQueryProvider();
        ReflectionHelpers.setStaticField(AncLibrary.class, "instance", ancLibrary);
        Mockito.doReturn(repositoryMock).when(drishtiApplication).getRepository();
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);
        registerInteractor.saveRegistration(pair, jsonString, false, callBack);
        Mockito.verify(syncHelper, timeout(ASYNC_TIMEOUT)).addClient(stringArgumentCaptor.capture(), jsonArgumentCaptor.capture());
        assertEquals(baseEntityId, stringArgumentCaptor.getValue());
        assertEquals(clientObject.get("type"), jsonArgumentCaptor.getValue().get("type"));
        assertEquals(clientObject.get("baseEntityId"), jsonArgumentCaptor.getValue().get("baseEntityId"));
        assertEquals(clientObject.getJSONObject("identifiers").get("anc_id"), jsonArgumentCaptor.getValue().getJSONObject("identifiers").get("anc_id"));
        Mockito.verify(syncHelper, timeout(ASYNC_TIMEOUT)).addEvent(stringArgumentCaptor.capture(), jsonArgumentCaptor.capture());
        assertEquals(baseEntityId, stringArgumentCaptor.getValue());
        assertEquals(eventObject.get("type"), jsonArgumentCaptor.getValue().get("type"));
        assertEquals(eventObject.getString("baseEntityId"), jsonArgumentCaptor.getValue().getString("baseEntityId"));
        assertEquals(eventObject.getString("duration"), jsonArgumentCaptor.getValue().getString("duration"));
        assertEquals(eventObject.getString("version"), jsonArgumentCaptor.getValue().getString("version"));

        Mockito.verify(uniqueIdRepository, timeout(ASYNC_TIMEOUT)).close(stringArgumentCaptor.capture());
        assertEquals(ancId, stringArgumentCaptor.getValue());

        Mockito.verify(baseAncClientProcessorForJava, timeout(ASYNC_TIMEOUT)).processClient(eventClientArgumentCaptor.capture());
        assertEquals(eventClients, eventClientArgumentCaptor.getValue());

        Mockito.verify(allSharedPreferences, timeout(ASYNC_TIMEOUT)).saveLastUpdatedAtDate(longArgumentCaptor.capture());
        assertEquals(new Long(timestamp), longArgumentCaptor.getValue());
        Mockito.verify(callBack, timeout(ASYNC_TIMEOUT)).onRegistrationSaved(ArgumentMatchers.anyBoolean());

    }

    @Test
    public void testSaveEditRegistration() throws Exception {
        UniqueIdRepository uniqueIdRepository = Mockito.mock(UniqueIdRepository.class);
        ECSyncHelper syncHelper = Mockito.mock(ECSyncHelper.class);
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        BaseAncClientProcessorForJava baseAncClientProcessorForJava = Mockito.mock(BaseAncClientProcessorForJava.class);
        RegisterContract.InteractorCallBack callBack = Mockito.mock(RegisterContract.InteractorCallBack.class);
        RegisterInteractor registerInteractor = (RegisterInteractor) interactor;
        registerInteractor.setUniqueIdRepository(uniqueIdRepository);
        registerInteractor.setSyncHelper(syncHelper);
        registerInteractor.setAllSharedPreferences(allSharedPreferences);
        registerInteractor.setClientProcessorForJava(baseAncClientProcessorForJava);
        String baseEntityId = "112123";
        String ancId = "1324354";
        String originalAncId = "456456456456";
        Client client = new Client(baseEntityId);
        Map<String, String> identifiers = new HashMap<>();
        identifiers.put("anc_id", ancId);
        client.setIdentifiers(identifiers);
        Event event = new Event();
        event.setBaseEntityId(baseEntityId);
        Pair<Client, Event> pair = Pair.create(client, event);
        JSONObject clientObject = new JSONObject(ANCJsonFormUtils.gson.toJson(client));
        JSONObject eventObject = new JSONObject(ANCJsonFormUtils.gson.toJson(event));
        String jsonString = "{\"" + DBConstantsUtils.KeyUtils.ANC_ID + "\":\"" + originalAncId + "\"}";
        long timestamp = new Date().getTime();
        List<EventClient> eventClients = new ArrayList<>();
        EventClient eventClient = new EventClient(ANCJsonFormUtils.gson.fromJson(eventObject.toString(), org.smartregister.domain.Event.class),
                ANCJsonFormUtils.gson.fromJson(clientObject.toString(), org.smartregister.domain.Client.class));
        eventClients.add(eventClient);
        JSONObject orginalClientObject = clientObject;
        orginalClientObject.put("original", "yes");
        Mockito.doReturn(orginalClientObject).when(syncHelper).getClient(Mockito.anyString());
        Mockito.doReturn(timestamp).when(allSharedPreferences).fetchLastUpdatedAtDate(0);
        Mockito.doReturn(eventClients).when(syncHelper).getEvents(new Date(timestamp), BaseRepository.TYPE_Unsynced);
        Mockito.doReturn(syncHelper).when(ancLibrary).getEcSyncHelper();
        ReflectionHelpers.setStaticField(AncLibrary.class, "instance", ancLibrary);
        registerInteractor.saveRegistration(pair, jsonString, true, callBack);
        Mockito.verify(syncHelper, timeout(ASYNC_TIMEOUT)).getClient(stringArgumentCaptor.capture());
        assertEquals(baseEntityId, stringArgumentCaptor.getValue());
        Mockito.verify(syncHelper, timeout(ASYNC_TIMEOUT)).addClient(stringArgumentCaptor.capture(), jsonArgumentCaptor.capture());
        assertEquals(baseEntityId, stringArgumentCaptor.getValue());
        assertEquals(orginalClientObject.get("type"), jsonArgumentCaptor.getValue().get("type"));
        assertEquals(orginalClientObject.get("baseEntityId"), jsonArgumentCaptor.getValue().get("baseEntityId"));
        assertEquals(orginalClientObject.getJSONObject("identifiers").get("anc_id"), jsonArgumentCaptor.getValue().getJSONObject("identifiers").get("anc_id"));
        assertEquals(orginalClientObject.get("original"), jsonArgumentCaptor.getValue().get("original"));
        Mockito.verify(syncHelper, timeout(ASYNC_TIMEOUT)).addEvent(stringArgumentCaptor.capture(), jsonArgumentCaptor.capture());
        assertEquals(baseEntityId, stringArgumentCaptor.getValue());
        assertEquals(eventObject.get("type"), jsonArgumentCaptor.getValue().get("type"));
        assertEquals(eventObject.getString("baseEntityId"), jsonArgumentCaptor.getValue().getString("baseEntityId"));
        assertEquals(eventObject.getString("duration"), jsonArgumentCaptor.getValue().getString("duration"));
        assertEquals(eventObject.getString("version"), jsonArgumentCaptor.getValue().getString("version"));
        Mockito.doReturn(uniqueIdRepository).when(ancLibrary).getUniqueIdRepository();
        assertEquals(originalAncId, "456456456456");
    }

}