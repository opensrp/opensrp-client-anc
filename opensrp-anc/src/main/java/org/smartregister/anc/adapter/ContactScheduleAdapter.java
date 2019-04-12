package org.smartregister.anc.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.smartregister.anc.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ContactScheduleAdapter extends RecyclerView.Adapter<ContactScheduleAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    private List<String> contactsSchedule;

    public ContactScheduleAdapter(Context context, List<String> contactsSchedule) {
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
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (contactsSchedule.size() > 0) {
            String[] scheduleString = contactsSchedule.get(i).split(":");
            if (scheduleString.length >= 2) {
                String ga = scheduleString[0];
                viewHolder.gaWeeksDisplay.setText(String.format(context.getResources().getString(R.string.ga_weeks), ga));

                String contactDate = scheduleString[1];
                Date nextContactDate = null;
                try {
                    nextContactDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(contactDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                viewHolder.nextContactDate.setText((CharSequence) nextContactDate);


                String timeAway = String.valueOf(generateTimeAway(String.valueOf(contactDate)));
                viewHolder.timeAwayDisplay.setText(String.format(context.getResources().getString(R.string.timeline_away),
                        timeAway));

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
        TextView gaWeeksDisplay;
        TextView nextContactDate;
        TextView timeAwayDisplay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gaWeeksDisplay = itemView.findViewById(R.id.ga_weeks);
            nextContactDate = itemView.findViewById(R.id.contact_date);
            timeAwayDisplay = itemView.findViewById(R.id.schedule_weeks_away);
        }
    }
}
