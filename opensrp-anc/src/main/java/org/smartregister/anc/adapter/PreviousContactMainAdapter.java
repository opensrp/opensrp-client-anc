package org.smartregister.anc.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.smartregister.anc.R;
import org.smartregister.anc.domain.Contact;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PreviousContactMainAdapter extends RecyclerView.Adapter<PreviousContactMainAdapter.ViewHolder> {

    private Context context;
    private List<Contact> contacts;

    private View.OnClickListener clickListener;

    public PreviousContactMainAdapter(Context context, List<Contact> contacts, View.OnClickListener clickListener) {
        this.context = context;
        this.contacts = contacts;
        this.clickListener = clickListener;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.previous_contact_card_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Contact contact = contacts.get(position);

        holder.linearLayout.setBackgroundResource(contact.getBackground());
        holder.linearLayout.setOnClickListener(clickListener);
        holder.linearLayout.setTag(contact);

        holder.name.setText(contact.getName());
        holder.contactImage.setImageResource(contact.getBackground());
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        View linearLayout;
        CircleImageView contactImage;


        public ViewHolder(View view) {
            super(view);
            linearLayout = view.findViewById(R.id.card_layout);
            name = view.findViewById(R.id.container_name);
            contactImage = view.findViewById(R.id.contact_image);
        }
    }
}
