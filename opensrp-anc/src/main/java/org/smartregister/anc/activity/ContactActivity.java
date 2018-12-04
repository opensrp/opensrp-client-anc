package org.smartregister.anc.activity;

import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.rules.RuleConstant;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.anc.R;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.ContactContract;
import org.smartregister.anc.domain.Contact;
import org.smartregister.anc.fragment.QuickCheckFragment;
import org.smartregister.anc.model.PartialContact;
import org.smartregister.anc.presenter.ContactPresenter;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.Utils;
import org.smartregister.util.FormUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ContactActivity extends BaseContactActivity implements ContactContract.View {

    public static final String DIALOG_TAG = "CONTACT_DIALOG_TAG";

    private TextView patientNameView;

    private Integer contactNo;

    private Map<String, Integer> requiredFieldsMap = new HashMap<>();
    private Map<String, String> eventToFileMap = new HashMap<>();

    @Override
    protected void onResume() {
        super.onResume();
        if (!presenter.baseEntityIdExists()) {
            presenter.setBaseEntityId(getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID));
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
    public void displayToast(int resourceId) {
        Utils.showToast(getApplicationContext(), getString(resourceId));
    }

    @Override
    public void startQuickCheck(Contact contact) {
        QuickCheckFragment.launchDialog(ContactActivity.this, DIALOG_TAG);
    }

    @Override
    protected void createContacts() {
        try {

            contactNo = getIntent().getIntExtra(Constants.INTENT_KEY.CONTACT_NO, 1);

            eventToFileMap.put(getString(R.string.quick_check), "anc_quick_check");
            eventToFileMap.put(getString(R.string.profile), Constants.JSON_FORM.ANC_PROFILE);
            eventToFileMap.put(getString(R.string.physical_exam), Constants.JSON_FORM.ANC_PHYSICAL_EXAM);
            eventToFileMap.put(getString(R.string.tests), Constants.JSON_FORM.ANC_TEST);
            eventToFileMap.put(getString(R.string.counselling_treatment), Constants.JSON_FORM.ANC_COUNSELLING_TREATMENT);
            eventToFileMap.put(getString(R.string.symptoms_follow_up), Constants.JSON_FORM.ANC_SYMPTOMS_FOLLOW_UP);

            process(new String[]{getString(R.string.quick_check), getString(R.string.symptoms_follow_up), getString(R.string.physical_exam), getString(R.string.tests), getString(R.string.counselling_treatment), getString(R.string.profile)});

            List<Contact> contacts = new ArrayList<>();

            Contact quickCheck = new Contact();
            quickCheck.setName(getString(R.string.quick_check));
            quickCheck.setContactNumber(contactNo);
            quickCheck.setBackground(R.drawable.quick_check_bg);
            Integer quickCheckFields = requiredFieldsMap.get(quickCheck.getName());
            quickCheck.setRequiredFields(quickCheckFields != null ? quickCheckFields : 3);
            contacts.add(quickCheck);

            Contact profile = new Contact();
            profile.setName(getString(R.string.profile));
            profile.setContactNumber(contactNo);
            profile.setBackground(R.drawable.profile_bg);
            profile.setActionBarBackground(R.color.contact_profile_actionbar);
            profile.setNavigationBackground(R.color.contact_profile_navigation);
            profile.setRequiredFields(requiredFieldsMap.get(profile.getName()));
            profile.setFormName(Constants.JSON_FORM.ANC_PROFILE);
            contacts.add(profile);

            Contact symptomsAndFollowUp = new Contact();
            symptomsAndFollowUp.setName(getString(R.string.symptoms_follow_up));
            symptomsAndFollowUp.setContactNumber(contactNo);
            symptomsAndFollowUp.setBackground(R.drawable.symptoms_bg);
            symptomsAndFollowUp.setActionBarBackground(R.color.contact_symptoms_actionbar);
            symptomsAndFollowUp.setNavigationBackground(R.color.contact_symptoms_navigation);
            symptomsAndFollowUp.setRequiredFields(requiredFieldsMap.get(symptomsAndFollowUp.getName()));
            symptomsAndFollowUp.setFormName(Constants.JSON_FORM.ANC_SYMPTOMS_FOLLOW_UP);
            contacts.add(symptomsAndFollowUp);

            Contact physicalExam = new Contact();
            physicalExam.setName(getString(R.string.physical_exam));
            physicalExam.setContactNumber(contactNo);
            physicalExam.setBackground(R.drawable.physical_exam_bg);
            physicalExam.setActionBarBackground(R.color.contact_exam_actionbar);
            physicalExam.setNavigationBackground(R.color.contact_exam_navigation);
            physicalExam.setRequiredFields(requiredFieldsMap.get(physicalExam.getName()));
            physicalExam.setFormName(Constants.JSON_FORM.ANC_PHYSICAL_EXAM);
            contacts.add(physicalExam);

            Contact tests = new Contact();
            tests.setName(getString(R.string.tests));
            tests.setContactNumber(contactNo);
            tests.setBackground(R.drawable.tests_bg);
            tests.setActionBarBackground(R.color.contact_tests_actionbar);
            tests.setNavigationBackground(R.color.contact_tests_navigation);
            tests.setRequiredFields(requiredFieldsMap.get(tests.getName()));
            tests.setFormName(Constants.JSON_FORM.ANC_TEST);
            contacts.add(tests);

            Contact counsellingAndTreatment = new Contact();
            counsellingAndTreatment.setName(getString(R.string.counselling_treatment));
            counsellingAndTreatment.setContactNumber(contactNo);
            counsellingAndTreatment.setBackground(R.drawable.counselling_bg);
            counsellingAndTreatment.setActionBarBackground(R.color.contact_counselling_actionbar);
            counsellingAndTreatment.setNavigationBackground(R.color.contact_counselling_navigation);
            counsellingAndTreatment.setRequiredFields(requiredFieldsMap.get(counsellingAndTreatment.getName()));
            counsellingAndTreatment.setFormName(Constants.JSON_FORM.ANC_COUNSELLING_TREATMENT);
            contacts.add(counsellingAndTreatment);

            contactAdapter.setContacts(contacts);
            contactAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            Log.e("$$", e.getMessage());
        }
    }

    @Override
    protected void onCreation() { //Overriden from Secured Activity
    }

    @Override
    protected void onResumption() {//Overriden from Secured Activity

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

                        if (fieldObject.has(JsonFormConstants.V_REQUIRED) && fieldObject.getJSONObject(JsonFormConstants.V_REQUIRED).getBoolean(JsonFormConstants.VALUE)) {

                            if (!fieldObject.has(JsonFormConstants.VALUE) || TextUtils.isEmpty(fieldObject.getString(JsonFormConstants.VALUE))) {

                                Integer requiredFieldCount = requiredFieldsMap.get(object.getString(Constants.JSON_FORM_KEY.ENCOUNTER_TYPE));

                                requiredFieldCount = requiredFieldCount == null ? 1 : ++requiredFieldCount;

                                requiredFieldsMap.put(object.getString(Constants.JSON_FORM_KEY.ENCOUNTER_TYPE), requiredFieldCount);
                            }

//Total Count
                            Integer requiredFieldCount = requiredFieldsMap.get(object.getString(Constants.JSON_FORM_KEY.ENCOUNTER_TYPE) + Constants.SUFFIX.TOTAL_COUNT);

                            requiredFieldCount = requiredFieldCount == null ? 1 : ++requiredFieldCount;

                            requiredFieldsMap.put(object.getString(Constants.JSON_FORM_KEY.ENCOUNTER_TYPE) + Constants.SUFFIX.TOTAL_COUNT, requiredFieldCount);

                        }

                    }

                }
            }
        }
    }

    private void process(String[] mainContactForms) throws Exception {

        JSONObject object;
        List<String> partialForms = new ArrayList<>(Arrays.asList(mainContactForms));

        List<PartialContact> partialContacts = AncApplication.getInstance().getPartialContactRepository().getPartialContacts(getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID), contactNo);

        for (PartialContact partialContact : partialContacts) {
            if (partialContact.getFormJson() != null) {
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
}
