package org.smartregister.anc.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;
import com.vijay.jsonwizard.rules.RuleConstant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jeasy.rules.api.Facts;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.R;
import org.smartregister.anc.contract.AncGenericDialogInterface;
import org.smartregister.anc.contract.JsonApiInterface;
import org.smartregister.anc.domain.Contact;
import org.smartregister.anc.event.RefreshExpansionPanelEvent;
import org.smartregister.anc.fragment.ContactJsonFormFragment;
import org.smartregister.anc.helper.AncRulesEngineFactory;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.ContactJsonFormUtils;
import org.smartregister.anc.util.Utils;
import org.smartregister.anc.view.AncGenericPopupDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ndegwamartin on 30/06/2018.
 */

public class ContactJsonFormActivity extends JsonFormActivity implements JsonApiInterface {

    protected AncRulesEngineFactory rulesEngineFactory = null;
    private ProgressDialog progressDialog;
    private AncGenericDialogInterface genericDialogInterface;
    private ContactJsonFormUtils formUtils = new ContactJsonFormUtils();
    private Utils utils = new Utils();
    private String TAG = this.getClass().getSimpleName();
    private String formName;

    public void init(String json) {
        try {
            mJSONObject = new JSONObject(json);
            if (!mJSONObject.has("encounter_type")) {
                mJSONObject = new JSONObject();
                throw new JSONException("Form encounter_type not set");
            }

            //populate them global values
            if (mJSONObject.has(JsonFormConstants.JSON_FORM_KEY.GLOBAL)) {
                globalValues = new Gson()
                        .fromJson(mJSONObject.getJSONObject(JsonFormConstants.JSON_FORM_KEY.GLOBAL).toString(),
                                new TypeToken<HashMap<String, String>>() {
                                }.getType());
            } else {
                globalValues = new HashMap<>();
            }

            rulesEngineFactory = new AncRulesEngineFactory(this, globalValues, mJSONObject);
            setRulesEngineFactory(rulesEngineFactory);

            confirmCloseTitle = getString(com.vijay.jsonwizard.R.string.confirm_form_close);
            confirmCloseMessage = getString(com.vijay.jsonwizard.R.string.confirm_form_close_explanation);
            localBroadcastManager = LocalBroadcastManager.getInstance(this);

        } catch (JSONException e) {
            Log.e(TAG, "Initialization error. Json passed is invalid : " + e.getMessage(), e);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        formName = getIntent().getStringExtra(Constants.INTENT_KEY.FORM_NAME);
        super.onCreate(savedInstanceState);
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
        JsonWizardFormFragment contactJsonFormFragment =
                ContactJsonFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME);

        getSupportFragmentManager().beginTransaction().add(com.vijay.jsonwizard.R.id.container, contactJsonFormFragment)
                .commit();
    }

    public Contact getContact() {
        Form form = getForm();
        if (form instanceof Contact) {
            return (Contact) form;
        }
        return null;
    }

    @Override
    public void onBackPressed() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {

                //  showProgressDialog("Saving contact progress...");
            }

