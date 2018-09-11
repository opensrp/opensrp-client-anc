package org.smartregister.anc.model;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.ContactContract;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.JsonFormUtils;
import org.smartregister.util.FormUtils;

import java.util.Map;

public class ContactModel implements ContactContract.Model {

    private FormUtils formUtils;

    @Override
    public String extractPatientName(Map<String, String> womanDetails) {
        String firstName = extractValue(womanDetails, DBConstants.KEY.FIRST_NAME);
        String lastName = extractValue(womanDetails, DBConstants.KEY.LAST_NAME);

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

    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception {
        JSONObject form = getFormUtils().getFormJson(formName);
        if (form == null) {
            return null;
        }
        return JsonFormUtils.getFormAsJson(form, formName, entityId, currentLocationId);
    }

    private String extractValue(Map<String, String> details, String key) {
        if (details == null || details.isEmpty() || StringUtils.isBlank(key)) {
            return "";
        }

        return details.get(key);
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

}
