package org.smartregister.anc.library.sync;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.reflect.TypeToken;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.helper.ECSyncHelper;
import org.smartregister.anc.library.model.PreviousContact;
import org.smartregister.anc.library.model.Task;
import org.smartregister.anc.library.repository.ContactTasksRepository;
import org.smartregister.anc.library.util.ANCJsonFormUtils;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.domain.Client;
import org.smartregister.domain.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.domain.jsonmapping.ClientField;
import org.smartregister.domain.jsonmapping.Table;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.MiniClientProcessorForJava;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 15/03/2018.
 */

public class BaseAncClientProcessorForJava extends ClientProcessorForJava implements MiniClientProcessorForJava {
    private HashSet<String> eventTypes = new HashSet<>();

    public BaseAncClientProcessorForJava(Context context) {
        super(context);
    }

    public static BaseAncClientProcessorForJava getInstance(Context context) {
        if (instance == null) {
            instance = new BaseAncClientProcessorForJava(context);
        }

        return (BaseAncClientProcessorForJava) instance;
    }

    @Override
    public void processClient(List<EventClient> eventClients) throws Exception {
        Timber.d("Inside the BaseAncClientProcessorForJava");
        ClientClassification clientClassification =
                assetJsonToJava(ConstantsUtils.EcFileUtils.CLIENT_CLASSIFICATION, ClientClassification.class);

        if (!eventClients.isEmpty()) {
            List<Event> unsyncEvents = new ArrayList<>();
            for (EventClient eventClient : eventClients) {
                processEventClient(eventClient, unsyncEvents, clientClassification);
            }

            // Unsync events that are should not be in this device
            if (!unsyncEvents.isEmpty()) {
                unSync(unsyncEvents);
            }
        }
    }

    private void processVisit(Event event) {
        //Attention flags
        getDetailsRepository()
                .add(event.getBaseEntityId(), ConstantsUtils.DetailsKeyUtils.ATTENTION_FLAG_FACTS,
                        event.getDetails().get(ConstantsUtils.DetailsKeyUtils.ATTENTION_FLAG_FACTS),
                        Calendar.getInstance().getTimeInMillis());
        processPreviousContacts(event);
        processContactTasks(event);

    }

