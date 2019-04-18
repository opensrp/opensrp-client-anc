package org.smartregister.anc.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.TextView;

import org.jeasy.rules.api.Facts;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.R;
import org.smartregister.anc.adapter.ContactScheduleAdapter;
import org.smartregister.anc.adapter.LastContactAdapter;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.PreviousContacts;
import org.smartregister.anc.domain.LastContactDetailsWrapper;
import org.smartregister.anc.domain.YamlConfig;
import org.smartregister.anc.domain.YamlConfigItem;
import org.smartregister.anc.domain.YamlConfigWrapper;
import org.smartregister.anc.model.ContactSummaryModel;
import org.smartregister.anc.presenter.PreviousContactsPresenter;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.FilePath;
import org.smartregister.anc.util.JsonFormUtils;
import org.smartregister.anc.util.Utils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PreviousContactsActivity extends AppCompatActivity implements PreviousContacts.View {

    private HashMap<String, String> clientDetails;
    protected PreviousContacts.Presenter mProfilePresenter;
    protected ActionBar actionBar;
    private TextView deliveryDate;
    private RecyclerView previousContacts;
    private List<YamlConfigWrapper> lastContactDetails;
    RecyclerView contactSchedule;
    private JsonFormUtils formUtils = new JsonFormUtils();

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
        clientDetails = (HashMap<String, String>) getIntent().getSerializableExtra(Constants.INTENT_KEY.CLIENT_MAP);
        String contactNo = String.valueOf(Utils.getTodayContact(clientDetails.get(DBConstants.KEY.NEXT_CONTACT)));
        mProfilePresenter = new PreviousContactsPresenter(this);
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


                loadPreviousContacts(baseEntityId);
                loadPreviousContactSchedule(baseEntityId, contactNo, clientDetails.get(DBConstants.KEY.EDD));
            }
        } catch (ParseException | JSONException e) {
            e.printStackTrace();
        }

    }

    private void loadPreviousContactSchedule(String baseEntityId, String contactNo, String edd) throws JSONException,
            ParseException {
        Facts immediatePreviousSchedule = AncApplication.getInstance().getPreviousContactRepository()
                .getImmediatePreviousSchedule(baseEntityId, contactNo);
        String contactScheduleString = "";
        if (immediatePreviousSchedule != null) {
            Map<String, Object> scheduleMap = immediatePreviousSchedule.asMap();
            for (Map.Entry<String, Object> entry : scheduleMap.entrySet()) {
                if (Constants.CONTACT_SCHEDULE.equals(entry.getKey())) {
                    contactScheduleString = entry.getValue().toString();
                }
            }
        }
        List<String> scheduleList = Utils.getListFromString(contactScheduleString);
        Date lastContactEdd = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(edd);
        String formattedEdd =
                new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(lastContactEdd);
        List<ContactSummaryModel> schedule = formUtils
                .generateNextContactSchedule(formattedEdd, scheduleList, Integer.valueOf(contactNo));

        ContactScheduleAdapter adapter = new ContactScheduleAdapter(this, schedule);
        adapter.notifyDataSetChanged();
        contactSchedule.setLayoutManager(new LinearLayoutManager(this));
        contactSchedule.setAdapter(adapter);
    }

    private void loadPreviousContacts(String baseEntityId) {
        String contactNo = String.valueOf(Utils.getTodayContact(clientDetails.get(DBConstants.KEY.NEXT_CONTACT)));
        HashMap<String, Facts> previousContactsFacts = AncApplication.getInstance().getPreviousContactRepository()
                .getPreviousContactsFacts(baseEntityId, contactNo);

        List<Facts> contactFactsList = new ArrayList<>();

        for (Map.Entry<String, Facts> entry : previousContactsFacts.entrySet()) {
            if (Integer.parseInt(entry.getKey()) > 0) {
                Facts facts = entry.getValue();
                contactFactsList.add(facts);
            }
        }

        List<Facts> factsList = reverseList(contactFactsList);

        if (factsList.size() > 0) {
            for (int i = 0; i < factsList.size(); i++) {
                try {
                    String specificContactNo = String.valueOf(factsList.size() - i);
                    Facts contactFacts = factsList.get(i);

                    Facts attentionFlagsFacts = new Facts();
                    for (Map.Entry<String, Object> entry : contactFacts.asMap().entrySet()) {
                        if (entry.getKey().equals(Constants.DETAILS_KEY.ATTENTION_FLAG_FACTS)) {
                            JSONObject attentionFlags = new JSONObject(String.valueOf(entry.getValue()));
                            Iterator<String> keys = attentionFlags.keys();

                            while (keys.hasNext()) {
                                String key = keys.next();
                                attentionFlagsFacts.put(key, attentionFlags.get(key));

                            }
                        }
                    }

                    loadPreviousContactsTest(attentionFlagsFacts, contactFacts, specificContactNo);
                } catch (JSONException | IOException | ParseException e) {
                    e.printStackTrace();
                }
            }

        }
    }


    private void loadPreviousContactsTest(Facts facts, Facts contactFacts, String contactNo) throws
            IOException, ParseException {
        List<LastContactDetailsWrapper> lastContactDetailsWrapperList = new ArrayList<>();

        lastContactDetails = new ArrayList<>();
        addOtherRuleObjects(contactFacts);
        addAttentionFlagsRuleObjects(facts);

        Date lastContactDate =
                new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(
                        String.valueOf(contactFacts.asMap().get(Constants.CONTACT_DATE)));

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


    private static List<Facts> reverseList(List<Facts> list) {
        List<Facts> reverse = new ArrayList<>(list);
        Collections.reverse(reverse);
        return reverse;
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
