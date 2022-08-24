package org.smartregister.anc.library.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vijay.jsonwizard.activities.FormConfigurationJsonFormActivity;
import org.smartregister.anc.library.constants.ANCJsonFormConstants;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.domain.Contact;
import org.smartregister.anc.library.fragment.ContactWizardJsonFormFragment;
import org.smartregister.anc.library.helper.AncRulesEngineFactory;
import org.smartregister.anc.library.util.ANCFormUtils;
import org.smartregister.anc.library.util.ConstantsUtils;

import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 30/06/2018.
 */

public class ContactJsonFormActivity extends FormConfigurationJsonFormActivity {
    protected AncRulesEngineFactory rulesEngineFactory = null;
    private ProgressDialog progressDialog;
    private String formName;
    private final ANCFormUtils ancFormUtils = new ANCFormUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent() != null) {
            formName = getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.FORM_NAME);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void init(String json) {
        try {
            setmJSONObject(new JSONObject(json));
            if (!getmJSONObject().has(ConstantsUtils.JsonFormKeyUtils.ENCOUNTER_TYPE)) {
                setmJSONObject(new JSONObject());
                throw new JSONException("Form encounter_type not set");
            }

            //populate them global values
            if (getmJSONObject().has(ANCJsonFormConstants.JSON_FORM_KEY.GLOBAL)) {
                globalValues = new Gson()
                        .fromJson(getmJSONObject().getJSONObject(ANCJsonFormConstants.JSON_FORM_KEY.GLOBAL).toString(),
                                new TypeToken<HashMap<String, String>>() {
                                }.getType());
            } else {
                globalValues = new HashMap<>();
            }

            rulesEngineFactory = new AncRulesEngineFactory(this, globalValues, getmJSONObject());
            setRulesEngineFactory(rulesEngineFactory);

            confirmCloseTitle = getString(com.vijay.jsonwizard.R.string.confirm_form_close);
            confirmCloseMessage = getString(com.vijay.jsonwizard.R.string.confirm_form_close_explanation);
            localBroadcastManager = LocalBroadcastManager.getInstance(this);

        } catch (JSONException e) {
            Timber.e(e, "Initialization error. Json passed is invalid : ");
        }
    }

    @Override
    public void initializeFormFragment() {
        initializeFormFragmentCore();
    }

    protected void initializeFormFragmentCore() {
        JsonWizardFormFragment contactJsonFormFragment =
                ContactWizardJsonFormFragment.getFormFragment(ANCJsonFormConstants.FIRST_STEP_NAME);

        getSupportFragmentManager().beginTransaction().add(com.vijay.jsonwizard.R.id.container, contactJsonFormFragment).commit();
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

    @Override
    protected void checkBoxWriteValue(String stepName, String parentKey, String childObjectKey, String childKey,
                                      String value, boolean popup) throws JSONException {
        synchronized (getmJSONObject()) {
            JSONObject jsonObject = getmJSONObject().getJSONObject(stepName);
            JSONArray fields = fetchFields(jsonObject, popup);

            for (int i = 0; i < fields.length(); i++) {
                JSONObject item = fields.getJSONObject(i);
                String keyAtIndex = item.getString(ANCJsonFormConstants.KEY);
                if (parentKey.equals(keyAtIndex)) {
                    JSONArray jsonArray = item.getJSONArray(childObjectKey);
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject innerItem = jsonArray.getJSONObject(j);
                        String anotherKeyAtIndex = innerItem.getString(ANCJsonFormConstants.KEY);

                        if (childKey.equals(anotherKeyAtIndex)) {
                            innerItem.put(ANCJsonFormConstants.VALUE, value);
                            if (StringUtils.isNotBlank(formName) && formName.equals(ConstantsUtils.JsonFormUtils.ANC_QUICK_CHECK)) {
                                quickCheckDangerSignsSelectionHandler(fields);
                            }

                            invokeRefreshLogic(value, popup, parentKey, childKey, stepName, false);
                            return;
                        }
                    }
                }
            }

        }
    }

    @Override
    public void onBackPressed() {
        if (getmJSONObject().optString(ANCJsonFormConstants.ENCOUNTER_TYPE).equals(ConstantsUtils.JsonFormUtils.ANC_PROFILE_ENCOUNTER_TYPE)) {

            int contactNo = getIntent().getIntExtra(ConstantsUtils.IntentKeyUtils.CONTACT_NO, 0);

            // Skip validation on further contacts
            if (contactNo > 1) {
                proceedToMainContactPage();
            }

            else {
                ContactWizardJsonFormFragment contactWizardJsonFormFragment = (ContactWizardJsonFormFragment) getVisibleFragment();
                contactWizardJsonFormFragment.getPresenter().validateAndWriteValues();
                Intent intent = new Intent();
                intent.putExtra("formInvalidFields",
                        getmJSONObject().optString(ANCJsonFormConstants.ENCOUNTER_TYPE) + ":" + contactWizardJsonFormFragment.getPresenter().getInvalidFields().size());
                setResult(RESULT_OK, intent);
            }

        }

        proceedToMainContactPage();
        // new BackPressedPersistPartialTask(getContact(), this, getIntent(), currentJsonState()).execute();
    }

    public Contact getContact() {
        Form form = getForm();
        if (form instanceof Contact) {
            return (Contact) form;
        }
        return null;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getIntent() != null) {
            formName = getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.FORM_NAME);
        }
        try {
            ANCFormUtils.processCheckboxFilteredItems(mJSONObject);
        } catch (JSONException e) {
            Timber.e(e, "An error occurred while trying to filter checkbox items");
        }
    }

    /**
     * Finds gets the currently selected dangers signs on the quick change page and sets the none {@link Boolean} and other
     * {@link Boolean} so as  to identify times to show the refer and proceed buttons on quick check
     * <p>
     * This fix is a bit hacky but feel free to use it
     *
     * @param fields {@link JSONArray}
     * @throws JSONException
     * @author dubdabasoduba
     */
    public void quickCheckDangerSignsSelectionHandler(JSONArray fields) throws JSONException {
        boolean none = false;
        boolean other = false;

        Fragment fragment = getVisibleFragment();
        if (fragment instanceof ContactWizardJsonFormFragment) {
            for (int i = 0; i < fields.length(); i++) {
                JSONObject jsonObject = fields.getJSONObject(i);
                if (jsonObject != null && jsonObject.getString(ANCJsonFormConstants.KEY).equals(ConstantsUtils.DANGER_SIGNS)) {

                    JSONArray jsonArray = jsonObject.getJSONArray(ANCJsonFormConstants.OPTIONS_FIELD_NAME);
                    for (int k = 0; k < jsonArray.length(); k++) {
                        JSONObject item = jsonArray.getJSONObject(k);
                        if (item != null && item.getBoolean(ANCJsonFormConstants.VALUE)) {
                            if (item.getString(ANCJsonFormConstants.KEY).equals(ConstantsUtils.DANGER_NONE)) {
                                none = true;
                            }

                            if (!item.getString(ANCJsonFormConstants.KEY).equals(ConstantsUtils.DANGER_NONE)) {
                                other = true;
                            }
                        }
                    }
                }
            }

            ((ContactWizardJsonFormFragment) fragment).displayQuickCheckBottomReferralButtons(none, other);
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
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible()) return fragment;
        }
        return null;
    }

    protected void callSuperWriteValue(String stepName, String key, String value, String openMrsEntityParent,
                                       String openMrsEntity, String openMrsEntityId, Boolean popup) throws JSONException {
        super.writeValue(stepName, key, value, openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);


    }

    public void showProgressDialog(String titleIdentifier) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle(titleIdentifier);
            progressDialog.setMessage(getString(R.string.dialog_please_wait));
        }

        if (!isFinishing()) progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * Partially saves the contact forms details then proceeds to the main contact page
     *
     * @author dubdabasoduba
     */
    public void proceedToMainContactPage() {
        Intent intent = new Intent(this, AncLibrary.getInstance().getActivityConfiguration().getMainContactActivityClass());

        int contactNo = getIntent().getIntExtra(ConstantsUtils.IntentKeyUtils.CONTACT_NO, 0);
        String baseEntityId = getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID);

        intent.putExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID, baseEntityId);
        intent.putExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP, getIntent().getSerializableExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP));
        intent.putExtra(ConstantsUtils.IntentKeyUtils.FORM_NAME, getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.FORM_NAME));
        intent.putExtra(ConstantsUtils.IntentKeyUtils.CONTACT_NO, contactNo);
        Contact contact = getContact();
        contact.setJsonForm(ancFormUtils.addFormDetails(currentJsonState()));
        contact.setContactNumber(contactNo);
        ANCFormUtils.persistPartial(baseEntityId, getContact());
        this.startActivity(intent);
        this.finish();
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