package org.smartregister.anc.helper;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.configurableviews.helper.PrefsHelper;
import org.smartregister.domain.db.EventClient;
import org.smartregister.repository.EventClientRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.smartregister.configurableviews.util.Constants.CONFIGURATION.LOGIN;
import static org.smartregister.configurableviews.util.Constants.LAST_VIEWS_SYNC_TIMESTAMP;
import static org.smartregister.configurableviews.util.Constants.VIEW_CONFIGURATION_PREFIX;

/**
 * Created by ndegwamartin on 15/03/2018.
 */

public class ECSyncHelper extends org.smartregister.sync.helper.ECSyncHelper implements PrefsHelper {

    public static final String MOVE_TO_CATCHMENT_EVENT = "MOVE_TO_CATCHMENT_EVENT";

    private static ECSyncHelper instance;

    protected ECSyncHelper(Context context, EventClientRepository eventClientRepository) {
        super(context, eventClientRepository);
    }

    public static ECSyncHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ECSyncHelper(context, CoreLibrary.getInstance().context().getEventClientRepository());
        }
        return instance;
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

    public List<EventClient> allEvents(long startSyncTimeStamp, long lastSyncTimeStamp) {
        try {
            return eventClientRepository.fetchEventClients(startSyncTimeStamp, lastSyncTimeStamp);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
        return new ArrayList<>();
    }


    public long getLastViewsSyncTimeStamp() {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(LAST_VIEWS_SYNC_TIMESTAMP, 0);
    }

    public void updateLastViewsSyncTimeStamp(long lastSyncTimeStamp) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(LAST_VIEWS_SYNC_TIMESTAMP, lastSyncTimeStamp).commit();
    }

    public void updateLoginConfigurableViewPreference(String loginJson) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(VIEW_CONFIGURATION_PREFIX + LOGIN, loginJson).commit();
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
        return eventClientRepository.deleteEventsByBaseEntityId(baseEntityId, MOVE_TO_CATCHMENT_EVENT);
    }
}
