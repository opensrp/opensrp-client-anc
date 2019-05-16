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

import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Facts;
import org.smartregister.anc.R;
import org.smartregister.anc.domain.LastContactDetailsWrapper;
import org.smartregister.anc.domain.YamlConfigWrapper;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.JsonFormUtils;

import java.util.List;

public class LastContactAdapter extends RecyclerView.Adapter<LastContactAdapter.ViewHolder> {
    private List<LastContactDetailsWrapper> lastContactDetailsList;
    private LayoutInflater inflater;
    private JsonFormUtils formUtils = new JsonFormUtils();
    private Context context;

    public LastContactAdapter(List<LastContactDetailsWrapper> lastContactDetailsList, Context context) {
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

            String gestAge = facts.get(Constants.GEST_AGE_OPENMRS);
            if (TextUtils.isEmpty(gestAge)) {
                gestAge = "";
            }

            String contactNo = "";
            if (!lastContactDetails.getContactNo().contains("-")) {
                contactNo = lastContactDetails.getContactNo();
            }

            if (!StringUtils.isEmpty(gestAge)) {
                viewHolder.contactTextView.setText(
                        !StringUtils.isEmpty(contactNo) ? String
                                .format(context.getResources().getString(R.string.contact_details), gestAge,
                                        contactNo) : String
                                .format(context.getResources().getString(R.string.ga_weeks), gestAge));
            }

            if (StringUtils.isEmpty(gestAge) && StringUtils.isEmpty(contactNo)) {
                viewHolder.contactTextView.setText("");
            }

            viewHolder.contactDate.setText(lastContactDetails.getContactDate());

            if (lastContactDetails.getContactNo() != null && Integer.parseInt(lastContactDetails.getContactNo()) < 1) {
                viewHolder.referral.setVisibility(View.VISIBLE);
            }

            createContactDetailsView(lastContactDetails.getExtraInformation(), facts, viewHolder);
        }
    }

    private void createContactDetailsView(List<YamlConfigWrapper> data, Facts facts, ViewHolder viewHolder) {
        if (data != null && data.size() > 0) {
            for (int position = 0; position < data.size(); position++) {
                if (data.get(position).getYamlConfigItem() != null) {
                    ConstraintLayout constraintLayout = formUtils.createListViewItems(data, facts, position, context);
                    viewHolder.lastContactDetails.addView(constraintLayout);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return lastContactDetailsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView contactTextView;
        public TextView referral;
        private TextView contactDate;
        private LinearLayout lastContactDetails;

        ViewHolder(View itemView) {
            super(itemView);
            contactTextView = itemView.findViewById(R.id.contact);
            referral = itemView.findViewById(R.id.referral);
            contactDate = itemView.findViewById(R.id.contact_date);
            lastContactDetails = itemView.findViewById(R.id.last_contact_details);
        }
    }
}
