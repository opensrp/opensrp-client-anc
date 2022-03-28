package org.smartregister.anc.library.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jeasy.rules.api.Facts;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.domain.YamlConfig;
import org.smartregister.anc.library.domain.YamlConfigItem;
import org.smartregister.anc.library.util.Utils;

import java.util.List;

/**
 * Created by ndegwamartin on 04/12/2018.
 */
public class ContactSummaryFinishAdapter extends RecyclerView.Adapter<ContactSummaryFinishAdapter.ViewHolder> {

    private List<YamlConfig> mData;
    private LayoutInflater mInflater;
    private Facts facts;

    // data is passed into the constructor
    public ContactSummaryFinishAdapter(Context context, List<YamlConfig> data, Facts facts) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.facts = facts;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activity_contact_summary_finish_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.sectionHeader.setText(processUnderscores(mData.get(position).getGroup()));

        List<YamlConfigItem> fields = mData.get(position).getFields();
        StringBuilder outputBuilder = new StringBuilder();
        for (YamlConfigItem yamlConfigItem : fields) {
            if (yamlConfigItem.isMultiWidget() != null && yamlConfigItem.isMultiWidget()) {
                prefillInjectableFacts(facts, yamlConfigItem.getTemplate());
            }
            if (AncLibrary.getInstance().getAncRulesEngineHelper().getRelevance(facts, yamlConfigItem.getRelevance())) {
                outputBuilder.append(Utils.fillTemplate(yamlConfigItem.getTemplate(), this.facts)).append("\n\n");
            }
        }
        String output = outputBuilder.toString();

        holder.sectionDetails.setText(output);

        if (output.trim().isEmpty()) {
            holder.parent.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            holder.parent.setVisibility(View.GONE);
        } else {
            holder.parent.setLayoutParams(
                    new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            holder.parent.setVisibility(View.VISIBLE);
        }


    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    private String processUnderscores(String string) {
        return string.replace("_", " ").toUpperCase();
    }

    private void prefillInjectableFacts(Facts facts, String template) {
        String[] relevanceToken = template.split(",");
        String key;
        for (String token : relevanceToken) {
            if (token.contains("{") && token.contains("}")) {
                key = token.substring(token.indexOf('{') + 1, token.indexOf('}'));
                if (facts.get(key) == null) {
                    facts.put(key, "");
                }
            }
        }
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView sectionHeader;
        public TextView sectionDetails;
        public View parent;

        ViewHolder(View itemView) {
            super(itemView);
            sectionHeader = itemView.findViewById(R.id.contact_summary_section_header);
            sectionDetails = itemView.findViewById(R.id.contact_summary_section_details);
            parent = itemView;
        }
    }

}
