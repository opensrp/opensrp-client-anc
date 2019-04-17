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
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.PreviousContacts;
import org.smartregister.anc.domain.LastContactDetailsWrapper;
import org.smartregister.anc.domain.YamlConfig;
import org.smartregister.anc.domain.YamlConfigItem;
import org.smartregister.anc.domain.YamlConfigWrapper;
import org.smartregister.anc.presenter.PreviousContactsPresenter;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.FilePath;
import org.smartregister.anc.util.Utils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PreviousContactsTestsActivity extends AppCompatActivity implements PreviousContacts.View {

    private String baseEntityId;
    private HashMap<String, String> clientDetails;
    protected PreviousContacts.Presenter mProfilePresenter;
    protected ActionBar actionBar;
    private RecyclerView lastContactsTestsRecyclerView;
    private LinearLayout notTestShown;

    private List<YamlConfigWrapper> lastContactTests;

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

        baseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);
        clientDetails = (HashMap<String, String>) getIntent().getSerializableExtra(Constants.INTENT_KEY.CLIENT_MAP);
        mProfilePresenter = new PreviousContactsPresenter(this);

        lastContactTests = new ArrayList<>();
        setUpViews();
        try {
            loadPreviousContactsTest();
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPreviousContactsTest() throws ParseException, IOException {
        List<LastContactDetailsWrapper> lastContactDetailsTestsWrapperList = new ArrayList<>();
        Facts previousContactsFacts = AncApplication.getInstance().getPreviousContactRepository()
                .getPreviousContactTestsFacts(baseEntityId);

        addTestsRuleObjects(previousContactsFacts);

        String contactNo = String.valueOf(Utils.getTodayContact(clientDetails.get(DBConstants.KEY.NEXT_CONTACT)));
        Date lastContactDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .parse(clientDetails.get(DBConstants.KEY.LAST_CONTACT_RECORD_DATE));

        lastContactDetailsTestsWrapperList.add(new LastContactDetailsWrapper(contactNo, new SimpleDateFormat("dd MMM " +
                "yyyy", Locale.getDefault()).format(lastContactDate), lastContactTests, previousContactsFacts));

        setUpContactTestsDetailsRecycler(lastContactDetailsTestsWrapperList);
    }

    private void setUpContactTestsDetailsRecycler(List<LastContactDetailsWrapper> lastContactDetailsTestsWrapperList) {
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
            ProfileOverviewAdapter adapter = new ProfileOverviewAdapter(this, data, facts);
            lastContactsTestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            lastContactsTestsRecyclerView.setAdapter(adapter);
        } else {
            notTestShown.setVisibility(View.VISIBLE);
            lastContactsTestsRecyclerView.setVisibility(View.GONE);
        }
    }

    private void addTestsRuleObjects(Facts facts) throws IOException {
        Iterable<Object> testsRuleObjects = AncApplication.getInstance().readYaml(FilePath.FILE.PROFILE_LAST_CONTACT_TEST);

        for (Object ruleObject : testsRuleObjects) {
            List<YamlConfigWrapper> yamlConfigList = new ArrayList<>();
            int valueCount = 0;

            YamlConfig testsConfig = (YamlConfig) ruleObject;

            if (testsConfig.getSubGroup() != null) {
                yamlConfigList.add(new YamlConfigWrapper(null, testsConfig.getSubGroup(), null, false));
            }

            for (YamlConfigItem yamlConfigItem : testsConfig.getFields()) {

                if (AncApplication.getInstance().getAncRulesEngineHelper()
                        .getRelevance(facts, yamlConfigItem.getRelevance())) {
                    yamlConfigList.add(new YamlConfigWrapper(null, null, yamlConfigItem, false));
                    valueCount = +1;

                }

            }

            if (testsConfig.isAllTests()) {
                yamlConfigList.add(new YamlConfigWrapper(null, null, null, true));
            }

            if (valueCount > 0) {
                lastContactTests.addAll(yamlConfigList);
            }
        }
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
