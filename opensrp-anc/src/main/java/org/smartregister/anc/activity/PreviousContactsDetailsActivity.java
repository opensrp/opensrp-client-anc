package org.smartregister.anc.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import org.jeasy.rules.api.Facts;
import org.json.JSONException;
import org.smartregister.anc.R;
import org.smartregister.anc.adapter.ContactScheduleAdapter;
import org.smartregister.anc.adapter.LastContactAdapter;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.PreviousContactsDetails;
import org.smartregister.anc.domain.LastContactDetailsWrapper;
import org.smartregister.anc.domain.YamlConfig;
import org.smartregister.anc.domain.YamlConfigItem;
import org.smartregister.anc.domain.YamlConfigWrapper;
import org.smartregister.anc.model.ContactSummaryModel;
import org.smartregister.anc.presenter.PreviousContactDetailsPresenter;
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

public class PreviousContactsDetailsActivity extends AppCompatActivity implements PreviousContactsDetails.View {
    private static final String TAG = PreviousContactsDetailsActivity.class.getCanonicalName();
    protected PreviousContactsDetails.Presenter mProfilePresenter;
    protected ActionBar actionBar;
    RecyclerView contactSchedule;
    private TextView deliveryDate;
    private RecyclerView previousContacts;
    private List<YamlConfigWrapper> lastContactDetails;


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

        String baseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);
        HashMap<String, String> clientDetails =
                (HashMap<String, String>) getIntent().getSerializableExtra(Constants.INTENT_KEY.CLIENT_MAP);
        String contactNo = String.valueOf(Utils.getTodayContact(clientDetails.get(DBConstants.KEY.NEXT_CONTACT)));
        mProfilePresenter = new PreviousContactDetailsPresenter(this);
        setUpViews();

        try {
            if (!clientDetails.isEmpty()) {
                Date lastContactDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .parse(clientDetails.get(DBConstants.KEY.EDD));
                String displayContactDate =
                        new SimpleDateFormat("MMMM dd " + ", " + "yyyy", Locale.getDefault()).format(lastContactDate);
                if (!TextUtils.isEmpty(displayContactDate)) {
                    deliveryDate.setText(displayContactDate);
                }

                mProfilePresenter.loadPreviousContacts(baseEntityId, contactNo);
                mProfilePresenter
                        .loadPreviousContactSchedule(baseEntityId, contactNo, clientDetails.get(DBConstants.KEY.EDD));
            }
        } catch (ParseException | IOException | JSONException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

    }

    @Override
    public void displayPreviousContactSchedule(List<ContactSummaryModel> schedule) {
        ContactScheduleAdapter adapter = new ContactScheduleAdapter(this, schedule);
        adapter.notifyDataSetChanged();
        contactSchedule.setLayoutManager(new LinearLayoutManager(this));
        contactSchedule.setAdapter(adapter);
    }

    @Override
    public void loadPreviousContactsDetails(Facts facts, Facts contactFacts, String contactNo)
    throws IOException, ParseException {
        List<LastContactDetailsWrapper> lastContactDetailsWrapperList = new ArrayList<>();
        lastContactDetails = new ArrayList<>();

        addOtherRuleObjects(contactFacts);
        addAttentionFlagsRuleObjects(facts);

        Date lastContactDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .parse(String.valueOf(contactFacts.asMap().get(Constants.CONTACT_DATE)));

        String displayContactDate = new SimpleDateFormat("dd MMM " + "yyyy", Locale.getDefault()).format(lastContactDate);

        lastContactDetailsWrapperList
                .add(new LastContactDetailsWrapper(contactNo, displayContactDate, lastContactDetails, facts));
        setUpContactDetailsRecycler(lastContactDetailsWrapperList);

    }

    private void addOtherRuleObjects(Facts facts) throws IOException {
        Iterable<Object> ruleObjects = loadFile(FilePath.FILE.PROFILE_LAST_CONTACT);

        for (Object ruleObject : ruleObjects) {
            List<YamlConfigWrapper> yamlConfigList = new ArrayList<>();
            int valueCount = 0;
            YamlConfig yamlConfig = (YamlConfig) ruleObject;

            List<YamlConfigItem> configItems = yamlConfig.getFields();

            for (YamlConfigItem configItem : configItems) {
                if (AncApplication.getInstance().getAncRulesEngineHelper().getRelevance(facts, configItem.getRelevance())) {
                    yamlConfigList.add(new YamlConfigWrapper(null, null, configItem, false));
                    valueCount += 1;
                }
            }

            if (valueCount > 0) {
                lastContactDetails.addAll(yamlConfigList);
            }
        }
    }

    private void addAttentionFlagsRuleObjects(Facts facts) throws IOException {
        Iterable<Object> attentionFlagsRuleObjects = loadFile(FilePath.FILE.ATTENTION_FLAGS);

        for (Object ruleObject : attentionFlagsRuleObjects) {
            YamlConfig attentionFlagConfig = (YamlConfig) ruleObject;
            for (YamlConfigItem yamlConfigItem : attentionFlagConfig.getFields()) {

                if (AncApplication.getInstance().getAncRulesEngineHelper()
                        .getRelevance(facts, yamlConfigItem.getRelevance())) {

                    lastContactDetails.add(new YamlConfigWrapper(null, null, yamlConfigItem, false));

                }

            }
        }
    }

    private Iterable<Object> loadFile(String filename) throws IOException {
        return AncApplication.getInstance().readYaml(filename);
    }

    private void setUpContactDetailsRecycler(List<LastContactDetailsWrapper> lastContactDetailsWrappers) {
        LastContactAdapter adapter = new LastContactAdapter(lastContactDetailsWrappers, this);
        adapter.notifyDataSetChanged();
        previousContacts.setLayoutManager(new LinearLayoutManager(this));
        previousContacts.setAdapter(adapter);
    }

    private void setUpViews() {
        deliveryDate = findViewById(R.id.delivery_date);
        previousContacts = findViewById(R.id.previous_contacts);
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
