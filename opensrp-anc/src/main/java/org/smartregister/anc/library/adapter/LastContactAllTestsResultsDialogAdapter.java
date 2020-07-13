package org.smartregister.anc.library.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.domain.TestResults;

import java.util.List;

public class LastContactAllTestsResultsDialogAdapter
        extends RecyclerView.Adapter<LastContactAllTestsResultsDialogAdapter.ViewHolder> {
    private List<TestResults> mData;
    private LayoutInflater mInflater;
    private Context context;

    // data is passed into the constructor
    public LastContactAllTestsResultsDialogAdapter(Context context, List<TestResults> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.all_tests_results_dialog_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TestResults testResults = mData.get(position);

        holder.gaText.setText(testResults.getGestAge());
        holder.gaText.setTextColor(context.getResources().getColor(R.color.overview_font_right));

        holder.dateText.setText(testResults.getTestDate());
        holder.dateText.setTextColor(context.getResources().getColor(R.color.overview_font_right));

        holder.resultText.setText(testResults.getTestValue());
        holder.resultText.setTextColor(context.getResources().getColor(R.color.overview_font_right));

        if (position == 0) {
            holder.gaText.setTypeface(holder.gaText.getTypeface(), Typeface.BOLD);
            holder.gaText.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
            holder.dateText.setTypeface(holder.dateText.getTypeface(), Typeface.BOLD);
            holder.dateText.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
            holder.resultText.setTypeface(holder.resultText.getTypeface(), Typeface.BOLD);
            holder.resultText.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        public View parent;
        private TextView gaText;
        private TextView dateText;
        private TextView resultText;

        ViewHolder(View itemView) {
            super(itemView);
            gaText = itemView.findViewById(R.id.all_tests_ga);
            dateText = itemView.findViewById(R.id.all_tests_dates);
            resultText = itemView.findViewById(R.id.all_tests_result);
            parent = itemView;
        }
    }
}
