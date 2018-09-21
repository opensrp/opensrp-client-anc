package org.smartregister.anc.helper;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.service.intent.SettingsSyncIntentService;
import org.smartregister.anc.util.AncPreferenceHelper;
import org.smartregister.anc.util.Constants;
import org.smartregister.domain.Response;
import org.smartregister.domain.Setting;
import org.smartregister.domain.SyncStatus;
import org.smartregister.service.HTTPAgent;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;

import static org.smartregister.util.Log.logError;

/**
 * Created by ndegwamartin on 14/09/2018.
 */
public class SyncSettingsServiceHelper {
    private static final String TAG = SyncSettingsServiceHelper.class.getCanonicalName();

    private Context applicationContext;
    private HTTPAgent httpAgent;
    private String baseUrl;
    private AncPreferenceHelper preferenceHelper;
    public final static String SETTINGS_LAST_SYNC_FROM_SERVER_TIMESTAMP = "SETTINGS_LAST_SYNC_FROM_SERVER_TIMESTAMP";
    public final static String SETTINGS_LAST_SYNC_TO_SERVER_TIMESTAMP = "SETTINGS_LAST_SYNC_TO_SERVER_TIMESTAMP";

    public SyncSettingsServiceHelper(Context applicationContext, String baseUrl, HTTPAgent httpAgent) {

        this.applicationContext = applicationContext;
        this.httpAgent = httpAgent;
        this.baseUrl = baseUrl;
        this.preferenceHelper = AncPreferenceHelper.getInstance(applicationContext);
    }


    public int processIntent() throws Exception {

        try {
            pushSettingsToServer();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        JSONArray settings = pullSettingsFromServer();

        if (settings != null && settings.length() > 0) {
            settings = saveSetting(settings);
            preferenceHelper.updateLastSettingsSyncFromServerTimeStamp(Calendar.getInstance().getTimeInMillis());
        }

        return settings == null ? 0 : settings.length();
    }

    private JSONArray saveSetting(JSONArray serverSettings) throws JSONException {
        for (int i = 0; i < serverSettings.length(); i++) {

            JSONObject jsonObject = serverSettings.getJSONObject(i);
            Setting characteristic = new Setting();
            characteristic.setKey(jsonObject.getString("identifier"));
            characteristic.setValue(jsonObject.getString("settings"));
            characteristic.setSyncStatus(SyncStatus.SYNCED.name());

            AncApplication.getInstance().getContext().allSettings().put(SETTINGS_LAST_SYNC_FROM_SERVER_TIMESTAMP, characteristic.getVersion());
            AncApplication.getInstance().getContext().allSettings().putSetting(characteristic);


        }
        return serverSettings;
    }

    private JSONArray pullSettingsFromServer() throws JSONException {

        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }

        String url = baseUrl + SettingsSyncIntentService.SETTINGS_URL + "?serverVersion=" + AncPreferenceHelper.getInstance(applicationContext).getLastSettingsSyncFromServerTimeStamp();

        Log.i(TAG, "URL: " + url);

        if (httpAgent == null) {
            logError(url + " http agent is null");
            return null;
        }

        Response resp = httpAgent.fetchWithCredentials(url, AncApplication.getInstance().getContext().userService().getAllSharedPreferences().fetchRegisteredANM(), AncApplication.getInstance().getContext().allSettings().fetchANMPassword());

        if (resp.isFailure()) {
            logError(url + " not returned data");
            return null;
        }
        return new JSONArray((String) resp.payload());
    }

    private JSONArray pushSettingsToServer() throws JSONException {

        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }

        String url = MessageFormat.format("{0}/{1}", baseUrl, SettingsSyncIntentService.SETTINGS_URL);
        Log.i(TAG, "URL: " + url);

        if (httpAgent == null) {
            logError(url + " http agent is null");
            return null;
        }


        Response<String> response = httpAgent.postWithJsonResponse(url, createSettingsConfigurationPayload());


        return new JSONArray(response.payload());
    }

    private String createSettingsConfigurationPayload() throws JSONException {


        JSONObject siteSettingsPayload = new JSONObject();

        JSONArray settingsArray = new JSONArray();

        List<Setting> unsyncedSettings = AncApplication.getInstance().getContext().allSettings().getUnsyncedSettings();


        for (int i = 0; i < unsyncedSettings.size(); i++) {

            SyncableJSONObject settingsWrapper = new SyncableJSONObject();

            settingsWrapper.put("settings", new JSONArray(unsyncedSettings.get(i).getValue()));

            settingsWrapper.put("identifier", unsyncedSettings.get(i).getKey());

            settingsWrapper.put("version", unsyncedSettings.get(i).getVersion());

            settingsArray.put(settingsWrapper);
        }


        siteSettingsPayload.put(Constants.KEY.SETTING_CONFIGURATIONS, settingsArray);

        return siteSettingsPayload.toString();
    }

}

