package org.smartregister.anc.library.model;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.contract.RegisterFragmentContract;
import org.smartregister.anc.library.cursor.AdvancedMatrixCursor;
import org.smartregister.anc.library.util.ConfigHelperUtils;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.clientandeventmodel.DateUtil;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.View;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.smartregister.anc.library.util.ConstantsUtils.GLOBAL_IDENTIFIER;

/**
 * Created by keyman on 12/07/2018.
 */
public class RegisterFragmentModel implements RegisterFragmentContract.Model {

    @Override
    public RegisterConfiguration defaultRegisterConfiguration() {
        return ConfigHelperUtils.defaultRegisterConfiguration(AncLibrary.getInstance().getApplicationContext());
    }

    @Override
    public ViewConfiguration getViewConfiguration(String viewConfigurationIdentifier) {
        return ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper()
                .getViewConfiguration(viewConfigurationIdentifier);
    }

    @Override
    public Set<View> getRegisterActiveColumns(String viewConfigurationIdentifier) {
        return ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper()
                .getRegisterActiveColumns(viewConfigurationIdentifier);
    }

    @Override
    public String countSelect(String tableName, String mainCondition) {
        SmartRegisterQueryBuilder countQueryBuilder = new SmartRegisterQueryBuilder();
        countQueryBuilder.SelectInitiateMainTableCounts(tableName);
        return countQueryBuilder.mainCondition(mainCondition);
    }

    @Override
    public String mainSelect(String tableName, String mainCondition) {
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        String[] columns = new String[]{tableName + ".relationalid", tableName + "." + DBConstantsUtils.KEY_UTILS.LAST_INTERACTED_WITH,
                tableName + "." + DBConstantsUtils.KEY_UTILS.BASE_ENTITY_ID, tableName + "." + DBConstantsUtils.KEY_UTILS.FIRST_NAME,
                tableName + "." + DBConstantsUtils.KEY_UTILS.LAST_NAME, tableName + "." + DBConstantsUtils.KEY_UTILS.ANC_ID,
                tableName + "." + DBConstantsUtils.KEY_UTILS.DOB, tableName + "." + DBConstantsUtils.KEY_UTILS.PHONE_NUMBER,
                tableName + "." + DBConstantsUtils.KEY_UTILS.ALT_NAME, tableName + "." + DBConstantsUtils.KEY_UTILS.DATE_REMOVED,
                tableName + "." + DBConstantsUtils.KEY_UTILS.EDD, tableName + "." + DBConstantsUtils.KEY_UTILS.RED_FLAG_COUNT,
                tableName + "." + DBConstantsUtils.KEY_UTILS.YELLOW_FLAG_COUNT, tableName + "." + DBConstantsUtils.KEY_UTILS.CONTACT_STATUS,
                tableName + "." + DBConstantsUtils.KEY_UTILS.NEXT_CONTACT, tableName + "." + DBConstantsUtils.KEY_UTILS.NEXT_CONTACT_DATE,
                tableName + "." + DBConstantsUtils.KEY_UTILS.LAST_CONTACT_RECORD_DATE};
        queryBUilder.SelectInitiateMainTable(tableName, columns);
        return queryBUilder.mainCondition(mainCondition);
    }

    @Override
    public String getFilterText(List<Field> list, String filterTitle) {
        List<Field> filterList = list;
        if (filterList == null) {
            filterList = new ArrayList<>();
        }

        String filter = filterTitle;
        if (filter == null) {
            filter = "";
        }
        return "<font color=#727272>" + filter + "</font> <font color=#f0ab41>(" + filterList.size() + ")</font>";
    }

    @Override
    public String getSortText(Field sortField) {
        String sortText = "";
        if (sortField != null) {
            if (StringUtils.isNotBlank(sortField.getDisplayName())) {
                sortText = "(Sort: " + sortField.getDisplayName() + ")";
            } else if (StringUtils.isNotBlank(sortField.getDbAlias())) {
                sortText = "(Sort: " + sortField.getDbAlias() + ")";
            }
        }
        return sortText;
    }

    @Override
    public Map<String, String> createEditMap(String ancId) {
        Map<String, String> editMap = new LinkedHashMap<>();
        if (StringUtils.isNotBlank(ancId)) {

            editMap.put(GLOBAL_IDENTIFIER, ConstantsUtils.IDENTIFIER_UTILS.ANC_ID + ":" + ancId);
        }
        return editMap;
    }

    @Override
    public AdvancedMatrixCursor createMatrixCursor(Response<String> response) {
        String[] columns = new String[]{"_id", "relationalid", DBConstantsUtils.KEY_UTILS.FIRST_NAME, DBConstantsUtils.KEY_UTILS.LAST_NAME,
                DBConstantsUtils.KEY_UTILS.DOB, DBConstantsUtils.KEY_UTILS.ANC_ID, DBConstantsUtils.KEY_UTILS.PHONE_NUMBER, DBConstantsUtils.KEY_UTILS.ALT_NAME};
        AdvancedMatrixCursor matrixCursor = new AdvancedMatrixCursor(columns);

        if (response == null || response.isFailure() || StringUtils.isBlank(response.payload())) {
            return matrixCursor;
        }

        JSONArray jsonArray = getJsonArray(response);
        if (jsonArray != null) {

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject client = getJsonObject(jsonArray, i);
                String entityId;
                String firstName;
                String lastName;
                String dob;
                String ancId;
                String phoneNumber;
                String altContactName;
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

                ancId = getJsonString(getJsonObject(client, "identifiers"), ConstantsUtils.IDENTIFIER_UTILS.ANC_ID);
                if (StringUtils.isNotBlank(ancId)) {
                    ancId = ancId.replace("-", "");
                }

                phoneNumber = getJsonString(getJsonObject(client, "attributes"), "phone_number");

                altContactName = getJsonString(getJsonObject(client, "attributes"), "alt_name");


                matrixCursor
                        .addRow(new Object[]{entityId, null, firstName, lastName, dob, ancId, phoneNumber, altContactName});
            }
        }
        return matrixCursor;
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

    private String getJsonString(JSONObject jsonObject, String field) {
        try {
            if (jsonObject != null && jsonObject.has(field)) {
                String string = jsonObject.getString(field);
                if (StringUtils.isBlank(string)) {
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

    @Override
    public JSONArray getJsonArray(Response<String> response) {
        try {
            if (response.status().equals(ResponseStatus.success)) {
                return new JSONArray(response.payload());
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), "", e);
        }
        return null;
    }
}
