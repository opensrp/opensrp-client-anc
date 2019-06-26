package org.smartregister.anc.library.interactor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.anc.library.application.BaseAncApplication;
import org.smartregister.anc.library.contract.SiteCharacteristicsContract;
import org.smartregister.anc.library.util.Constants;
import org.smartregister.domain.Setting;
import org.smartregister.domain.SyncStatus;
import org.smartregister.repository.AllSettings;

import java.util.Map;

/**
 * Created by ndegwamartin on 13/07/2018.
 */
public class CharacteristicsInteractor implements SiteCharacteristicsContract.Interactor {

    @Override
    public void saveSiteCharacteristics(Map<String, String> siteCharacteristicsSettingsMap) throws JSONException {

        JSONArray localSettings;
        Setting characteristic = getAllSettingsRepo().getSetting(Constants.PREF_KEY.SITE_CHARACTERISTICS);


        JSONObject settingObject = characteristic != null ? new JSONObject(characteristic.getValue()) : null;
        if (settingObject != null) {
            localSettings = settingObject.getJSONArray(AllConstants.SETTINGS);

            if (localSettings != null) {

                for (int i = 0; i < localSettings.length(); i++) {
                    JSONObject localSetting = localSettings.getJSONObject(i);

                    //updating by java object reference
                    localSetting.put(Constants.KEY.VALUE,
                            "1".equals(siteCharacteristicsSettingsMap.get(localSetting.getString(Constants.KEY.KEY))));

                }

            }

            settingObject.put(AllConstants.SETTINGS, localSettings);
            characteristic.setValue(settingObject.toString());
            characteristic
                    .setKey(Constants.PREF_KEY.SITE_CHARACTERISTICS); //We know only site characteristics are being saved
            // at this time
            characteristic.setSyncStatus(SyncStatus.PENDING.name());

            getAllSettingsRepo().putSetting(characteristic);

            BaseAncApplication.getInstance().populateGlobalSettings();//Refresh global settings
        }
    }

    protected AllSettings getAllSettingsRepo() {
        return BaseAncApplication.getInstance().getContext().allSettings();
    }
}