    private boolean unSync(ECSyncHelper ecSyncHelper, DetailsRepository detailsRepository, List<Table> bindObjects,
                           Event event, String registeredAnm) {
        try {
            String baseEntityId = event.getBaseEntityId();
            String providerId = event.getProviderId();

            if (providerId.equals(registeredAnm)) {
                ecSyncHelper.deleteEventsByBaseEntityId(baseEntityId);
                ecSyncHelper.deleteClient(baseEntityId);
                detailsRepository.deleteDetails(baseEntityId);

                for (Table bindObject : bindObjects) {
                    String tableName = bindObject.name;
                    deleteCase(tableName, baseEntityId);
                }

                return true;
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return false;
    }

    public DetailsRepository getDetailsRepository() {
        return AncLibrary.getInstance().getDetailsRepository();
    }

    private void processPreviousContacts(Event event) {
        //Previous contact state
        String previousContactsRaw = event.getDetails().get(ConstantsUtils.DetailsKeyUtils.PREVIOUS_CONTACTS);
        Map<String, String> previousContactMap = getPreviousContactMap(previousContactsRaw);

        if (previousContactMap != null) {
            String contactNo = getContact(event);
            for (Map.Entry<String, String> entry : previousContactMap.entrySet()) {
                AncLibrary.getInstance().getPreviousContactRepository().savePreviousContact(
                        new PreviousContact(event.getBaseEntityId(), entry.getKey(), entry.getValue(), contactNo));
            }
        }
    }

    private void processContactTasks(Event event) {
        try {
            String openTasks = event.getDetails().get(ConstantsUtils.DetailsKeyUtils.OPEN_TEST_TASKS);
            if (StringUtils.isNotBlank(openTasks)) {
                JSONArray openTasksArray = new JSONArray(openTasks);
                getContactTasksRepository().deleteAllTasks(event.getBaseEntityId());
                for (int i = 0; i < openTasksArray.length(); i++) {
                    JSONObject tasks = new JSONObject(openTasksArray.getString(i));
                    String key = tasks.optString(JsonFormConstants.KEY);

                    Task task = getTask(tasks, key, event.getBaseEntityId());
                    getContactTasksRepository().saveOrUpdateTasks(task);
                }
            }
        } catch (JSONException e) {
            Timber.e(e, " --> processContactTasks");
        }
    }

    public Map<String, String> getPreviousContactMap(String previousContactsRaw) {
        return AncLibrary.getInstance().getGsonInstance().fromJson(previousContactsRaw, new TypeToken<Map<String, String>>() {
        }.getType());
    }

    @NotNull
    private String getContact(Event event) {
        String contactNo = "";
        if (!TextUtils.isEmpty(event.getDetails().get(ConstantsUtils.CONTACT))) {
            String[] contacts = event.getDetails().get(ConstantsUtils.CONTACT).split(" ");
            if (contacts.length >= 2) {
                int nextContact = Integer.parseInt(contacts[1]);
                if (nextContact > 0) {
                    contactNo = String.valueOf(nextContact - 1);
                } else {
                    contactNo = String.valueOf(nextContact + 1);
                }
            }
        }
        return contactNo;
    }

    @NotNull
    private Task getTask(JSONObject field, String key, String baseEntityId) {
        Task task = new Task();
        task.setBaseEntityId(baseEntityId);
        task.setKey(key);
        task.setValue(String.valueOf(field));
        task.setUpdated(false);
        task.setComplete(ANCJsonFormUtils.checkIfTaskIsComplete(field));
        task.setCreatedAt(Calendar.getInstance().getTimeInMillis());
        return task;
    }

    public ContactTasksRepository getContactTasksRepository() {
        return AncLibrary.getInstance().getContactTasksRepository();
    }

    @Override
    public void updateFTSsearch(String tableName, String entityId, ContentValues contentValues) {
        AllCommonsRepository allCommonsRepository = org.smartregister.CoreLibrary.getInstance().context().allCommonsRepositoryobjects(tableName);

        if (allCommonsRepository != null) {
            allCommonsRepository.updateSearch(entityId);
        }
    }

    @Override
    public String[] getOpenmrsGenIds() {
        /*
        This method is not currently used because the ANC_ID is always a number and does not contain hyphens.
        This method is used to get the identifiers used by OpenMRS so that we remove the hyphens in the
        content values for such identifiers
         */
        return new String[]{DBConstantsUtils.KeyUtils.ANC_ID, ConstantsUtils.JsonFormKeyUtils.ANC_ID};
    }

    @Override
    public void processEventClient(@NonNull EventClient eventClient, @NonNull List<Event> unsyncEvents, @Nullable ClientClassification clientClassification) throws Exception {
        Event event = eventClient.getEvent();
        if (event == null) {
            return;
        }

        String eventType = event.getEventType();
        if (StringUtils.isBlank(eventType)) {
            return;
        }
        Client client = eventClient.getClient();
        switch (eventType) {
            case ConstantsUtils.EventTypeUtils.CLOSE:
            case ConstantsUtils.EventTypeUtils.REGISTRATION:
            case ConstantsUtils.EventTypeUtils.UPDATE_REGISTRATION:
                if (clientClassification == null) {
                    return;
                }
                //iterate through the events
                if (client != null) {
                    processEvent(event, client, clientClassification);
                }
                break;
            case ConstantsUtils.EventTypeUtils.CONTACT_VISIT:
                processVisit(event);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean unSync(@Nullable List<Event> events) {
        try {

            if (events == null || events.isEmpty()) {
                return false;
            }

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);
            String registeredAnm = allSharedPreferences.fetchRegisteredANM();

            ClientField clientField = assetJsonToJava(ConstantsUtils.EcFileUtils.CLIENT_FIELDS, ClientField.class);
            if (clientField == null) {
                return false;
            }

            List<Table> bindObjects = clientField.bindobjects;
            DetailsRepository detailsRepository = AncLibrary.getInstance().getContext().detailsRepository();
            ECSyncHelper ecUpdater = ECSyncHelper.getInstance(getContext());

            for (Event event : events) {
                unSync(ecUpdater, detailsRepository, bindObjects, event, registeredAnm);
            }

            return true;

        } catch (Exception e) {
            Timber.e(e, " --> unSync");
        }

        return false;
    }


    @NonNull
    @Override
    public HashSet<String> getEventTypes() {
        if (eventTypes.isEmpty()) {
            eventTypes.add(ConstantsUtils.EventTypeUtils.REGISTRATION);
            eventTypes.add(ConstantsUtils.EventTypeUtils.UPDATE_REGISTRATION);
            eventTypes.add(ConstantsUtils.EventTypeUtils.QUICK_CHECK);
            eventTypes.add(ConstantsUtils.EventTypeUtils.CONTACT_VISIT);
            eventTypes.add(ConstantsUtils.EventTypeUtils.CLOSE);
            eventTypes.add(ConstantsUtils.EventTypeUtils.SITE_CHARACTERISTICS);
        }

        return eventTypes;
    }

    @Override
    public boolean canProcess(@NonNull String eventType) {
        return getEventTypes().contains(eventType);
    }
}