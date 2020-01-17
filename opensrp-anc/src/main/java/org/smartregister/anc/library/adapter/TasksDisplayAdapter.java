package org.smartregister.anc.library.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.smartregister.anc.library.R;
import org.smartregister.anc.library.listener.TaskDisplayClickListener;

public class TasksDisplayAdapter extends RecyclerView.Adapter<TasksDisplayAdapter.ViewHolder> {
    private TaskDisplayClickListener taskDisplayClickListener = new TaskDisplayClickListener();

    @NonNull
    @Override
    public TasksDisplayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull TasksDisplayAdapter.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View parent;
        private TextView sectionHeader;
        private TextView subSectionHeader;
        private TextView sectionDetails;
        private TextView sectionDetailTitle;
        private TextView allTestResultsButton;

        ViewHolder(View itemView) {
            super(itemView);
            sectionHeader = itemView.findViewById(R.id.overview_section_header);
            subSectionHeader = itemView.findViewById(R.id.overview_subsection_header);
            sectionDetailTitle = itemView.findViewById(R.id.overview_section_details_left);
            sectionDetails = itemView.findViewById(R.id.overview_section_details_right);

            allTestResultsButton = itemView.findViewById(R.id.all_test_results_button);
            allTestResultsButton.setOnClickListener(taskDisplayClickListener);
            parent = itemView;
        }
    }
}
