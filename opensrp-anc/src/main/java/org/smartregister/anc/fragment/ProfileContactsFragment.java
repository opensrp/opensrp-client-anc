package org.smartregister.anc.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jeasy.rules.api.Facts;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.PreviousContactsDetailsActivity;
import org.smartregister.anc.activity.PreviousContactsTestsActivity;
import org.smartregister.anc.adapter.LastContactAdapter;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.ProfileFragmentContract;
import org.smartregister.anc.domain.LastContactDetailsWrapper;
import org.smartregister.anc.domain.YamlConfig;
import org.smartregister.anc.domain.YamlConfigItem;
import org.smartregister.anc.domain.YamlConfigWrapper;
import org.smartregister.anc.presenter.ProfileFragmentPresenter;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.FilePath;
import org.smartregister.anc.util.JsonFormUtils;
import org.smartregister.anc.util.Utils;
import org.smartregister.view.fragment.BaseProfileFragment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by ndegwamartin on 12/07/2018.
 */
public class ProfileContactsFragment extends BaseProfileFragment implements ProfileFragmentContract.View {
    public static final String TAG = ProfileOverviewFragment.class.getCanonicalName();
    private List<YamlConfigWrapper> lastContactDetails;
    private List<YamlConfigWrapper> lastContactTests;
    private TextView testsHeader;
    private LinearLayout lastContactLayout;
    private LinearLayout testLayout;
    private LinearLayout testsDisplayLayout;
    private ProfileContactsActionHandler profileContactsActionHandler = new ProfileContactsActionHandler();
    private JsonFormUtils formUtils = new JsonFormUtils();
    private ProfileFragmentContract.Presenter presenter;
    private String baseEntityId;
    private String contactNo;

    public static ProfileContactsFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        ProfileContactsFragment fragment = new ProfileContactsFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        presenter = new ProfileFragmentPresenter(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializePresenter();
    }

    @Override
    protected void onCreation() {
        lastContactDetails = new ArrayList<>();
        lastContactTests = new ArrayList<>();
        if (testsDisplayLayout != null) {
            testsDisplayLayout.removeAllViews();
        }
    }

