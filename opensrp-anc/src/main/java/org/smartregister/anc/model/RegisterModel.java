package org.smartregister.anc.model;

import android.util.Log;
import android.util.Pair;
import org.apache.commons.lang3.StringUtils;
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

    private AllSharedPreferences allSharedPreferences;

    private FormUtils formUtils;

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
        return JsonFormUtils.processRegistrationForm(getAllSharedPreferences(), jsonString);
    }

    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception {
        JSONObject form = getFormUtils().getFormJson(formName);
        if (form == null) {
            return null;
        }
        return JsonFormUtils.getFormAsJson(form, formName, entityId, currentLocationId);
    }
    
    private FormUtils getFormUtils() {
        if (formUtils == null) {
            try {
                formUtils = FormUtils.getInstance(AncApplication.getInstance().getApplicationContext());
            } catch (Exception e) {
                Log.e(RegisterModel.class.getCanonicalName(), e.getMessage(), e);
            }
        }
        return formUtils;
    }

    public void setFormUtils(FormUtils formUtils) {
        this.formUtils = formUtils;
    }

    public AllSharedPreferences getAllSharedPreferences() {
        if (allSharedPreferences == null) {
            allSharedPreferences = AncApplication.getInstance().getContext().allSharedPreferences();
        }
        return allSharedPreferences;
    }

    public void setAllSharedPreferences(AllSharedPreferences allSharedPreferences) {
        this.allSharedPreferences = allSharedPreferences;
    }
	
	@Override
	public   String getInitials() {
		String initials = null;
		String preferredName = getPrefferedName();
		
		if (StringUtils.isNotBlank(preferredName)) {
			String[] preferredNameArray = preferredName.split(" ");
			initials = "";
			if (preferredNameArray.length > 1) {
				initials = String.valueOf(preferredNameArray[0].charAt(0)) + String.valueOf(preferredNameArray[1].charAt(0));
			} else if (preferredNameArray.length == 1) {
				initials = String.valueOf(preferredNameArray[0].charAt(0));
			}
		}
		return initials;
	}
	
	private  String getPrefferedName() {
		if (getAllSharedPreferences() == null) {
			return null;
		}
		
		return getAllSharedPreferences().getANMPreferredName(getAllSharedPreferences().fetchRegisteredANM());
	}
}
