package org.smartregister.anc.model;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.contract.AdvancedSearchContract;
import org.smartregister.anc.cursor.AdvancedMatrixCursor;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.JsonFormUtils;
import org.smartregister.clientandeventmodel.DateUtil;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvancedSearchModel extends RegisterFramentModel implements AdvancedSearchContract.Model {


    private static final String GLOBAL_FIRST_NAME = "firstName";
    private static final String GLOBAL_LAST_NAME = "lastName";
    private static final String GLOBAL_BIRTH_DATE = "birthdate";
    private static final String GLOBAL_ATTRIBUTE = "attribute";
    private static final String GLOBAL_IDENTIFIER = "identifier";
    private static final String ANC_ID = "ANC_ID";
    private static final String PHONE_NUMBER = "phone_number";
    private static final String ALT_CONTACT_NAME = "alt_name";


    @Override
    public Map<String, String> createEditMap(String firstName, String lastName, String ancId, String edd, String dob, String phoneNumber, String alternateContact, boolean isLocal) {
        Map<String, String> editMap = new HashMap<>();
        if (StringUtils.isNotBlank(firstName)) {
            editMap.put(isLocal ? DBConstants.KEY.FIRST_NAME : GLOBAL_FIRST_NAME, firstName);
        }
        if (StringUtils.isNotBlank(lastName)) {
            editMap.put(isLocal ? DBConstants.KEY.LAST_NAME : GLOBAL_LAST_NAME, lastName);
        }
        if (StringUtils.isNotBlank(ancId)) {
            editMap.put(isLocal ? DBConstants.KEY.ANC_ID : GLOBAL_IDENTIFIER, isLocal ? ancId : ANC_ID + ":" + ancId);
        }
        if (StringUtils.isNotBlank(edd)) {
            editMap.put(isLocal ? DBConstants.KEY.EDD : GLOBAL_ATTRIBUTE, isLocal ? edd : "edd:" + edd);
        }
        if (StringUtils.isNotBlank(dob)) {
            editMap.put(isLocal ? DBConstants.KEY.DOB : GLOBAL_BIRTH_DATE, dob);
        }
        if (StringUtils.isNotBlank(phoneNumber)) {
            editMap.put(isLocal ? DBConstants.KEY.PHONE_NUMBER : GLOBAL_ATTRIBUTE, isLocal ? phoneNumber : PHONE_NUMBER + ":" + phoneNumber);
        }
        if (StringUtils.isNotBlank(alternateContact)) {
            editMap.put(isLocal ? DBConstants.KEY.ALT_CONTACT_NAME : GLOBAL_ATTRIBUTE, isLocal ? alternateContact : ALT_CONTACT_NAME + ":" + alternateContact);
        }
        return editMap;
    }

    @Override
    public String createSearchString(String firstName, String lastName, String ancId, String edd, String dob, String phoneNumber, String alternateContact) {
        String searchCriteria = "";

        if (StringUtils.isNotBlank(firstName)) {
            searchCriteria += " First name: " + firstName + ",";
        }
        if (StringUtils.isNotBlank(lastName)) {
            searchCriteria += " Last name: " + lastName + ",";
        }
        if (StringUtils.isNotBlank(ancId)) {
            searchCriteria += " Anc ID: " + ancId + ",";
        }
        if (StringUtils.isNotBlank(edd)) {
            searchCriteria += " Edd: " + edd + ",";
        }
        if (StringUtils.isNotBlank(dob)) {
            searchCriteria += " Dob: " + dob + ",";
        }
        if (StringUtils.isNotBlank(phoneNumber)) {
            searchCriteria += " Mobile phone number: " + phoneNumber + ",";
        }
        if (StringUtils.isNotBlank(alternateContact)) {
            searchCriteria += " Alternate contact name: " + alternateContact + ",";
        }
        return removeLastComma(searchCriteria);
    }

    @Override
    public String getMainConditionString(Map<String, String> editMap) {

        String mainConditionString = "";
        if (editMap == null || editMap.isEmpty()) {
            return mainConditionString;
        }

        for (Map.Entry<String, String> entry : editMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (StringUtils.isBlank(mainConditionString)) {
                mainConditionString += " " + key + " Like '%" + value + "%'";
            } else {
                mainConditionString += " AND " + key + " Like '%" + value + "%'";
            }
        }

        return mainConditionString;

    }

    @Override
    public AdvancedMatrixCursor createMatrixCursor(Response<String> response) {
        String[] columns = new String[]{"_id", "relationalid", DBConstants.KEY.FIRST_NAME, DBConstants.KEY.LAST_NAME, DBConstants.KEY.DOB, DBConstants.KEY.ANC_ID, DBConstants.KEY.PHONE_NUMBER, DBConstants.KEY.ALT_CONTACT_NAME};
        AdvancedMatrixCursor matrixCursor = new AdvancedMatrixCursor(columns);

        JSONArray jsonArray = getJsonArray(response);
        if (jsonArray != null) {

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject client = getJsonObject(jsonArray, i);
                String entityId = "";
                String firstName = "";
                String lastName = "";
                String dob = "";
                String ancId = "";
                String phoneNumber = "";
                String altContactName = "";
                if (client == null) {
                    continue;
                }

                // Skip deceased children
                if (StringUtils.isNotBlank(getJsonString(client, "deathdate"))) {
                    continue;
                }

                entityId = getJsonString(client, "baseEntityId");
                firstName = getJsonString(client, "firstName");
                lastName = getJsonString(client, "lastName");

                dob = getJsonString(client, "birthdate");
                if (StringUtils.isNotBlank(dob) && StringUtils.isNumeric(dob)) {
                    try {
                        Long dobLong = Long.valueOf(dob);
                        Date date = new Date(dobLong);
                        dob = DateUtil.yyyyMMddTHHmmssSSSZ.format(date);
                    } catch (Exception e) {
                        Log.e(getClass().getName(), e.toString(), e);
                    }
                }

                ancId = getJsonString(getJsonObject(client, "identifiers"), JsonFormUtils.ANC_ID);
                if (StringUtils.isNotBlank(ancId)) {
                    ancId = ancId.replace("-", "");
                }

                phoneNumber = getJsonString(getJsonObject(client, "attributes"), "phone_number");

                altContactName = getJsonString(getJsonObject(client, "attributes"), "alt_name");


                matrixCursor.addRow(new Object[]{entityId, null, firstName, lastName, dob, ancId, phoneNumber, altContactName});
            }
        }
        return matrixCursor;
    }

    private String getJsonString(JSONObject jsonObject, String field) {
        try {
            if (jsonObject != null && jsonObject.has(field)) {
                String string = jsonObject.getString(field);
                if (string.equals("null")) {
                    return "";
                } else {
                    return string;
                }
            }
        } catch (JSONException e) {
            Log.e(getClass().getName(), "", e);
        }
        return "";

    }

    private JSONObject getJsonObject(JSONObject jsonObject, String field) {
        try {
            if (jsonObject != null && jsonObject.has(field)) {
                return jsonObject.getJSONObject(field);
            }
        } catch (JSONException e) {
            Log.e(getClass().getName(), "", e);
        }
        return null;

    }

    private JSONObject getJsonObject(JSONArray jsonArray, int position) {
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                return jsonArray.getJSONObject(position);
            }
        } catch (JSONException e) {
            Log.e(getClass().getName(), "", e);
        }
        return null;

    }

    private JSONArray getJsonArray(Response<String> response) {
        try {
            if (response.status().equals(ResponseStatus.success)) {
                return new JSONArray(response.payload());
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), "", e);
        }
        return null;
    }

    private String removeLastComma(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        String s = str.trim();
        if (s.charAt(s.length() - 1) == ',') {
            return s.substring(0, s.length() - 1);
        }
        return s;
    }

}
