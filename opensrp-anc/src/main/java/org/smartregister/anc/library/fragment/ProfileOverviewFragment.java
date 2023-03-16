package org.smartregister.anc.library.fragment;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.jeasy.rules.api.Facts;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.ProfileActivity;
import org.smartregister.anc.library.adapter.ProfileOverviewAdapter;
import org.smartregister.anc.library.domain.ButtonAlertStatus;
import org.smartregister.anc.library.domain.YamlConfig;
import org.smartregister.anc.library.domain.YamlConfigItem;
import org.smartregister.anc.library.domain.YamlConfigWrapper;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.anc.library.util.FilePathUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.view.fragment.BaseProfileFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 12/07/2018.
 */
public class ProfileOverviewFragment extends BaseProfileFragment {
    private List<YamlConfigWrapper> yamlConfigListGlobal;
    private Button dueButton;
    private ButtonAlertStatus buttonAlertStatus;
    private String baseEntityId;
    private String contactNo;
    private View noHealthRecordLayout;
    private RecyclerView profileOverviewRecycler;
    private Utils utils = new Utils();

    public static ProfileOverviewFragment newInstance(Bundle bundle) {
        Bundle bundles = bundle;
        ProfileOverviewFragment fragment = new ProfileOverviewFragment();
        if (bundles == null) {
            bundles = new Bundle();
        }
        fragment.setArguments(bundles);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onCreation() {
        if (getActivity() != null && getActivity().getIntent() != null) {
            HashMap<String, String> clientDetails =
                    (HashMap<String, String>) getActivity().getIntent().getSerializableExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP);
            if (clientDetails != null) {
                buttonAlertStatus = Utils.getButtonAlertStatus(clientDetails, getActivity().getApplicationContext(), true);
                contactNo = String.valueOf(Utils.getTodayContact(clientDetails.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT)));
            }
            yamlConfigListGlobal = new ArrayList<>();
            baseEntityId = getActivity().getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID);
        } else {
            Timber.d("getIntent or getActivity might be null");
        }
    }

    @Override
    protected void onResumption() {
        try {
            yamlConfigListGlobal = new ArrayList<>(); //This makes sure no data duplication happens
            Facts facts = AncLibrary.getInstance().getPreviousContactRepository().getPreviousContactFacts(baseEntityId, contactNo, false);
            Iterable<Object> ruleObjects = utils.loadRulesFiles(FilePathUtils.FileUtils.PROFILE_OVERVIEW);

            for (Object ruleObject : ruleObjects) {
                List<YamlConfigWrapper> yamlConfigList = new ArrayList<>();
                int valueCount = 0;

                YamlConfig yamlConfig = (YamlConfig) ruleObject;
                if (yamlConfig.getGroup() != null) {
                    yamlConfigList.add(new YamlConfigWrapper(yamlConfig.getGroup(), null, null));
                }

                if (yamlConfig.getSubGroup() != null) {
                    yamlConfigList.add(new YamlConfigWrapper(null, yamlConfig.getSubGroup(), null));
                }

                List<YamlConfigItem> configItems = yamlConfig.getFields();

                for (YamlConfigItem configItem : configItems) {
                    if (AncLibrary.getInstance().getAncRulesEngineHelper().getRelevance(facts, configItem.getRelevance())) {
                        yamlConfigList.add(new YamlConfigWrapper(null, null, configItem));
                        valueCount += 1;
                    }
                }

                if (valueCount > 0) {
                    yamlConfigListGlobal.addAll(yamlConfigList);
                }
            }

            Utils.processButtonAlertStatus(getActivity(), dueButton, buttonAlertStatus);

            attachRecyclerView(facts);

            if (yamlConfigListGlobal.isEmpty()) {
                noHealthRecordLayout.setVisibility(View.VISIBLE);
                profileOverviewRecycler.setVisibility(View.GONE);
            } else {
                noHealthRecordLayout.setVisibility(View.GONE);
                profileOverviewRecycler.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Timber.e(e, " --> onResumption");
        }
    }

    private void attachRecyclerView(Facts facts) {
        ProfileOverviewAdapter adapter = new ProfileOverviewAdapter(getActivity(), yamlConfigListGlobal, facts);
        adapter.notifyDataSetChanged();
        profileOverviewRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        profileOverviewRecycler.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_profile_overview, container, false);
        noHealthRecordLayout = fragmentView.findViewById(R.id.no_health_data_recorded_profile_overview_layout);
        profileOverviewRecycler = fragmentView.findViewById(R.id.profile_overview_recycler);
        dueButton = ((ProfileActivity) getActivity()).getDueButton();
        if (!ConstantsUtils.AlertStatusUtils.TODAY.equals(buttonAlertStatus.buttonAlertStatus)) {
            dueButton.setOnClickListener((ProfileActivity) getActivity());
        } else {
            dueButton.setEnabled(false);
        }

        return fragmentView;
    }
}
