package org.smartregister.anc.helper;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.repository.EventClientRepository;

/**
 * Created by ndegwamartin on 07/09/2018.
 */
public class ECSyncHelperTest extends BaseUnitTest {

    private ECSyncHelper syncHelper;

    @Mock
    private EventClientRepository eventClientRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        syncHelper = ECSyncHelper.getInstance(RuntimeEnvironment.application);
        Whitebox.setInternalState(syncHelper, "eventClientRepository", eventClientRepository);
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

        syncHelperSpy.saveAllClientsAndEvents(object);

        Mockito.verify(syncHelperSpy).batchSave(eventsArray, clientsArray);

    }

}
