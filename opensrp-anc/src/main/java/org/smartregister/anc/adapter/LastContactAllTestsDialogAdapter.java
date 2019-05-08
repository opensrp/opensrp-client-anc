package org.smartregister.anc.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.smartregister.anc.R;
import org.smartregister.anc.domain.TestResultsDialog;

import java.util.List;

public class LastContactAllTestsDialogAdapter extends RecyclerView.Adapter<LastContactAllTestsDialogAdapter.ViewHolder> {
    private List<TestResultsDialog> mData;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    public LastContactAllTestsDialogAdapter(Context context, List<TestResultsDialog> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.profile_overview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        public View parent;

        ViewHolder(View itemView) {
            super(itemView);
            parent = itemView;
        }
    }
}
