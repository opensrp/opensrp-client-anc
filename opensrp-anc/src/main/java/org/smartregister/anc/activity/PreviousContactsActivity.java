package org.smartregister.anc.activity;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.TextView;

import org.jeasy.rules.api.Facts;
import org.smartregister.anc.R;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.PreviousContacts;
import org.smartregister.anc.domain.YamlConfigWrapper;
import org.smartregister.anc.presenter.PreviousContactsPresenter;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PreviousContactsActivity extends AppCompatActivity implements PreviousContacts.View {

    private String baseEntityId;
    private HashMap<String, String> clientDetails;
    private List<YamlConfigWrapper> lastContactDetails;
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
        clientDetails = (HashMap<String, String>) getIntent().getSerializableExtra(Constants.INTENT_KEY.CLIENT_MAP);
        mProfilePresenter = new PreviousContactsPresenter(this);
        setUpViews();

        try {
            Date lastContactDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .parse(clientDetails.get(DBConstants.KEY.EDD));
            String displayContactDate =
                    new SimpleDateFormat("MMMM dd " + ", " + "yyyy", Locale.getDefault()).format(lastContactDate);
            if (!TextUtils.isEmpty(displayContactDate)) {
                deliveryDate.setText(displayContactDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        loadPreviousContacts();

    }

    private void loadPreviousContacts() {
        String contactNo = String.valueOf(Utils.getTodayContact(clientDetails.get(DBConstants.KEY.NEXT_CONTACT)));
        HashMap<String, Facts> previousContactsFacts = AncApplication.getInstance().getPreviousContactRepository()
                .getPreviousContactsFacts(getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID), contactNo);

        List<Facts> contactFactsList = new ArrayList<>();

        for (Map.Entry<String, Facts> entry : previousContactsFacts.entrySet()) {
            if (Integer.parseInt(entry.getKey()) > 0) {
                int index = Integer.parseInt(entry.getKey())-1;
                Facts facts = entry.getValue();
                contactFactsList.add(index, facts);
            }
        }
    }



    /*private void loadPreviousContactsTest() throws ParseException, IOException, JSONException {
        List<LastContactDetailsWrapper> lastContactDetailsWrapperList = new ArrayList<>();
        List<LastContactDetailsWrapper> lastContactDetailsTestsWrapperList = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(
                AncApplication.getInstance().getDetailsRepository()
                        .getAllDetailsForClient(getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID))
                        .get(Constants.DETAILS_KEY.ATTENTION_FLAG_FACTS));

        Iterator<String> keys = jsonObject.keys();

        String contactNo = String.valueOf(Utils.getTodayContact(clientDetails.get(DBConstants.KEY.NEXT_CONTACT)));
        Facts facts = AncApplication.getInstance().getPreviousContactRepository()
                .getPreviousContactFacts(getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID)
                        , contactNo);

        while (keys.hasNext()) {
            String key = keys.next();
            facts.put(key, jsonObject.get(key));
        }

        addOtherRuleObjects(facts);
        addAttentionFlagsRuleObjects(facts);

        Date lastContactDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .parse(clientDetails.get(DBConstants.KEY.LAST_CONTACT_RECORD_DATE));

        String displayContactDate =
                new SimpleDateFormat("dd MMM " + "yyyy", Locale.getDefault()).format(lastContactDate);

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
        Iterable<Object> attentionFlagsRuleObjects = AncApplication.getInstance()
                .readYaml(FilePath.FILE.ATTENTION_FLAGS);

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
        RecyclerView recyclerView = last_contact_layout.findViewById(R.id.last_contact_information);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
*/

    private void setUpViews() {
        topSection = findViewById(R.id.layout_top);
        middleSection = findViewById(R.id.layout_middle);
        bottomSection = findViewById(R.id.layout_bottom);
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
