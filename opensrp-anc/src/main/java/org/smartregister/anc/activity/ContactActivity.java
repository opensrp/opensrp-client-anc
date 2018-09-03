package org.smartregister.anc.activity;

import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.R;
import org.smartregister.anc.contract.ContactContract;
import org.smartregister.anc.domain.Contact;
import org.smartregister.anc.presenter.ContactPresenter;
import org.smartregister.anc.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends BaseContactActivity implements ContactContract.View {


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
        profile.setRequiredFields(7);
        contacts.add(profile);

        Contact symptomsAndFollowUp = new Contact();
        symptomsAndFollowUp.setName(getString(R.string.symptoms_follow_up));
        symptomsAndFollowUp.setBackground(R.drawable.symptoms_bg);
        symptomsAndFollowUp.setRequiredFields(0);
        contacts.add(symptomsAndFollowUp);

        Contact physicalExam = new Contact();
        physicalExam.setName(getString(R.string.physical_exam));
        physicalExam.setBackground(R.drawable.physical_exam_bg);
        physicalExam.setRequiredFields(18);
        contacts.add(physicalExam);

        Contact tests = new Contact();
        tests.setName(getString(R.string.tests));
        tests.setBackground(R.drawable.tests_bg);
        tests.setRequiredFields(12);
        contacts.add(tests);

        Contact counsellingAndTreatment = new Contact();
        counsellingAndTreatment.setName(getString(R.string.counselling_treatment));
        counsellingAndTreatment.setBackground(R.drawable.counselling_bg);
        counsellingAndTreatment.setRequiredFields(5);
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
