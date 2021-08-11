package org.smartregister.anc.library.model;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.util.ANCJsonFormUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;

import java.util.Map;

public abstract class BaseContactModel {
    protected String extractPatientName(Map<String, String> womanDetails) {
        String firstName = extractValue(womanDetails, DBConstantsUtils.KeyUtils.FIRST_NAME);
        String lastName = extractValue(womanDetails, DBConstantsUtils.KeyUtils.LAST_NAME);

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
        JSONObject form = new com.vijay.jsonwizard.utils.FormUtils().getFormJsonFromRepositoryOrAssets(AncLibrary.getInstance().getApplicationContext(), formName);
        if (form == null) {
            return null;
        }
        return ANCJsonFormUtils.getFormAsJson(form, formName, entityId, currentLocationId);
    }
}