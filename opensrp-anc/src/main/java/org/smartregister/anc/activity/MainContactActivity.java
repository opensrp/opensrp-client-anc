package org.smartregister.anc.activity;

import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

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
import org.smartregister.anc.model.PartialContact;
import org.smartregister.anc.model.PreviousContact;
import org.smartregister.anc.presenter.ContactPresenter;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.ContactJsonFormUtils;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.FilePath;
import org.smartregister.anc.util.Utils;
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

public class MainContactActivity extends BaseContactActivity implements ContactContract.View {

    public static final String TAG = MainContactActivity.class.getCanonicalName();

    private TextView patientNameView;

    private Map<String, Integer> requiredFieldsMap = new HashMap<>();
    private Map<String, String> eventToFileMap = new HashMap<>();
    private Yaml yaml = new Yaml();
    private Map<String, List<String>> formGlobalKeys = new HashMap<>();
    private Map<String, String> formGlobalValues = new HashMap<>();
    private Set<String> globalKeys = new HashSet<>();
    private List<String> defaultValueFields = new ArrayList<>();
    private List<String> globalValueFields = new ArrayList<>();
    private List<String> editableFields = new ArrayList<>();
    private String baseEntityId;
    private String womanAge = "";

    public static void processAbnormalValues(Map<String, String> facts, JSONObject jsonObject) throws Exception {

        String fieldKey = ContactJsonFormUtils.getKey(jsonObject);
        Object fieldValue = ContactJsonFormUtils.getValue(jsonObject);
        String fieldKeySecondary = fieldKey.contains(Constants.SUFFIX.OTHER) ?
                fieldKey.substring(0, fieldKey.indexOf(Constants.SUFFIX.OTHER)) + Constants.SUFFIX.VALUE : "";
        String fieldKeyOtherValue = fieldKey + Constants.SUFFIX.VALUE;

        if (fieldKey.endsWith(Constants.SUFFIX.OTHER) && !fieldKeySecondary.isEmpty() &&
                facts.get(fieldKeySecondary) != null && facts.get(fieldKeyOtherValue) != null) {

            List<String> tempList = new ArrayList<>(Arrays.asList(facts.get(fieldKeySecondary).split("\\s*,\\s*")));
            tempList.remove(tempList.size() - 1);
            tempList.add(StringUtils.capitalize(facts.get(fieldKeyOtherValue)));
            facts.put(fieldKeySecondary, ContactJsonFormUtils.getListValuesAsString(tempList));

        } else {
            facts.put(fieldKey, fieldValue.toString());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        baseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);
        contactNo = getIntent().getIntExtra(Constants.INTENT_KEY.CONTACT_NO, 1);
        @SuppressWarnings("unchecked")
        Map<String, String> womanDetails = (Map<String, String>) getIntent()
                .getSerializableExtra(Constants.INTENT_KEY.CLIENT_MAP);
        womanAge = String.valueOf(Utils.getAgeFromDate(womanDetails.get(DBConstants.KEY.DOB)));

        if (!presenter.baseEntityIdExists()) {
            presenter.setBaseEntityId(baseEntityId);
        }

        initializeMainContactContainers();

        //Enable/Diable FinalizeButton
        findViewById(R.id.finalize_contact).setEnabled(getRequiredCountTotal() > 0); //TO REMOVE (SWITCH OPERATOR TO ==)

    }

