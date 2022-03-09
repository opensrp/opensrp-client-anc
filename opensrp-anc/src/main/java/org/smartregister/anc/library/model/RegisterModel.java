package org.smartregister.anc.library.model;

import androidx.annotation.VisibleForTesting;
import androidx.core.util.Pair;

import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONObject;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.contract.RegisterContract;
import org.smartregister.anc.library.util.ANCJsonFormUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.location.helper.LocationHelper;

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
        return AncLibrary.getInstance().getJsonSpecHelper().getAvailableLanguagesMap();
    }

    @Override
    public String getLocationId(String locationName) {
        return LocationHelper.getInstance().getOpenMrsLocationId(locationName);
    }

    @Override
    public Pair<Client, Event> processRegistration(String jsonString) {
        return ANCJsonFormUtils.processRegistrationForm(Utils.getAllSharedPreferences(), jsonString);
    }

    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception {
        JSONObject form = getFormUtils().getFormJsonFromRepositoryOrAssets(AncLibrary.getInstance().getApplicationContext(), formName);
        if (form == null) {
            return null;
        }
        return ANCJsonFormUtils.getFormAsJson(form, formName, entityId, currentLocationId);
    }

    @Override
    public String getInitials() {
        return Utils.getUserInitials();
    }

    @VisibleForTesting
    protected FormUtils getFormUtils() {
        return new FormUtils();
    }
}
