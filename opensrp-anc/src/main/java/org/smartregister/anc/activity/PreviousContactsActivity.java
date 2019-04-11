package org.smartregister.anc.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.smartregister.anc.R;
import org.smartregister.anc.contract.PreviousContacts;
import org.smartregister.anc.presenter.PreviousContactsPresenter;
import org.smartregister.anc.util.Constants;

public class PreviousContactsActivity extends AppCompatActivity implements PreviousContacts.View {

    private String baseEntityId;
    protected PreviousContacts.Presenter mProfilePresenter;
    protected ActionBar actionBar;

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile_activity, menu);
        return true;
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
