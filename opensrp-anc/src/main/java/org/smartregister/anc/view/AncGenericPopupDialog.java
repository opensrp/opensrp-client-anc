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
import java.util.List;
import java.util.Map;

import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;

public class AncGenericPopupDialog extends GenericPopupDialog implements AncGenericDialogInterface {
    private static ContactJsonFormInteractor jsonFormInteractor = ContactJsonFormInteractor.getInstance();
    protected Toolbar mToolbar;
    protected String container;
    private String TAG = this.getClass().getSimpleName();
    private Map<String, ExpansionPanelValuesModel> secondaryValuesMap = new HashMap<>();
    private ContactJsonFormUtils formUtils = new ContactJsonFormUtils();
    private JsonApi jsonApi;
    private Activity activity;
    private Context context;
    private String header;
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
            onDataPass(getParentKey(), getStepName(), getChildKey());
        } else {
            onGenericDataPass(getParentKey(), getStepName(), getChildKey());
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
                        setSubFormsFields(addFormValues(getSpecifyContent()));
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
                    if (item.has(JsonFormConstants.KEY) && item.getString(JsonFormConstants.KEY).equals(getParentKey()) &&
                            item.has(JsonFormConstants.VALUE)) {
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
                    InputMethodManager inputManager =
                            (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
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
                    AncGenericPopupDialog.this.dismissAllowingStateLoss();
                }
            });

            okButton = dialogView.findViewById(R.id.generic_dialog_done_button);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    passData();
                    jsonApi.updateGenericPopupSecondaryValues(null);
                    AncGenericPopupDialog.this.dismissAllowingStateLoss();
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
     * @param parentKey
     * @param stepName
     * @param childKey
     */
    public void onDataPass(String parentKey, String stepName, String childKey) {
        JSONObject mJSONObject = getJsonApi().getmJSONObject();
        if (mJSONObject != null) {
            JSONArray fields = formUtils.getFormFields(stepName, context);
            try {

                if (fields.length() > 0) {
                    for (int i = 0; i < fields.length(); i++) {
                        JSONObject item = fields.getJSONObject(i);
                        if (item != null && item.getString(JsonFormConstants.KEY).equals(parentKey)) {
                            addValues(getJsonObjectToUpdate(item, childKey));
                        }
                    }
                }

                getJsonApi().setmJSONObject(mJSONObject);

            } catch (JSONException e) {
                Log.i(TAG, Log.getStackTraceString(e));
            }
        }
    }

    protected void addValues(JSONObject item) throws JSONException {
        JSONArray secondaryValuesArray = createValues();
        try {
            JSONArray orderedValues = orderExpansionPanelValues(secondaryValuesArray);
            item.remove(JsonFormConstants.VALUE);
            item.put(JsonFormConstants.VALUE, orderedValues);
            setNewSelectedValues(orderedValues);
            org.smartregister.anc.util.Utils.postEvent(new RefreshExpansionPanelEvent(orderedValues, linearLayout));
        } catch (Exception e) {
            Log.i(TAG, Log.getStackTraceString(e));
        }
    }

    @Override
    protected JSONArray createValues() throws JSONException {
        JSONArray selectedValues = new JSONArray();
        JSONArray formFields = getSubFormsFields();
        for (int i = 0; i < formFields.length(); i++) {
            JSONObject field = formFields.getJSONObject(i);
            if (field != null && field.has(JsonFormConstants.TYPE) &&
                    !JsonFormConstants.LABEL.equals(field.getString(JsonFormConstants.TYPE)) &&
                    !JsonFormConstants.SECTIONS.equals(field.getString(JsonFormConstants.TYPE))) {
                JSONArray valueOpenMRSAttributes = new JSONArray();
                JSONObject openMRSAttributes = getFieldOpenMRSAttributes(field);
                String key = field.getString(JsonFormConstants.KEY);
                String type = field.getString(JsonFormConstants.TYPE);
                String label = getWidgetLabel(field);
                JSONArray values = new JSONArray();
                if (JsonFormConstants.CHECK_BOX.equals(field.getString(JsonFormConstants.TYPE)) &&
                        field.has(JsonFormConstants.OPTIONS_FIELD_NAME)) {
                    values = getOptionsValueCheckBox(field.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME));
                    getOptionsOpenMRSAttributes(field, valueOpenMRSAttributes);
                } else if ((JsonFormConstants.ANC_RADIO_BUTTON.equals(field.getString(JsonFormConstants.TYPE)) ||
                        JsonFormConstants.NATIVE_RADIO_BUTTON.equals(field.getString(JsonFormConstants.TYPE))) &&
                        field.has(JsonFormConstants.OPTIONS_FIELD_NAME) && field.has(JsonFormConstants.VALUE)) {
                    values.put(getOptionsValueRadioButton(field.optString(JsonFormConstants.VALUE),
                            field.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME)));
                    getOptionsOpenMRSAttributes(field, valueOpenMRSAttributes);
                } else if (JsonFormConstants.SPINNER.equals(field.getString(JsonFormConstants.TYPE)) &&
                        field.has(JsonFormConstants.VALUE)) {
                    values.put(field.optString(JsonFormConstants.VALUE));
                    getSpinnerValueOpenMRSAttributes(field, valueOpenMRSAttributes);
                } else {
                    if (field.has(JsonFormConstants.VALUE)) {
                        values.put(field.optString(JsonFormConstants.VALUE));
                    }
                }

                if (values.length() > 0) {
                    if (!TextUtils.isEmpty(label)) {
                        int index = field.optInt(Constants.INDEX);
                        selectedValues.put(createValueObject(key, type, label, index, values, openMRSAttributes,
                                valueOpenMRSAttributes));
                    } else {
                        selectedValues.put(createSecondaryValueObject(key, type, values, openMRSAttributes,
                                valueOpenMRSAttributes));
                    }
                }
            }
        }
        return selectedValues;
    }

    protected JSONObject createValueObject(String key, String type, String label, int index, JSONArray values,
                                           JSONObject openMRSAttributes, JSONArray valueOpenMRSAttributes) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (values.length() > 0) {
                jsonObject.put(JsonFormConstants.KEY, key);
                jsonObject.put(JsonFormConstants.TYPE, type);
                jsonObject.put(JsonFormConstants.LABEL, label);
                jsonObject.put(Constants.INDEX, index);
                jsonObject.put(JsonFormConstants.VALUES, values);
                jsonObject.put(JsonFormConstants.OPENMRS_ATTRIBUTES, openMRSAttributes);
                if (valueOpenMRSAttributes.length() > 0) {
                    jsonObject.put(JsonFormConstants.VALUE_OPENMRS_ATTRIBUTES, valueOpenMRSAttributes);
                }
            }
        } catch (Exception e) {
            Log.i(TAG, Log.getStackTraceString(e));

        }
        return jsonObject;
    }

    private String getWidgetLabel(JSONObject jsonObject) throws JSONException {
        String label = "";
        String widgetType = jsonObject.getString(JsonFormConstants.TYPE);
        if (!TextUtils.isEmpty(widgetType) && getWidgetType().equals(Constants.EXPANSION_PANEL)) {
            switch (widgetType) {
                case JsonFormConstants.EDIT_TEXT:
                    label = jsonObject.optString(JsonFormConstants.HINT, "");
                    break;
                case JsonFormConstants.DATE_PICKER:
                    label = jsonObject.optString(JsonFormConstants.HINT, "");
                    break;
                default:
                    label = jsonObject.optString(JsonFormConstants.LABEL, "");
                    break;
            }
        }
        return label;
    }

    private JSONArray orderExpansionPanelValues(JSONArray expansionPanelValues) throws JSONException {
        JSONArray formattedArray = new JSONArray();
        if (expansionPanelValues != null && expansionPanelValues.length() > 0) {
            JSONArray sortedItemsWithNulls = new JSONArray();
            for (int i = 0; i < expansionPanelValues.length(); i++) {
                JSONObject valueItem = expansionPanelValues.getJSONObject(i);
                if (valueItem.has(Constants.INDEX)) {
                    int itemIndex = valueItem.getInt(Constants.INDEX);
                    sortedItemsWithNulls.put(itemIndex, valueItem);
                }
            }

            for (int k = 0; k < sortedItemsWithNulls.length(); k++) {
                if (!sortedItemsWithNulls.isNull(k)) {
                    formattedArray.put(sortedItemsWithNulls.getJSONObject(k));
                }
            }
        }

        return formattedArray;
    }

    @Override
    protected JSONArray addFormValues(JSONArray jsonArray) {
        if (!TextUtils.isEmpty(getWidgetType()) && getWidgetType().equals(Constants.EXPANSION_PANEL)) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject;
                try {
                    jsonObject = jsonArray.getJSONObject(i);
                    String key = jsonObject.getString(JsonFormConstants.KEY);
                    jsonObject.put(Constants.INDEX, String.valueOf(i));
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
                            if (type != null && (type.equals(JsonFormConstants.NATIVE_RADIO_BUTTON) ||
                                    type.equals(Constants.ANC_RADIO_BUTTON))) {
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
            return jsonArray;
        } else {
            super.addFormValues(jsonArray);
        }
        return jsonArray;
    }

    /**
     * Finds the actual widget to be updated and secondary values added on
     *
     * @param jsonObject {@link JSONObject}
     * @param childKey   {@link String}
     *
     * @return item {@link JSONObject}
     */
    @Override
    protected JSONObject getJsonObjectToUpdate(JSONObject jsonObject, String childKey) {
        JSONObject item = new JSONObject();
        try {
            if (jsonObject != null && jsonObject.has(JsonFormConstants.TYPE)) {
                if ((jsonObject.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.CHECK_BOX) ||
                        jsonObject.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.NATIVE_RADIO_BUTTON)) &&
                        childKey != null) {
                    JSONArray options = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                    if (options != null) {
                        for (int i = 0; i < options.length(); i++) {
                            JSONObject childItem = options.getJSONObject(i);
                            if (childItem != null && childItem.has(JsonFormConstants.KEY) &&
                                    childKey.equals(childItem.getString(JsonFormConstants.KEY))) {
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
                    if (!getSecondaryValues().isNull(i)) {
                        try {
                            jsonObject = getSecondaryValues().getJSONObject(i);
                            String key = jsonObject.getString(JsonFormConstants.KEY);
                            String type = jsonObject.getString(JsonFormConstants.TYPE);
                            String label = jsonObject.getString(JsonFormConstants.LABEL);
                            JSONArray values = jsonObject.getJSONArray(JsonFormConstants.VALUES);
                            int index = jsonObject.optInt(Constants.INDEX);

                            JSONObject openmrsAttributes = new JSONObject();
                            if (jsonObject.has(JsonFormConstants.OPENMRS_ATTRIBUTES)) {
                                openmrsAttributes = jsonObject.getJSONObject(JsonFormConstants.OPENMRS_ATTRIBUTES);
                            }
                            JSONArray valueOpenMRSAttributes = new JSONArray();
                            if (jsonObject.has(JsonFormConstants.VALUE_OPENMRS_ATTRIBUTES)) {
                                valueOpenMRSAttributes = jsonObject.getJSONArray(JsonFormConstants.VALUE_OPENMRS_ATTRIBUTES);
                            }

                            secondaryValuesMap.put(key,
                                    new ExpansionPanelValuesModel(key, type, label, index, values, openmrsAttributes,
                                            valueOpenMRSAttributes));
                        } catch (JSONException e) {
                            Log.i(TAG, Log.getStackTraceString(e));
                        }
                    }
                }
            }
        } else {
            super.createSecondaryValuesMap();
        }

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
