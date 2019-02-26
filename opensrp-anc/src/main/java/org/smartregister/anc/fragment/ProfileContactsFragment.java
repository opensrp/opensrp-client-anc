package org.smartregister.anc.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jeasy.rules.api.Facts;
import org.smartregister.anc.R;
import org.smartregister.anc.adapter.ProfileOverviewAdapter;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.domain.YamlConfig;
import org.smartregister.anc.domain.YamlConfigItem;
import org.smartregister.anc.domain.YamlConfigWrapper;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.FilePath;
import org.smartregister.anc.util.Utils;
import org.smartregister.view.fragment.BaseProfileFragment;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by ndegwamartin on 12/07/2018.
 */
public class ProfileContactsFragment extends BaseProfileFragment {
    public static final String TAG = ProfileOverviewFragment.class.getCanonicalName();
    private List<YamlConfigWrapper> yamlConfigListGlobal;
    private TextView contactTextView;
    private TextView referral;
    private TextView contactDate;
    private TextView tests_header;

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
        yamlConfigListGlobal = new ArrayList<>();
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

            Facts facts = AncApplication.getInstance().getPreviousContactRepository()
                    .getPreviousContactsFacts(getActivity().getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID));

            String contactNo = String.valueOf(Utils.getTodayContact(clientDetails.get(DBConstants.KEY.NEXT_CONTACT)));
            Date lastContactDate =
                    new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(clientDetails.get(DBConstants.KEY.LAST_CONTACT_RECORD_DATE));

            Iterable<Object> ruleObjects = loadFile(FilePath.FILE.PROFILE_LAST_CONTACT);

            for (Object ruleObject : ruleObjects) {
                List<YamlConfigWrapper> yamlConfigList = new ArrayList<>();
                int valueCount = 0;

                YamlConfig yamlConfig = (YamlConfig) ruleObject;

                List<YamlConfigItem> configItems = yamlConfig.getFields();

                for (YamlConfigItem configItem : configItems) {
                    if (AncApplication.getInstance().getRulesEngineHelper().getRelevance(facts, configItem.getRelevance())) {
                        yamlConfigList.add(new YamlConfigWrapper(null, null, configItem));
                        valueCount += 1;
                    }
                }

                if (valueCount > 0) {
                    yamlConfigListGlobal.addAll(yamlConfigList);
                }
            }

            String gestAge = facts.get(Constants.GEST_AGE);
            if (TextUtils.isEmpty(gestAge)) {
                gestAge = "";
            }

            contactTextView.setText(String.format(getActivity().getString(R.string.contact_details), gestAge, contactNo));
            contactDate.setText(new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(lastContactDate));
            ProfileOverviewAdapter adapter = new ProfileOverviewAdapter(getActivity(), yamlConfigListGlobal, facts);

            // set up the RecyclerView
            RecyclerView recyclerView = getActivity().findViewById(R.id.last_contact_details_recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
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
        contactTextView = fragmentView.findViewById(R.id.contact);
        referral = fragmentView.findViewById(R.id.referral);
        contactDate = fragmentView.findViewById(R.id.contact_date);
        tests_header = fragmentView.findViewById(R.id.tests_header);
        return fragmentView;
    }

    private Iterable<Object> loadFile(String filename) throws IOException {
        return AncApplication.getInstance().readYaml(filename);
    }


}
