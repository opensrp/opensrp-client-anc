package org.smartregister.anc.library.model;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.anc.library.util.JsonFormUtils;
import org.smartregister.util.FormUtils;

import java.util.Map;

public abstract class BaseContactModel {
    private FormUtils formUtils;

    protected String extractPatientName(Map<String, String> womanDetails) {
        String firstName = extractValue(womanDetails, DBConstantsUtils.KEY_UTILS.FIRST_NAME);
        String lastName = extractValue(womanDetails, DBConstantsUtils.KEY_UTILS.LAST_NAME);

        if (StringUtils.isBlank(firstName) && StringUtils.isBlank(lastName)) {
            return "";
        } else if (StringUtils.isBlank(firstName)) {
            return lastName.trim();
        } else if (StringUtils.isBlank(lastName)) {
            return firstName.trim();
        } else {
            return firstName.trim() + " " + lastName.trim();
        }

    }

    private String extractValue(Map<String, String> details, String key) {
        if (details == null || details.isEmpty() || StringUtils.isBlank(key)) {
            return "";
        }

        return details.get(key);
    }

    protected JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception {
        JSONObject form = getFormUtils().getFormJson(formName);
        if (form == null) {
            return null;
        }
        return JsonFormUtils.getFormAsJson(form, formName, entityId, currentLocationId);
    }

    private FormUtils getFormUtils() {
        if (formUtils == null) {
            try {
                formUtils = FormUtils.getInstance(AncLibrary.getInstance().getApplicationContext());
            } catch (Exception e) {
                Log.e(RegisterModel.class.getCanonicalName(), e.getMessage(), e);
            }
        }
        return formUtils;
    }
}
