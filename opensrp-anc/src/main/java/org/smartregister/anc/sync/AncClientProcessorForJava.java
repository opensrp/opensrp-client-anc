package org.smartregister.anc.sync;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.helper.ECSyncHelper;
import org.smartregister.anc.model.PreviousContact;
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
import java.util.Calendar;
import java.util.List;
import java.util.Map;

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

        ClientClassification clientClassification = assetJsonToJava(Constants.EC_FILE.CLIENT_CLASSIFICATION,
                ClientClassification.class);

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
                } else if (eventType.equals(Constants.EventType.REGISTRATION) || eventType
                        .equals(Constants.EventType.UPDATE_REGISTRATION)) {
                    if (clientClassification == null) {
                        continue;
                    }

                    Client client = eventClient.getClient();
                    //iterate through the events
                    if (client != null) {
                        processEvent(event, client, clientClassification);

                    }
                } else if (eventType.equals(Constants.EventType.CONTACT_VISIT)) {

                    processVisit(event);
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
    private boolean unSync(ECSyncHelper ecSyncHelper, DetailsRepository detailsRepository, List<Table> bindObjects,
                           Event event, String registeredAnm) {
        try {
            String baseEntityId = event.getBaseEntityId();
            String providerId = event.getProviderId();

            if (providerId.equals(registeredAnm)) {
                ecSyncHelper.deleteEventsByBaseEntityId(baseEntityId);
                ecSyncHelper.deleteClient(baseEntityId);
                //  Log.d(getClass().getName(), "EVENT_DELETED: " + eventDeleted);
                // Log.d(getClass().getName(), "ClIENT_DELETED: " + clientDeleted);

                detailsRepository.deleteDetails(baseEntityId);
                // Log.d(getClass().getName(), "DETAILS_DELETED: " + detailsDeleted);

                for (Table bindObject : bindObjects) {
                    String tableName = bindObject.name;

                    deleteCase(tableName, baseEntityId);
                    //    Log.d(getClass().getName(), "CASE_DELETED: " + caseDeleted);
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

            ClientField clientField = assetJsonToJava(Constants.EC_FILE.CLIENT_FIELDS, ClientField.class);
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

    private void processVisit(Event event) {

        //event.getEvents();

        //Attention flags
        AncApplication.getInstance().getDetailsRepository()
                .add(event.getBaseEntityId(), Constants.DETAILS_KEY.ATTENTION_FLAG_FACTS,
                        event.getDetails().get(Constants.DETAILS_KEY.ATTENTION_FLAG_FACTS),
                        Calendar.getInstance().getTimeInMillis());

        //Previous contact state
        String previousContactsRaw = event.getDetails().get(Constants.DETAILS_KEY.PREVIOUS_CONTACTS);
        Map<String, String> previousContactMap = AncApplication.getInstance().getGsonInstance()
                .fromJson(previousContactsRaw, new TypeToken<Map<String, String>>() {
                }.getType());

        if (previousContactMap != null) {
            String contactNo = "";
            if (!TextUtils.isEmpty(event.getDetails().get(Constants.CONTACT))) {
                String[] contacts = event.getDetails().get(Constants.CONTACT).split(" ");
                if (contacts.length >= 2) {
                    int nextContact;
                    if (Integer.parseInt(contacts[1]) > 0) {
                        nextContact = Integer.parseInt(contacts[1]) - 1;
                    } else {
                        nextContact = Integer.parseInt(contacts[1]) + 1;
                    }
                    contactNo = String.valueOf(nextContact);
                }
            }

            for (Map.Entry<String, String> entry : previousContactMap.entrySet()) {
                AncApplication.getInstance().getPreviousContactRepository().savePreviousContact(
                        new PreviousContact(event.getBaseEntityId(), entry.getKey(), entry.getValue(), contactNo));
            }
        }

    }

    @Override
    public String[] getOpenmrsGenIds() {
        return new String[]{DBConstants.KEY.ANC_ID};
    }

    @Override
    public void updateFTSsearch(String tableName, String entityId, ContentValues contentValues) {

        // Log.d(TAG, "Starting updateFTSsearch table: " + tableName);

        AllCommonsRepository allCommonsRepository = org.smartregister.CoreLibrary.getInstance().context().
                allCommonsRepositoryobjects(tableName);

        if (allCommonsRepository != null) {
            allCommonsRepository.updateSearch(entityId);
        }

        //  Log.d(TAG, "Finished updateFTSsearch table: " + tableName);
    }
}