    private void initializeMainContactContainers() {

        try {

            requiredFieldsMap.clear();

            loadContactGlobalsConfig();

            process(new String[]{Constants.JSON_FORM.ANC_QUICK_CHECK, Constants.JSON_FORM.ANC_PROFILE,
                    Constants.JSON_FORM.ANC_SYMPTOMS_FOLLOW_UP, Constants.JSON_FORM.ANC_PHYSICAL_EXAM,
                    Constants.JSON_FORM.ANC_TEST, Constants.JSON_FORM.ANC_COUNSELLING_TREATMENT});

            List<Contact> contacts = new ArrayList<>();

            Contact quickCheck = new Contact();
            quickCheck.setName(getString(R.string.quick_check));
            quickCheck.setContactNumber(contactNo);
            quickCheck.setActionBarBackground(R.color.quick_check_red);
            quickCheck.setBackground(R.drawable.quick_check_bg);
            quickCheck.setWizard(false);
            quickCheck.setHideSaveLabel(true);
            if (requiredFieldsMap.containsKey(quickCheck.getName())) {
                Integer quickCheckFields = requiredFieldsMap.get(quickCheck.getName());
                quickCheck.setRequiredFields(quickCheckFields != null ? quickCheckFields : 0);
            }

            quickCheck.setFormName(Constants.JSON_FORM.ANC_QUICK_CHECK);
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
            JSONObject object = ContactJsonFormUtils.getFormJsonCore(partialContactRequest, form);

            preProcessDefaultValues(object);

            return object.toString();

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return "";

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

            //Inject some form defaults from client details
            map.put(Constants.KEY.CONTACT_NO, contactNo.toString());
            map.put(Constants.PREVIOUS_CONTACT_NO, contactNo > 1 ? String.valueOf(contactNo - 1) : "0");
            map.put(Constants.AGE, womanAge);

            //Handle Gestation age. Use the latest calculated gestation age. Checks if the Current
            //Gestation Age is greater than the previously stored Gestation age
            String gestAgeInMap = formGlobalValues.get(Constants.GEST_AGE_OPENMRS);
            int previousGestAge = gestAgeInMap != null ? Integer.parseInt(gestAgeInMap) : 0;
            if (previousGestAge < presenter.getGestationAge()) {
                map.put(Constants.GEST_AGE_OPENMRS, String.valueOf(presenter.getGestationAge()));
            }

            String lastContactDate =
                    ((HashMap<String, String>) getIntent().getSerializableExtra(Constants.INTENT_KEY.CLIENT_MAP))
                            .get(DBConstants.KEY.LAST_CONTACT_RECORD_DATE);
            map.put(Constants.KEY.LAST_CONTACT_DATE,
                    !TextUtils.isEmpty(lastContactDate) ? Utils.reverseHyphenSeperatedValues(lastContactDate, "-") : "");

            contact.setGlobals(map);
        }

    }

