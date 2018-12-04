package org.smartregister.anc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.smartregister.anc.R;
import org.smartregister.anc.domain.ContactSummary;

import java.util.List;
import java.util.Map;

/**
 * Created by ndegwamartin on 04/12/2018.
 */
public class ContactSummaryFinishAdapter extends RecyclerView.Adapter<ContactSummaryFinishAdapter.ViewHolder> {

    private List<ContactSummary> mData;
    private LayoutInflater mInflater;
    private Map<String, String> mValueMap;

    // data is passed into the constructor
    public ContactSummaryFinishAdapter(Context context, List<ContactSummary> data, Map<String, String> valueMap) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mValueMap = valueMap;
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
        holder.sectionHeader.setText(mData.get(position).getGroup());

        List<String> templates = mData.get(position).getFields();
        String crazyOutput = "";
        for (String template : templates) {
            crazyOutput += fillTemplate(template, mValueMap);
            crazyOutput += "\n\n";

        }
        holder.sectionDetails.setText(crazyOutput);
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

        ViewHolder(View itemView) {
            super(itemView);
            sectionHeader = itemView.findViewById(R.id.contact_summary_section_header);
            sectionDetails = itemView.findViewById(R.id.contact_summary_section_details);
        }
    }


    public String fillTemplate(String str, Map<String, String> map) {
        while (str.contains("{")) {
            String key = str.substring(str.indexOf("{") + 1, str.indexOf("}"));
            String value = map.get(key);
            str = str.replace("{" + key + "}", value != null ? value : "");
        }

        return str;
    }

}
