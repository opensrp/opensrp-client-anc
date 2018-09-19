package org.smartregister.anc.helper;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.service.intent.SettingsSyncIntentService;
import org.smartregister.configurableviews.helper.PreferenceHelper;
import org.smartregister.configurableviews.helper.PrefsHelper;
import org.smartregister.domain.Response;
import org.smartregister.domain.Setting;
import org.smartregister.repository.AllSettings;
import org.smartregister.service.HTTPAgent;

import static org.smartregister.util.Log.logError;

/**
 * Created by ndegwamartin on 14/09/2018.
 */
public class SyncSettingsServiceHelper {
    private static final String TAG = SyncSettingsServiceHelper.class.getCanonicalName();

    private boolean databaseCreated;

    private Context applicationContext;
    private AllSettings allSettings;
    private HTTPAgent httpAgent;
    private String baseUrl;
    private PrefsHelper syncHelper;
    public final static String SETTINGS_LAST_SYNC_FROM_SERVER_TIMESTAMP = "SETTINGS_LAST_SYNC_FROM_SERVER_TIMESTAMP";
    public final static String SETTINGS_LAST_SYNC_TO_SERVER_TIMESTAMP = "SETTINGS_LAST_SYNC_TO_SERVER_TIMESTAMP";

    public SyncSettingsServiceHelper(Context applicationContext, AllSettings allSettings,
                                     HTTPAgent httpAgent, String baseUrl, PrefsHelper syncHelper, boolean databaseCreated) {

        this.applicationContext = applicationContext;
        this.allSettings = allSettings;
        this.httpAgent = httpAgent;
        this.baseUrl = baseUrl;
        this.syncHelper = syncHelper;
        this.databaseCreated = databaseCreated;
    }


    public int processIntent() throws Exception {
        JSONArray settings = fetchSettings();
        if (settings != null && settings.length() > 0) {
            //There is any other previous install
            if (!databaseCreated) {
                saveSetting(settings);
            } else {
                settings = saveSetting(settings);
                //   long lastSyncTimeStamp = allSettings.saveSettings(settings);
                //    syncHelper.updateLastViewsSyncTimeStamp(lastSyncTimeStamp);
                settings.toString();
            }
        }
        return settings == null || !databaseCreated ? 0 : settings.length();
    }

    private JSONArray saveSetting(JSONArray serverSettings) throws JSONException {
        for (int i = 0; i < serverSettings.length(); i++) {

            JSONObject jsonObject = serverSettings.getJSONObject(i);
            Setting characteristic = new Setting();
            characteristic.setKey(jsonObject.getString("identifier"));
            characteristic.setValue(jsonObject.getString("settings"));

            AncApplication.getInstance().getContext().allSettings().put(SETTINGS_LAST_SYNC_FROM_SERVER_TIMESTAMP, characteristic.getVersion());
            AncApplication.getInstance().getContext().allSettings().putSetting(characteristic);


        }
        return serverSettings;
    }

    private JSONArray fetchSettings() throws JSONException {
        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }

        String url = baseUrl + SettingsSyncIntentService.SETTINGS_URL + "?serverVersion=" + PreferenceHelper.getInstance(applicationContext).getLastViewsSyncTimeStamp();

        Log.i(TAG, "URL: " + url);

        if (httpAgent == null) {
            logError(url + " http agent is null");
            return null;
        }


        Response resp = httpAgent.fetchWithCredentials(url, "demo", "Amani123");
        if (resp.isFailure()) {
            logError(url + " not returned data");
            return null;
        }
        return new JSONArray((String) resp.payload());
    }
}

