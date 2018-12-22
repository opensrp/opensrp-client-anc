package org.smartregister.anc.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.florent37.expansionpanel.ExpansionHeader;
import com.github.florent37.expansionpanel.ExpansionLayout;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.rules.RuleConstant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.R;
import org.smartregister.anc.adapter.ExpansionWidgetAdapter;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.AncGenericDialogInterface;
import org.smartregister.anc.contract.JsonApiInterface;
import org.smartregister.anc.domain.Contact;
import org.smartregister.anc.event.RefreshExpansionPanelEvent;
import org.smartregister.anc.fragment.ContactJsonFormFragment;
import org.smartregister.anc.model.PartialContact;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.ContactJsonFormUtils;
import org.smartregister.anc.util.Utils;
import org.smartregister.anc.view.AncGenericDialogPopup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ndegwamartin on 30/06/2018.
 */
public class ContactJsonFormActivity extends JsonFormActivity implements JsonApiInterface {

    private static final String CONTACT_STATE = "contactState";
    private Contact contact;
    private ProgressDialog progressDialog;
    private AncGenericDialogInterface genericDialogInterface;
    private ContactJsonFormUtils formUtils = new ContactJsonFormUtils();
    private Utils utils = new Utils();
    private String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            this.contact = extractContact(getIntent().getSerializableExtra(Constants.JSON_FORM_EXTRA.CONTACT));
        } else {
            this.contact = extractContact(savedInstanceState.getSerializable(CONTACT_STATE));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(CONTACT_STATE, contact);
    }

    @Override
    public void initializeFormFragment() {
        initializeFormFragmentCore();
    }

    @Override
    public void writeValue(String stepName, String key, String value, String openMrsEntityParent, String openMrsEntity,
                           String openMrsEntityId, boolean popup) throws JSONException {
        callSuperWriteValue(stepName, key, value, openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);
    }

    @Override
    public void onFormFinish() {
        callSuperFinish();
    }

    protected void callSuperFinish() {
        super.onFormFinish();
    }

    protected void callSuperWriteValue(String stepName, String key, String value, String openMrsEntityParent,
                                       String openMrsEntity, String openMrsEntityId, Boolean popup) throws JSONException {
        super.writeValue(stepName, key, value, openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);
    }

    protected void initializeFormFragmentCore() {
        ContactJsonFormFragment contactJsonFormFragment =
                ContactJsonFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        getSupportFragmentManager().beginTransaction()
                .add(com.vijay.jsonwizard.R.id.container, contactJsonFormFragment).commit();
    }

    private Contact extractContact(Serializable serializable) {
        if (serializable != null && serializable instanceof Contact) {
            return (Contact) serializable;
        }
        return null;
    }

    public Contact getContact() {
        return contact;
    }

    @Override
    public void onBackPressed() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {

                showProgressDialog("Saving contact progress...");
            }

            @Override
            protected Void doInBackground(Void... nada) {

                persistPartial();

                return null;

            }

            @Override
            protected void onPostExecute(Void result) {
                hideProgressDialog();
                ContactJsonFormActivity.this.finish();

            }
        }.execute();

    }

    public void showProgressDialog(String titleIdentifier) {

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle(titleIdentifier);
            progressDialog.setMessage(getString(R.string.please_wait_message));
        }

        if (!isFinishing())
            progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void persistPartial() {
        String baseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);

        PartialContact partialContact = new PartialContact();
        partialContact.setBaseEntityId(baseEntityId);
        partialContact.setContactNo(1);//Hardcoded to remove
        partialContact.setFinalized(false);

        partialContact.setType(getContact().getFormName());
        partialContact.setFormJsonDraft(currentJsonState());

        AncApplication.getInstance().getPartialContactRepository().savePartialContact(partialContact);
    }

    @Override
    protected JSONArray fetchFields(JSONObject parentJson, Boolean popup) {
        JSONArray fields = new JSONArray();
        if (genericDialogInterface != null && genericDialogInterface.getWidgetType() != null && genericDialogInterface.getWidgetType()
                .equals(Constants.EXPANSION_PANEL)) {
            try {
                if (parentJson.has(JsonFormConstants.SECTIONS) && parentJson
                        .get(JsonFormConstants.SECTIONS) instanceof JSONArray) {
                    JSONArray sections = parentJson.getJSONArray(JsonFormConstants.SECTIONS);
                    for (int i = 0; i < sections.length(); i++) {
                        JSONObject sectionJson = sections.getJSONObject(i);
                        fields = returnFormWithSectionFields(sectionJson, popup);
                    }
                } else if (parentJson.has(JsonFormConstants.FIELDS) && parentJson
                        .get(JsonFormConstants.FIELDS) instanceof JSONArray) {
                    fields = returnFormFields(parentJson, popup);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            return super.fetchFields(parentJson, popup);
        }

        return fields;
    }

    /**
     * Get form fields from JSON forms that have sections in the form steps.
     * The JSONObject {@link JSONObject} argument is the object after getting the section in the specified step name
     * The popup {@link boolean} argument is a boolean value to let the function know that the form is being executed
     * on a popup and not the main android view.
     * <p>
     * This function returns a JSONArray {@link JSONArray} of the fields contained in the section
     * for the given step
     *
     * @param sectionJson
     * @param popup
     * @return
     * @throws JSONException
     * @author dubdabasoduba
     */
    private JSONArray returnFormWithSectionFields(JSONObject sectionJson, boolean popup) throws JSONException {
        JSONArray fields = new JSONArray();
        if (sectionJson.has(JsonFormConstants.FIELDS)) {
            if (popup) {
                JSONArray jsonArray = sectionJson.getJSONArray(JsonFormConstants.FIELDS);
                for (int k = 0; k < jsonArray.length(); k++) {
                    JSONObject item = jsonArray.getJSONObject(k);
                    if (item.getString(JsonFormConstants.KEY).equals(genericDialogInterface.getParentKey())) {
                        fields = formUtils.concatArray(fields, specifyFields(item));
                    }
                }
            } else {
                fields = formUtils.concatArray(fields, sectionJson.getJSONArray(JsonFormConstants.FIELDS));
            }
        }
        return fields;
    }

    /**
     * Get the form fields for the JSON forms that do not use the sections in the steps
     * The JSONObject {@link JSONObject} argument is the object after getting the step name
     * The popup {@link boolean} argument is a boolean value to let the function know that the form is being executed
     * on a popup and not the main android view.
     * <p>
     * This function returns a JSONArray {@link JSONArray} of the fields contained in the step
     *
     * @param parentJson
     * @param popup
     * @return fields
     * @throws JSONException
     * @author dubdabasoduba
     */
    private JSONArray returnFormFields(JSONObject parentJson, boolean popup) throws JSONException {
        JSONArray fields = new JSONArray();
        if (popup) {
            JSONArray jsonArray = parentJson.getJSONArray(JsonFormConstants.FIELDS);
            for (int k = 0; k < jsonArray.length(); k++) {
                JSONObject item = jsonArray.getJSONObject(k);
                if (item.getString(JsonFormConstants.KEY).equals(genericDialogInterface.getParentKey())) {
                    fields = specifyFields(item);
                }
            }
        } else {
            fields = parentJson.getJSONArray(JsonFormConstants.FIELDS);
        }
        return fields;
    }

    @Override
    protected JSONArray specifyFields(JSONObject parentJson) {
        JSONArray fields = new JSONArray();
        if (genericDialogInterface != null && genericDialogInterface.getWidgetType() != null && genericDialogInterface.getWidgetType()
                .equals(Constants.EXPANSION_PANEL)) {
            try {
                if (parentJson.has(JsonFormConstants.CONTENT_FORM)) {
                    if (getExtraFieldsWithValues() != null) {
                        fields = getExtraFieldsWithValues();
                    } else {
                        String formLocation = parentJson.has(JsonFormConstants.CONTENT_FORM_LOCATION) ? parentJson.getString(JsonFormConstants.CONTENT_FORM_LOCATION) : "";
                        fields = getSubFormFields(parentJson.get(JsonFormConstants.CONTENT_FORM).toString(),
                                formLocation, fields);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            return super.specifyFields(parentJson);
        }
        return fields;
    }

    @Override
    protected void widgetsWriteValue(String stepName, String key, String value, String openMrsEntityParent,
                                     String openMrsEntity, String openMrsEntityId, boolean popup) throws JSONException {
        synchronized (getmJSONObject()) {
            JSONObject jsonObject = getmJSONObject().getJSONObject(stepName);
            JSONArray fields = fetchFields(jsonObject, popup);
            for (int i = 0; i < fields.length(); i++) {
                JSONObject item = fields.getJSONObject(i);
                String keyAtIndex = item.getString(JsonFormConstants.KEY);
                String itemType = "";
                if (popup) {
                    itemType = item.getString(JsonFormConstants.TYPE);
                    String widgetLabel = getWidgetLabel(item);
                    if (!TextUtils.isEmpty(widgetLabel)) {
                        itemType = itemType + ";" + widgetLabel;
                    }
                }
                if (key.equals(keyAtIndex)) {
                    if (item.has(JsonFormConstants.TEXT)) {
                        item.put(JsonFormConstants.TEXT, value);
                    } else {
                        if (popup) {
                            String itemText = "";
                            String type = item.getString(JsonFormConstants.TYPE);
                            if (type.equals(JsonFormConstants.NATIVE_RADIO_BUTTON) || type.equals(Constants.ANC_RADIO_BUTTON)) {
                                itemText = formUtils.getRadioButtonText(item, value);
                            }

                            genericDialogInterface
                                    .addSelectedValues(
                                            formUtils.createAssignedValue(genericDialogInterface, keyAtIndex, "", value, itemType, itemText));
                            setExtraFieldsWithValues(fields);
                        }
                        item.put(JsonFormConstants.VALUE, value);
                    }
                    item.put(JsonFormConstants.OPENMRS_ENTITY_PARENT, openMrsEntityParent);
                    item.put(JsonFormConstants.OPENMRS_ENTITY, openMrsEntity);
                    item.put(JsonFormConstants.OPENMRS_ENTITY_ID, openMrsEntityId);
                    refreshCalculationLogic(key, null, popup);
                    refreshSkipLogic(key, null, popup);
                    refreshConstraints(key, null);
                    refreshMediaLogic(key, value);
                    return;
                }
            }
        }
    }

    @Override
    protected void checkBoxWriteValue(String stepName, String parentKey, String childObjectKey, String childKey,
                                      String value, boolean popup) throws JSONException {
        synchronized (getmJSONObject()) {
            JSONObject jsonObject = getmJSONObject().getJSONObject(stepName);
            JSONArray fields = fetchFields(jsonObject, popup);
            for (int i = 0; i < fields.length(); i++) {
                JSONObject item = fields.getJSONObject(i);
                String keyAtIndex = item.getString(JsonFormConstants.KEY);
                StringBuilder itemType = new StringBuilder();

                if (popup) {
                    itemType = new StringBuilder(item.getString(JsonFormConstants.TYPE));
                    String widgetLabel = getWidgetLabel(item);
                    if (!TextUtils.isEmpty(widgetLabel)) {
                        itemType.append(";").append(widgetLabel);
                    }
                }
                if (parentKey.equals(keyAtIndex)) {
                    JSONArray jsonArray = item.getJSONArray(childObjectKey);
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject innerItem = jsonArray.getJSONObject(j);
                        String anotherKeyAtIndex = innerItem.getString(JsonFormConstants.KEY);
                        String itemText = "";
                        String type = item.getString(JsonFormConstants.TYPE);
                        if (type.equals(JsonFormConstants.CHECK_BOX)) {
                            itemText = innerItem.getString(JsonFormConstants.TEXT);
                        }

                        if (childKey.equals(anotherKeyAtIndex)) {
                            innerItem.put(JsonFormConstants.VALUE, value);
                            if (popup) {
                                genericDialogInterface.addSelectedValues(
                                        formUtils.createAssignedValue(genericDialogInterface, keyAtIndex, childKey, value, itemType.toString(), itemText));
                                setExtraFieldsWithValues(fields);
                            }
                            refreshCalculationLogic(parentKey, childKey, popup);
                            refreshSkipLogic(parentKey, childKey, popup);
                            refreshConstraints(parentKey, childKey);
                            return;
                        }
                    }
                }
            }
        }
    }

    private String getWidgetLabel(JSONObject jsonObject) throws JSONException {
        String label = "";
        String widgetType = jsonObject.getString(JsonFormConstants.TYPE);
        if (!TextUtils.isEmpty(widgetType) && genericDialogInterface.getWidgetType().equals(Constants.EXPANSION_PANEL)) {
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

    @Override
    public Map<String, String> getValueFromAddressCore(JSONObject object) throws JSONException {
        Map<String, String> result = new HashMap<>();
        if (genericDialogInterface != null && genericDialogInterface.getWidgetType() != null && genericDialogInterface.getWidgetType()
                .equals(Constants.EXPANSION_PANEL)) {
            if (object != null) {
                switch (object.getString(JsonFormConstants.TYPE)) {
                    case JsonFormConstants.CHECK_BOX:
                        result = getCheckBoxResults(object);
                        break;
                    case JsonFormConstants.NATIVE_RADIO_BUTTON:
                        Boolean multiRelevance = object.optBoolean(JsonFormConstants.NATIVE_RADIO_BUTTON_MULTI_RELEVANCE, false);
                        result = getRadioButtonResults(multiRelevance, object);
                        break;
                    case Constants.ANC_RADIO_BUTTON:
                        Boolean relevance = object.optBoolean(JsonFormConstants.NATIVE_RADIO_BUTTON_MULTI_RELEVANCE, false);
                        result = getRadioButtonResults(relevance, object);
                        break;

                    default:
                        result.put(getKey(object), getValue(object));
                        break;
                }

                if (object.has(RuleConstant.IS_RULE_CHECK) && object.getBoolean(RuleConstant.IS_RULE_CHECK) && (object.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.CHECK_BOX) || (object.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.NATIVE_RADIO_BUTTON) && object.optBoolean(JsonFormConstants.NATIVE_RADIO_BUTTON_MULTI_RELEVANCE, false)))) {
                    List<String> selectedValues = new ArrayList<>(result.keySet());
                    result.clear();
                    result.put(getKey(object), selectedValues.toString());
                }
            }
        } else {
            return super.getValueFromAddressCore(object);
        }
        return result;
    }

    private Map<String, String> getCheckBoxResults(JSONObject jsonObject) throws JSONException {
        Map<String, String> result = new HashMap<>();
        JSONArray options = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
        for (int j = 0; j < options.length(); j++) {
            if (options.getJSONObject(j).has(JsonFormConstants.VALUE)) {
                if (jsonObject.has(RuleConstant.IS_RULE_CHECK) && jsonObject.getBoolean(RuleConstant.IS_RULE_CHECK)) {
                    if (Boolean.valueOf(options.getJSONObject(j).getString(JsonFormConstants.VALUE))) {//Rules engine useth only true values
                        result.put(options.getJSONObject(j).getString(JsonFormConstants.KEY), options.getJSONObject(j).getString(JsonFormConstants.VALUE));
                    }
                } else {
                    result.put(options.getJSONObject(j).getString(JsonFormConstants.KEY), options.getJSONObject(j).getString(JsonFormConstants.VALUE));
                }
            } else {
                Log.e(TAG, "option for Key " + options.getJSONObject(j).getString(JsonFormConstants.KEY) + " has NO value");
            }

            //Backward compatibility Fix
            if (jsonObject.has(RuleConstant.IS_RULE_CHECK) && !jsonObject.getBoolean(RuleConstant.IS_RULE_CHECK)) {
                if (options.getJSONObject(j).has(JsonFormConstants.VALUE)) {
                    result.put(JsonFormConstants.VALUE, options.getJSONObject(j).getString(JsonFormConstants.VALUE));
                } else {
                    result.put(JsonFormConstants.VALUE, "false");
                }
            }
        }
        return result;
    }

    private Map<String, String> getRadioButtonResults(Boolean multiRelevance, JSONObject jsonObject) throws JSONException {
        Map<String, String> result = new HashMap<>();
        if (multiRelevance) {
            JSONArray jsonArray = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
            for (int j = 0; j < jsonArray.length(); j++) {
                if (jsonObject.has(JsonFormConstants.VALUE)) {
                    if (jsonObject.getString(JsonFormConstants.VALUE).equals(jsonArray.getJSONObject(j).getString(JsonFormConstants.KEY))) {
                        result.put(jsonArray.getJSONObject(j).getString(JsonFormConstants.KEY), String.valueOf(true));
                    } else {
                        if (!jsonObject.has(RuleConstant.IS_RULE_CHECK) || !jsonObject.getBoolean(RuleConstant.IS_RULE_CHECK)) {
                            result.put(jsonArray.getJSONObject(j).getString(JsonFormConstants.KEY), String.valueOf(false));
                        }
                    }
                } else {
                    Log.e(TAG, "option for Key " + jsonArray.getJSONObject(j).getString(JsonFormConstants.KEY) + " has NO value");
                }
            }
        } else {
            result.put(getKey(jsonObject), getValue(jsonObject));
        }

        return result;
    }

    @Override
    public void setGenericPopup(AncGenericDialogPopup context) {
        genericDialogInterface = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshExpansionPanel(RefreshExpansionPanelEvent refreshExpansionPanelEvent) {
        if (refreshExpansionPanelEvent != null) {
            try {
                List<String> values = utils.createExpansionPanelChildren(refreshExpansionPanelEvent.getValues());
                LinearLayout linearLayout = refreshExpansionPanelEvent.getLinearLayout();
                ExpansionHeader layoutHeader = (ExpansionHeader) linearLayout.getChildAt(0);
                ImageView status = layoutHeader.findViewById(R.id.statusImageView);
                changeRecycler(values, status);
                ExpansionLayout contentLayout = (ExpansionLayout) linearLayout.getChildAt(1);
                RecyclerView recyclerView = contentLayout.findViewById(R.id.contentRecyclerView);
                ExpansionWidgetAdapter adapter = (ExpansionWidgetAdapter) recyclerView.getAdapter();
                adapter.setExpansionWidgetValues(values);
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void changeRecycler(List<String> values, ImageView status) throws JSONException {
        JSONArray list = new JSONArray(values);
        for (int k = 0; k < list.length(); k++) {
            String valueDisplay = list.getString(k).split(":")[1];
            formUtils.changeIcon(status, valueDisplay, getApplicationContext());
        }
    }
}

