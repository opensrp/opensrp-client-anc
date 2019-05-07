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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

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

        holder.allTestResultsButton.setVisibility(mData.get(position).isAllTests() ? View.VISIBLE : View.GONE);
        holder.allTestResultsText.setVisibility(mData.get(position).isAllTests() ? View.VISIBLE : View.GONE);
        if (!TextUtils.isEmpty(mData.get(position).getTestResults())) {
            holder.allTestResultsText.setText(mData.get(position).getTestResults());
        } else {
            holder.allTestResultsText.setText("");
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

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        public View parent;
        private TextView sectionHeader;
        private TextView subSectionHeader;
        private TextView sectionDetails;
        private TextView sectionDetailTitle;
        private TextView allTestResultsButton;
        private TextView allTestResultsText;

        ViewHolder(View itemView) {
            super(itemView);
            sectionHeader = itemView.findViewById(R.id.overview_section_header);
            subSectionHeader = itemView.findViewById(R.id.overview_subsection_header);
            sectionDetailTitle = itemView.findViewById(R.id.overview_section_details_left);
            sectionDetails = itemView.findViewById(R.id.overview_section_details_right);
            allTestResultsButton = itemView.findViewById(R.id.all_test_results_button);
            allTestResultsText = itemView.findViewById(R.id.all_test_fetch_text);
            parent = itemView;
        }
    }

    /*private void displayUndoDialog() {
        Activity activity = (Activity) context;
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.all_tests_results_dialog, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(dialogLayout);

        final ImageView cancel = dialogLayout.findViewById(R.id.all_test_popup_cancel);
        CustomFontTextView headerTextView = dialogLayout.findViewById(R.id.txt_title_label);
        headerTextView.setText("Testing stuff to see if this work well without issue");

        final AlertDialog dialog = builder.create();

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams param = window.getAttributes();
            param.gravity = Gravity.TOP;
            window.setAttributes(param);
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }*/

    private class Template {
        public String title = "";
        public String detail = "";
    }
}
