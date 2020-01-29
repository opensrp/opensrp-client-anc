package org.smartregister.anc.library.helper;

import android.app.Activity;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.anc.library.activity.BaseActivityUnitTest;
import org.smartregister.domain.db.EventClient;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;

import java.util.List;

/**
 * Created by ndegwamartin on 07/09/2018.
 */
public class ECSyncHelperTest extends BaseActivityUnitTest {

    private static final String EVENT_CLIENT_REPOSITORY = "eventClientRepository";
    private ECSyncHelper syncHelper;
    @Mock
    private EventClientRepository eventClientRepository;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Before
    public void setUp() {
        super.setUp();

        syncHelper = ECSyncHelper.getInstance(RuntimeEnvironment.application);
        Whitebox.setInternalState(syncHelper, EVENT_CLIENT_REPOSITORY, eventClientRepository);
        Whitebox.setInternalState(syncHelper, allSharedPreferences, allSharedPreferences);
    }

    @Override
    protected Activity getActivity() {
        return null;
    }

    @Override
    protected ActivityController getActivityController() {
        return null;
    }

    @Test
    public void testSaveAllClientsAndEventsInvokesBatchSaveWithCorrectParams() throws Exception {
        ECSyncHelper syncHelperSpy = Mockito.spy(syncHelper);

        JSONObject object = new JSONObject();

        JSONArray clientsArray = new JSONArray();
        clientsArray.put("Some Client");

        JSONArray eventsArray = new JSONArray();
        eventsArray.put("Some Event");

        object.put("clients", clientsArray);
        object.put("events", eventsArray);

        boolean result = syncHelperSpy.saveAllClientsAndEvents(object);

        Mockito.verify(syncHelperSpy).batchSave(eventsArray, clientsArray);
        Assert.assertTrue(result);

        result = syncHelperSpy.saveAllClientsAndEvents(object);
        Assert.assertTrue(result);
    }

    @Test
    public void testSaveAllClientsAndEventsShouldReturnFalseForNullParam() throws Exception {
        ECSyncHelper syncHelperSpy = Mockito.spy(syncHelper);

        boolean result = syncHelperSpy.saveAllClientsAndEvents(null);
        Assert.assertFalse(result);
    }

    @Test
    public void testAllEventClientsInvokesRepositoryFetchEventClientsWithCorrectParams() {

        ECSyncHelper syncHelperSpy = Mockito.spy(syncHelper);

        Mockito.doReturn(null).when(eventClientRepository).fetchEventClients(DUMMY_LONG, DUMMY_LONG);

        List<EventClient> result = syncHelperSpy.allEventClients(DUMMY_LONG, DUMMY_LONG);
        Assert.assertNull(result);

        Mockito.verify(eventClientRepository).fetchEventClients(DUMMY_LONG, DUMMY_LONG);

        //On Exception
        EventClientRepository eventClientRepository = null;
        Whitebox.setInternalState(syncHelperSpy, EVENT_CLIENT_REPOSITORY, eventClientRepository);
        result = syncHelperSpy.allEventClients(DUMMY_LONG, DUMMY_LONG);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testGetEventsInvokesRepositoryFetchEventClientsWithCorrectParams() {

        ECSyncHelper syncHelperSpy = Mockito.spy(syncHelper);

        Mockito.doReturn(null).when(eventClientRepository).fetchEventClients(DUMMY_DATE, TEST_STRING);

        List<EventClient> result = syncHelperSpy.getEvents(DUMMY_DATE, TEST_STRING);
        Assert.assertNull(result);

        Mockito.verify(eventClientRepository).fetchEventClients(DUMMY_DATE, TEST_STRING);


        //On Exception
        EventClientRepository eventClientRepository = null;
        Whitebox.setInternalState(syncHelperSpy, EVENT_CLIENT_REPOSITORY, eventClientRepository);
        result = syncHelperSpy.getEvents(DUMMY_DATE, TEST_STRING);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());

    }

    @Test
    public void testGetClientInvokesRepositoryGetClientByBaseEntityIdWithCorrectParams() {

        ECSyncHelper syncHelperSpy = Mockito.spy(syncHelper);

        Mockito.doReturn(null).when(eventClientRepository).getClientByBaseEntityId(DUMMY_BASE_ENTITY_ID);

        JSONObject result = syncHelperSpy.getClient(DUMMY_BASE_ENTITY_ID);
        Assert.assertNull(result);

        Mockito.verify(eventClientRepository).getClientByBaseEntityId(DUMMY_BASE_ENTITY_ID);

        //On Exception
        EventClientRepository eventClientRepository = null;
        Whitebox.setInternalState(syncHelperSpy, EVENT_CLIENT_REPOSITORY, eventClientRepository);
        result = syncHelperSpy.getClient(TEST_STRING);
        Assert.assertNull(result);

    }

    @Test
    public void testAddClientInvokesRepositoryAddOrUpdateClientWithCorrectParams() {

        ECSyncHelper syncHelperSpy = Mockito.spy(syncHelper);

        JSONObject object = new JSONObject();
        Mockito.doNothing().when(eventClientRepository).addorUpdateClient(DUMMY_BASE_ENTITY_ID, object);

        syncHelperSpy.addClient(DUMMY_BASE_ENTITY_ID, object);

        Mockito.verify(eventClientRepository).addorUpdateClient(DUMMY_BASE_ENTITY_ID, object);

        //On Exception
        EventClientRepository eventClientRepository = null;
        Whitebox.setInternalState(syncHelperSpy, EVENT_CLIENT_REPOSITORY, eventClientRepository);
        syncHelperSpy.addClient(DUMMY_BASE_ENTITY_ID, object);

    }

