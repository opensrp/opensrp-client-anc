package org.smartregister.anc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.smartregister.anc.R;
import org.smartregister.anc.adapter.ContactSummaryAdapter;
import org.smartregister.anc.contract.ContactSummaryContract;
import org.smartregister.anc.interactor.ContactSummaryInteractor;
import org.smartregister.anc.model.ContactSummaryModel;
import org.smartregister.anc.presenter.ContactSummaryPresenter;
import org.smartregister.anc.util.Constants;

import java.util.List;

public class ContactSummaryActivity extends AppCompatActivity implements ContactSummaryContract.View, View.OnClickListener {

    private Button goToClientProfileButton;
    private TextView womanNameTextView;
    private ContactSummaryContract.Presenter contactConfirmationPresenter;
    private RecyclerView contactDatesRecyclerView;
    private ContactSummaryAdapter contactSummaryAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_summary);
        setupView();
        contactConfirmationPresenter = new ContactSummaryPresenter(new ContactSummaryInteractor());
        contactConfirmationPresenter.attachView(this);
        contactConfirmationPresenter.loadWoman(getEntityId());
        contactConfirmationPresenter.loadUpcomingContacts(getEntityId());

    }

    private void setupView() {
        goToClientProfileButton = findViewById(R.id.button_go_to_client_profile);
        goToClientProfileButton.setOnClickListener(this);
        womanNameTextView = findViewById(R.id.contact_summary_woman_name);

        contactSummaryAdapter = new ContactSummaryAdapter();
        contactDatesRecyclerView = findViewById(R.id.contact_summary_recycler);
        contactDatesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactDatesRecyclerView.setAdapter(contactSummaryAdapter);
    }

    private String getEntityId() {
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
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, Constants.DUMMY_DATA.DUMMY_ENTITY_ID);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_go_to_client_profile:
                goToClientProfile();
        }
    }
}
