package org.smartregister.anc.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jeasy.rules.api.Facts;
import org.json.JSONObject;
import org.smartregister.anc.R;
import org.smartregister.anc.adapter.LastContactAdapter;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.domain.LastContactDetailsWrapper;
import org.smartregister.anc.domain.YamlConfig;
import org.smartregister.anc.domain.YamlConfigItem;
import org.smartregister.anc.domain.YamlConfigWrapper;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.FilePath;
import org.smartregister.anc.util.Utils;
import org.smartregister.view.fragment.BaseProfileFragment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by ndegwamartin on 12/07/2018.
 */
public class ProfileContactsFragment extends BaseProfileFragment {
    public static final String TAG = ProfileOverviewFragment.class.getCanonicalName();
    private List<YamlConfigWrapper> lastContactDetails;
    private TextView tests_header;
    private LinearLayout last_contact_layout;

    public static ProfileContactsFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        ProfileContactsFragment fragment = new ProfileContactsFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onCreation() {
        lastContactDetails = new ArrayList<>();
    }

    @Override
    protected void onResumption() {
        HashMap<String, String> clientDetails = (HashMap<String, String>) getActivity().getIntent()
                .getSerializableExtra(Constants.INTENT_KEY.CLIENT_MAP);
        initializeLastContactDetails(clientDetails);
        initializeTestDetails(clientDetails);
    }

    private void initializeLastContactDetails(HashMap<String, String> clientDetails) {
        try {
            List<LastContactDetailsWrapper> lastContactDetailsWrapperList = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(
                    AncApplication.getInstance().getDetailsRepository().getAllDetailsForClient(
                            getActivity().getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID))
                            .get(Constants.DETAILS_KEY.ATTENTION_FLAG_FACTS));

            Iterator<String> keys = jsonObject.keys();

            Facts facts = AncApplication.getInstance().getPreviousContactRepository()
                    .getPreviousContactsFacts(getActivity().getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID));

            while (keys.hasNext()) {
                String key = keys.next();
                facts.put(key, jsonObject.get(key));
            }

            addOtherRuleObjects(facts);

            addAttentionFlagsRuleObjects(facts);

            String contactNo = String.valueOf(Utils.getTodayContact(clientDetails.get(DBConstants.KEY.NEXT_CONTACT)));
            Date lastContactDate =
                    new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .parse(clientDetails.get(DBConstants.KEY.LAST_CONTACT_RECORD_DATE));
            lastContactDetailsWrapperList.add(new LastContactDetailsWrapper(contactNo, new SimpleDateFormat("dd MMM yyyy",
                    Locale.getDefault()).format(lastContactDate), lastContactDetails, facts));

            setUpContactDetailsRecycler(lastContactDetailsWrapperList);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
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
                if (AncApplication.getInstance().getAncRulesEngineHelper().getRelevance(facts, configItem.getRelevance())) {
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
        Iterable<Object> attentionFlagsRuleObjects = AncApplication.getInstance()
                .readYaml(FilePath.FILE.ATTENTION_FLAGS);

        for (Object ruleObject : attentionFlagsRuleObjects) {
            YamlConfig attentionFlagConfig = (YamlConfig) ruleObject;
            for (YamlConfigItem yamlConfigItem : attentionFlagConfig.getFields()) {

                if (AncApplication.getInstance().getAncRulesEngineHelper()
                        .getRelevance(facts, yamlConfigItem.getRelevance())) {

                    lastContactDetails.add(new YamlConfigWrapper(null, null, yamlConfigItem));

                }

            }
        }
    }

    private void setUpContactDetailsRecycler(List<LastContactDetailsWrapper> lastContactDetailsWrappers) {
        LastContactAdapter adapter = new LastContactAdapter(lastContactDetailsWrappers, getActivity());

        RecyclerView recyclerView = last_contact_layout.findViewById(R.id.last_contact_information);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private void initializeTestDetails(HashMap<String, String> clientDetails) {
        try {
            Date lastContactDate =
                    new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .parse(clientDetails.get(DBConstants.KEY.LAST_CONTACT_RECORD_DATE));
            tests_header.setText(String.format(getActivity().getString(R.string.recent_test),
                    new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(lastContactDate)));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_profile_contacts, container, false);
        tests_header = fragmentView.findViewById(R.id.tests_header);
        last_contact_layout = fragmentView.findViewById(R.id.last_contact_layout);
        return fragmentView;
    }

    private Iterable<Object> loadFile(String filename) throws IOException {
        return AncApplication.getInstance().readYaml(filename);
    }


}
