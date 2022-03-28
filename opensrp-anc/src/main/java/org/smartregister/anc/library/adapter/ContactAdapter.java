package org.smartregister.anc.library.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.domain.Contact;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private Context context;
    private List<Contact> contacts;

    private View.OnClickListener clickListener;

    public ContactAdapter(Context context, List<Contact> contacts, View.OnClickListener clickListener) {
        this.context = context;
        this.contacts = contacts;
        this.clickListener = clickListener;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_card_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Contact contact = contacts.get(position);

        holder.cardLayout.setBackgroundResource(contact.getBackground());
        holder.cardLayout.setOnClickListener(clickListener);
        holder.cardLayout.setTag(contact);

        holder.name.setText(contact.getName());

        if (contact.getRequiredFields() == null) {
            holder.requiredFields.setVisibility(View.GONE);
            holder.completeLayout.setVisibility(View.GONE);
        } else if (contact.getRequiredFields() == 0) {
            holder.completeLayout.setVisibility(View.VISIBLE);
            holder.requiredFields.setVisibility(View.GONE);
        } else {
            holder.requiredFields.setText(String.format(context.getString(R.string.required_fields), contact.getRequiredFields()));
            holder.requiredFields.setVisibility(View.VISIBLE);
            holder.completeLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View cardLayout;
        public TextView name;
        public TextView requiredFields;
        public View completeLayout;


        public ViewHolder(View view) {
            super(view);
            cardLayout = view.findViewById(R.id.card_layout);
            name = view.findViewById(R.id.container_name);
            requiredFields = view.findViewById(R.id.required_fields);
            completeLayout = view.findViewById(R.id.complete_layout);
        }
    }
}