            @Override
            protected Void doInBackground(Void... nada) {
                Integer contactNo = getIntent().getIntExtra(Constants.INTENT_KEY.CONTACT_NO, 0);

                Contact contact = getContact();
                contact.setJsonForm(currentJsonState());
                contact.setContactNumber(contactNo);
                ContactJsonFormUtils
                        .persistPartial(getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID), contact);
                return null;

            }

            @Override
            protected void onPostExecute(Void result) {
                // hideProgressDialog();
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

        if (!isFinishing()) progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * Get form fields from JSON forms that have sections in the form steps. The JSONObject {@link JSONObject} argument is
     * the object after getting the section in the specified step name The popup {@link boolean} argument is a boolean value
     * to let the function know that the form is being executed on a popup and not the main android view.
     * <p>
     * This function returns a JSONArray {@link JSONArray} of the fields contained in the section for the given step
     *
     * @param sectionJson
     * @param popup
     *
     * @return
     * @throws JSONException
     * @author dubdabasoduba
     */
    @Override
    protected JSONArray returnFormWithSectionFields(JSONObject sectionJson, boolean popup) throws JSONException {
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
     * Get the form fields for the JSON forms that do not use the sections in the steps The JSONObject {@link JSONObject}
     * argument is the object after getting the step name The popup {@link Boolean} argument is a boolean value to let the
     * function know that the form is being executed on a popup and not the main android view.
     * <p>
     * This function returns a JSONArray {@link JSONArray} of the fields contained in the step
     *
     * @param parentJson {@link JSONObject}
     * @param popup      {@link Boolean}
     *
     * @return fields {@link JSONArray}
     * @throws JSONException
     * @author dubdabasoduba
     */
    @Override
    protected JSONArray returnWithFormFields(JSONObject parentJson, boolean popup) throws JSONException {
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
        if (genericDialogInterface != null && genericDialogInterface.getWidgetType() != null &&
                genericDialogInterface.getWidgetType().equals(Constants.EXPANSION_PANEL)) {
            try {
                if (parentJson.has(JsonFormConstants.CONTENT_FORM)) {
                    if (getExtraFieldsWithValues() != null) {
                        fields = getExtraFieldsWithValues();
                    } else {
                        String formLocation = parentJson.has(JsonFormConstants.CONTENT_FORM_LOCATION) ?
                                parentJson.getString(JsonFormConstants.CONTENT_FORM_LOCATION) : "";
                        fields = getSubFormFields(parentJson.get(JsonFormConstants.CONTENT_FORM).toString(), formLocation,
                                fields);
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
                String itemType = item.optString(JsonFormConstants.TYPE, "");
                boolean isSpecialWidget = isSpecialWidget(itemType);

                String parentKey = isSpecialWidget ? cleanWidgetKey(key, itemType) : key;

                if (parentKey.equals(keyAtIndex)) {

                    if (item.has(JsonFormConstants.TEXT)) {
                        item.put(JsonFormConstants.TEXT, value);
                    } else {
                        item.put(JsonFormConstants.VALUE,
                                itemType.equals(JsonFormConstants.HIDDEN) && TextUtils.isEmpty(value) ?
                                        item.has(JsonFormConstants.VALUE) &&
                                                !TextUtils.isEmpty(item.getString(JsonFormConstants.VALUE)) ?
                                                item.getString(JsonFormConstants.VALUE) : value : value);
                    }

                    invokeRefreshLogic(value, popup, parentKey, null);
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
                if (parentKey.equals(keyAtIndex)) {
                    JSONArray jsonArray = item.getJSONArray(childObjectKey);
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject innerItem = jsonArray.getJSONObject(j);
                        String anotherKeyAtIndex = innerItem.getString(JsonFormConstants.KEY);

                        if (childKey.equals(anotherKeyAtIndex)) {
                            innerItem.put(JsonFormConstants.VALUE, value);
                            if (!TextUtils.isEmpty(formName) && formName.equals(Constants.JSON_FORM.ANC_QUICK_CHECK)) {
                                quickCheckDangerSignsSelectionHandler(fields);
                            }

                            invokeRefreshLogic(value, popup, parentKey, childKey);
                            return;
                        }
                    }
                }
            }

        }
    }

    /**
     * Finds gets the currently selected dangers signs on the quick change page and sets the none {@link Boolean} and other
     * {@link Boolean} so as  to identify times to show the refer and proceed buttons on quick check
     * <p>
     * This fix is a bit hacky but feel free to use it
     *
     * @param fields {@link JSONArray}
     *
     * @throws JSONException
     * @author dubdabasoduba
     */
    public void quickCheckDangerSignsSelectionHandler(JSONArray fields) throws JSONException {
        boolean none = false;
        boolean other = false;

        Fragment fragment = getVisibleFragment();
        if (fragment instanceof ContactJsonFormFragment) {
            for (int i = 0; i < fields.length(); i++) {
                JSONObject jsonObject = fields.getJSONObject(i);
                if (jsonObject != null && jsonObject.getString(JsonFormConstants.KEY).equals(Constants.DANGER_SIGNS)) {

                    JSONArray jsonArray = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                    for (int k = 0; k < jsonArray.length(); k++) {
                        JSONObject item = jsonArray.getJSONObject(k);
                        if (item != null && item.getBoolean(JsonFormConstants.VALUE)) {
                            if (item.getString(JsonFormConstants.KEY).equals(Constants.DANGER_NONE)) {
                                none = true;
                            }

                            if (!item.getString(JsonFormConstants.KEY).equals(Constants.DANGER_NONE)) {
                                other = true;
                            }
                        }
                    }
                }
            }

            ((ContactJsonFormFragment) fragment).displayQuickCheckBottomReferralButtons(none, other);
        }
    }

    /**
     * Returns the current visible fragment on the device
     *
     * @return fragment {@link Fragment}
     * @author dubdabasoduba
     */
    public Fragment getVisibleFragment() {
        List<Fragment> fragments = this.getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible()) return fragment;
            }
        }
        return null;
    }


    @Override
    public Facts getValueFromAddressCore(JSONObject object) throws JSONException {
        Facts result = new Facts();
        if (genericDialogInterface != null && genericDialogInterface.getWidgetType() != null &&
                genericDialogInterface.getWidgetType().equals(Constants.EXPANSION_PANEL)) {
            if (object != null) {
                switch (object.getString(JsonFormConstants.TYPE)) {
                    case JsonFormConstants.CHECK_BOX:
                        result = formUtils.getCheckBoxResults(object);
                        break;
                    case JsonFormConstants.NATIVE_RADIO_BUTTON:
                        Boolean multiRelevance =
                                object.optBoolean(JsonFormConstants.NATIVE_RADIO_BUTTON_MULTI_RELEVANCE, false);
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

                if (object.has(RuleConstant.IS_RULE_CHECK) && object.getBoolean(RuleConstant.IS_RULE_CHECK) &&
                        (object.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.CHECK_BOX) ||
                                (object.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.NATIVE_RADIO_BUTTON) &&
                                        object.optBoolean(JsonFormConstants.NATIVE_RADIO_BUTTON_MULTI_RELEVANCE, false)))) {
                    List<String> selectedValues = new ArrayList<>(result.asMap().keySet());
                    result = new Facts();
                    result.put(getKey(object), selectedValues.toString());
                }
            }
        } else {
            return super.getValueFromAddressCore(object);
        }
        return result;
    }

    private Facts getRadioButtonResults(Boolean multiRelevance, JSONObject jsonObject) throws JSONException {
        Facts result = new Facts();
        if (multiRelevance) {
            JSONArray jsonArray = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
            for (int j = 0; j < jsonArray.length(); j++) {
                if (jsonObject.has(JsonFormConstants.VALUE)) {
                    if (jsonObject.getString(JsonFormConstants.VALUE)
                            .equals(jsonArray.getJSONObject(j).getString(JsonFormConstants.KEY))) {
                        result.put(jsonArray.getJSONObject(j).getString(JsonFormConstants.KEY), String.valueOf(true));
                    } else {
                        if (!jsonObject.has(RuleConstant.IS_RULE_CHECK) ||
                                !jsonObject.getBoolean(RuleConstant.IS_RULE_CHECK)) {
                            result.put(jsonArray.getJSONObject(j).getString(JsonFormConstants.KEY), String.valueOf(false));
                        }
                    }
                } else {
                    Log.e(TAG, "option for Key " + jsonArray.getJSONObject(j).getString(JsonFormConstants.KEY) +
                            " has NO value");
                }
            }
        } else {
            result.put(getKey(jsonObject), getValue(jsonObject));
        }

        return result;
    }

    @Override
    public void setGenericPopup(AncGenericPopupDialog context) {
        genericDialogInterface = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        formName = getIntent().getStringExtra(Constants.INTENT_KEY.FORM_NAME);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshExpansionPanel(RefreshExpansionPanelEvent refreshExpansionPanelEvent) {
        if (refreshExpansionPanelEvent != null) {
            try {
                List<String> values;

                if (refreshExpansionPanelEvent.getValues() != null) {
                    values = utils.createExpansionPanelChildren(refreshExpansionPanelEvent.getValues());
                } else {
                    values = new ArrayList<>();
                }
                LinearLayout linearLayout = refreshExpansionPanelEvent.getLinearLayout();
                RelativeLayout layoutHeader = (RelativeLayout) linearLayout.getChildAt(0);
                ImageView status = layoutHeader.findViewById(R.id.statusImageView);
                formUtils.updateExpansionPanelRecyclerView(values, status, getApplicationContext());

                LinearLayout contentLayout = (LinearLayout) linearLayout.getChildAt(1);
                LinearLayout mainContentView = contentLayout.findViewById(R.id.contentView);
                formUtils.addValuesDisplay(values, mainContentView, getApplicationContext());

                LinearLayout buttonLayout = contentLayout.findViewById(R.id.accordion_bottom_navigation);
                Button undoButton = buttonLayout.findViewById(R.id.undo_button);
                if (values != null && values.size() > 0) {
                    undoButton.setVisibility(View.VISIBLE);
                    contentLayout.setVisibility(View.VISIBLE);
                    buttonLayout.setVisibility(View.VISIBLE);
                } else {
                    undoButton.setVisibility(View.GONE);
                    contentLayout.setVisibility(View.GONE);
                    buttonLayout.setVisibility(View.GONE);
                    status.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_task_256));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Partially saves the Quick Check forms details then proceeds to the main contact page
     *
     * @author dubdabasoduba
     */
    public void proceedToMainContactPage() {
        Intent intent = new Intent(this, MainContactActivity.class);

        int contactNo = getIntent().getIntExtra(Constants.INTENT_KEY.CONTACT_NO, 0);
        String baseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);

        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, baseEntityId);
        intent.putExtra(Constants.INTENT_KEY.CLIENT_MAP, getIntent().getSerializableExtra(Constants.INTENT_KEY.CLIENT_MAP));
        intent.putExtra(Constants.INTENT_KEY.FORM_NAME, getIntent().getStringExtra(Constants.INTENT_KEY.FORM_NAME));
        intent.putExtra(Constants.INTENT_KEY.CONTACT_NO, contactNo);

        Contact contact = getContact();
        contact.setJsonForm(currentJsonState());
        contact.setContactNumber(contactNo);
        ContactJsonFormUtils.persistPartial(baseEntityId, getContact());
        this.startActivity(intent);
    }

    /**
     * Stops the ContactJsonForm activity and move to the main register page
     *
     * @author dubdabasoduba
     */
    public void finishInitialQuickCheck() {
        ContactJsonFormActivity.this.finish();
    }
}
