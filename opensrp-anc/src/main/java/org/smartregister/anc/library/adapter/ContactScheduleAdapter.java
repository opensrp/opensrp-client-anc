package org.smartregister.anc.library.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.model.ContactSummaryModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ContactScheduleAdapter extends RecyclerView.Adapter<ContactScheduleAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    private List<ContactSummaryModel> contactsSchedule;

    public ContactScheduleAdapter(Context context, List<ContactSummaryModel> contactsSchedule) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.contactsSchedule = contactsSchedule;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.contact_schedule_row, viewGroup, false);
        return new ContactScheduleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if (contactsSchedule.size() > 0) {
            if (position == 0) {
                viewHolder.overviewSummaryRow.setBackgroundColor(context.getResources().getColor(R.color.accent));
                viewHolder.gaWeeksDisplay.setTextColor(context.getResources().getColor(R.color.white));
                viewHolder.timeAwayDisplay.setTextColor(context.getResources().getColor(R.color.white));
                viewHolder.nextContactDate.setTextColor(context.getResources().getColor(R.color.white));
            }
            ContactSummaryModel contactSummaryModel = contactsSchedule.get(position);
            if (contactSummaryModel != null) {
                String ga = contactSummaryModel.getContactWeeks();
                Date locateDate = contactSummaryModel.getLocalDate();

                viewHolder.gaWeeksDisplay.setText(String.format(context.getResources().getString(R.string.ga_weeks), ga));
                viewHolder.nextContactDate
                        .setText(new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(locateDate));
                String formattedEdd = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(locateDate);
                String timeAway = String.valueOf(generateTimeAway(formattedEdd));
                viewHolder.timeAwayDisplay
                        .setText(String.format(context.getResources().getString(R.string.timeline_away), timeAway));
            }
        }
    }

    public int generateTimeAway(String contactDate) {
        DateTime contact = new DateTime(contactDate);
        int timeAway = 0;
        if (contactDate != null) {
            timeAway = Weeks.weeksBetween(new DateTime(), contact).getWeeks();
        }
        return timeAway;
    }

    @Override
    public int getItemCount() {
        return contactsSchedule.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView gaWeeksDisplay;
        private TextView nextContactDate;
        private TextView timeAwayDisplay;
        private ConstraintLayout overviewSummaryRow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gaWeeksDisplay = itemView.findViewById(R.id.ga_weeks);
            nextContactDate = itemView.findViewById(R.id.contact_date);
            timeAwayDisplay = itemView.findViewById(R.id.schedule_weeks_away);
            overviewSummaryRow = itemView.findViewById(R.id.overview_summary_row);
        }
    }
}
