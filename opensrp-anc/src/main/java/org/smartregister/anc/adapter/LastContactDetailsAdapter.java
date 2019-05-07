package org.smartregister.anc.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jeasy.rules.api.Facts;
import org.smartregister.anc.R;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.domain.YamlConfigItem;
import org.smartregister.anc.domain.YamlConfigWrapper;
import org.smartregister.anc.util.JsonFormUtils;
import org.smartregister.anc.util.Utils;

import java.util.List;

public class LastContactDetailsAdapter extends RecyclerView.Adapter<LastContactDetailsAdapter.ViewHolder> {

    private List<YamlConfigWrapper> data;
    private Facts facts;
    private LayoutInflater mInflater;
    private Context context;
    private JsonFormUtils jsonFormUtils = new JsonFormUtils();

    // data is passed into the constructor
    public LastContactDetailsAdapter(Context context, List<YamlConfigWrapper> data, Facts facts) {
        this.mInflater = LayoutInflater.from(context);
        this.data = data;
        this.facts = facts;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.previous_contacts_preview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (data != null && data.size() > 0 && data.get(position).getYamlConfigItem() != null) {

            YamlConfigItem yamlConfigItem = data.get(position).getYamlConfigItem();

            JsonFormUtils.Template template = jsonFormUtils.getTemplate(yamlConfigItem.getTemplate());
            String output = "";
            if (!TextUtils.isEmpty(template.detail)) {
                output = Utils.fillTemplate(template.detail, facts);
            }

            holder.sectionDetailTitle.setText(template.title);
            holder.sectionDetails.setText(output);

            if (AncApplication.getInstance().getAncRulesEngineHelper()
                    .getRelevance(facts, yamlConfigItem.getIsRedFont())) {
                holder.sectionDetailTitle.setTextColor(context.getResources().getColor(R.color.overview_font_red));
                holder.sectionDetails.setTextColor(context.getResources().getColor(R.color.overview_font_red));
            } else {
                holder.sectionDetailTitle.setTextColor(context.getResources().getColor(R.color.overview_font_left));
                holder.sectionDetails.setTextColor(context.getResources().getColor(R.color.overview_font_right));


            }

            holder.sectionDetailTitle.setVisibility(View.VISIBLE);
            holder.sectionDetails.setVisibility(View.VISIBLE);
        }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return data.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        public View parent;
        private TextView sectionDetails;
        private TextView sectionDetailTitle;

        ViewHolder(View itemView) {
            super(itemView);
            sectionDetailTitle = itemView.findViewById(R.id.overview_section_details_left);
            sectionDetails = itemView.findViewById(R.id.overview_section_details_right);
            parent = itemView;
        }
    }
}