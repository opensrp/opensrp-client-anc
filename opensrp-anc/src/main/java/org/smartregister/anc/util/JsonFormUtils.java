package org.smartregister.anc.util;

import android.content.Context;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.util.FormUtils;

/**
 * Created by keyman on 27/06/2018.
 */
public class JsonFormUtils extends org.smartregister.util.JsonFormUtils {
    private static final String TAG = JsonFormUtils.class.getCanonicalName();


    public static JSONObject getFormAsJson(Context context,
                                           String formName, String entityId,
                                           String currentLocationId) throws Exception {
        JSONObject form = FormUtils.getInstance(context).getFormJson(formName);
        if (form != null) {
            form.getJSONObject("metadata").put("encounter_location", currentLocationId);

            if (Constants.JSON_FORM.ANC_REGISTRATION.equals(formName)) {
                if (StringUtils.isNotBlank(entityId)) {
                    entityId = entityId.replace("-", "");
                }

                // Inject opensrp id into the form
                JSONObject stepOne = form.getJSONObject(JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString(JsonFormUtils.KEY)
                            .equalsIgnoreCase(DBConstants.KEY.OPENSRP_ID)) {
                        jsonObject.remove(JsonFormUtils.VALUE);
                        jsonObject.put(JsonFormUtils.VALUE, entityId);
                    }
                }
            } else if (Constants.JSON_FORM.ANC_REGISTRATION.equals(formName)) {
                if (StringUtils.isNotBlank(entityId)) {
                    // Inject entity id into the remove form
                    form.remove(JsonFormUtils.ENTITY_ID);
                    form.put(JsonFormUtils.ENTITY_ID, entityId);
                }
            } else {
                Log.w(TAG, "Unsupported form requested for launch " + formName);
            }
            Log.d(TAG, "form is " + form.toString());
            return form;
        }
        return null;
    }


}
