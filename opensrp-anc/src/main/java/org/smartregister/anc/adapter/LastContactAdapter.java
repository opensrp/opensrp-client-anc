package org.smartregister.anc.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jeasy.rules.api.Facts;
import org.smartregister.anc.R;
import org.smartregister.anc.domain.LastContactDetailsWrapper;
import org.smartregister.anc.util.Constants;

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

            viewHolder.contactTextView
                    .setText(String.format(context.getResources().getString(R.string.contact_details), gestAge,
                            lastContactDetails.getContactNo()));
            viewHolder.contactDate.setText(lastContactDetails.getContactDate());

            LastContactDetailsAdapter adapter = new LastContactDetailsAdapter(context,
                    lastContactDetails.getExtraInformation(), facts);

            // set up the RecyclerView
            RecyclerView recyclerView = viewHolder.lastContactDetailsRecycler;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public int getItemCount() {
        return lastContactDetailsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView contactTextView;
        public TextView referral;
        public TextView contactDate;
        public RecyclerView lastContactDetailsRecycler;

        ViewHolder(View itemView) {
            super(itemView);
            contactTextView = itemView.findViewById(R.id.contact);
            referral = itemView.findViewById(R.id.referral);
            contactDate = itemView.findViewById(R.id.contact_date);
            lastContactDetailsRecycler = itemView.findViewById(R.id.last_contact_details_recycler);
        }
    }
}
