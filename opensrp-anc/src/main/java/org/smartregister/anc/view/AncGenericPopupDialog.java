package org.smartregister.anc.view;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.GenericPopupDialog;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.SecondaryValueModel;
import com.vijay.jsonwizard.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.R;
import org.smartregister.anc.contract.AncGenericDialogInterface;
import org.smartregister.anc.contract.JsonApiInterface;
import org.smartregister.anc.event.RefreshExpansionPanelEvent;
import org.smartregister.anc.interactor.ContactJsonFormInteractor;
import org.smartregister.anc.model.ExpansionPanelValuesModel;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.ContactJsonFormUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;

public class AncGenericPopupDialog extends GenericPopupDialog implements AncGenericDialogInterface {
    private String TAG = this.getClass().getSimpleName();
    private static ContactJsonFormInteractor jsonFormInteractor = ContactJsonFormInteractor.getInstance();
    private Map<String, ExpansionPanelValuesModel> popAssignedValue = new HashMap<>();
    private Map<String, ExpansionPanelValuesModel> secondaryValuesMap = new HashMap<>();
    private ContactJsonFormUtils formUtils = new ContactJsonFormUtils();
    private JsonApi jsonApi;
    private Activity activity;
    private Context context;
    private String header;
    protected Toolbar mToolbar;
    protected String container;
    private LinearLayout linearLayout;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        activity = (Activity) context;
        jsonApi = (JsonApi) activity;
        JsonApiInterface ancJsonApi = (JsonApiInterface) activity;
        ancJsonApi.setGenericPopup(this);
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
    protected List<View> initiateViews() {
        List<View> listOfViews = new ArrayList<>();
        jsonFormInteractor
                .fetchFields(listOfViews, getStepName(), getFormFragment(), getSpecifyContent(), getCommonListener(), true);
        return listOfViews;
    }

