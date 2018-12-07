package org.smartregister.anc.activity;

import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.anc.R;
import org.smartregister.anc.contract.ContactContract;
import org.smartregister.anc.domain.Contact;
import org.smartregister.anc.fragment.QuickCheckFragment;
import org.smartregister.anc.presenter.ContactPresenter;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends BaseContactActivity implements ContactContract.View {

    public static final String DIALOG_TAG = "CONTACT_DIALOG_TAG";

    private TextView patientNameView;

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
        List<Contact> contacts = new ArrayList<>();

        Contact quickCheck = new Contact();
        quickCheck.setName(getString(R.string.quick_check));
        quickCheck.setBackground(R.drawable.quick_check_bg);
        quickCheck.setRequiredFields(0);
        contacts.add(quickCheck);

        Contact profile = new Contact();
        profile.setName(getString(R.string.profile));
        profile.setBackground(R.drawable.profile_bg);
        profile.setActionBarBackground(R.color.contact_profile_actionbar);
        profile.setNavigationBackground(R.color.contact_profile_navigation);
        profile.setHomeAsUpIndicator(R.drawable.ic_contact_menu);
        profile.setRequiredFields(7);
        profile.setFormName(Constants.JSON_FORM.ANC_PROFILE);
        contacts.add(profile);

        Contact symptomsAndFollowUp = new Contact();
        symptomsAndFollowUp.setName(getString(R.string.symptoms_follow_up));
        symptomsAndFollowUp.setBackground(R.drawable.symptoms_bg);
        symptomsAndFollowUp.setActionBarBackground(R.color.contact_symptoms_actionbar);
        symptomsAndFollowUp.setNavigationBackground(R.color.contact_symptoms_navigation);
        profile.setHomeAsUpIndicator(R.drawable.ic_contact_menu);
        symptomsAndFollowUp.setRequiredFields(0);
        symptomsAndFollowUp.setFormName(Constants.JSON_FORM.ANC_SYMPTOMS_FOLLOW_UP);
        contacts.add(symptomsAndFollowUp);

        Contact physicalExam = new Contact();
        physicalExam.setName(getString(R.string.physical_exam));
        physicalExam.setBackground(R.drawable.physical_exam_bg);
        physicalExam.setActionBarBackground(R.color.contact_exam_actionbar);
        physicalExam.setNavigationBackground(R.color.contact_exam_navigation);
        profile.setHomeAsUpIndicator(R.drawable.ic_contact_menu);
        physicalExam.setRequiredFields(18);
        physicalExam.setFormName(Constants.JSON_FORM.ANC_PHYSICAL_EXAM);
        contacts.add(physicalExam);

        Contact tests = new Contact();
        tests.setName(getString(R.string.tests));
        tests.setBackground(R.drawable.tests_bg);
        tests.setActionBarBackground(R.color.contact_tests_actionbar);
        tests.setNavigationBackground(R.color.contact_tests_navigation);
        profile.setHomeAsUpIndicator(R.drawable.ic_contact_menu);
        tests.setRequiredFields(12);
        tests.setFormName(Constants.JSON_FORM.ANC_TEST);
        contacts.add(tests);

        Contact counsellingAndTreatment = new Contact();
        counsellingAndTreatment.setName(getString(R.string.counselling_treatment));
        counsellingAndTreatment.setBackground(R.drawable.counselling_bg);
        counsellingAndTreatment.setActionBarBackground(R.color.contact_counselling_actionbar);
        counsellingAndTreatment.setNavigationBackground(R.color.contact_counselling_navigation);
        profile.setHomeAsUpIndicator(R.drawable.ic_contact_menu);
        counsellingAndTreatment.setRequiredFields(5);
        counsellingAndTreatment.setFormName(Constants.JSON_FORM.ANC_COUNSELLING_TREATMENT);
        contacts.add(counsellingAndTreatment);

        contactAdapter.setContacts(contacts);
        contactAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreation() { //Overriden from Secured Activity
    }

    @Override
    protected void onResumption() {//Overriden from Secured Activity

    }
}
