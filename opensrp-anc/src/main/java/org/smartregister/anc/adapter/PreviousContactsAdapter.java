package org.smartregister.anc.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jeasy.rules.api.Facts;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.R;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.domain.LastContactDetailsWrapper;
import org.smartregister.anc.domain.YamlConfig;
import org.smartregister.anc.domain.YamlConfigItem;
import org.smartregister.anc.domain.YamlConfigWrapper;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.FilePath;
import org.smartregister.anc.util.JsonFormUtils;
import org.smartregister.anc.util.Utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class PreviousContactsAdapter extends RecyclerView.Adapter<PreviousContactsAdapter.ViewHolder>  {
    private List<Facts> factsList;
    private LayoutInflater inflater;
    private JsonFormUtils formUtils  = new JsonFormUtils();
    private Context context;

    public PreviousContactsAdapter(List<Facts> factsList, Context context) {
        this.factsList = factsList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public PreviousContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.last_contact_row, viewGroup, false);
        return new PreviousContactsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviousContactsAdapter.ViewHolder viewHolder, int position) {
        if (factsList.size() > 0) {
            String contactNo = String.valueOf(position + 1);
            Facts facts = factsList.get(position);

            for (Map.Entry<String, Objects> entry : facts.) {
                if (Integer.parseInt(entry.getKey()) > 0) {
                    int index = Integer.parseInt(entry.getKey())-1;
                    Facts facts = entry.getValue();
                    contactFactsList.add(index, facts);
                }
            }

        }
    }

    private void loadPreviousContactsTest(String baseEntityId, Facts facts, String contactNo) throws ParseException,
            IOException,
            JSONException {
        List<LastContactDetailsWrapper> lastContactDetailsWrapperList = new ArrayList<>();


        Iterator<String> keys = jsonObject.keys();

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
        LastContactAdapter adapter = new LastContactAdapter(lastContactDetailsWrappers, context);
        adapter.notifyDataSetChanged();
        RecyclerView recyclerView = last_contact_layout.findViewById(R.id.last_contact_information);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return factsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
       private RecyclerView contactDisplay;

        ViewHolder(View itemView) {
            super(itemView);
            contactDisplay = itemView.findViewById(R.id.contact_display);
        }
    }
}
