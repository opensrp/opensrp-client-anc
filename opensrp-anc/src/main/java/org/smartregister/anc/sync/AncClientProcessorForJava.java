package org.smartregister.anc.sync;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.helper.ECSyncHelper;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.domain.db.Client;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.domain.jsonmapping.ClientField;
import org.smartregister.domain.jsonmapping.Table;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.sync.ClientProcessorForJava;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ndegwamartin on 15/03/2018.
 */

public class AncClientProcessorForJava extends ClientProcessorForJava {

    private static final String TAG = AncClientProcessorForJava.class.getCanonicalName();
    private static AncClientProcessorForJava instance;

    public AncClientProcessorForJava(Context context) {
        super(context);
    }

    public static AncClientProcessorForJava getInstance(Context context) {
        if (instance == null) {
            instance = new AncClientProcessorForJava(context);
        }

        return instance;
    }

    @Override
    public void processClient(List<EventClient> eventClients) throws Exception {

        ClientClassification clientClassification = assetJsonToJava("ec_client_classification.json", ClientClassification.class);

        if (!eventClients.isEmpty()) {
            List<Event> unsyncEvents = new ArrayList<>();
            for (EventClient eventClient : eventClients) {
                Event event = eventClient.getEvent();
                if (event == null) {
                    return;
                }

                String eventType = event.getEventType();
                if (eventType == null) {
                    continue;
                }

                if (eventType.equals(Constants.EventType.CLOSE)) {
                    unsyncEvents.add(event);
                } else if (eventType.equals(Constants.EventType.REGISTRATION) || eventType.equals(Constants.EventType.UPDATE_REGISTRATION)) {
                    if (clientClassification == null) {
                        continue;
                    }

                    Client client = eventClient.getClient();
                    //iterate through the events
                    if (client != null) {
                        processEvent(event, client, clientClassification);

                    }
                }
            }

            // Unsync events that are should not be in this device
            if (!unsyncEvents.isEmpty()) {
                unSync(unsyncEvents);
            }
        }
    }

    /*
        private Integer parseInt(String string) {
            try {
                return Integer.valueOf(string);
            } catch (NumberFormatException e) {
                Log.e(TAG, e.toString(), e);
            }
            return null;
        }

        private ContentValues processCaseModel(EventClient eventClient, Table table) {
            try {
                List<Column> columns = table.columns;
                ContentValues contentValues = new ContentValues();

                for (Column column : columns) {
                    processCaseModel(eventClient.getEvent(), eventClient.getClient(), column, contentValues);
                }

                return contentValues;
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
            return null;
        }

        private Date getDate(String eventDateStr) {
            Date date = null;
            if (StringUtils.isNotBlank(eventDateStr)) {
                try {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");
                    date = dateFormat.parse(eventDateStr);
                } catch (ParseException e) {
                    try {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                        date = dateFormat.parse(eventDateStr);
                    } catch (ParseException pe) {
                        try {
                            date = DateUtil.parseDate(eventDateStr);
                        } catch (ParseException pee) {
                            Log.e(TAG, pee.toString(), pee);
                        }
                    }
                }
            }
            return date;
        }

    */
    private boolean unSync(ECSyncHelper ecSyncHelper, DetailsRepository detailsRepository, List<Table> bindObjects, Event event, String registeredAnm) {
        try {
            String baseEntityId = event.getBaseEntityId();
            String providerId = event.getProviderId();

            if (providerId.equals(registeredAnm)) {
                boolean eventDeleted = ecSyncHelper.deleteEventsByBaseEntityId(baseEntityId);
                boolean clientDeleted = ecSyncHelper.deleteClient(baseEntityId);
                Log.d(getClass().getName(), "EVENT_DELETED: " + eventDeleted);
                Log.d(getClass().getName(), "ClIENT_DELETED: " + clientDeleted);

                boolean detailsDeleted = detailsRepository.deleteDetails(baseEntityId);
                Log.d(getClass().getName(), "DETAILS_DELETED: " + detailsDeleted);

                for (Table bindObject : bindObjects) {
                    String tableName = bindObject.name;

                    boolean caseDeleted = deleteCase(tableName, baseEntityId);
                    Log.d(getClass().getName(), "CASE_DELETED: " + caseDeleted);
                }

                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
        return false;
    }

    private boolean unSync(List<Event> events) {
        try {

            if (events == null || events.isEmpty()) {
                return false;
            }

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);
            String registeredAnm = allSharedPreferences.fetchRegisteredANM();

            ClientField clientField = assetJsonToJava("ec_client_fields.json", ClientField.class);
            if (clientField == null) {
                return false;
            }

            List<Table> bindObjects = clientField.bindobjects;
            DetailsRepository detailsRepository = AncApplication.getInstance().getContext().detailsRepository();
            ECSyncHelper ecUpdater = ECSyncHelper.getInstance(getContext());

            for (Event event : events) {
                unSync(ecUpdater, detailsRepository, bindObjects, event, registeredAnm);
            }

            return true;

        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }

        return false;
    }

    @Override
    public String[] getOpenmrsGenIds() {
        return new String[]{DBConstants.KEY.ANC_ID};
    }

    @Override
    public void updateFTSsearch(String tableName, String entityId, ContentValues contentValues) {

        Log.i(TAG, "Starting updateFTSsearch table: " + tableName);

        AllCommonsRepository allCommonsRepository = org.smartregister.CoreLibrary.getInstance().context().
                allCommonsRepositoryobjects(tableName);

        if (allCommonsRepository != null) {
            allCommonsRepository.updateSearch(entityId);
        }

        Log.i(TAG, "Finished updateFTSsearch table: " + tableName);
    }
}