    @Override
    protected void createContacts() {
        try {
            eventToFileMap.put(getString(R.string.quick_check), Constants.JSON_FORM.ANC_QUICK_CHECK);
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

    private List<String> getListValues(JSONArray jsonArray) {
        if (jsonArray != null) {
            return AncApplication.getInstance().getGsonInstance()
                    .fromJson(jsonArray.toString(), new TypeToken<List<String>>() {
                    }.getType());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    protected void onResumption() {//Overridden from Secured Activity

    }

    private void processRequiredStepsField(JSONObject object) throws Exception {
        if (object != null) {
            Iterator<String> keys = object.keys();

            while (keys.hasNext()) {
                String key = keys.next();

                if (key.startsWith(RuleConstant.STEP)) {
                    JSONArray stepArray = object.getJSONObject(key).getJSONArray(JsonFormConstants.FIELDS);

                    for (int i = 0; i < stepArray.length(); i++) {
                        JSONObject fieldObject = stepArray.getJSONObject(i);
                        ContactJsonFormUtils.processSpecialWidgets(fieldObject);
                        boolean isValueRequired = false;

                        if (fieldObject.has(JsonFormConstants.V_REQUIRED)) {
                            JSONObject valueRequired = fieldObject.getJSONObject(JsonFormConstants.V_REQUIRED);
                            String value = valueRequired.getString(JsonFormConstants.VALUE);
                            isValueRequired = Boolean.parseBoolean(value);
                        }

                        boolean isRequiredField =
                                !fieldObject.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.LABEL) &&
                                        !fieldObject.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.HIDDEN) &&
                                        isValueRequired;

                        setRequiredCount(object, fieldObject, isRequiredField);
                        updateFormGlobalValues(fieldObject);
                        checkRequiredForSubForms(object, fieldObject);
                    }
                }
            }
        }
    }

    private void updateFormGlobalValues(JSONObject fieldObject) throws Exception {
        if (globalKeys.contains(fieldObject.getString(JsonFormConstants.KEY)) &&
                fieldObject.has(JsonFormConstants.VALUE)) {

            formGlobalValues.put(fieldObject.getString(JsonFormConstants.KEY),
                    fieldObject.getString(JsonFormConstants.VALUE));//Normal value
            processAbnormalValues(formGlobalValues, fieldObject);


            String secKey = ContactJsonFormUtils.getSecondaryKey(fieldObject);
            if (fieldObject.has(secKey)) {
                formGlobalValues.put(secKey, fieldObject.getString(secKey));//Normal value secondary key
            }

            if (fieldObject.has(Constants.KEY.SECONDARY_VALUES)) {

                fieldObject.put(Constants.KEY.SECONDARY_VALUES,
                        ContactJsonFormUtils.sortSecondaryValues(fieldObject));//sort and reset

                JSONArray secondaryValues = fieldObject.getJSONArray(Constants.KEY.SECONDARY_VALUES);

                for (int j = 0; j < secondaryValues.length(); j++) {
                    JSONObject jsonObject = secondaryValues.getJSONObject(j);
                    processAbnormalValues(formGlobalValues, jsonObject);


                }
            }
            checkRequiredForCheckBoxOther(fieldObject);

        }
    }

    private void setRequiredCount(JSONObject object, JSONObject fieldObject, boolean isRequiredField) throws JSONException {
        if (isRequiredField && (!fieldObject.has(JsonFormConstants.VALUE) ||
                TextUtils.isEmpty(fieldObject.getString(JsonFormConstants.VALUE)))) {

            Integer requiredFieldCount = requiredFieldsMap.get(object.getString(Constants.JSON_FORM_KEY.ENCOUNTER_TYPE));

            requiredFieldCount = requiredFieldCount == null ? 1 : ++requiredFieldCount;

            if (fieldObject.has(JsonFormConstants.IS_VISIBLE) && !fieldObject.getBoolean(JsonFormConstants.IS_VISIBLE)) {
                --requiredFieldCount;
            }

            requiredFieldsMap.put(object.getString(Constants.JSON_FORM_KEY.ENCOUNTER_TYPE), requiredFieldCount);

        }
    }

    private void checkRequiredForCheckBoxOther(JSONObject fieldObject) throws Exception {
        //Other field for check boxes
        if (fieldObject.has(JsonFormConstants.VALUE) && !TextUtils.isEmpty(fieldObject.getString(JsonFormConstants.VALUE)) &&
                fieldObject.getString(Constants.KEY.KEY).endsWith(Constants.SUFFIX.OTHER) && formGlobalValues
                .get(fieldObject.getString(Constants.KEY.KEY).replace(Constants.SUFFIX.OTHER, Constants.SUFFIX.VALUE)) !=
                null) {

            formGlobalValues
                    .put(ContactJsonFormUtils.getSecondaryKey(fieldObject), fieldObject.getString(JsonFormConstants.VALUE));
            processAbnormalValues(formGlobalValues, fieldObject);

        }
    }

    private void checkRequiredForSubForms(JSONObject object, JSONObject fieldObject) throws JSONException {
        if (fieldObject.has(JsonFormConstants.CONTENT_FORM)) {

            if ((fieldObject.has(JsonFormConstants.IS_VISIBLE) && !fieldObject.getBoolean(JsonFormConstants.IS_VISIBLE))) {
                return;
            }
            try {

                JSONObject subFormJson = com.vijay.jsonwizard.utils.FormUtils
                        .getSubFormJson(fieldObject.getString(JsonFormConstants.CONTENT_FORM),
                                fieldObject.has(JsonFormConstants.CONTENT_FORM_LOCATION) ?
                                        fieldObject.getString(JsonFormConstants.CONTENT_FORM_LOCATION) : "", this);
                processRequiredStepsField(ContactJsonFormUtils.createSecondaryFormObject(fieldObject, subFormJson,
                        object.getString(Constants.JSON_FORM_KEY.ENCOUNTER_TYPE)));

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private void process(String[] mainContactForms) throws Exception {

        JSONObject object;
        List<String> partialForms = new ArrayList<>(Arrays.asList(mainContactForms));

        List<PartialContact> partialContacts = AncApplication.getInstance().getPartialContactRepository()
                .getPartialContacts(getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID), contactNo);

        for (PartialContact partialContact : partialContacts) {
            if (partialContact.getFormJsonDraft() != null || partialContact.getFormJson() != null) {
                object = new JSONObject(partialContact.getFormJsonDraft() != null ? partialContact.getFormJsonDraft() :
                        partialContact.getFormJson());
                processRequiredStepsField(object);
                if (object.has(Constants.JSON_FORM_KEY.ENCOUNTER_TYPE)) {
                    partialForms.remove(eventToFileMap.get(object.getString(Constants.JSON_FORM_KEY.ENCOUNTER_TYPE)));
                }
            }
        }

        Set<String> myKeys = new HashSet<>();
        for (String nonDraftForm : partialForms) {
            List<String> formKeys = formGlobalKeys.get(nonDraftForm);
            if (formKeys != null) {
                myKeys.addAll(formKeys);
            }
        }

        for (String key : myKeys) {
            String value = getMapValue(key);
            if (value != null) {
                formGlobalValues.put(key, value);
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
        InputStreamReader inputStreamReader =
                new InputStreamReader(this.getAssets().open((FilePath.FOLDER.CONFIG_FOLDER_PATH + filename)));
        return yaml.loadAll(inputStreamReader);
    }

    private String getMapValue(String key) {

        PreviousContact request = new PreviousContact();
        request.setBaseEntityId(baseEntityId);
        request.setKey(key);

        PreviousContact previousContact =
                AncApplication.getInstance().getPreviousContactRepository().getPreviousContact(request);

        return previousContact != null ? previousContact.getValue() : null;
    }

    private void preProcessDefaultValues(JSONObject object) {
        try {
            if (object != null) {
                Iterator<String> keys = object.keys();

                while (keys.hasNext()) {
                    String key = keys.next();

                    if (Constants.DEFAULT_VALUES.equals(key)) {

                        JSONArray globalPreviousValues = object.getJSONArray(key);
                        defaultValueFields = getListValues(globalPreviousValues);
                    }

                    if (Constants.GLOBAL_PREVIOUS.equals(key)) {

                        JSONArray globalPreviousValues = object.getJSONArray(key);
                        globalValueFields = getListValues(globalPreviousValues);

                    }

                    if (Constants.EDITABLE_FIELDS.equals(key)) {

                        JSONArray editableFieldValues = object.getJSONArray(key);
                        editableFields = getListValues(editableFieldValues);

                    }

                    if (key.startsWith(RuleConstant.STEP)) {
                        JSONArray stepArray = object.getJSONObject(key).getJSONArray(JsonFormConstants.FIELDS);

                        for (int i = 0; i < stepArray.length(); i++) {
                            JSONObject fieldObject = stepArray.getJSONObject(i);
                            updateDefaultValues(stepArray, i, fieldObject);
                        }
                    }
                }
                getValueMap(object);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /***
     * This method initializes all global_previous values with empty strings
     * @param object Form Json object
     * @throws JSONException
     */
    private void initializeGlobalPreviousValues(JSONObject object) throws JSONException {
        if (object.has(Constants.GLOBAL_PREVIOUS)) {
            JSONArray globalPreviousArray = object.getJSONArray(Constants.GLOBAL_PREVIOUS);
            for (int i = 0; i < globalPreviousArray.length(); i++) {
                if (object.has(JsonFormConstants.JSON_FORM_KEY.GLOBAL)) {
                    object.getJSONObject(JsonFormConstants.JSON_FORM_KEY.GLOBAL)
                            .put(Constants.PREFIX.PREVIOUS + globalPreviousArray.getString(i), "");
                }
            }
        }
    }

    private void updateDefaultValues(JSONArray stepArray, int i, JSONObject fieldObject) throws JSONException {
        if (defaultValueFields.contains(fieldObject.getString(JsonFormConstants.KEY))) {

            if (!fieldObject.has(JsonFormConstants.VALUE) ||
                    TextUtils.isEmpty(fieldObject.getString(JsonFormConstants.VALUE))) {

                String defaultKey = fieldObject.getString(JsonFormConstants.KEY);
                String mapValue = getMapValue(defaultKey);

                if (mapValue != null) {
                    fieldObject.put(JsonFormConstants.VALUE, mapValue);
                    fieldObject.put(JsonFormConstants.EDITABLE, editableFields.contains(defaultKey));
                    fieldObject.put(JsonFormConstants.READ_ONLY, editableFields.contains(defaultKey));
                }

            }

            if (fieldObject.has(JsonFormConstants.OPTIONS_FIELD_NAME)) {
                boolean addDefaults = true;

                for (int m = 0; m < fieldObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME).length(); m++) {
                    String optionValue;
                    if (fieldObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME).getJSONObject(m)
                            .has(JsonFormConstants.VALUE)) {
                        optionValue = fieldObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME).getJSONObject(m)
                                .getString(JsonFormConstants.VALUE);
                        if (Constants.BOOLEAN.TRUE.equals(optionValue)) {
                            addDefaults = false;
                            break;
                        }
                    }
                }

                if (addDefaults && fieldObject.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.CHECK_BOX) &&
                        fieldObject.has(JsonFormConstants.VALUE)) {
                    List<String> values = Arrays.asList(fieldObject.getString(JsonFormConstants.VALUE)
                            .substring(1, fieldObject.getString(JsonFormConstants.VALUE).length() - 1).split(", "));

                    for (int m = 0; m < fieldObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME).length(); m++) {

                        if (values.contains(fieldObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME).getJSONObject(m)
                                .getString(JsonFormConstants.KEY))) {
                            stepArray.getJSONObject(i).getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME).getJSONObject(m)
                                    .put(JsonFormConstants.VALUE, true);
                            fieldObject.put(JsonFormConstants.EDITABLE,
                                    editableFields.contains(fieldObject.getString(JsonFormConstants.KEY)));
                            fieldObject.put(JsonFormConstants.READ_ONLY,
                                    editableFields.contains(fieldObject.getString(JsonFormConstants.KEY)));
                        }

                    }

                }
            }

        }
    }

    private void getValueMap(JSONObject object) throws JSONException {
        initializeGlobalPreviousValues(object);
        for (int i = 0; i < globalValueFields.size(); i++) {
            String mapValue = getMapValue(globalValueFields.get(i));
            if (mapValue != null) {
                if (object.has(JsonFormConstants.JSON_FORM_KEY.GLOBAL)) {
                    object.getJSONObject(JsonFormConstants.JSON_FORM_KEY.GLOBAL)
                            .put(Constants.PREFIX.PREVIOUS + globalValueFields.get(i), mapValue);
                } else {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(Constants.PREFIX.PREVIOUS + globalValueFields.get(i), mapValue);
                    object.put(JsonFormConstants.JSON_FORM_KEY.GLOBAL, jsonObject);
                }
            }

        }
    }
}
