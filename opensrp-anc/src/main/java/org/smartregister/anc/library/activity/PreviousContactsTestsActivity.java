package org.smartregister.anc.library.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jeasy.rules.api.Facts;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.adapter.ProfileOverviewAdapter;
import org.smartregister.anc.library.contract.PreviousContactsTests;
import org.smartregister.anc.library.domain.LastContactDetailsWrapper;
import org.smartregister.anc.library.domain.TestResults;
import org.smartregister.anc.library.domain.YamlConfigWrapper;
import org.smartregister.anc.library.presenter.PreviousContactTestsPresenter;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.anc.library.util.Utils;

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
    private String baseEntityId;
    private String contactNo;

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

        baseEntityId = getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID);
        HashMap<String, String> clientDetails =
                (HashMap<String, String>) getIntent().getSerializableExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP);

        mProfilePresenter = new PreviousContactTestsPresenter(this);
        contactNo = String.valueOf(Utils.getTodayContact(clientDetails.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT)));
        String lastContactRecordDate = clientDetails.get(DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE);

        setUpViews();
        try {
            mProfilePresenter.loadPreviousContactsTest(baseEntityId, contactNo, lastContactRecordDate);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    protected int getViewLayoutId() {
        return R.layout.activity_previous_contacts_tests;
    }

    private void setUpViews() {
        lastContactsTestsRecyclerView = findViewById(R.id.last_contacts_tests);
        notTestShown = findViewById(R.id.show_no_tests);
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
            ProfileOverviewAdapter adapter = new ProfileOverviewAdapter(this, data, facts, mProfilePresenter, baseEntityId,
                    contactNo);
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

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
