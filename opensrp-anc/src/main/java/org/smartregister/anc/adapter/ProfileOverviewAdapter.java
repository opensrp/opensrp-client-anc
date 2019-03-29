package org.smartregister.anc.adapter;

import android.content.Context;
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
import org.smartregister.anc.util.Utils;

import java.util.List;

/**
 * Created by ndegwamartin on 04/12/2018.
 */
public class ProfileOverviewAdapter extends RecyclerView.Adapter<ProfileOverviewAdapter.ViewHolder> {

    private List<YamlConfigWrapper> mData;
    private LayoutInflater mInflater;
    private Facts facts;
    private Context context;

    // data is passed into the constructor
    public ProfileOverviewAdapter(Context context, List<YamlConfigWrapper> data, Facts facts) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.facts = facts;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.profile_overview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (!TextUtils.isEmpty(mData.get(position).getGroup())) {

            holder.sectionHeader.setText(processUnderscores(mData.get(position).getGroup()));
            holder.sectionHeader.setVisibility(View.VISIBLE);

        } else {
            holder.sectionHeader.setVisibility(View.GONE);

        }

        if (!TextUtils.isEmpty(mData.get(position).getSubGroup())) {

            holder.subSectionHeader.setText(processUnderscores(mData.get(position).getSubGroup()));
            holder.subSectionHeader.setVisibility(View.VISIBLE);

        } else {
            holder.subSectionHeader.setVisibility(View.GONE);

        }

        if (mData.get(position).getYamlConfigItem() != null) {

            YamlConfigItem yamlConfigItem = mData.get(position).getYamlConfigItem();

            Template template = getTemplate(yamlConfigItem.getTemplate());
            String output = Utils.fillTemplate(template.detail, this.facts);

            holder.sectionDetailTitle.setText(template.title);
            holder.sectionDetails.setText(output);//Perhaps refactor to use Json Form Parser Implementation

            if (AncApplication.getInstance().getAncRulesEngineHelper().getRelevance(facts, yamlConfigItem.getIsRedFont())) {
                holder.sectionDetailTitle.setTextColor(context.getResources().getColor(R.color.overview_font_red));
                holder.sectionDetails.setTextColor(context.getResources().getColor(R.color.overview_font_red));
            } else {
                holder.sectionDetailTitle.setTextColor(context.getResources().getColor(R.color.overview_font_left));
                holder.sectionDetails.setTextColor(context.getResources().getColor(R.color.overview_font_right));


            }

            holder.sectionDetailTitle.setVisibility(View.VISIBLE);
            holder.sectionDetails.setVisibility(View.VISIBLE);

        } else {
            holder.sectionDetailTitle.setVisibility(View.GONE);
            holder.sectionDetails.setVisibility(View.GONE);
        }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView sectionHeader;
        public TextView subSectionHeader;
        public TextView sectionDetails;
        public TextView sectionDetailTitle;
        public View parent;

        ViewHolder(View itemView) {
            super(itemView);
            sectionHeader = itemView.findViewById(R.id.overview_section_header);
            subSectionHeader = itemView.findViewById(R.id.overview_subsection_header);
            sectionDetailTitle = itemView.findViewById(R.id.overview_section_details_left);
            sectionDetails = itemView.findViewById(R.id.overview_section_details_right);
            parent = itemView;
        }
    }

    private String processUnderscores(String string) {
        return string.replace("_", " ").toUpperCase();
    }

    private Template getTemplate(String rawTemplate) {
        Template template = new Template();

        if (rawTemplate.contains(":")) {
            String[] templateArray = rawTemplate.split(":");
            if (templateArray.length > 1) {
                template.title = templateArray[0].trim();
                template.detail = templateArray[1].trim();
            }
        } else {
            template.title = rawTemplate;
        }

        return template;

    }

    private class Template {
        public String title = "";
        public String detail = "";
    }

}
