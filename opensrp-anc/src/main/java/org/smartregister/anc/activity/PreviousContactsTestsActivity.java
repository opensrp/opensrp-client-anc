package org.smartregister.anc.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import org.jeasy.rules.api.Facts;
import org.smartregister.anc.R;
import org.smartregister.anc.adapter.ProfileOverviewAdapter;
import org.smartregister.anc.contract.PreviousContactsTests;
import org.smartregister.anc.domain.LastContactDetailsWrapper;
import org.smartregister.anc.domain.TestResults;
import org.smartregister.anc.domain.YamlConfigWrapper;
import org.smartregister.anc.presenter.PreviousContactTestsPresenter;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.Utils;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PreviousContactsTestsActivity extends AppCompatActivity implements PreviousContactsTests.View {

    protected PreviousContactsTests.Presenter mProfilePresenter;
    protected ActionBar actionBar;
    private RecyclerView lastContactsTestsRecyclerView;
    private LinearLayout notTestShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getViewLayoutId());
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_back));
            actionBar.setTitle(getResources().getString(R.string.previous_contacts_tests_header));
        }

        String baseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);
        HashMap<String, String> clientDetails =
                (HashMap<String, String>) getIntent().getSerializableExtra(Constants.INTENT_KEY.CLIENT_MAP);
        mProfilePresenter = new PreviousContactTestsPresenter(this);
        String contactNo = String.valueOf(Utils.getTodayContact(clientDetails.get(DBConstants.KEY.NEXT_CONTACT)));
        String lastContactRecordDate = clientDetails.get(DBConstants.KEY.LAST_CONTACT_RECORD_DATE);

        setUpViews();
        try {
            mProfilePresenter.loadPreviousContactsTest(baseEntityId, contactNo, lastContactRecordDate);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setUpContactTestsDetailsRecycler(List<LastContactDetailsWrapper> lastContactDetailsTestsWrapperList) {
        List<YamlConfigWrapper> data = new ArrayList<>();
        Facts facts = new Facts();
        if (lastContactDetailsTestsWrapperList.size() > 0) {
            for (int i = 0; i < lastContactDetailsTestsWrapperList.size(); i++) {
                LastContactDetailsWrapper lastContactDetailsTest = lastContactDetailsTestsWrapperList.get(i);
                data = lastContactDetailsTest.getExtraInformation();
                facts = lastContactDetailsTest.getFacts();
            }
        }
        if (data.size() > 0) {
            ProfileOverviewAdapter adapter = new ProfileOverviewAdapter(this, data, facts, mProfilePresenter);
            lastContactsTestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            lastContactsTestsRecyclerView.setAdapter(adapter);
        } else {
            notTestShown.setVisibility(View.VISIBLE);
            lastContactsTestsRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setAllTestResults(List<TestResults> allTestResults) {
        // todo
    }

    private void setUpViews() {
        lastContactsTestsRecyclerView = findViewById(R.id.last_contacts_tests);
        notTestShown = findViewById(R.id.show_no_tests);
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
        return R.layout.activity_previous_contacts_tests;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
