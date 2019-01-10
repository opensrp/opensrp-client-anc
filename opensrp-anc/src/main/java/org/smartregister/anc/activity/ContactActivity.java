package org.smartregister.anc.activity;

import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.rules.RuleConstant;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.R;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.ContactContract;
import org.smartregister.anc.domain.Contact;
import org.smartregister.anc.fragment.QuickCheckFragment;
import org.smartregister.anc.model.PartialContact;
import org.smartregister.anc.model.PreviousContact;
import org.smartregister.anc.presenter.ContactPresenter;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.FilePath;
import org.smartregister.anc.util.Utils;
import org.smartregister.util.FormUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContactActivity extends BaseContactActivity implements ContactContract.View {

    public static final String DIALOG_TAG = "CONTACT_DIALOG_TAG";
    public static final String TAG = ContactActivity.class.getCanonicalName();

    private TextView patientNameView;

    private Integer contactNo;

    private Map<String, Integer> requiredFieldsMap = new HashMap<>();
    private Map<String, String> eventToFileMap = new HashMap<>();
    private Yaml yaml = new Yaml();
    private Map<String, List<String>> formGlobalKeys = new HashMap<>();
    private Map<String, String> formGlobalValues = new HashMap<>();
    private Set<String> globalKeys = new HashSet<>();
    private Map<String, Map<String, String>> defaultValues = new HashMap<>();
    private Map<String, Map<String, String>> globalValuesMap = new HashMap<>();

    @Override
    protected void onResume() {
        super.onResume();
        if (!presenter.baseEntityIdExists()) {
            presenter.setBaseEntityId(getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID));
        }

        initializeMainContactContainers();

        //Enable/Diable FinalizeButton
        findViewById(R.id.finalize_contact).setEnabled(getRequiredCountTotal() > 0); //TO REMOVE (SWITCH OPERATOR TO ==)

    }

    private void initializeMainContactContainers() {

        try {

            requiredFieldsMap.clear();

            loadContactGlobalsConfig();

            contactNo = getIntent().getIntExtra(Constants.INTENT_KEY.CONTACT_NO, 1);
            process(new String[]{getString(R.string.quick_check), getString(R.string.symptoms_follow_up),
                    getString(R.string.physical_exam), getString(R.string.tests), getString(R.string.counselling_treatment), getString(R.string.profile)});

            List<Contact> contacts = new ArrayList<>();

            Contact quickCheck = new Contact();
            quickCheck.setName(getString(R.string.quick_check));
            quickCheck.setContactNumber(contactNo);
            quickCheck.setBackground(R.drawable.quick_check_bg);
            if (requiredFieldsMap.containsKey(quickCheck.getName())) {
                Integer quickCheckFields = requiredFieldsMap.get(quickCheck.getName());
                quickCheck.setRequiredFields(quickCheckFields != null ? quickCheckFields : 3);
            }
            contacts.add(quickCheck);

            Contact profile = new Contact();
            profile.setName(getString(R.string.profile));
            profile.setContactNumber(contactNo);
            profile.setBackground(R.drawable.profile_bg);
            profile.setActionBarBackground(R.color.contact_profile_actionbar);
            profile.setNavigationBackground(R.color.contact_profile_navigation);
            if (requiredFieldsMap.containsKey(profile.getName())) {
                profile.setRequiredFields(requiredFieldsMap.get(profile.getName()));
            }
            profile.setFormName(Constants.JSON_FORM.ANC_PROFILE);
            contacts.add(profile);

            Contact symptomsAndFollowUp = new Contact();
            symptomsAndFollowUp.setName(getString(R.string.symptoms_follow_up));
            symptomsAndFollowUp.setContactNumber(contactNo);
            symptomsAndFollowUp.setBackground(R.drawable.symptoms_bg);
            symptomsAndFollowUp.setActionBarBackground(R.color.contact_symptoms_actionbar);
            symptomsAndFollowUp.setNavigationBackground(R.color.contact_symptoms_navigation);
            if (requiredFieldsMap.containsKey(symptomsAndFollowUp.getName())) {
                symptomsAndFollowUp.setRequiredFields(requiredFieldsMap.get(symptomsAndFollowUp.getName()));
            }
            symptomsAndFollowUp.setFormName(Constants.JSON_FORM.ANC_SYMPTOMS_FOLLOW_UP);
            contacts.add(symptomsAndFollowUp);

            Contact physicalExam = new Contact();
            physicalExam.setName(getString(R.string.physical_exam));
            physicalExam.setContactNumber(contactNo);
            physicalExam.setBackground(R.drawable.physical_exam_bg);
            physicalExam.setActionBarBackground(R.color.contact_exam_actionbar);
            physicalExam.setNavigationBackground(R.color.contact_exam_navigation);
            if (requiredFieldsMap.containsKey(physicalExam.getName())) {
                physicalExam.setRequiredFields(requiredFieldsMap.get(physicalExam.getName()));
            }
            physicalExam.setFormName(Constants.JSON_FORM.ANC_PHYSICAL_EXAM);
            contacts.add(physicalExam);

            Contact tests = new Contact();
            tests.setName(getString(R.string.tests));
            tests.setContactNumber(contactNo);
            tests.setBackground(R.drawable.tests_bg);
            tests.setActionBarBackground(R.color.contact_tests_actionbar);
            tests.setNavigationBackground(R.color.contact_tests_navigation);
            if (requiredFieldsMap.containsKey(tests.getName())) {
                tests.setRequiredFields(requiredFieldsMap.get(tests.getName()));
            }
            tests.setFormName(Constants.JSON_FORM.ANC_TEST);
            contacts.add(tests);

            Contact counsellingAndTreatment = new Contact();
            counsellingAndTreatment.setName(getString(R.string.counselling_treatment));
            counsellingAndTreatment.setContactNumber(contactNo);
            counsellingAndTreatment.setBackground(R.drawable.counselling_bg);
            counsellingAndTreatment.setActionBarBackground(R.color.contact_counselling_actionbar);
            counsellingAndTreatment.setNavigationBackground(R.color.contact_counselling_navigation);
            if (requiredFieldsMap.containsKey(counsellingAndTreatment.getName())) {
                counsellingAndTreatment.setRequiredFields(requiredFieldsMap.get(counsellingAndTreatment.getName()));
            }
            counsellingAndTreatment.setFormName(Constants.JSON_FORM.ANC_COUNSELLING_TREATMENT);
            contacts.add(counsellingAndTreatment);

            contactAdapter.setContacts(contacts);
            contactAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

    }

    @Override
    protected void initializePresenter() {
        presenter = new ContactPresenter(this);
    }

    @Override
    protected void setupViews() {
        super.setupViews();
        patientNameView = findViewById(R.id.top_patient_name);
        AncApplication.getInstance().populateGlobalSettings();
    }

    @Override
    public void displayPatientName(String patientName) {
        if (patientNameView != null && StringUtils.isNotBlank(patientName)) {
            patientNameView.setText(patientName);
        }
    }

    @Override
    public void startFormActivity(JSONObject form, Contact contact) {
        super.startFormActivity(form, contact);
    }

    @Override
    protected String getFormJson(PartialContact partialContactRequest, JSONObject form) {

        try {
            //partial contact exists?

            PartialContact partialContact = AncApplication.getInstance().getPartialContactRepository().getPartialContact(partialContactRequest);
            String formJsonString = partialContact != null && (partialContact.getFormJson() != null || partialContact.getFormJsonDraft() != null) ? (partialContact.getFormJsonDraft() != null ? partialContact.getFormJsonDraft() : partialContact.getFormJson()) : form.toString();
            JSONObject object = new JSONObject(formJsonString);
            JSONObject globals = form.getJSONObject(Constants.GLOBAL);

            if (globals != null) {
                object.put(Constants.GLOBAL, globals);
            }

            preprocessDefaultValues(object);

            return object.toString();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return "";
        }

    }

    @Override
    public void displayToast(int resourceId) {
        Utils.showToast(getApplicationContext(), getString(resourceId));
    }

    @Override
    public void loadGlobals(Contact contact) {
        List<String> contactGlobals = formGlobalKeys.get(contact.getFormName());

        if (contactGlobals != null) {

            Map<String, String> map = new HashMap<>();
            for (String cg : contactGlobals) {
                if (formGlobalValues.containsKey(cg)) {
                    String some = map.get(cg);
                    if (some == null || !some.equals(formGlobalValues.get(cg))) {

                        map.put(cg, formGlobalValues.get(cg));
                    }
                } else {
                    map.put(cg, "");
                }
            }

            map.put(Constants.KEY.CONTACT_NO, contactNo.toString());

            if (contactNo > 1) {
                map.put(Constants.PREVIOUS_CONTACT_NO, String.valueOf(contactNo - 1));
            }

            contact.setGlobals(map);
        }

    }

    @Override
    public void startQuickCheck(Contact contact) {
        QuickCheckFragment.launchDialog(ContactActivity.this, DIALOG_TAG);
    }

    @Override
    protected void createContacts() {
        try {

            eventToFileMap.put(getString(R.string.quick_check), "anc_quick_check");
            eventToFileMap.put(getString(R.string.profile), Constants.JSON_FORM.ANC_PROFILE);
            eventToFileMap.put(getString(R.string.physical_exam), Constants.JSON_FORM.ANC_PHYSICAL_EXAM);
            eventToFileMap.put(getString(R.string.tests), Constants.JSON_FORM.ANC_TEST);
            eventToFileMap.put(getString(R.string.counselling_treatment), Constants.JSON_FORM.ANC_COUNSELLING_TREATMENT);
            eventToFileMap.put(getString(R.string.symptoms_follow_up), Constants.JSON_FORM.ANC_SYMPTOMS_FOLLOW_UP);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    protected void onCreation() {//Overriden
    }

    private void populateDefaultValues(JSONObject mJSONObject) {

        if (mJSONObject != null) {

            Map<String, Map<String, String>> valueMap = new Gson().fromJson(mJSONObject.toString(), new TypeToken<HashMap<String, Map<String, String>>>() {
            }.getType());

            defaultValues.putAll(valueMap);
        }
    }

    private void populateGlobalValuesMap(JSONObject mJSONObject) {

        if (mJSONObject != null) {

            Map<String, Map<String, String>> valueMap = new Gson().fromJson(mJSONObject.toString(), new TypeToken<HashMap<String, Map<String, String>>>() {
            }.getType());

            globalValuesMap.putAll(valueMap);
        }
    }

    @Override
    protected void onResumption() {//Overriden from Secured Activity

    }

    private void processRequiredStepsField(JSONObject object) throws JSONException {
        if (object != null) {
            Iterator<String> keys = object.keys();

            while (keys.hasNext()) {
                String key = keys.next();

                if (key.startsWith(RuleConstant.STEP)) {
                    JSONArray stepArray = object.getJSONObject(key).getJSONArray(JsonFormConstants.FIELDS);

                    for (int i = 0; i < stepArray.length(); i++) {

                        JSONObject fieldObject = stepArray.getJSONObject(i);

                        processSpecialWidgets(fieldObject);

                        if (fieldObject.has(JsonFormConstants.V_REQUIRED) && fieldObject.getJSONObject(JsonFormConstants.V_REQUIRED).getBoolean(JsonFormConstants.VALUE)) {

                            if (!fieldObject.has(JsonFormConstants.VALUE) || TextUtils.isEmpty(fieldObject.getString(JsonFormConstants.VALUE))) {

                                Integer requiredFieldCount = requiredFieldsMap.get(object.getString(Constants.JSON_FORM_KEY.ENCOUNTER_TYPE));

                                requiredFieldCount = requiredFieldCount == null ? 1 : ++requiredFieldCount;

                                requiredFieldsMap.put(object.getString(Constants.JSON_FORM_KEY.ENCOUNTER_TYPE), requiredFieldCount);
                            }
                        }

                        if (globalKeys.contains(fieldObject.getString(JsonFormConstants.KEY)) && fieldObject.has(JsonFormConstants.VALUE)) {
                            formGlobalValues.put(fieldObject.getString(JsonFormConstants.KEY), fieldObject.getString(JsonFormConstants.VALUE));
                        }

                        if (fieldObject.has(JsonFormConstants.CONTENT_FORM)) {
                            try {

                                JSONObject subFormJson = com.vijay.jsonwizard.utils.FormUtils.getSubFormJson(fieldObject.getString(JsonFormConstants.CONTENT_FORM), fieldObject.has(JsonFormConstants.CONTENT_FORM_LOCATION) ? fieldObject.getString(JsonFormConstants.CONTENT_FORM_LOCATION) : "", this);
                                processRequiredStepsField(createSecondaryFormObject(fieldObject, subFormJson, object.getString(Constants.JSON_FORM_KEY.ENCOUNTER_TYPE)));

                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }

                    }

                }
            }
        }

    }

    private void processSpecialWidgets(JSONObject widget) throws JSONException {
        String widgetType = widget.getString(JsonFormConstants.TYPE);
        List<String> valueList = new ArrayList<>();


        if (widgetType.equals(JsonFormConstants.CHECK_BOX)) {

            JSONArray jsonArray = widget.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                if (jsonObject.has(JsonFormConstants.VALUE) && !TextUtils.isEmpty(jsonObject.getString(JsonFormConstants.VALUE)) && jsonObject.getString(JsonFormConstants.VALUE).equals("true")) {

                    valueList.add(jsonObject.getString(JsonFormConstants.KEY));

                }

            }

            if (valueList.size() > 0) {
                widget.put(JsonFormConstants.VALUE, valueList.toString());
            }
        }
    }

    private JSONObject createSecondaryFormObject(JSONObject parentObject, JSONObject jsonSubForm, String encounterType) throws JSONException {

        Map<String, String> vMap = new HashMap<>();

        JSONObject resultJsonObject = new JSONObject();

        JSONObject stepJsonObject = new JSONObject();

        JSONArray fieldsJsonArray = jsonSubForm.getJSONArray(JsonFormConstants.CONTENT_FORM);

        if (parentObject.has(JsonFormConstants.VALUE) && !TextUtils.isEmpty(parentObject.getString(JsonFormConstants.VALUE))) {
            if (parentObject.get(JsonFormConstants.VALUE) instanceof JSONArray) {
                JSONArray jsonArray = parentObject.getJSONArray(JsonFormConstants.VALUE);
                for (int j = 0; j < jsonArray.length(); j++) {

                    populateValueMap(vMap, jsonArray.getJSONObject(j));
                }

            } else {

                populateValueMap(vMap, parentObject.getJSONObject(JsonFormConstants.VALUE));
            }

            for (int l = 0; l < fieldsJsonArray.length(); l++) {

                String value = vMap.get(fieldsJsonArray.getJSONObject(l).getString(JsonFormConstants.KEY));
                if (!TextUtils.isEmpty(value)) {
                    fieldsJsonArray.getJSONObject(l).put(JsonFormConstants.VALUE, value);
                }
            }

        }

        stepJsonObject.put(JsonFormConstants.FIELDS, fieldsJsonArray);

        resultJsonObject.put(JsonFormConstants.FIRST_STEP_NAME, stepJsonObject);

        resultJsonObject.put(Constants.JSON_FORM_KEY.ENCOUNTER_TYPE, encounterType);

        return resultJsonObject;

    }

    private void populateValueMap(Map<String, String> vMap, JSONObject jsonObject) throws JSONException {

        JSONObject valueObject = jsonObject;
        String key = valueObject.getString(JsonFormConstants.KEY);
        JSONArray values = valueObject.getJSONArray(JsonFormConstants.VALUES);
        for (int k = 0; k < values.length(); k++) {
            String valuesString = values.getString(k);

            vMap.put(key, valuesString.contains(":") ? valuesString.substring(0, valuesString.indexOf(":")) : valuesString);
        }
    }

    private void process(String[] mainContactForms) throws Exception {

        JSONObject object;
        List<String> partialForms = new ArrayList<>(Arrays.asList(mainContactForms));

        List<PartialContact> partialContacts = AncApplication.getInstance().getPartialContactRepository().getPartialContacts(getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID), contactNo);

        for (PartialContact partialContact : partialContacts) {
            if (partialContact.getFormJsonDraft() != null || partialContact.getFormJson() != null) {
                object = new JSONObject(partialContact.getFormJsonDraft() != null ? partialContact.getFormJsonDraft() : partialContact.getFormJson());
                processRequiredStepsField(object);
                partialForms.remove(object.getString(Constants.JSON_FORM_KEY.ENCOUNTER_TYPE));
            }
        }

        for (String formEventType : partialForms) {

            if (eventToFileMap.containsKey(formEventType)) {
                object = FormUtils.getInstance(AncApplication.getInstance().getApplicationContext()).getFormJson(eventToFileMap.get(formEventType));
                processRequiredStepsField(object);
            }

        }
    }

    private int getRequiredCountTotal() {

        int count = 0;
        for (Map.Entry<String, Integer> entry : requiredFieldsMap.entrySet()) {
            count += entry.getValue();
        }

        return count;
    }

    private void loadContactGlobalsConfig() throws IOException {
        Iterable<Object> contactGlobals = readYaml(FilePath.FILE.CONTACT_GLOBALS);

        for (Object ruleObject : contactGlobals) {
            Map<String, Object> map = ((Map<String, Object>) ruleObject);

            formGlobalKeys.put(map.get(Constants.FORM).toString(), (List<String>) map.get(JsonFormConstants.FIELDS));
            globalKeys.addAll((List<String>) map.get(JsonFormConstants.FIELDS));
        }
    }

    public Iterable<Object> readYaml(String filename) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(this.getAssets().open((FilePath.FOLDER.CONFIG_FOLDER_PATH + filename)));
        return yaml.loadAll(inputStreamReader);
    }

    private String getMapValue(Map<String, String> valueMap) throws JSONException {

        String value = null;

        PreviousContact previousContact = AncApplication.getInstance().getPreviousContactRepository().getPreviousContacts(getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID), valueMap.get(Constants.KEY.FORM));

        if (previousContact != null) {
            String step = valueMap.get(Constants.KEY.STEP);
            String fieldKey = valueMap.get(Constants.KEY.KEY);

            value = getFormValue(previousContact.getFormJson(), step, fieldKey);
        }

        return value;
    }

    private String getFormValue(String formJson, String step, String fieldKey) throws JSONException {
        JSONObject object = new JSONObject(formJson);
        String value = "";
        if (object != null) {
            Iterator<String> keys = object.keys();
            boolean broken = false;
            while (keys.hasNext() && !broken) {
                String key = keys.next();

                if (key.equals(Constants.KEY.STEP + step)) {
                    JSONArray stepArray = object.getJSONObject(key).getJSONArray(JsonFormConstants.FIELDS);
                    for (int i = 0; i < stepArray.length(); i++) {

                        JSONObject fieldObject = stepArray.getJSONObject(i);
                        if (fieldKey.equals(fieldObject.getString(JsonFormConstants.KEY))) {
                            processSpecialWidgets(fieldObject);
                            value = fieldObject.has(JsonFormConstants.VALUE) && fieldObject.getString(JsonFormConstants.VALUE) != null ? fieldObject.getString(JsonFormConstants.VALUE) : "";

                            broken = true;
                            break;
                        }
                    }
                }
            }
        }


        return value;
    }


    private void preprocessDefaultValues(JSONObject object) {
        try {
            if (object != null) {
                Iterator<String> keys = object.keys();

                while (keys.hasNext()) {
                    String key = keys.next();

                    if (key.equals(Constants.DEFAULT_VALUES)) {

                        JSONArray defautValues = object.getJSONArray(key);

                        for (int i = 0; i < defautValues.length(); i++) {

                            JSONObject defaultValue = defautValues.getJSONObject(i);
                            populateDefaultValues(defaultValue);
                        }
                    }


                    if (key.equals(Constants.GLOBAL_PREVIOUS)) {

                        JSONArray defautValues = object.getJSONArray(key);

                        for (int i = 0; i < defautValues.length(); i++) {

                            JSONObject defaultValue = defautValues.getJSONObject(i);
                            populateGlobalValuesMap(defaultValue);
                        }
                    }

                    if (key.startsWith(RuleConstant.STEP)) {
                        JSONArray stepArray = object.getJSONObject(key).getJSONArray(JsonFormConstants.FIELDS);

                        for (int i = 0; i < stepArray.length(); i++) {

                            JSONObject fieldObject = stepArray.getJSONObject(i);

                            if (defaultValues.containsKey(fieldObject.getString(JsonFormConstants.KEY)) && (!fieldObject.has(JsonFormConstants.VALUE) || TextUtils.isEmpty(fieldObject.getString(JsonFormConstants.VALUE)))) {

                                Map<String, String> defaultMap = defaultValues.get(fieldObject.getString(JsonFormConstants.KEY));

                                String mapValue = getMapValue(defaultMap);
                                if (mapValue != null) {
                                    fieldObject.put(JsonFormConstants.VALUE, mapValue);
                                }

                            }

                            if (globalValuesMap.containsKey(fieldObject.getString(JsonFormConstants.KEY)) && fieldObject.has(JsonFormConstants.VALUE) && !TextUtils.isEmpty(fieldObject.getString(JsonFormConstants.VALUE))) {

                                Map<String, String> defaultMap = globalValuesMap.get(fieldObject.getString(JsonFormConstants.KEY));

                                String mapValue = getMapValue(defaultMap);
                                if (mapValue != null) {
                                    if (object.has(Constants.GLOBAL)) {
                                        object.getJSONObject(Constants.GLOBAL).put(fieldObject.getString(JsonFormConstants.KEY), mapValue);
                                    } else {

                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("previous_" + fieldObject.getString(JsonFormConstants.KEY), mapValue);
                                        object.put(Constants.GLOBAL, jsonObject);
                                    }
                                }

                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());

        }
    }

}


