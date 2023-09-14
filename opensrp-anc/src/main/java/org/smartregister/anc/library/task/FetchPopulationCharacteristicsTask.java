package org.smartregister.anc.library.task;


import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.account.AccountAuthenticatorXml;
import org.smartregister.account.AccountHelper;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.contract.PopulationCharacteristicsContract;
import org.smartregister.anc.library.util.AppExecutors;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.domain.Response;
import org.smartregister.domain.ServerSetting;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.service.HTTPAgent;
import org.smartregister.sync.helper.ServerSettingsHelper;
import org.smartregister.sync.intent.BaseSyncIntentService;
import org.smartregister.sync.intent.SettingsSyncIntentService;

import java.util.List;

/**
 * Created by ndegwamartin on 28/08/2018.
 */
public class FetchPopulationCharacteristicsTask {

    private final PopulationCharacteristicsContract.Presenter presenter;
    private AppExecutors appExecutorService;

    public FetchPopulationCharacteristicsTask(PopulationCharacteristicsContract.Presenter presenter) {
        this.presenter = presenter;
    }

    public void execute() {
        appExecutorService = new AppExecutors();
        appExecutorService.diskIO().execute(() -> {
            List<ServerSetting> result = this.getServerSettingsService();
            if(result.size() == 0) {
                // get it from the api
                AllSharedPreferences allSharedPreferences = AncLibrary.getInstance().getContext().allSharedPreferences();
                String providerId = allSharedPreferences.fetchRegisteredANM();
                String locale = getLocaleFromPresenter();
                BaseSyncIntentService.RequestParamsBuilder builder = new BaseSyncIntentService.RequestParamsBuilder()
                        .addParam(ConstantsUtils.SettingsSyncParamsUtils.LOCATION_ID, allSharedPreferences.fetchDefaultLocalityId(providerId))
                        .addParam(ConstantsUtils.SettingsSyncParamsUtils.IDENTIFIER, ConstantsUtils.PrefKeyUtils.POPULATION_CHARACTERISTICS+locale);
                String syncParams = builder.toString();
                BaseSyncIntentService.RequestParamsBuilder baseSyncIntentServiceBldr = new BaseSyncIntentService.RequestParamsBuilder().addParam(AllConstants.SERVER_VERSION, "0").addParam(AllConstants.RESOLVE, true);
                String url = SettingsSyncIntentService.SETTINGS_URL + "?" + syncParams + "&" + baseSyncIntentServiceBldr.toString();

                AccountAuthenticatorXml authenticatorXml = CoreLibrary.getInstance().getAccountAuthenticatorXml();
                String accessToken = AccountHelper.getCachedOAuthToken(allSharedPreferences.fetchRegisteredANM(), authenticatorXml.getAccountType(), AccountHelper.TOKEN_TYPE.PROVIDER);
                HTTPAgent httpAgent = AncLibrary.getInstance().getContext().httpAgent();
                Response<String> settingsResponse = httpAgent.fetchWithCredentials(AncLibrary.getInstance().getContext().configuration().dristhiBaseURL()+url, accessToken);
                try {
                    JSONArray settingsConfigs = new JSONArray((String) settingsResponse.payload());
                    if (settingsConfigs.get(0) != null) {
                        JSONObject jsonObject = (JSONObject) settingsConfigs.get(0);
                        JSONArray settingArray = jsonObject.getJSONArray(AllConstants.SETTINGS);
                        Gson gson = new Gson();
                        result = gson.fromJson(settingArray.toString(), new TypeToken<List<ServerSetting>>() {
                        }.getType());

                    }


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            List<ServerSetting> finalResult = result;
            appExecutorService.mainThread().execute(() -> this.renderViewOnPostExec(finalResult));
        });
    }


    public List<ServerSetting> getServerSettingsService() {
        String locale = getLocaleFromPresenter();
        ServerSettingsHelper helper = new ServerSettingsHelper(ConstantsUtils.PrefKeyUtils.POPULATION_CHARACTERISTICS+locale);
        return helper.getServerSettings();
    }

    @NonNull
    private String getLocaleFromPresenter() {
        return (!StringUtils.isAllEmpty(presenter.getLocale())&& !presenter.getLocale().equals("en")) ? "-"+presenter.getLocale() : "";
    }

    protected void renderViewOnPostExec(final List<ServerSetting> result) {
        presenter.renderView(result);
    }
}
