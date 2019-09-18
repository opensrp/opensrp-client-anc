package org.smartregister.anc.library.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.adapter.ContactSummaryAdapter;
import org.smartregister.anc.library.contract.ContactSummarySendContract;
import org.smartregister.anc.library.interactor.ContactSummaryInteractor;
import org.smartregister.anc.library.model.ContactSummaryModel;
import org.smartregister.anc.library.util.Constants;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.anc.library.presenter.ContactSummaryPresenter;
import org.smartregister.helper.ImageRenderHelper;

import java.util.HashMap;
import java.util.List;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_summary);
        setupView();
        contactSummaryPresenter = new ContactSummaryPresenter(new ContactSummaryInteractor());
        contactSummaryPresenter.attachView(this);
        imageRenderHelper = new ImageRenderHelper(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        contactSummaryPresenter.loadWoman(getEntityId());
        contactSummaryPresenter.loadUpcomingContacts(getEntityId(), getReferredContactNo());
        contactSummaryPresenter.showWomanProfileImage(getEntityId());
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

    public String getEntityId() {
        String entityId = getIntent().getExtras().getString(Constants.INTENT_KEY.BASE_ENTITY_ID);
        if (entityId != null) {
            return entityId;
        }
        return null;
    }

    public String getReferredContactNo() {
        HashMap<String, String> client =
                (HashMap<String, String>) getIntent().getExtras().get(Constants.INTENT_KEY.CLIENT_MAP);
        if (client != null) {
            String contactNo = client.get(Constants.REFERRAL);
            if (contactNo != null) {
                return contactNo;
            }
        }
        return null;
    }

    @Override
    public void goToClientProfile() {
        finish();
        Utils.navigateToProfile(this,
                (HashMap<String, String>) getIntent().getExtras().getSerializable(Constants.INTENT_KEY.CLIENT_MAP));
    }

    @Override
    public void displayPatientName(String fullName) {
        womanNameTextView.setText(fullName);
    }

    @Override
    public void displayUpcomingContactDates(List<ContactSummaryModel> models) {
        if (models.size() <= 0) {
            contactDatesRecyclerView.setVisibility(View.GONE);
            contactScheduleHeadingTextView.setVisibility(View.GONE);
        }
        contactSummaryAdapter.setContactDates(models.size() > 5 ? models.subList(0, 4) : models);
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
            recordedContactTextView.setVisibility(View.GONE);
        }
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
    public void onBackPressed() {
        Utils.navigateToHomeRegister(this, false, AncLibrary.getInstance().getActivityConfiguration().getHomeRegisterActivityClass());
    }
}