    @Test
    public void testAddEventInvokesRepositoryAddEventWithCorrectParams() {

        ECSyncHelper syncHelperSpy = Mockito.spy(syncHelper);

        JSONObject object = new JSONObject();
        Mockito.doNothing().when(eventClientRepository).addEvent(DUMMY_BASE_ENTITY_ID, object);

        syncHelperSpy.addEvent(DUMMY_BASE_ENTITY_ID, object);

        Mockito.verify(eventClientRepository).addEvent(DUMMY_BASE_ENTITY_ID, object);

        //On Exception
        EventClientRepository eventClientRepository = null;
        Whitebox.setInternalState(syncHelperSpy, EVENT_CLIENT_REPOSITORY, eventClientRepository);
        syncHelperSpy.addEvent(DUMMY_BASE_ENTITY_ID, object);

    }

    @Test
    public void testAllEventsInvokesRepositoryFetchEventClientsWithCorrectParams() {

        ECSyncHelper syncHelperSpy = Mockito.spy(syncHelper);

        Mockito.doReturn(null).when(eventClientRepository).fetchEventClients(DUMMY_LONG, DUMMY_LONG);

        List<EventClient> result = syncHelperSpy.allEvents(DUMMY_LONG, DUMMY_LONG);
        Assert.assertNull(result);

        Mockito.verify(eventClientRepository).fetchEventClients(DUMMY_LONG, DUMMY_LONG);

        //On Exception
        EventClientRepository eventClientRepository = null;
        Whitebox.setInternalState(syncHelperSpy, EVENT_CLIENT_REPOSITORY, eventClientRepository);
        result = syncHelperSpy.allEvents(DUMMY_LONG, DUMMY_LONG);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());

    }

    @Test
    public void testBatchSaveInvokesBatchInsertClientsWithCorrectParams() throws Exception {
        ECSyncHelper syncHelperSpy = Mockito.spy(syncHelper);

        JSONObject object = new JSONObject();

        JSONArray clientsArray = new JSONArray();
        clientsArray.put("Some Client");

        JSONArray eventsArray = new JSONArray();
        eventsArray.put("Some Event");

        object.put("clients", clientsArray);
        object.put("events", eventsArray);

        syncHelperSpy.batchSave(eventsArray, clientsArray);

        Mockito.verify(eventClientRepository).batchInsertClients(clientsArray);
    }

    @Test
    public void testBatchSaveInvokesBatchInsertEventsWithCorrectParams() throws Exception {
        ECSyncHelper syncHelperSpy = Mockito.spy(syncHelper);

        JSONObject object = new JSONObject();

        JSONArray eventsArray = new JSONArray();
        eventsArray.put("Some Event");

        object.put("events", eventsArray);

        syncHelperSpy.batchInsertEvents(eventsArray);

        Mockito.verify(eventClientRepository).batchInsertEvents(ArgumentMatchers.eq(eventsArray), ArgumentMatchers.anyLong());
    }

    @Test
    public void testBatchInsertClientsInvokesRepositoryBatchInsertClientsWithCorrectParams() throws Exception {
        ECSyncHelper syncHelperSpy = Mockito.spy(syncHelper);

        JSONObject object = new JSONObject();

        JSONArray clientsArray = new JSONArray();
        clientsArray.put("Some Client");

        object.put("clients", clientsArray);

        syncHelperSpy.batchInsertClients(clientsArray);

        Mockito.verify(eventClientRepository).batchInsertClients(clientsArray);
    }

    @Test
    public void testBatchInsertEventsInvokesRepositoryBatchInsertEventsWithCorrectParams() throws Exception {
        ECSyncHelper syncHelperSpy = Mockito.spy(syncHelper);

        JSONObject object = new JSONObject();

        JSONArray eventsArray = new JSONArray();
        eventsArray.put("Some Event");

        object.put("events", eventsArray);

        syncHelperSpy.batchInsertEvents(eventsArray);

        Mockito.verify(eventClientRepository).batchInsertEvents(ArgumentMatchers.eq(eventsArray), ArgumentMatchers.anyLong());
    }

    @Test
    public void testConvertToJsonInvokesRepositoryConvertToJsonWithCorrectParams() {

        ECSyncHelper syncHelperSpy = Mockito.spy(syncHelper);
        JSONObject object = new JSONObject();

        Mockito.doReturn(null).when(eventClientRepository).convertToJson(object);
        syncHelperSpy.convertToJson(object);

        Mockito.verify(eventClientRepository).convertToJson(object);

    }

    @Test
    public void testDeleteClientInvokesRepositoryDeleteClientWithCorrectParams() {

        ECSyncHelper syncHelperSpy = Mockito.spy(syncHelper);
        Mockito.doReturn(true).when(eventClientRepository).deleteClient(DUMMY_BASE_ENTITY_ID);

        syncHelperSpy.deleteClient(DUMMY_BASE_ENTITY_ID);

        Mockito.verify(eventClientRepository).deleteClient(DUMMY_BASE_ENTITY_ID);

    }

    @Test
    public void testDeleteEventsByBaseEntityIdInvokesRepositoryDeleteEventsByBaseEntityIdWithCorrectParams() {

        ECSyncHelper syncHelperSpy = Mockito.spy(syncHelper);
        Mockito.doReturn(true).when(eventClientRepository).deleteEventsByBaseEntityId(DUMMY_BASE_ENTITY_ID, ECSyncHelper.MOVE_TO_CATCHMENT_EVENT);

        syncHelperSpy.deleteEventsByBaseEntityId(DUMMY_BASE_ENTITY_ID);

        Mockito.verify(eventClientRepository).deleteEventsByBaseEntityId(DUMMY_BASE_ENTITY_ID, ECSyncHelper.MOVE_TO_CATCHMENT_EVENT);

    }
}
