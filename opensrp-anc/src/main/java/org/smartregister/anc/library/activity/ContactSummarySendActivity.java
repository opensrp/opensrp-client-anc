package org.smartregister.anc.library.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.commons.lang3.StringUtils;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.adapter.ContactSummaryAdapter;
import org.smartregister.anc.library.contract.ContactSummarySendContract;
import org.smartregister.anc.library.interactor.ContactSummaryInteractor;
import org.smartregister.anc.library.model.ContactSummaryModel;
import org.smartregister.anc.library.presenter.ContactSummaryPresenter;
import org.smartregister.anc.library.repository.PatientRepository;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.helper.ImageRenderHelper;

import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

public class ContactSummarySendActivity extends AppCompatActivity
        implements ContactSummarySendContract.View, View.OnClickListener {

    private TextView womanNameTextView;
    private ContactSummarySendContract.Presenter contactSummaryPresenter;
    private ContactSummaryAdapter contactSummaryAdapter;
    private ImageView womanProfileImage;
    private ImageRenderHelper imageRenderHelper;
    private TextView recordedContactTextView;
    private TextView contactScheduleHeadingTextView;
    private RecyclerView contactDatesRecyclerView;
    private HashMap<String, String> womanDetails;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_summary);
        setupView();
        contactSummaryPresenter = new ContactSummaryPresenter(new ContactSummaryInteractor());
        contactSummaryPresenter.attachView(this);
        imageRenderHelper = new ImageRenderHelper(this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            womanDetails = (HashMap<String, String>) getIntent().getExtras().getSerializable(ConstantsUtils.IntentKeyUtils.CLIENT_MAP);
        }

    }

    private void setupView() {
        Button goToClientProfileButton = findViewById(R.id.button_go_to_client_profile);
        goToClientProfileButton.setOnClickListener(this);
        womanNameTextView = findViewById(R.id.contact_summary_woman_name);
        womanProfileImage = findViewById(R.id.contact_summary_woman_profile);
        recordedContactTextView = findViewById(R.id.contact_summary_contact_recorded);
        contactScheduleHeadingTextView = findViewById(R.id.contact_schedule_heading);

        contactSummaryAdapter = new ContactSummaryAdapter();
        contactDatesRecyclerView = findViewById(R.id.contact_summary_recycler);
        contactDatesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactDatesRecyclerView.setAdapter(contactSummaryAdapter);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_go_to_client_profile) {
            goToClientProfile();
        } else {
            Toast.makeText(this, "Action not recognized", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void goToClientProfile() {
        finish();
        HashMap<String, String> womanProfileDetails = getWomanProfileDetails();
        if (womanProfileDetails != null) {
            Utils.navigateToProfile(this, womanProfileDetails);
        } else {
            Timber.e("Make sure the person object was fetched successfully");
        }
    }

    /**
     * Get the woman details using the {@link PatientRepository}
     *
     * @return womanDetails {@link HashMap<>}
     */
    public HashMap<String, String> getWomanProfileDetails() {
        return (HashMap<String, String>) PatientRepository.getWomanProfileDetails(getEntityId());
    }

    public String getEntityId() {
        return getIntent().getExtras().getString(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID);
    }

    @Override
    public void displayPatientName(String fullName) {
        womanNameTextView.setText(fullName);
    }

    @Override
    public void displayUpcomingContactDates(List<ContactSummaryModel> models) {
        if (models == null || models.isEmpty()) {
            contactDatesRecyclerView.setVisibility(View.GONE);
            contactScheduleHeadingTextView.setVisibility(View.GONE);
            return;
        }
        String maxContactToDisplay = Utils.getProperties(getApplicationContext()).getProperty(ConstantsUtils.Properties.MAX_CONTACT_SCHEDULE_DISPLAYED, "");
        if (StringUtils.isNotBlank(maxContactToDisplay)) {
            try {
                int count = Integer.parseInt(maxContactToDisplay);
                contactSummaryAdapter.setContactDates(models.size() > count ? models.subList(0, (count - 1)) : models);
            } catch (NumberFormatException e) {
                contactSummaryAdapter.setContactDates(models);
                Timber.e(e);
            }
        } else {
            contactSummaryAdapter.setContactDates(models);
        }

    }

    @Override
    public void setProfileImage(String baseEntityId) {
        imageRenderHelper.refreshProfileImage(baseEntityId, womanProfileImage, Utils.getProfileImageResourceIdentifier());
    }

    @Override
    public void updateRecordedContact(Integer contactNumber) {
        recordedContactTextView
                .setText(String.format(this.getResources().getString(R.string.contact_recorded), contactNumber));
        if (getReferredContactNo() != null) {
            recordedContactTextView.setText(getString(R.string.hospital_referral_title));
        }
    }

    public String getReferredContactNo() {
        if (womanDetails != null) {
            return womanDetails.get(ConstantsUtils.REFERRAL);
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        Utils.navigateToHomeRegister(this, false, AncLibrary.getInstance().getActivityConfiguration().getHomeRegisterActivityClass());
    }

    @Override
    protected void onResume() {
        super.onResume();
        contactSummaryPresenter.loadWoman(getEntityId());
        contactSummaryPresenter.loadUpcomingContacts(getEntityId(), getReferredContactNo());
        contactSummaryPresenter.showWomanProfileImage(getEntityId());
    }
}
