package org.smartregister.anc.util;

import android.content.Context;

import org.smartregister.anc.helper.SyncSettingsServiceHelper;

import static org.smartregister.util.Utils.getPreference;

/**
 * Created by ndegwamartin on 20/09/2018.
 */
public class AncPreferenceHelper

{

    private final Context context;

    private static AncPreferenceHelper instance;

    public static AncPreferenceHelper getInstance(Context context) {
        if (instance == null) {
            instance = new AncPreferenceHelper(context);
        }
        return instance;
    }

    private AncPreferenceHelper(Context context) {
        this.context = context;
    }

    public long getLastSettingsSyncFromServerTimeStamp() {
        return Long.parseLong(getPreference(context, SyncSettingsServiceHelper.SETTINGS_LAST_SYNC_FROM_SERVER_TIMESTAMP, "0"));
    }

    public void updateLastSettingsSyncFromServerTimeStamp(long lastSyncTimeStamp) {
        org.smartregister.util.Utils.writePreference(context, SyncSettingsServiceHelper.SETTINGS_LAST_SYNC_FROM_SERVER_TIMESTAMP, lastSyncTimeStamp + "");
    }

    public long getLastSettingsSyncToServerTimeStamp() {
        return Long.parseLong(getPreference(context, SyncSettingsServiceHelper.SETTINGS_LAST_SYNC_TO_SERVER_TIMESTAMP, "0"));
    }

    public void updateLastSettingsSyncToServerTimeStamp(long lastSyncTimeStamp) {
        org.smartregister.util.Utils.writePreference(context, SyncSettingsServiceHelper.SETTINGS_LAST_SYNC_TO_SERVER_TIMESTAMP, lastSyncTimeStamp + "");
    }

}
