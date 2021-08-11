package org.smartregister.anc.library.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.anc.library.R;
import org.smartregister.anc.library.model.ContactSummaryModel;

import java.util.ArrayList;
import java.util.List;

public class ContactSummaryAdapter extends RecyclerView.Adapter<ContactSummaryAdapter.ViewHolder> {
    private List<ContactSummaryModel> contactDates;

    public ContactSummaryAdapter() {
        contactDates = new ArrayList<>();
    }

    public void setContactDates(List<ContactSummaryModel> contactDates) {
        this.contactDates = contactDates;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.partial_contact_dates, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContactSummaryModel model = contactDates.get(position);
        holder.contactDate.setText(model.getContactDate());
        holder.contactName.setText(model.getContactName());

    }

    @Override
    public int getItemCount() {
        return contactDates.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView contactName;
        private TextView contactDate;

        ViewHolder(View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contact_summary_contact_name);
            contactDate = itemView.findViewById(R.id.contact_summary_contact_date);
        }

    }
}
