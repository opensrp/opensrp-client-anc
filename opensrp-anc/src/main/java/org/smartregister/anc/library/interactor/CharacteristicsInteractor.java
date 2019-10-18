package org.smartregister.anc.library.interactor;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.contract.SiteCharacteristicsContract;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.SiteCharacteristicsFormUtils;
import org.smartregister.domain.Setting;
import org.smartregister.domain.SyncStatus;
import org.smartregister.repository.AllSettings;
import org.smartregister.util.Utils;

import java.util.Map;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 13/07/2018.
 */
public class CharacteristicsInteractor implements SiteCharacteristicsContract.Interactor {

    @Override
    public void saveSiteCharacteristics(Map<String, String> siteCharacteristicsSettingsMap) throws JSONException {

        JSONArray localSettings;
        JSONObject settingObject;
        Context context = AncLibrary.getInstance().getApplicationContext();
        Setting characteristic = getAllSettingsRepo().getSetting(ConstantsUtils.PrefKeyUtils.SITE_CHARACTERISTICS);
        boolean canSaveInitialSetting = getPropertyForInitialSaveAction(context);
        if (characteristic == null) {

            if (canSaveInitialSetting) {
                try {
                    settingObject = SiteCharacteristicsFormUtils.structureFormForRequest(context);
                    characteristic = new Setting();
                } catch (Exception e) {
                    Timber.e(e);
                    return;
                }
            } else {
                return;
            }
        } else {
            settingObject = new JSONObject(characteristic.getValue());
        }
        localSettings = settingObject.has(AllConstants.SETTINGS) ? settingObject.getJSONArray(AllConstants.SETTINGS) : null;
        if (localSettings != null) {
            for (int i = 0; i < localSettings.length(); i++) {
                JSONObject localSetting = localSettings.getJSONObject(i);
                localSetting.put(ConstantsUtils.KeyUtils.VALUE,
                        "1".equals(siteCharacteristicsSettingsMap.get(localSetting.getString(ConstantsUtils.KeyUtils.KEY))));
            }
        }

        settingObject.put(AllConstants.SETTINGS, localSettings);
        characteristic.setValue(settingObject.toString());
        characteristic.setKey(ConstantsUtils.PrefKeyUtils.SITE_CHARACTERISTICS);
        characteristic.setSyncStatus(SyncStatus.PENDING.name());
        getAllSettingsRepo().putSetting(characteristic);
        AncLibrary.getInstance().populateGlobalSettings();
    }

    protected AllSettings getAllSettingsRepo() {
        return AncLibrary.getInstance().getContext().allSettings();
    }

    private Boolean getPropertyForInitialSaveAction(Context context) {
        String value = Utils.getProperties(context).getProperty(ConstantsUtils.Properties.CAN_SAVE_SITE_INITIAL_SETTING, "false");
        return Boolean.valueOf(value);
    }
}
