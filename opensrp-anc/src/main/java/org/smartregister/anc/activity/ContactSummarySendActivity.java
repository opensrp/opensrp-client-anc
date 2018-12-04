package org.smartregister.anc.activity;

import android.content.Intent;
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

import org.smartregister.anc.R;
import org.smartregister.anc.adapter.ContactSummaryAdapter;
import org.smartregister.anc.contract.ContactSummarySendContract;
import org.smartregister.anc.helper.ImageRenderHelper;
import org.smartregister.anc.interactor.ContactSummaryInteractor;
import org.smartregister.anc.model.ContactSummaryModel;
import org.smartregister.anc.presenter.ContactSummaryPresenter;
import org.smartregister.anc.util.Constants;

import java.util.List;

public class ContactSummarySendActivity extends AppCompatActivity implements ContactSummarySendContract.View, View.OnClickListener {

    private TextView womanNameTextView;
    private ContactSummarySendContract.Presenter contactSummaryPresenter;
    private ContactSummaryAdapter contactSummaryAdapter;
    private ImageView womanProfileImage;
    private ImageRenderHelper imageRenderHelper;
    private TextView recordedContactTextView;

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
        contactSummaryPresenter.loadUpcomingContacts(getEntityId());
        contactSummaryPresenter.showWomanProfileImage(getEntityId());
    }

    private void setupView() {
        Button goToClientProfileButton = findViewById(R.id.button_go_to_client_profile);
        goToClientProfileButton.setOnClickListener(this);
        womanNameTextView = findViewById(R.id.contact_summary_woman_name);
        womanProfileImage = findViewById(R.id.contact_summary_woman_profile);
        recordedContactTextView = findViewById(R.id.contact_summary_contact_recorded);

        contactSummaryAdapter = new ContactSummaryAdapter();
        RecyclerView contactDatesRecyclerView = findViewById(R.id.contact_summary_recycler);
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

    @Override
    public void goToClientProfile() {
        finish();
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID));
        startActivity(intent);
    }

    @Override
    public void displayWomansName(String fullName) {
        womanNameTextView.setText(fullName);
    }

    @Override
    public void displayUpcomingContactDates(List<ContactSummaryModel> models) {
        contactSummaryAdapter.setContactDates(models);
    }

    @Override
    public void setProfileImage(String baseEntityId) {
        imageRenderHelper.refreshProfileImage(baseEntityId, womanProfileImage);
    }

    @Override
    public void updateRecordedContact(Integer contactNumber) {
        recordedContactTextView.setText(String.format(this.getResources().getString(R.string.contact_recorded), contactNumber));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_go_to_client_profile:
                goToClientProfile();
                break;
            default:
                Toast.makeText(this, "Action not recognized", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
