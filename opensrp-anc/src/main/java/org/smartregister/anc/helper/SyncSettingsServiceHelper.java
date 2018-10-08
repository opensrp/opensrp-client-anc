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

    private HTTPAgent httpAgent;
    private String baseUrl;
    private AncPreferenceHelper preferenceHelper;
    public final static String SETTINGS_LAST_SYNC_FROM_SERVER_TIMESTAMP = "SETTINGS_LAST_SYNC_FROM_SERVER_TIMESTAMP";
    public final static String SETTINGS_LAST_SYNC_TO_SERVER_TIMESTAMP = "SETTINGS_LAST_SYNC_TO_SERVER_TIMESTAMP";
    private String username;
    private String password;

    public SyncSettingsServiceHelper(Context applicationContext, String baseUrl, HTTPAgent httpAgent) {

        this.httpAgent = httpAgent;
        this.baseUrl = baseUrl;
        this.preferenceHelper = AncPreferenceHelper.getInstance(applicationContext);
    }


    public int processIntent() throws Exception {

        try {
            JSONObject response = pushSettingsToServer();
            if (response != null && response.has(Constants.KEY.VALIDATED_RECORDS)) {
                JSONArray records = response.getJSONArray(Constants.KEY.VALIDATED_RECORDS);
                Setting setting;
                for (int i = 0; i < records.length(); i++) {
                    setting = AncApplication.getInstance().getContext().allSettings().getSetting(records.getString(0));
                    setting.setSyncStatus(SyncStatus.SYNCED.name());
                    AncApplication.getInstance().getContext().allSettings().putSetting(setting);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        JSONArray settings = pullSettingsFromServer();

        if (settings != null && settings.length() > 0) {
            settings = saveSetting(settings);
            updateLastSettingServerSyncTimetamp();
        }

        return settings == null ? 0 : settings.length();
    }

    public JSONArray pullSettingsFromServer() throws JSONException {

        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }

        String url = baseUrl + SettingsSyncIntentService.SETTINGS_URL + "?serverVersion=0";

        Log.i(TAG, "URL: " + url);

        if (httpAgent == null) {
            logError(url + " http agent is null");
            return null;
        }

        Response resp = httpAgent.fetchWithCredentials(url, getUsername(), getPassword());

        if (resp.isFailure()) {
            logError(url + " not returned data");
            return null;
        }
        return new JSONArray((String) resp.payload());
    }

    private JSONObject pushSettingsToServer() throws JSONException {

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

        JSONObject payload = createSettingsConfigurationPayload();

        if (payload.getJSONArray(Constants.KEY.SETTING_CONFIGURATIONS).length() > 0) {

            preferenceHelper.updateLastSettingsSyncToServerTimeStamp(Calendar.getInstance().getTimeInMillis());

            Response<String> response = httpAgent.postWithJsonResponse(url, payload.toString());

            return new JSONObject(response.payload());

        } else return null;
    }

    private JSONObject createSettingsConfigurationPayload() throws JSONException {


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

        return siteSettingsPayload;
    }

    public String getUsername() {
        return username != null ? username : AncApplication.getInstance().getContext().userService().getAllSharedPreferences().fetchRegisteredANM();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password != null ? password : AncApplication.getInstance().getContext().allSettings().fetchANMPassword();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public JSONArray saveSetting(JSONArray serverSettings) throws JSONException {
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

    public void updateLastSettingServerSyncTimetamp() {

        preferenceHelper.updateLastSettingsSyncFromServerTimeStamp(Calendar.getInstance().getTimeInMillis());
    }
}

