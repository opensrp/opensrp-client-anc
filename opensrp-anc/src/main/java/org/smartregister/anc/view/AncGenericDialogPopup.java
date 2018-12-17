package org.smartregister.anc.view;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.GenericPopupDialog;
import com.vijay.jsonwizard.utils.SecondaryValueModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.interactor.ContactJsonFormInteractor;
import org.smartregister.anc.model.AccordionValuesModel;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.ContactJsonFormUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AncGenericDialogPopup extends GenericPopupDialog {
    private String TAG = this.getClass().getSimpleName();
    private static AncGenericDialogPopup ancGenericDialogPopup = new AncGenericDialogPopup();
    private static ContactJsonFormInteractor jsonFormInteractor = ContactJsonFormInteractor.getInstance();
    private Map<String, AccordionValuesModel> popAssignedValue = new HashMap<>();
    private Map<String, AccordionValuesModel> secondaryValuesMap = new HashMap<>();
    private ContactJsonFormUtils formUtils = new ContactJsonFormUtils();

    public static AncGenericDialogPopup getInstance() {
        return ancGenericDialogPopup;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyVariables();
    }

    private void destroyVariables() {
        popAssignedValue = new HashMap<>();
        secondaryValuesMap = new HashMap<>();
    }

    @Override
    protected void initiateViews(ViewGroup dialogView) {
        List<View> listOfViews = new ArrayList<>();
        jsonFormInteractor.fetchFields(listOfViews, getStepName(), getFormFragment(), getSpecifyContent(), getCommonListener(), true);

        LinearLayout genericDialogContent = dialogView.findViewById(
                com.vijay.jsonwizard.R.id.generic_dialog_content);
        for (View view : listOfViews) {
            genericDialogContent.addView(view);
        }
    }

    @Override
    protected void passData() {
        if (getWidgetType().equals(JsonFormConstants.NATIVE_ACCORDION)) {
            onDataPass(popAssignedValue, getParentKey(), getStepName(), getChildKey());
        } else {
            onGenericDataPass(getPopAssignedValue(), getParentKey(), getStepName(), getChildKey());
        }

    }

    /**
     * Receives the generic popup data from Generic Dialog fragment
     *
     * @param selectedValues
     * @param parentKey
     * @param stepName
     * @param childKey
     */
    public void onDataPass(Map<String, AccordionValuesModel> selectedValues, String parentKey, String stepName, String childKey) {
        JSONObject mJSONObject = getJsonApi().getmJSONObject();
        if (mJSONObject != null) {
            JSONObject parentJson = getJsonApi().getStep(stepName);
            JSONArray fields = new JSONArray();
            try {
                if (parentJson.has(JsonFormConstants.SECTIONS) && parentJson.get(JsonFormConstants.SECTIONS) instanceof JSONArray) {
                    JSONArray sections = parentJson.getJSONArray(JsonFormConstants.SECTIONS);
                    for (int i = 0; i < sections.length(); i++) {
                        JSONObject sectionJson = sections.getJSONObject(i);
                        if (sectionJson.has(JsonFormConstants.FIELDS)) {
                            fields = formUtils.concatArray(fields, sectionJson.getJSONArray(JsonFormConstants.FIELDS));
                        }
                    }
                } else if (parentJson.has(JsonFormConstants.FIELDS) && parentJson.get(JsonFormConstants.FIELDS) instanceof JSONArray) {
                    fields = parentJson.getJSONArray(JsonFormConstants.FIELDS);

                }

                if (fields.length() > 0) {
                    for (int i = 0; i < fields.length(); i++) {
                        JSONObject item = fields.getJSONObject(i);
                        if (item != null && item.getString(JsonFormConstants.KEY).equals(parentKey)) {
                            addValues(getJsonObjectToUpdate(item, childKey), selectedValues);
                        }
                    }
                }

                getJsonApi().setmJSONObject(mJSONObject);

            } catch (JSONException e) {
                Log.i(TAG, Log.getStackTraceString(e));
            }
        }
    }

    @Override
    protected void addFormValues(JSONArray jsonArray) {
        if (getWidgetType().equals(JsonFormConstants.NATIVE_ACCORDION)) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject;
                try {
                    jsonObject = jsonArray.getJSONObject(i);
                    String key = jsonObject.getString(JsonFormConstants.KEY);
                    if (secondaryValuesMap != null && secondaryValuesMap.containsKey(key)) {
                        SecondaryValueModel secondaryValueModel = secondaryValuesMap.get(key);
                        String type = secondaryValueModel.getType();
                        if (type != null && (type.equals(JsonFormConstants.CHECK_BOX))) {
                            if (jsonObject.has(JsonFormConstants.OPTIONS_FIELD_NAME)) {
                                JSONArray options = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                                JSONArray values = secondaryValueModel.getValues();
                                setCompoundButtonValues(options, values);
                            }
                        } else {
                            JSONArray values = secondaryValueModel.getValues();
                            if (type != null && (type.equals(JsonFormConstants.NATIVE_RADIO_BUTTON) || type.equals(Constants.ANC_RADIO_BUTTON))) {
                                for (int k = 0; k < values.length(); k++) {
                                    jsonObject.put(JsonFormConstants.VALUE, getValueKey(values.getString(k)));
                                }
                            } else {
                                jsonObject.put(JsonFormConstants.VALUE, setValues(values, type));
                            }
                        }
                    }
                } catch (JSONException e) {
                    Log.i(TAG, Log.getStackTraceString(e));
                }
            }
        } else {
            super.addFormValues(jsonArray);
        }
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
                if ((jsonObject.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.CHECK_BOX) || jsonObject.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.NATIVE_RADIO_BUTTON)) && childKey != null) {
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

    protected void addValues(JSONObject item, Map<String, AccordionValuesModel> secondaryValueModel) {
        JSONObject valueObject;
        JSONArray secondaryValuesArray = new JSONArray();
        AccordionValuesModel secondaryValue;
        for (Object object : secondaryValueModel.entrySet()) {
            Map.Entry pair = (Map.Entry) object;
            secondaryValue = (AccordionValuesModel) pair.getValue();
            valueObject = createSecValues(secondaryValue);
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
        if (getWidgetType().equals(JsonFormConstants.NATIVE_ACCORDION)) {
            if (getSecondaryValues() != null) {
                for (int i = 0; i < getSecondaryValues().length(); i++) {
                    try {
                        jsonObject = getSecondaryValues().getJSONObject(i);
                        String key = jsonObject.getString(JsonFormConstants.KEY);
                        String type = jsonObject.getString(JsonFormConstants.TYPE);
                        String label = jsonObject.getString(JsonFormConstants.LABEL);
                        JSONArray values = jsonObject.getJSONArray(JsonFormConstants.VALUES);
                        secondaryValuesMap.put(key, new AccordionValuesModel(key, type, label, values));
                        popAssignedValue = secondaryValuesMap;
                    } catch (JSONException e) {
                        Log.i(TAG, Log.getStackTraceString(e));
                    }
                }
            }
        } else {
            super.createSecondaryValuesMap();
        }

    }

    private JSONObject createSecValues(AccordionValuesModel valuesModel) {
        JSONObject jsonObject = new JSONObject();
        try {
            String key = valuesModel.getKey();
            String type = valuesModel.getType();
            String label = valuesModel.getLabel();
            JSONArray values = valuesModel.getValues();

            jsonObject.put(JsonFormConstants.KEY, key);
            jsonObject.put(JsonFormConstants.TYPE, type);
            jsonObject.put(JsonFormConstants.LABEL, label);
            jsonObject.put(JsonFormConstants.VALUES, values);
        } catch (Exception e) {
            Log.i(TAG, Log.getStackTraceString(e));

        }
        return jsonObject;
    }

    @Override
    public void addSelectedValues(Map<String, String> newValue) {
        Iterator newValueIterator = newValue.entrySet().iterator();
        String key = "";
        String type = "";
        String iteratorValue = "";
        String value = "";
        while (newValueIterator.hasNext()) {
            Map.Entry pair = (Map.Entry) newValueIterator.next();
            key = String.valueOf(pair.getKey());
            iteratorValue = String.valueOf(pair.getValue());
        }

        String[] widgetValues = getWidgetType(iteratorValue);
        if (widgetValues.length > 1) {
            type = widgetValues[1] + ";" + widgetValues[2];
            value = widgetValues[0];
        }

        createSecondaryValues(key, type, value);

    }


    @Override
    protected void createSecondaryValues(String key, String labelType, String value) {
        JSONArray values = new JSONArray();
        values.put(value);
        String[] string = getWidgetLabel(labelType);
        if (getWidgetType().equals(JsonFormConstants.NATIVE_ACCORDION)) {
            if (string.length > 1) {
                String type = string[0];
                String label = string[1];
                if (type != null && type.equals(JsonFormConstants.CHECK_BOX)) {
                    if (popAssignedValue != null && popAssignedValue.containsKey(key)) {
                        AccordionValuesModel valueModel = popAssignedValue.get(key);
                        if (valueModel != null) {
                            JSONArray jsonArray = valueModel.getValues();
                            if (!checkSimilarity(jsonArray, value)) {
                                jsonArray.put(value);
                            }

                            valueModel.setValues(removeUnselectedItems(jsonArray, value));
                        }
                    } else {
                        if (popAssignedValue != null) {
                            popAssignedValue.put(key, new AccordionValuesModel(key, type, label, values));
                        }
                    }
                } else {
                    popAssignedValue.put(key, new AccordionValuesModel(key, type, label, values));
                }
            }
        } else {
            super.createSecondaryValues(key, labelType, value);
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

    private String[] getWidgetLabel(String type) {
        return type.split(";");
    }
}
