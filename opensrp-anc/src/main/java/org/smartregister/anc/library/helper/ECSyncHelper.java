package org.smartregister.anc.library.helper;

import static org.smartregister.configurableviews.util.Constants.CONFIGURATION.LOGIN;
import static org.smartregister.configurableviews.util.Constants.LAST_VIEWS_SYNC_TIMESTAMP;
import static org.smartregister.configurableviews.util.Constants.VIEW_CONFIGURATION_PREFIX;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import org.smartregister.CoreLibrary;
import org.smartregister.configurableviews.helper.PrefsHelper;
import org.smartregister.repository.EventClientRepository;

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

    public long getLastViewsSyncTimeStamp() {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(LAST_VIEWS_SYNC_TIMESTAMP, 0);
    }

    public void updateLastViewsSyncTimeStamp(long lastSyncTimeStamp) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(LAST_VIEWS_SYNC_TIMESTAMP, lastSyncTimeStamp)
                .commit();
    }

    public void updateLoginConfigurableViewPreference(String loginJson) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(VIEW_CONFIGURATION_PREFIX + LOGIN, loginJson)
                .commit();
    }

    public boolean deleteEventsByBaseEntityId(String baseEntityId) {
        return eventClientRepository.deleteEventsByBaseEntityId(baseEntityId, MOVE_TO_CATCHMENT_EVENT);
    }

    private class SyncException extends Exception {
        public SyncException(String s) {
            Log.e(getClass().getName(), s);
        }

        public SyncException(String s, Throwable e) {
            Log.e(getClass().getName(), "SyncException: " + s, e);
        }
    }
}
