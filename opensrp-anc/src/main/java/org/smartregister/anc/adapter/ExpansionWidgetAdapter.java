package org.smartregister.anc.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vijay.jsonwizard.views.CustomTextView;

import org.smartregister.anc.R;

import java.util.List;

public class ExpansionWidgetAdapter extends RecyclerView.Adapter<ExpansionWidgetAdapter.ViewHolder> {
    private List<String> expansionWidgetValues;

    public ExpansionWidgetAdapter(List<String> expansionWidgetValues) {
        this.expansionWidgetValues = expansionWidgetValues;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.native_expansion_panel_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpansionWidgetAdapter.ViewHolder holder, int position) {
        String[] valueObject = expansionWidgetValues.get(position).split(":");
        if (valueObject.length >= 2) {
            holder.listHeader.setText(valueObject[0]);
            holder.listValue.setText(valueObject[1]);
        }
    }

    @Override
    public int getItemCount() {
        return expansionWidgetValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CustomTextView listHeader;
        private CustomTextView listValue;

        ViewHolder(View itemView) {
            super(itemView);
            listHeader = itemView.findViewById(R.id.item_header);
            listValue = itemView.findViewById(R.id.item_value);
        }
    }

    public void setExpansionWidgetValues(List<String> expansionWidgetValues) {
        this.expansionWidgetValues = expansionWidgetValues;
    }
}
