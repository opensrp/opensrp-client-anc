package org.smartregister.anc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jeasy.rules.api.Facts;
import org.smartregister.anc.R;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.domain.ContactSummary;
import org.smartregister.anc.domain.ContactSummaryItem;

import java.util.List;

/**
 * Created by ndegwamartin on 04/12/2018.
 */
public class ContactSummaryFinishAdapter extends RecyclerView.Adapter<ContactSummaryFinishAdapter.ViewHolder> {

    private List<ContactSummary> mData;
    private LayoutInflater mInflater;
    private Facts facts;

    // data is passed into the constructor
    public ContactSummaryFinishAdapter(Context context, List<ContactSummary> data, Facts facts) {
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

        List<ContactSummaryItem> fields = mData.get(position).getFields();
        String output = "";
        for (ContactSummaryItem contactSummaryItem : fields) {
            if (AncApplication.getInstance().getRulesEngineHelper().getRelevance(facts, contactSummaryItem.getRelevance()) || true) {
                output += fillTemplate(contactSummaryItem.getTemplate(), this.facts) + "\n\n";
            }
        }

        holder.sectionDetails.setText(output);
        holder.parent.setVisibility(output.trim().isEmpty() ? View.GONE : View.VISIBLE);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView sectionHeader;
        TextView sectionDetails;
        View parent;

        ViewHolder(View itemView) {
            super(itemView);
            sectionHeader = itemView.findViewById(R.id.contact_summary_section_header);
            sectionDetails = itemView.findViewById(R.id.contact_summary_section_details);
            parent = itemView.findViewById(R.id.contact_summary_row);
        }
    }


    public String fillTemplate(String str, Facts facts) {
        while (str.contains("{")) {
            String key = str.substring(str.indexOf("{") + 1, str.indexOf("}"));
            String value = facts.get(key);
            str = str.replace("{" + key + "}", value != null ? value : "");
        }

        return str;
    }

    private String processUnderscores(String string) {
        return string.replace("_", " ").toUpperCase();
    }

}
