package org.smartregister.anc.view;

import android.util.Log;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.GenericPopupDialog;
import com.vijay.jsonwizard.utils.SecondaryValueModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.model.AccordionValuesModel;

import java.util.ArrayList;
import java.util.Map;

public class AncGenericDialogPopup extends GenericPopupDialog {
    private String TAG = this.getClass().getSimpleName();
    private static AncGenericDialogPopup ancGenericDialogPopup = new AncGenericDialogPopup();

    public static AncGenericDialogPopup getInstance() {
        return ancGenericDialogPopup;
    }

    /**
     * Finds the actual widget to be updated and secondary values added on
     *
     * @param jsonObject
     * @param childKey
     * @return
     */
    @Override
    protected JSONObject getJsonObjectToUpdate(JSONObject jsonObject, String childKey) {
        JSONObject item = new JSONObject();
        try {
            if (jsonObject != null && jsonObject.has(JsonFormConstants.TYPE)) {
                if ((jsonObject.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.NATIVE_RADIO_BUTTON) || jsonObject.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.NATIVE_RADIO_BUTTON)) && childKey != null) {
                    JSONArray options = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                    if (options != null) {
                        for (int i = 0; i < options.length(); i++) {
                            JSONObject childItem = options.getJSONObject(i);
                            if (childItem != null && childItem.has(JsonFormConstants.KEY) && childKey.equals(childItem.getString(JsonFormConstants.KEY))) {
                                item = childItem;
                            }
                        }
                    }
                } else {
                    item = jsonObject;
                }
            } else {
                item = jsonObject;
            }
        } catch (Exception e) {
            Log.i(TAG, Log.getStackTraceString(e));
        }

        return item;
    }


    /**
     * Adding the secondary values on to the specific json widget
     *
     * @param item
     * @param secondaryValueModel
     */
    @Override
    protected void addSecondaryValues(JSONObject item, Map<String, SecondaryValueModel> secondaryValueModel) {
        JSONObject valueObject;
        JSONArray secondaryValuesArray = new JSONArray();
        SecondaryValueModel secondaryValue;
        for (Object object : secondaryValueModel.entrySet()) {
            Map.Entry pair = (Map.Entry) object;
            secondaryValue = (SecondaryValueModel) pair.getValue();
            valueObject = createSecondaryValueObject(secondaryValue);
            secondaryValuesArray.put(valueObject);
        }
        try {
            item.put(JsonFormConstants.VALUE, secondaryValuesArray);
            setNewSelectedValues(secondaryValuesArray);
        } catch (Exception e) {
            Log.i(TAG, Log.getStackTraceString(e));
        }
    }

    @Override
    protected void createSecondaryValuesMap() {
        JSONObject jsonObject;
        if (getSecondaryValues() != null) {
            for (int i = 0; i < getSecondaryValues().length(); i++) {
                try {
                    jsonObject = getSecondaryValues().getJSONObject(i);
                    String key = jsonObject.getString(JsonFormConstants.KEY);
                    String type = jsonObject.getString(JsonFormConstants.TYPE);
                    String label = jsonObject.getString(JsonFormConstants.LABEL);
                    JSONArray values = jsonObject.getJSONArray(JsonFormConstants.VALUES);
                    getSecondaryValuesMap().put(key, new AccordionValuesModel(key, type, label, values));
                    setPopAssignedValue(getSecondaryValuesMap());
                } catch (JSONException e) {
                    Log.i(TAG, Log.getStackTraceString(e));
                }
            }
        }
    }

    /**
     * Creates the secondary values objects
     *
     * @param value
     * @return
     */
    @Override
    protected JSONObject createSecondaryValueObject(SecondaryValueModel value) {
        JSONObject jsonObject = new JSONObject();
        try {
            String key = value.getKey();
            String type = value.getType();
            JSONArray values = value.getValues();

            jsonObject.put(JsonFormConstants.KEY, key);
            jsonObject.put(JsonFormConstants.TYPE, type);
            jsonObject.put(JsonFormConstants.VALUES, values);
        } catch (Exception e) {
            Log.i(TAG, Log.getStackTraceString(e));

        }
        return jsonObject;
    }

    @Override
    protected void createSecondaryValues(String key, String type, String value) {
        JSONArray values = new JSONArray();
        values.put(value);
        if (type != null && type.equals(JsonFormConstants.CHECK_BOX)) {
            if (getPopAssignedValue() != null && getPopAssignedValue().containsKey(key)) {
                SecondaryValueModel valueModel = getPopAssignedValue().get(key);
                if (valueModel != null) {
                    JSONArray jsonArray = valueModel.getValues();
                    if (!checkSimilarity(jsonArray, value)) {
                        jsonArray.put(value);
                    }

                    valueModel.setValues(removeUnselectedItems(jsonArray, value));
                }
            } else {
                if (getPopAssignedValue() != null) {
                    getPopAssignedValue().put(key, new SecondaryValueModel(key, type, values));
                }
            }
        } else {
            getPopAssignedValue().put(key, new SecondaryValueModel(key, type, values));
        }
    }

    @Override
    protected boolean checkSimilarity(JSONArray values, String value) {
        boolean same = false;
        try {
            for (int i = 0; i < values.length(); i++) {
                String currentValue = values.getString(i);
                if (currentValue.equals(value)) {
                    same = true;
                    break;
                }
            }
        } catch (Exception e) {
            Log.i(TAG, Log.getStackTraceString(e));
        }

        return same;
    }

    @Override
    protected JSONArray removeUnselectedItems(JSONArray jsonArray, String currentValue) {
        JSONArray values;
        ArrayList<String> list = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getString(i));
            }

            for (int k = 0; k < list.size(); k++) {
                String value = list.get(k);
                String[] splitValues = value.split(":");
                String[] currentValues = currentValue.split(":");
                if (splitValues.length == 3 && currentValues.length == 3 && splitValues[0].equals(currentValues[0]) && splitValues[1].equals(currentValues[1]) && currentValues[2].equals("false")) {
                    list.remove(k);
                }
            }
        } catch (Exception e) {
            Log.i(TAG, Log.getStackTraceString(e));
        }

        values = new JSONArray(list);
        return values;
    }
}