    @Override
    protected void onResumption() {
        lastContactDetails = new ArrayList<>();
        lastContactTests = new ArrayList<>();
        if (testsDisplayLayout != null) {
            testsDisplayLayout.removeAllViews();
        }
        baseEntityId = getActivity().getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);
        HashMap<String, String> clientDetails =
                (HashMap<String, String>) getActivity().getIntent().getSerializableExtra(Constants.INTENT_KEY.CLIENT_MAP);
        contactNo = String.valueOf(Utils.getTodayContact(clientDetails.get(DBConstants.KEY.NEXT_CONTACT)));
        initializeLastContactDetails(clientDetails);
    }

    private void initializeLastContactDetails(HashMap<String, String> clientDetails) {
        if (clientDetails != null) {
            try {
                List<LastContactDetailsWrapper> lastContactDetailsWrapperList = new ArrayList<>();
                List<LastContactDetailsWrapper> lastContactDetailsTestsWrapperList = new ArrayList<>();

                Facts facts = presenter.getImmediatePreviousContact(clientDetails, baseEntityId, contactNo);

                addOtherRuleObjects(facts);
                addAttentionFlagsRuleObjects(facts);
                addTestsRuleObjects(facts);

                Date lastContactDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .parse(clientDetails.get(DBConstants.KEY.LAST_CONTACT_RECORD_DATE));

                String displayContactDate =
                        new SimpleDateFormat("dd MMM " + "yyyy", Locale.getDefault()).format(lastContactDate);

                if (lastContactDetails.isEmpty()) {
                    lastContactLayout.setVisibility(View.GONE);
                } else {
                    lastContactDetailsWrapperList
                            .add(new LastContactDetailsWrapper(contactNo, displayContactDate, lastContactDetails, facts));
                    setUpContactDetailsRecycler(lastContactDetailsWrapperList);
                }

                if (lastContactTests.isEmpty()) {
                    testLayout.setVisibility(View.GONE);
                } else {
                    lastContactDetailsTestsWrapperList
                            .add(new LastContactDetailsWrapper(contactNo, displayContactDate, lastContactTests, facts));
                    testsHeader.setText(
                            String.format(getActivity().getResources().getString(R.string.recent_test), displayContactDate));
                    setUpContactTestsDetails(lastContactDetailsTestsWrapperList);
                }

            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
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
        Iterable<Object> attentionFlagsRuleObjects = AncApplication.getInstance().readYaml(FilePath.FILE.ATTENTION_FLAGS);

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

    private void addTestsRuleObjects(Facts facts) throws IOException {
        Iterable<Object> testsRuleObjects = AncApplication.getInstance().readYaml(FilePath.FILE.PROFILE_LAST_CONTACT_TEST);

        for (Object ruleObject : testsRuleObjects) {
            YamlConfig testsConfig = (YamlConfig) ruleObject;
            for (YamlConfigItem yamlConfigItem : testsConfig.getFields()) {

                if (AncApplication.getInstance().getAncRulesEngineHelper()
                        .getRelevance(facts, yamlConfigItem.getRelevance())) {
                    lastContactTests.add(new YamlConfigWrapper(null, null, yamlConfigItem));

                }

            }
        }
    }

    private void setUpContactDetailsRecycler(List<LastContactDetailsWrapper> lastContactDetailsWrappers) {
        LastContactAdapter adapter = new LastContactAdapter(lastContactDetailsWrappers, getActivity());
        adapter.notifyDataSetChanged();
        RecyclerView recyclerView = lastContactLayout.findViewById(R.id.last_contact_information);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private void setUpContactTestsDetails(List<LastContactDetailsWrapper> lastContactDetailsTestsWrapperList) {
        List<YamlConfigWrapper> data = new ArrayList<>();
        Facts facts = new Facts();
        if (lastContactDetailsTestsWrapperList.size() > 0) {
            for (int i = 0; i < lastContactDetailsTestsWrapperList.size(); i++) {
                LastContactDetailsWrapper lastContactDetailsTest = lastContactDetailsTestsWrapperList.get(i);
                data = lastContactDetailsTest.getExtraInformation();
                facts = lastContactDetailsTest.getFacts();
            }
        }

        populateTestDetails(data, facts);
    }

    private void populateTestDetails(List<YamlConfigWrapper> data, Facts facts) {
        if (data != null && data.size() > 0) {
            for (int position = 0; position < data.size(); position++) {
                if (data.get(position).getYamlConfigItem() != null) {
                    ConstraintLayout constraintLayout = formUtils.createListViewItems(data, facts, position, getActivity());
                    testsDisplayLayout.addView(constraintLayout);
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_profile_contacts, container, false);
        lastContactLayout = fragmentView.findViewById(R.id.last_contact_layout);
        TextView lastContactBottom = lastContactLayout.findViewById(R.id.last_contact_bottom);
        lastContactBottom.setOnClickListener(profileContactsActionHandler);

        testLayout = fragmentView.findViewById(R.id.test_layout);
        testsHeader = testLayout.findViewById(R.id.tests_header);
        TextView testsBottom = testLayout.findViewById(R.id.tests_bottom);
        testsBottom.setOnClickListener(profileContactsActionHandler);

        testsDisplayLayout = testLayout.findViewById(R.id.test_display_layout);

        return fragmentView;
    }

    private Iterable<Object> loadFile(String filename) throws IOException {
        return AncApplication.getInstance().readYaml(filename);
    }

    private void goToPreviousContacts() {
        Intent intent = new Intent(getActivity(), PreviousContactsDetailsActivity.class);
        String baseEntityId = getActivity().getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, baseEntityId);
        intent.putExtra(Constants.INTENT_KEY.CLIENT_MAP,
                getActivity().getIntent().getSerializableExtra(Constants.INTENT_KEY.CLIENT_MAP));

        this.startActivity(intent);
    }

    private void goToPreviousContactsTests() {
        Intent intent = new Intent(getActivity(), PreviousContactsTestsActivity.class);
        String baseEntityId = getActivity().getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, baseEntityId);
        intent.putExtra(Constants.INTENT_KEY.CLIENT_MAP,
                getActivity().getIntent().getSerializableExtra(Constants.INTENT_KEY.CLIENT_MAP));

        this.startActivity(intent);
    }

    /**
     * Handles the Click actions on any of the section in the page.
     */
    private class ProfileContactsActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.last_contact_bottom && !lastContactDetails.isEmpty()) {
                goToPreviousContacts();
            } else if (view.getId() == R.id.tests_bottom && !lastContactTests.isEmpty()) {
                goToPreviousContactsTests();
            }
        }

    }
}
