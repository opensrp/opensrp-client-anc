package org.smartregister.anc.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.jeasy.rules.api.Facts;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.ProfileActivity;
import org.smartregister.anc.adapter.ProfileOverviewAdapter;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.domain.ButtonAlertStatus;
import org.smartregister.anc.domain.YamlConfig;
import org.smartregister.anc.domain.YamlConfigItem;
import org.smartregister.anc.domain.YamlConfigWrapper;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.FilePath;
import org.smartregister.anc.util.Utils;
import org.smartregister.view.fragment.BaseProfileFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ndegwamartin on 12/07/2018.
 */
public class ProfileOverviewFragment extends BaseProfileFragment {
    public static final String TAG = ProfileOverviewFragment.class.getCanonicalName();
    private List<YamlConfigWrapper> yamlConfigListGlobal;

    private Button dueButton;
    private ButtonAlertStatus buttonAlertStatus;

    public static ProfileOverviewFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        ProfileOverviewFragment fragment = new ProfileOverviewFragment();
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
        buttonAlertStatus = Utils.getButtonAlertStatus((Map<String, String>) getActivity().getIntent().getSerializableExtra(Constants.INTENT_KEY.CLIENT_MAP), getString(R.string.contact_number_due));
        yamlConfigListGlobal = new ArrayList<>();
    }

    @Override
    protected void onResumption() {
        try {

            Facts facts = AncApplication.getInstance().getPreviousContactRepository()
                    .getPreviousContactsFacts(getActivity().getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID));

            Iterable<Object> ruleObjects = loadFile(FilePath.FILE.PROFILE_OVERVIEW);

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

                    if (AncApplication.getInstance().getAncRulesEngineHelper().getRelevance(facts, configItem.getRelevance())) {
                        yamlConfigList.add(new YamlConfigWrapper(null, null, configItem));
                        valueCount += 1;
                    }
                }

                if (valueCount > 0) {

                    yamlConfigListGlobal.addAll(yamlConfigList);

                }
            }

            Utils.processButtonAlertStatus(getActivity(), dueButton, dueButton, buttonAlertStatus);
            dueButton.setVisibility(View.VISIBLE);

            ProfileOverviewAdapter adapter = new ProfileOverviewAdapter(getActivity(), yamlConfigListGlobal, facts);

            // set up the RecyclerView
            RecyclerView recyclerView = getActivity().findViewById(R.id.profile_overview_recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_profile_overview, container, false);
        dueButton = fragmentView.findViewById(R.id.profile_overview_due_button);
        if (!Constants.ALERT_STATUS.TODAY.equals(buttonAlertStatus.buttonAlertStatus)) {
            dueButton.setOnClickListener((ProfileActivity) getActivity());
        } else {
            dueButton.setEnabled(false);
        }
        return fragmentView;
    }

    private Iterable<Object> loadFile(String filename) throws IOException {

        return AncApplication.getInstance().readYaml(filename);

    }
}
