package org.smartregister.anc.model;

import android.util.Pair;

import org.json.JSONObject;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.RegisterContract;
import org.smartregister.anc.helper.LocationHelper;
import org.smartregister.anc.util.JsonFormUtils;
import org.smartregister.anc.util.Utils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.FormUtils;

import java.util.List;
import java.util.Map;

public class RegisterModel implements RegisterContract.Model {
    @Override
    public void registerViewConfigurations(List<String> viewIdentifiers) {
        ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().registerViewConfigurations(viewIdentifiers);
    }

    @Override
    public void unregisterViewConfiguration(List<String> viewIdentifiers) {
        ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().unregisterViewConfiguration(viewIdentifiers);
    }

    @Override
    public void saveLanguage(String language) {
        Map<String, String> langs = getAvailableLanguagesMap();
        Utils.saveLanguage(Utils.getKeyByValue(langs, language));
    }

    private Map<String, String> getAvailableLanguagesMap() {
        return AncApplication.getJsonSpecHelper().getAvailableLanguagesMap();
    }

    @Override
    public String getLocationId(String locationName) {
        return LocationHelper.getInstance().getOpenMrsLocationId(locationName);
    }

    @Override
    public Pair<Client, Event> processRegistration(String jsonString) {
        AllSharedPreferences allSharedPreferences = AncApplication.getInstance().getContext().allSharedPreferences();
        return JsonFormUtils.processRegistration(jsonString, allSharedPreferences.fetchRegisteredANM());
    }

    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception {
        JSONObject form = getFormUtils().getFormJson(formName);
        if (form == null) {
            return null;
        }
        return JsonFormUtils.getFormAsJson(form, formName, entityId, currentLocationId);
    }

    private FormUtils getFormUtils() throws Exception {
        return FormUtils.getInstance(AncApplication.getInstance().getApplicationContext());
    }
}
