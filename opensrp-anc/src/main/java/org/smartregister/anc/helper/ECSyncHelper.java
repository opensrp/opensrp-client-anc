package org.smartregister.anc.helper;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.service.intent.SyncIntentService;
import org.smartregister.anc.util.Constants;
import org.smartregister.configurableviews.helper.PrefsHelper;
import org.smartregister.domain.Response;
import org.smartregister.domain.db.EventClient;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.smartregister.configurableviews.util.Constants.CONFIGURATION.LOGIN;
import static org.smartregister.configurableviews.util.Constants.LAST_VIEWS_SYNC_TIMESTAMP;
import static org.smartregister.configurableviews.util.Constants.VIEW_CONFIGURATION_PREFIX;
import static org.smartregister.util.Utils.getPreference;

/**
 * Created by ndegwamartin on 15/03/2018.
 */

public class ECSyncHelper implements PrefsHelper {

    public static final String SEARCH_URL = "/rest/event/sync";

    private final EventClientRepository eventClientRepository;
    private final Context context;

    private static ECSyncHelper instance;

    public static ECSyncHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ECSyncHelper(context, AncApplication.getInstance().getEventClientRepository());
        }
        return instance;
    }

    private ECSyncHelper(Context context, EventClientRepository eventClientRepository) {
        this.context = context;
        this.eventClientRepository = eventClientRepository;
    }

    public boolean saveAllClientsAndEvents(JSONObject jsonObject) {
        try {
            if (jsonObject == null) {
                return false;
            }

            JSONArray events = jsonObject.has("events") ? jsonObject.getJSONArray("events") : new JSONArray();
            JSONArray clients = jsonObject.has("clients") ? jsonObject.getJSONArray("clients") : new JSONArray();

            batchSave(events, clients);


            return true;
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
            return false;
        }
    }

    public List<EventClient> allEventClients(long startSyncTimeStamp, long lastSyncTimeStamp) {
        try {
            return eventClientRepository.fetchEventClients(startSyncTimeStamp, lastSyncTimeStamp);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
        return new ArrayList<>();
    }

    public List<EventClient> getEvents(Date lastSyncDate, String syncStatus) {
        try {
            return eventClientRepository.fetchEventClients(lastSyncDate, syncStatus);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
        return new ArrayList<>();
    }

    public JSONObject getClient(String baseEntityId) {
        try {
            return eventClientRepository.getClientByBaseEntityId(baseEntityId);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
        return null;
    }

    public void addClient(String baseEntityId, JSONObject jsonObject) {
        try {
            eventClientRepository.addorUpdateClient(baseEntityId, jsonObject);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
    }

    public void addEvent(String baseEntityId, JSONObject jsonObject) {
        try {
            eventClientRepository.addEvent(baseEntityId, jsonObject);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
    }

    public JSONObject fetchAsJsonObject(String filter, String filterValue) throws Exception {
        try {
            HTTPAgent httpAgent = AncApplication.getInstance().getContext().getHttpAgent();
            String baseUrl = AncApplication.getInstance().getContext().
                    configuration().dristhiBaseURL();
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/"));
            }

            Long lastSyncDatetime = getLastSyncTimeStamp();
            Log.i(ECSyncHelper.class.getName(), "LAST SYNC DT :" + new DateTime(lastSyncDatetime));

            String url = baseUrl + SEARCH_URL + "?" + filter + "=" + filterValue + "&serverVersion=" + lastSyncDatetime + "&limit=" + SyncIntentService.EVENT_PULL_LIMIT;
            Log.i(ECSyncHelper.class.getName(), "URL: " + url);

            if (httpAgent == null) {
                throw new SyncException(url + " http agent is null");
            }

            Response resp = httpAgent.fetch(url);
            if (resp.isFailure()) {
                throw new SyncException(url + " not returned data");
            }

            return new JSONObject((String) resp.payload());
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
            throw new SyncException(SEARCH_URL + " threw exception", e);
        }
    }

    public List<EventClient> allEvents(long startSyncTimeStamp, long lastSyncTimeStamp) {
        try {
            return eventClientRepository.fetchEventClients(startSyncTimeStamp, lastSyncTimeStamp);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
        return new ArrayList<>();
    }

    public Pair<Long, Long> getMinMaxServerVersions(JSONObject jsonObject) {
        final String EVENTS = "events";
        final String SERVER_VERSION = "serverVersion";
        try {
            if (jsonObject != null && jsonObject.has(EVENTS)) {
                JSONArray events = jsonObject.getJSONArray(EVENTS);

                long maxServerVersion = Long.MIN_VALUE;
                long minServerVersion = Long.MAX_VALUE;

                for (int i = 0; i < events.length(); i++) {
                    Object o = events.get(i);
                    if (o instanceof JSONObject) {
                        JSONObject jo = (JSONObject) o;
                        if (jo.has(SERVER_VERSION)) {
                            long serverVersion = jo.getLong(SERVER_VERSION);
                            if (serverVersion > maxServerVersion) {
                                maxServerVersion = serverVersion;
                            }

                            if (serverVersion < minServerVersion) {
                                minServerVersion = serverVersion;
                            }
                        }
                    }
                }
                return Pair.create(minServerVersion, maxServerVersion);
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage());
        }
        return Pair.create(0L, 0L);
    }

    public long getLastSyncTimeStamp() {
        return Long.parseLong(getPreference(context, Constants.LAST_SYNC_TIMESTAMP, "0"));
    }

    public void updateLastSyncTimeStamp(long lastSyncTimeStamp) {
        Utils.writePreference(context, Constants.LAST_SYNC_TIMESTAMP, lastSyncTimeStamp + "");
    }

    public long getLastViewsSyncTimeStamp() {
        return Long.parseLong(getPreference(context, LAST_VIEWS_SYNC_TIMESTAMP, "0"));
    }

    public void updateLastViewsSyncTimeStamp(long lastSyncTimeStamp) {
        Utils.writePreference(context, LAST_VIEWS_SYNC_TIMESTAMP, lastSyncTimeStamp + "");
    }

    public long getLastCheckTimeStamp() {
        return Long.parseLong(getPreference(context, Constants.LAST_CHECK_TIMESTAMP, "0"));
    }

    public void updateLastCheckTimeStamp(long lastSyncTimeStamp) {
        Utils.writePreference(context, Constants.LAST_CHECK_TIMESTAMP, lastSyncTimeStamp + "");
    }

    public void updateLoginConfigurableViewPreference(String loginJson) {
        Utils.writePreference(context, VIEW_CONFIGURATION_PREFIX + LOGIN, loginJson);
    }

    public void batchSave(JSONArray events, JSONArray clients) throws Exception {
        eventClientRepository.batchInsertClients(clients);
        eventClientRepository.batchInsertEvents(events, getLastSyncTimeStamp());
    }

    private class SyncException extends Exception {
        public SyncException(String s) {
            Log.e(getClass().getName(), s);
        }

        public SyncException(String s, Throwable e) {
            Log.e(getClass().getName(), "SyncException: " + s, e);
        }
    }

    public void batchInsertClients(JSONArray clients) {
        eventClientRepository.batchInsertClients(clients);
    }

    protected void batchInsertEvents(JSONArray events) {
        eventClientRepository.batchInsertEvents(events, getLastSyncTimeStamp());
    }

    public <T> T convert(JSONObject jo, Class<T> t) {
        return eventClientRepository.convert(jo, t);
    }

    public JSONObject convertToJson(Object object) {
        return eventClientRepository.convertToJson(object);
    }

    public boolean deleteClient(String baseEntityId) {
        return eventClientRepository.deleteClient(baseEntityId);
    }

    public boolean deleteEventsByBaseEntityId(String baseEntityId) {
        return eventClientRepository.deleteEventsByBaseEntityId(baseEntityId, "MOVE_TO_CATCHMENT_EVENT");
    }
}
