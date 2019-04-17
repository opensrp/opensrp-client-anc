package org.smartregister.anc.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jeasy.rules.api.Facts;
import org.smartregister.anc.R;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.domain.LastContactDetailsWrapper;
import org.smartregister.anc.domain.YamlConfigItem;
import org.smartregister.anc.domain.YamlConfigWrapper;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.Utils;

import java.util.List;

public class LastContactAdapter extends RecyclerView.Adapter<LastContactAdapter.ViewHolder> {
    private List<LastContactDetailsWrapper> lastContactDetailsList;
    private LayoutInflater inflater;
    private Context context;

    public LastContactAdapter(List<LastContactDetailsWrapper> lastContactDetailsList,
                              Context context) {
        this.lastContactDetailsList = lastContactDetailsList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.last_contact_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if (lastContactDetailsList.size() > 0) {
            LastContactDetailsWrapper lastContactDetails = lastContactDetailsList.get(position);
            Facts facts = lastContactDetails.getFacts();

            String gestAge = facts.get(Constants.GEST_AGE);
            if (TextUtils.isEmpty(gestAge)) {
                gestAge = "";
            }

            viewHolder.contactTextView.setText(String.format(context.getResources().getString(R.string.contact_details), gestAge, lastContactDetails.getContactNo()));
            viewHolder.contactDate.setText(lastContactDetails.getContactDate());
            createContactDetailsView(lastContactDetails.getExtraInformation(), facts, viewHolder);
        }
    }

    private void createContactDetailsView(List<YamlConfigWrapper> data, Facts facts, ViewHolder viewHolder) {
        if (data != null && data.size() > 0) {
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getYamlConfigItem() != null) {

                    YamlConfigItem yamlConfigItem = data.get(i).getYamlConfigItem();

                    Template template = getTemplate(yamlConfigItem.getTemplate());
                    String output = "";
                    if (!TextUtils.isEmpty(template.detail)) {
                        output = Utils.fillTemplate(template.detail, facts);
                    }


                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    ConstraintLayout constraintLayout = (ConstraintLayout) inflater
                            .inflate(R.layout.previous_contacts_preview_row, null);
                    TextView sectionDetailTitle = constraintLayout.findViewById(R.id.overview_section_details_left);
                    TextView sectionDetails = constraintLayout.findViewById(R.id.overview_section_details_right);


                    sectionDetailTitle.setText(template.title);
                    sectionDetails.setText(output);//Perhaps refactor to use Json Form Parser Implementation

                    if (AncApplication.getInstance().getAncRulesEngineHelper()
                            .getRelevance(facts, yamlConfigItem.getIsRedFont())) {
                        sectionDetailTitle.setTextColor(context.getResources().getColor(R.color.overview_font_red));
                        sectionDetails.setTextColor(context.getResources().getColor(R.color.overview_font_red));
                    } else {
                        sectionDetailTitle.setTextColor(context.getResources().getColor(R.color.overview_font_left));
                        sectionDetails.setTextColor(context.getResources().getColor(R.color.overview_font_right));


                    }

                    sectionDetailTitle.setVisibility(View.VISIBLE);
                    sectionDetails.setVisibility(View.VISIBLE);

                    viewHolder.lastContactDetails.addView(constraintLayout);
                }
            }
        }


    }

    private Template getTemplate(String rawTemplate) {
        Template template = new Template();

        if (rawTemplate.contains(":")) {
            String[] templateArray = rawTemplate.split(":");
            if (templateArray.length == 1) {
                template.title = templateArray[0].trim();
            } else if (templateArray.length > 1) {
                template.title = templateArray[0].trim();
                template.detail = templateArray[1].trim();
            }
        } else {
            template.title = rawTemplate;
            template.detail = "true";
        }

        return template;

    }

    private class Template {
        public String title = "";
        public String detail = "";
    }

    @Override
    public int getItemCount() {
        return lastContactDetailsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView contactTextView;
        public TextView referral;
        public TextView contactDate;
        public LinearLayout lastContactDetails;

        ViewHolder(View itemView) {
            super(itemView);
            contactTextView = itemView.findViewById(R.id.contact);
            referral = itemView.findViewById(R.id.referral);
            contactDate = itemView.findViewById(R.id.contact_date);
            lastContactDetails = itemView.findViewById(R.id.last_contact_details);
        }
    }
}
