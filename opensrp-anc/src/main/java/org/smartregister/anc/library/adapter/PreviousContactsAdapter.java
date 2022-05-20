package org.smartregister.anc.library.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jeasy.rules.api.Facts;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.domain.LastContactDetailsWrapper;
import org.smartregister.anc.library.domain.YamlConfig;
import org.smartregister.anc.library.domain.YamlConfigItem;
import org.smartregister.anc.library.domain.YamlConfigWrapper;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.FilePathUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PreviousContactsAdapter extends RecyclerView.Adapter<PreviousContactsAdapter.ViewHolder> {
    private List<Facts> factsList;
    private LayoutInflater inflater;
    private Context context;
    private List<YamlConfigWrapper> lastContactDetails;

    public PreviousContactsAdapter(List<Facts> factsList, Context context) {
        this.factsList = factsList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public PreviousContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.previous_contact_row, viewGroup, false);
        return new PreviousContactsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviousContactsAdapter.ViewHolder viewHolder, int position) {
        if (factsList.size() > 0) {
            try {
                String contactNo = String.valueOf(factsList.size() - position);
                Facts contactFacts = factsList.get(position);

                Facts attentionFlagsFacts = new Facts();
                for (Map.Entry<String, Object> entry : contactFacts.asMap().entrySet()) {
                    if (entry.getKey().equals(ConstantsUtils.DetailsKeyUtils.ATTENTION_FLAG_FACTS)) {
                        JSONObject attentionFlags = new JSONObject(String.valueOf(entry.getValue()));
                        Iterator<String> keys = attentionFlags.keys();

                        while (keys.hasNext()) {
                            String key = keys.next();
                            attentionFlagsFacts.put(key, attentionFlags.get(key));

                        }
                    }
                }

                loadPreviousContactsTest(attentionFlagsFacts, contactFacts, contactNo, viewHolder);
            } catch (JSONException | IOException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadPreviousContactsTest(Facts facts, Facts contactFacts, String contactNo, ViewHolder holder)
            throws IOException, ParseException {
        List<LastContactDetailsWrapper> lastContactDetailsWrapperList = new ArrayList<>();

        lastContactDetails = new ArrayList<>();
        addOtherRuleObjects(contactFacts);
        addAttentionFlagsRuleObjects(facts);

        Date lastContactDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .parse(String.valueOf(contactFacts.asMap().get(ConstantsUtils.CONTACT_DATE)));

        String displayContactDate = new SimpleDateFormat("dd MMM " + "yyyy", Locale.getDefault()).format(lastContactDate);

        lastContactDetailsWrapperList
                .add(new LastContactDetailsWrapper(contactNo, displayContactDate, lastContactDetails, facts));
        setUpContactDetailsRecycler(lastContactDetailsWrapperList, holder);

    }

    private void addOtherRuleObjects(Facts facts) throws IOException {
        Iterable<Object> ruleObjects = loadFile(FilePathUtils.FileUtils.PROFILE_LAST_CONTACT);

        for (Object ruleObject : ruleObjects) {
            List<YamlConfigWrapper> yamlConfigList = new ArrayList<>();
            int valueCount = 0;
            YamlConfig yamlConfig = (YamlConfig) ruleObject;

            List<YamlConfigItem> configItems = yamlConfig.getFields();

            for (YamlConfigItem configItem : configItems) {
                if (AncLibrary.getInstance().getAncRulesEngineHelper().getRelevance(facts, configItem.getRelevance())) {
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
        Iterable<Object> attentionFlagsRuleObjects = loadFile(FilePathUtils.FileUtils.ATTENTION_FLAGS);

        for (Object ruleObject : attentionFlagsRuleObjects) {
            YamlConfig attentionFlagConfig = (YamlConfig) ruleObject;
            for (YamlConfigItem yamlConfigItem : attentionFlagConfig.getFields()) {

                if (AncLibrary.getInstance().getAncRulesEngineHelper()
                        .getRelevance(facts, yamlConfigItem.getRelevance())) {

                    lastContactDetails.add(new YamlConfigWrapper(null, null, yamlConfigItem));

                }

            }
        }
    }

    private void setUpContactDetailsRecycler(List<LastContactDetailsWrapper> lastContactDetailsWrappers, ViewHolder holder) {
        LastContactAdapter adapter = new LastContactAdapter(lastContactDetailsWrappers, context);
        adapter.notifyDataSetChanged();
        holder.contactDisplay.setLayoutManager(new LinearLayoutManager(context));
        holder.contactDisplay.setAdapter(adapter);
    }

    private Iterable<Object> loadFile(String filename) {
        return AncLibrary.getInstance().readYaml(filename);
    }

    @Override
    public int getItemCount() {
        return factsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView contactDisplay;

        ViewHolder(View itemView) {
            super(itemView);
            contactDisplay = itemView.findViewById(R.id.previous_contacts_details);
        }
    }
}
