package org.smartregister.anc.activity;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import org.smartregister.anc.R;
import org.smartregister.anc.contract.PreviousContacts;
import org.smartregister.anc.presenter.PreviousContactsPresenter;
import org.smartregister.anc.util.Constants;

public class PreviousContactsActivity extends AppCompatActivity implements PreviousContacts.View {

    private String baseEntityId;
    protected PreviousContacts.Presenter mProfilePresenter;
    protected ActionBar actionBar;
    private ConstraintLayout bottomSection;
    private ConstraintLayout topSection;
    private ConstraintLayout middleSection;
    private TextView deliveryDate;
    private RecyclerView previousContacts;
    private RecyclerView contactSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getViewLayoutId());
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_back));
            actionBar.setTitle(getResources().getString(R.string.previous_contacts_header));
        }

        baseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);
        mProfilePresenter = new PreviousContactsPresenter(this);
        setUpViews();

    }

    private void setUpViews() {
        topSection = findViewById(R.id.layout_top);
        middleSection = findViewById(R.id.layout_middle);
        bottomSection = findViewById(R.id.layout_bottom);
        deliveryDate = findViewById(R.id.delivery_date);
        previousContacts = findViewById(R.id.last_contact_information);
        contactSchedule = findViewById(R.id.upcoming_contacts);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    protected int getViewLayoutId() {
        return R.layout.activity_previous_contacts;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
