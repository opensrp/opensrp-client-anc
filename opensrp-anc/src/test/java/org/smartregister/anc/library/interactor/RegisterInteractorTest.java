package org.smartregister.anc.library.interactor;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import androidx.core.util.Pair;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
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
import org.smartregister.repository.UniqueIdRepository;

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
        verify(uniqueIdRepository, timeout(ASYNC_TIMEOUT)).getNextUniqueId();

        verify(callBack, timeout(ASYNC_TIMEOUT)).onUniqueIdFetched(tripleArgumentCaptor.capture(), stringArgumentCaptor.capture());

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
        verify(uniqueIdRepository, timeout(ASYNC_TIMEOUT)).getNextUniqueId();

        verify(callBack, timeout(ASYNC_TIMEOUT)).onNoUniqueId();
    }


    @Test
    public void testSaveNewRegistration() throws Exception {
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

        String jsonString = "{'json':'string'}";

        long timestamp = new Date().getTime();

        List<EventClient> eventClients = new ArrayList<>();
        EventClient eventClient = new EventClient(ANCJsonFormUtils.gson.fromJson(eventObject.toString(), org.smartregister.domain.Event.class),
                ANCJsonFormUtils.gson.fromJson(clientObject.toString(), org.smartregister.domain.Client.class));
        eventClients.add(eventClient);

        Mockito.doReturn(timestamp).when(allSharedPreferences).fetchLastUpdatedAtDate(0);
        Mockito.doReturn(eventClients).when(syncHelper).getEvents(Arrays.asList(formSubmissionId));

        ReflectionHelpers.setStaticField(AncLibrary.class, "instance", ancLibrary);

        registerInteractor.saveRegistration(pair, jsonString, false, callBack);

        verify(syncHelper, timeout(ASYNC_TIMEOUT)).addClient(stringArgumentCaptor.capture(), jsonArgumentCaptor.capture());

        assertEquals(baseEntityId, stringArgumentCaptor.getValue());

        assertEquals(clientObject.get("type"), jsonArgumentCaptor.getValue().get("type"));
        assertEquals(clientObject.get("baseEntityId"), jsonArgumentCaptor.getValue().get("baseEntityId"));
        assertEquals(clientObject.getJSONObject("identifiers").get("anc_id"), jsonArgumentCaptor.getValue().getJSONObject("identifiers").get("anc_id"));

        verify(syncHelper, timeout(ASYNC_TIMEOUT)).addEvent(stringArgumentCaptor.capture(), jsonArgumentCaptor.capture());
        assertEquals(baseEntityId, stringArgumentCaptor.getValue());

        assertEquals(eventObject.get("type"), jsonArgumentCaptor.getValue().get("type"));
        assertEquals(eventObject.getString("baseEntityId"), jsonArgumentCaptor.getValue().getString("baseEntityId"));
        assertEquals(eventObject.getString("duration"), jsonArgumentCaptor.getValue().getString("duration"));
        assertEquals(eventObject.getString("version"), jsonArgumentCaptor.getValue().getString("version"));

        verify(uniqueIdRepository, timeout(ASYNC_TIMEOUT)).close(stringArgumentCaptor.capture());
        assertEquals(ancId, stringArgumentCaptor.getValue());

        verify(baseAncClientProcessorForJava, timeout(ASYNC_TIMEOUT)).processClient(eventClientArgumentCaptor.capture());
        assertEquals(eventClients, eventClientArgumentCaptor.getValue());

        verify(allSharedPreferences, timeout(ASYNC_TIMEOUT)).saveLastUpdatedAtDate(longArgumentCaptor.capture());
        assertEquals(new Long(timestamp), longArgumentCaptor.getValue());

        verify(callBack, timeout(ASYNC_TIMEOUT)).onRegistrationSaved(ArgumentMatchers.anyBoolean());
    }

    @Test
    @Ignore
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
        identifiers.put(DBConstantsUtils.KeyUtils.ANC_ID, ancId);
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

        registerInteractor.saveRegistration(pair, jsonString, true, callBack);

        verify(syncHelper, timeout(ASYNC_TIMEOUT)).getClient(stringArgumentCaptor.capture());
        assertEquals(baseEntityId, stringArgumentCaptor.getValue());

        verify(syncHelper, timeout(ASYNC_TIMEOUT)).addClient(stringArgumentCaptor.capture(), jsonArgumentCaptor.capture());

        assertEquals(baseEntityId, stringArgumentCaptor.getValue());

        assertEquals(orginalClientObject.get("type"), jsonArgumentCaptor.getValue().get("type"));
        assertEquals(orginalClientObject.get("baseEntityId"), jsonArgumentCaptor.getValue().get("baseEntityId"));
        assertEquals(orginalClientObject.getJSONObject("identifiers").get("anc_id"), jsonArgumentCaptor.getValue().getJSONObject("identifiers").get("anc_id"));
        assertEquals(orginalClientObject.get("original"), jsonArgumentCaptor.getValue().get("original"));

        verify(syncHelper, timeout(ASYNC_TIMEOUT)).addEvent(stringArgumentCaptor.capture(), jsonArgumentCaptor.capture());
        assertEquals(baseEntityId, stringArgumentCaptor.getValue());

        assertEquals(eventObject.get("type"), jsonArgumentCaptor.getValue().get("type"));
        assertEquals(eventObject.getString("baseEntityId"), jsonArgumentCaptor.getValue().getString("baseEntityId"));
        assertEquals(eventObject.getString("duration"), jsonArgumentCaptor.getValue().getString("duration"));
        assertEquals(eventObject.getString("version"), jsonArgumentCaptor.getValue().getString("version"));

        verify(uniqueIdRepository, timeout(ASYNC_TIMEOUT)).open(stringArgumentCaptor.capture());
        assertEquals(originalAncId, stringArgumentCaptor.getValue());

        verify(baseAncClientProcessorForJava, timeout(ASYNC_TIMEOUT)).processClient(eventClientArgumentCaptor.capture());
        assertEquals(eventClients, eventClientArgumentCaptor.getValue());

        verify(allSharedPreferences, timeout(ASYNC_TIMEOUT)).saveLastUpdatedAtDate(longArgumentCaptor.capture());
        assertEquals(new Long(timestamp), longArgumentCaptor.getValue());

        verify(callBack, timeout(ASYNC_TIMEOUT)).onRegistrationSaved(ArgumentMatchers.anyBoolean());
    }
}