    @Override
    protected void passData() {
        if (!TextUtils.isEmpty(getWidgetType()) && getWidgetType().equals(Constants.EXPANSION_PANEL)) {
            onDataPass(popAssignedValue, getParentKey(), getStepName(), getChildKey());
        } else {
            onGenericDataPass(getPopAssignedValue(), getParentKey(), getStepName(), getChildKey());
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(getWidgetType()) && getWidgetType().equals(Constants.EXPANSION_PANEL)) {
            ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (context == null) {
            throw new IllegalStateException(
                    "The Context is not set. Did you forget to set context with Generic Dialog setContext method?");
        }

        activity = (Activity) context;
        jsonApi = (JsonApi) activity;

        try {
            loadPartialSecondaryValues();
            createSecondaryValuesMap();
            loadSubForms();
            jsonApi.updateGenericPopupSecondaryValues(getSpecifyContent());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    protected void loadSubForms() {
        if (!TextUtils.isEmpty(getFormIdentity())) {
            JSONObject subForm = null;
            try {
                subForm = FormUtils.getSubFormJson(getFormIdentity(), getFormLocation(), context);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (subForm != null) {
                try {
                    if (subForm.has(JsonFormConstants.CONTENT_FORM)) {
                        setSpecifyContent(subForm.getJSONArray(JsonFormConstants.CONTENT_FORM));
                        addFormValues(getSpecifyContent());
                    } else {
                        Utils.showToast(activity, activity.getApplicationContext().getResources()
                                .getString(com.vijay.jsonwizard.R.string.please_specify_content));
                        AncGenericPopupDialog.this.dismiss();
                    }
                } catch (JSONException e) {
                    Log.i(TAG, Log.getStackTraceString(e));
                }

            }
        }
    }

    @Override
    protected void loadPartialSecondaryValues() throws JSONException {
        if (!TextUtils.isEmpty(getWidgetType()) && getWidgetType().equals(Constants.EXPANSION_PANEL)) {
            JSONArray fields = formUtils.getFormFields(getStepName(), context);
            if (fields != null && fields.length() > 0) {
                for (int i = 0; i < fields.length(); i++) {
                    JSONObject item = fields.getJSONObject(i);
                    if (item.has(JsonFormConstants.KEY) && item.getString(JsonFormConstants.KEY)
                            .equals(getParentKey()) && item.has
                            (JsonFormConstants.VALUE)) {
                        setSecondaryValues(item.getJSONArray(JsonFormConstants.VALUE));
                    }
                }
            }
        } else {
            super.loadPartialSecondaryValues();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!TextUtils.isEmpty(getWidgetType()) && getWidgetType().equals(Constants.EXPANSION_PANEL)) {
            ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.fragment_generic_dialog, container, false);
            mToolbar = dialogView.findViewById(R.id.generic_toolbar);
            changeToolbarColor();

            TextView toolBar = mToolbar.findViewById(R.id.txt_title_label);
            if (!TextUtils.isEmpty(header)) {
                toolBar.setText(header);
            }
            AppCompatImageButton cancelButton;
            Button okButton;

            new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    InputMethodManager inputManager = (InputMethodManager) context
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(),
                            HIDE_NOT_ALWAYS);
                }
            };

            List<View> viewList = initiateViews();
            LinearLayout genericDialogContent = dialogView.findViewById(R.id.generic_dialog_content);
            for (View view : viewList) {
                genericDialogContent.addView(view);
            }

            cancelButton = dialogView.findViewById(R.id.generic_dialog_cancel_button);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jsonApi.updateGenericPopupSecondaryValues(null);
                    AncGenericPopupDialog.this.dismiss();
                }
            });

            okButton = dialogView.findViewById(R.id.generic_dialog_done_button);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    passData();
                    jsonApi.updateGenericPopupSecondaryValues(null);
                    AncGenericPopupDialog.this.dismiss();
                }
            });
            if (getDialog().getWindow() != null) {
                getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            }
            jsonApi.invokeRefreshLogic(null, true, null, null);
            return dialogView;
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    private void changeToolbarColor() {
        if (!TextUtils.isEmpty(getContainer())) {
            switch (getContainer()) {
                case Constants.JSON_FORM.ANC_TEST:
                    mToolbar.setBackgroundColor(getResources().getColor(R.color.contact_tests_actionbar));
                    break;
                case Constants.JSON_FORM.ANC_COUNSELLING_TREATMENT:
                    mToolbar.setBackgroundColor(getResources().getColor(R.color.contact_counselling_actionbar));
                    break;
                default:
                    break;
            }
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
    public void onDataPass(Map<String, ExpansionPanelValuesModel> selectedValues, String parentKey, String stepName,
                           String childKey) {
        JSONObject mJSONObject = getJsonApi().getmJSONObject();
        if (mJSONObject != null) {
            JSONArray fields = formUtils.getFormFields(stepName, context);
            try {

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

    protected void addValues(JSONObject item, Map<String, ExpansionPanelValuesModel> secondaryValueModel) {
        JSONObject valueObject;
        JSONArray secondaryValuesArray = new JSONArray();
        ExpansionPanelValuesModel secondaryValue;
        for (Object object : secondaryValueModel.entrySet()) {
            Map.Entry pair = (Map.Entry) object;
            secondaryValue = (ExpansionPanelValuesModel) pair.getValue();
            valueObject = formUtils.createSecValues(secondaryValue);
            secondaryValuesArray.put(valueObject);
        }
        try {
            item.put(JsonFormConstants.VALUE, reverseValues(secondaryValuesArray));
            setNewSelectedValues(reverseValues(secondaryValuesArray));
            org.smartregister.anc.util.Utils
                    .postEvent(new RefreshExpansionPanelEvent(reverseValues(secondaryValuesArray), linearLayout));
        } catch (Exception e) {
            Log.i(TAG, Log.getStackTraceString(e));
        }
    }


    private JSONArray reverseValues(JSONArray jsonArray) throws JSONException {
        JSONArray newJsonArray = new JSONArray();
        for (int i = jsonArray.length() - 1; i >= 0; i--) {
            newJsonArray.put(jsonArray.getJSONObject(i));
        }
        return newJsonArray;
    }

    @Override
    protected void addFormValues(JSONArray jsonArray) {
        if (!TextUtils.isEmpty(getWidgetType()) && getWidgetType().equals(Constants.EXPANSION_PANEL)) {
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
                            if (type != null && (type.equals(JsonFormConstants.NATIVE_RADIO_BUTTON) || type.equals(Constants
                                    .ANC_RADIO_BUTTON))) {
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
     * @param jsonObject {@link JSONObject}
     * @param childKey   {@link String}
     * @return item {@link JSONObject}
     */
    @Override
    protected JSONObject getJsonObjectToUpdate(JSONObject jsonObject, String childKey) {
        JSONObject item = new JSONObject();
        try {
            if (jsonObject != null && jsonObject.has(JsonFormConstants.TYPE)) {
                if ((jsonObject.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.CHECK_BOX) || jsonObject.getString
                        (JsonFormConstants.TYPE).equals(JsonFormConstants.NATIVE_RADIO_BUTTON)) && childKey != null) {
                    JSONArray options = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                    if (options != null) {
                        for (int i = 0; i < options.length(); i++) {
                            JSONObject childItem = options.getJSONObject(i);
                            if (childItem != null && childItem.has(JsonFormConstants.KEY) && childKey
                                    .equals(childItem.getString
                                            (JsonFormConstants.KEY))) {
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


    @Override
    protected void createSecondaryValuesMap() {
        JSONObject jsonObject;
        if (!TextUtils.isEmpty(getWidgetType()) && getWidgetType().equals(Constants.EXPANSION_PANEL)) {
            if (getSecondaryValues() != null) {
                for (int i = 0; i < getSecondaryValues().length(); i++) {
                    try {
                        jsonObject = getSecondaryValues().getJSONObject(i);
                        String key = jsonObject.getString(JsonFormConstants.KEY);
                        String type = jsonObject.getString(JsonFormConstants.TYPE);
                        String label = jsonObject.getString(JsonFormConstants.LABEL);
                        JSONArray values = jsonObject.getJSONArray(JsonFormConstants.VALUES);

                        JSONObject openmrsAttributes = new JSONObject();
                        if (jsonObject.has(JsonFormConstants.OPENMRS_ATTRIBUTES)) {
                            openmrsAttributes =
                                    jsonObject.getJSONObject(JsonFormConstants.OPENMRS_ATTRIBUTES);
                        }
                        JSONArray valueOpenMRSAttributes = new JSONArray();
                        if (jsonObject.has(JsonFormConstants.VALUE_OPENMRS_ATTRIBUTES)) {
                            valueOpenMRSAttributes = jsonObject
                                    .getJSONArray(JsonFormConstants.VALUE_OPENMRS_ATTRIBUTES);
                        }

                        secondaryValuesMap.put(key, new ExpansionPanelValuesModel(key, type, label, values,
                                openmrsAttributes, valueOpenMRSAttributes));
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


    @Override
    public void addSelectedValues(JSONObject openMRSAttributes,
                                  JSONArray valueOpenMRSAttributes, Map<String, String> newValue) {
        if (newValue != null) {
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
            if (widgetValues != null && widgetValues.length > 2) {
                type = widgetValues[1] + ";" + widgetValues[2];
                value = widgetValues[0];
            } else if (widgetValues != null && widgetValues.length == 2) {
                type = widgetValues[1];
                value = widgetValues[0];
            }

            createSecondaryValues(key, type, value, openMRSAttributes, valueOpenMRSAttributes);
        }
    }


    @Override
    protected void createSecondaryValues(String key, String labelType, String value, JSONObject openMRSAttributes,
                                         JSONArray valueOpenMRSAttributes) {
        JSONArray values = new JSONArray();
        values.put(value);
        String[] string = splitText(labelType, ";");
        if (!TextUtils.isEmpty(getWidgetType()) && getWidgetType().equals(Constants.EXPANSION_PANEL)) {
            if (string.length >= 1) {
                String type = string[0];
                String label;
                if (JsonFormConstants.HIDDEN.equals(type)){
                    label= "";
                } else { label = string[1];}
                if (type != null && type.equals(JsonFormConstants.CHECK_BOX)) {
                    if (popAssignedValue != null && popAssignedValue.containsKey(key)) {
                        setValueModelAttributes(key, value, openMRSAttributes, valueOpenMRSAttributes);
                    } else {
                        if (popAssignedValue != null) {
                            popAssignedValue.put(key,
                                    new ExpansionPanelValuesModel(key, type, label, values, openMRSAttributes,
                                            valueOpenMRSAttributes));
                        }
                    }
                } else {
                    popAssignedValue.put(key, new ExpansionPanelValuesModel(key, type, label, values, openMRSAttributes,
                            valueOpenMRSAttributes));
                }
            }
        } else {
            super.createSecondaryValues(key, labelType, value, openMRSAttributes, valueOpenMRSAttributes);
        }
    }

    private void setValueModelAttributes(String key, String value, JSONObject openMRSAttributes,
                                         JSONArray valueOpenMRSAttributes) {
        ExpansionPanelValuesModel valueModel = popAssignedValue.get(key);
        if (valueModel != null) {
            JSONArray jsonArray = valueModel.getValues();
            if (!checkSimilarity(jsonArray, value)) {
                jsonArray.put(value);
            }

            valueModel.setValues(removeUnselectedItems(jsonArray, value));
            valueModel.setValuesOpenMRSAttributes(valueOpenMRSAttributes);
            valueModel.setOpenmrsAttributes(openMRSAttributes);
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
                String[] splitValues = splitText(value, ":");
                String[] currentValues = splitText(currentValue, ":");
                if (splitValues.length == 3 && currentValues.length == 3 && splitValues[0]
                        .equals(currentValues[0]) && splitValues[1]
                        .equals(currentValues[1]) && currentValues[2].equals(Constants.FALSE)) {
                    list.remove(k);
                }
            }
        } catch (Exception e) {
            Log.i(TAG, Log.getStackTraceString(e));
        }

        values = new JSONArray(list);
        return values;
    }

    private String[] splitText(String text, String spliter) {
        return text.split(spliter);
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setLinearLayout(LinearLayout linearLayout) {
        this.linearLayout = linearLayout;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

}
