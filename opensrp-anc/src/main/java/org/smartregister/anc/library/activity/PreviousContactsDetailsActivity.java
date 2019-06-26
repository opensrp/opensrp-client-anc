package org.smartregister.anc.library.activity;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.jeasy.rules.api.Facts;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.adapter.ContactScheduleAdapter;
import org.smartregister.anc.library.adapter.LastContactAdapter;
import org.smartregister.anc.library.application.BaseAncApplication;
import org.smartregister.anc.library.contract.PreviousContactsDetails;
import org.smartregister.anc.library.domain.LastContactDetailsWrapper;
import org.smartregister.anc.library.domain.YamlConfig;
import org.smartregister.anc.library.domain.YamlConfigItem;
import org.smartregister.anc.library.domain.YamlConfigWrapper;
import org.smartregister.anc.library.model.ContactSummaryModel;
import org.smartregister.anc.library.util.Constants;
import org.smartregister.anc.library.util.DBConstants;
import org.smartregister.anc.library.util.FilePath;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.anc.library.presenter.PreviousContactDetailsPresenter;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PreviousContactsDetailsActivity extends AppCompatActivity implements PreviousContactsDetails.View {
    private static final String TAG = PreviousContactsDetailsActivity.class.getCanonicalName();
    protected PreviousContactsDetails.Presenter mProfilePresenter;
    protected ActionBar actionBar;
    private RecyclerView contactSchedule;
    private TextView deliveryDate;
    private RecyclerView previousContacts;
    private ConstraintLayout layoutBottom;
    private ConstraintLayout layoutMiddle;
    private ConstraintLayout layoutStart;
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
            String edd = clientDetails.get(Constants.EDD);
            if (!clientDetails.isEmpty()) {
                if (!TextUtils.isEmpty(edd)) {
                    Date eddDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(edd);
                    String eddDisplayDate =
                            new SimpleDateFormat("MMMM dd" + ", " + "yyyy", Locale.getDefault()).format(eddDate);
                    if (!TextUtils.isEmpty(eddDisplayDate)) {
                        deliveryDate.setText(eddDisplayDate);
                    }
                } else {
                    layoutBottom.setVisibility(View.GONE);
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
        if (schedule.size() > 0) {
            contactSchedule.setVisibility(View.VISIBLE);
            ContactScheduleAdapter adapter = new ContactScheduleAdapter(this, schedule);
            adapter.notifyDataSetChanged();
            contactSchedule.setLayoutManager(new LinearLayoutManager(this));
            contactSchedule.setAdapter(adapter);
        } else {
            layoutMiddle.setVisibility(View.GONE);
        }
    }

    @Override
    public void loadPreviousContactsDetails(Map<String, List<Facts>> allContactFacts) throws IOException, ParseException {
        List<LastContactDetailsWrapper> lastContactDetailsWrapperList = new ArrayList<>();
        if (!allContactFacts.isEmpty()) {
            for (Map.Entry<String, List<Facts>> entry : allContactFacts.entrySet()) {
                List<Facts> factsList = entry.getValue();
                String contactNo = entry.getKey();
                Facts factsToUpdate = new Facts();

                for (Facts facts : factsList) {
                    if (facts != null) {
                        Map<String, Object> factObject = facts.asMap();
                        for (Map.Entry<String, Object> stringObjectEntry : factObject.entrySet()) {
                            factsToUpdate.put(stringObjectEntry.getKey(), stringObjectEntry.getValue());
                        }
                    }
                }

                lastContactDetails = new ArrayList<>();

                if (factsToUpdate.asMap().get(Constants.ATTENTION_FLAG_FACTS) != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(
                                (String) factsToUpdate.asMap().get(Constants.ATTENTION_FLAG_FACTS));
                        Iterator<String> keys = jsonObject.keys();

                        while (keys.hasNext()) {
                            String key = keys.next();
                            factsToUpdate.put(key, jsonObject.get(key));
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                    }
                }


                addOtherRuleObjects(factsToUpdate);
                addAttentionFlagsRuleObjects(factsToUpdate);
                Date lastContactDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .parse(String.valueOf(factsToUpdate.asMap().get(Constants.CONTACT_DATE)));

                String displayContactDate = new SimpleDateFormat("dd MMM " + "yyyy", Locale.getDefault())
                        .format(lastContactDate);

                lastContactDetailsWrapperList
                        .add(new LastContactDetailsWrapper(contactNo, displayContactDate, lastContactDetails,
                                factsToUpdate));
                setUpContactDetailsRecycler(lastContactDetailsWrapperList);
            }

        }


    }

    private void addOtherRuleObjects(Facts facts) throws IOException {
        Iterable<Object> ruleObjects = loadFile(FilePath.FILE.PROFILE_LAST_CONTACT);

        for (Object ruleObject : ruleObjects) {
            List<YamlConfigWrapper> yamlConfigList = new ArrayList<>();
            int valueCount = 0;
            YamlConfig yamlConfig = (YamlConfig) ruleObject;

            List<YamlConfigItem> configItems = yamlConfig.getFields();

            for (YamlConfigItem configItem : configItems) {
                if (BaseAncApplication.getInstance().getAncRulesEngineHelper().getRelevance(facts, configItem.getRelevance())) {
                    yamlConfigList.add(new YamlConfigWrapper(null, null, configItem));
                    valueCount += 1;
                }
            }

            if (valueCount > 0) {
                lastContactDetails.addAll(yamlConfigList);
            }
        }
    }

    private void addAttentionFlagsRuleObjects(Facts facts) throws IOException {
        Iterable<Object> ruleObjects = loadFile(FilePath.FILE.ATTENTION_FLAGS);

        for (Object ruleObject : ruleObjects) {
            YamlConfig attentionFlagConfig = (YamlConfig) ruleObject;
            for (YamlConfigItem yamlConfigItem : attentionFlagConfig.getFields()) {
                if (BaseAncApplication.getInstance().getAncRulesEngineHelper()
                        .getRelevance(facts, yamlConfigItem.getRelevance())) {
                    lastContactDetails.add(new YamlConfigWrapper(null, null, yamlConfigItem));
                }
            }
        }
    }

    private Iterable<Object> loadFile(String filename) throws IOException {
        return BaseAncApplication.getInstance().readYaml(filename);
    }

    private void setUpContactDetailsRecycler(List<LastContactDetailsWrapper> lastContactDetailsWrappers) {
        if (lastContactDetailsWrappers.size() > 0) {
            LastContactAdapter adapter = new LastContactAdapter(lastContactDetailsWrappers, this);
            adapter.notifyDataSetChanged();
            previousContacts.setLayoutManager(new LinearLayoutManager(this));
            previousContacts.setAdapter(adapter);
        } else {
            layoutStart.setVisibility(View.GONE);
        }
    }

    private void setUpViews() {
        deliveryDate = findViewById(R.id.delivery_date);
        previousContacts = findViewById(R.id.previous_contacts);
        contactSchedule = findViewById(R.id.upcoming_contacts);
        layoutBottom = findViewById(R.id.layout_bottom);
        layoutMiddle = findViewById(R.id.layout_middle);
        layoutStart = findViewById(R.id.layout_start);
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
