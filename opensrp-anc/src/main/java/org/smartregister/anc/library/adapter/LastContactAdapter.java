package org.smartregister.anc.library.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Facts;
import org.joda.time.LocalDate;
import org.joda.time.Weeks;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.domain.LastContactDetailsWrapper;
import org.smartregister.anc.library.domain.YamlConfigWrapper;
import org.smartregister.anc.library.util.ANCJsonFormUtils;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.Utils;

import java.util.List;

import timber.log.Timber;

public class LastContactAdapter extends RecyclerView.Adapter<LastContactAdapter.ViewHolder> {
    private List<LastContactDetailsWrapper> lastContactDetailsList;
    private LayoutInflater inflater;
    private ANCJsonFormUtils formUtils = new ANCJsonFormUtils();
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

            String gestAge = facts.get(ConstantsUtils.GEST_AGE_OPENMRS);
            if (TextUtils.isEmpty(gestAge)) {
                gestAge = "";
            }

            String contactNo = "";
            if (!lastContactDetails.getContactNo().contains("-")) {
                contactNo = lastContactDetails.getContactNo();
            }

            gestAge = updateGABasedOnDueStrategy(gestAge, contactNo, lastContactDetails);

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

    private String updateGABasedOnDueStrategy(@Nullable String gestAge, @NonNull String contactNo, @NonNull LastContactDetailsWrapper lastContactDetails) {
        String tempGestAge = gestAge;
        if (ConstantsUtils.DueCheckStrategy.CHECK_FOR_FIRST_CONTACT.equals(Utils.getDueCheckStrategy())) {
            Facts facts = lastContactDetails.getFacts();

            if (StringUtils.isBlank(tempGestAge)) {
                if ("1".equals(contactNo.trim()))
                    tempGestAge = "-";
                else if (lastContactDetailsList.size() > 1) {
                    try {
                        LastContactDetailsWrapper firstLastContactDetailsWrapper = lastContactDetailsList.get(0);
                        Facts firstFacts = firstLastContactDetailsWrapper.getFacts();
                        String edd = Utils.reverseHyphenSeperatedValues(firstFacts.get(ConstantsUtils.EDD), "-");
                        if (firstFacts.get(ConstantsUtils.GEST_AGE_OPENMRS) == null) {
                            String contactDate = facts.get(ConstantsUtils.CONTACT_DATE);
                            int diffWeeks = ConstantsUtils.DELIVERY_DATE_WEEKS - Math.abs(Weeks.weeksBetween(LocalDate.parse(edd), LocalDate.parse(contactDate)).getWeeks());
                            tempGestAge = String.valueOf(diffWeeks);
                        } else {
                            tempGestAge = firstFacts.get(ConstantsUtils.GEST_AGE_OPENMRS);
                        }
                    } catch (IllegalArgumentException e) {
                        tempGestAge = "-";
                        Timber.e(e);
                    }
                }
            }
        }
        return tempGestAge;
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
        public TextView referral;
        private TextView contactTextView;
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
