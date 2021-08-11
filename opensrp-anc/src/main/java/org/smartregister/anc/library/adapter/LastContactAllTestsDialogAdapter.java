package org.smartregister.anc.library.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.anc.library.R;
import org.smartregister.anc.library.domain.TestResults;
import org.smartregister.anc.library.domain.TestResultsDialog;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.List;

public class LastContactAllTestsDialogAdapter extends RecyclerView.Adapter<LastContactAllTestsDialogAdapter.ViewHolder> {
    private List<TestResultsDialog> mData;
    private LayoutInflater mInflater;
    private Context context;

    // data is passed into the constructor
    public LastContactAllTestsDialogAdapter(Context context, List<TestResultsDialog> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.all_tests_results_dialog_title_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TestResultsDialog testResultsDialog = mData.get(position);

        if (!TextUtils.isEmpty(testResultsDialog.getTestTitle()) && testResultsDialog.getTestResultsList().size() > 0) {
            holder.allTestsTitle.setText(testResultsDialog.getTestTitle());

            List<TestResults> results = testResultsDialog.getTestResultsList();
            results.add(0, addHeadingText());
            setUpRecyclerView(holder.allTestsContent, results);
        }
    }

    private TestResults addHeadingText() {
        return new TestResults("GA", "Date", "Value");
    }

    private void setUpRecyclerView(RecyclerView allTestsContent, List<TestResults> testResults) {
        LastContactAllTestsResultsDialogAdapter adapter = new LastContactAllTestsResultsDialogAdapter(context, testResults);
        adapter.notifyDataSetChanged();
        allTestsContent.setLayoutManager(new LinearLayoutManager(context));
        allTestsContent.setAdapter(adapter);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        public View parent;
        private CustomFontTextView allTestsTitle;
        private RecyclerView allTestsContent;

        ViewHolder(View itemView) {
            super(itemView);
            allTestsTitle = itemView.findViewById(R.id.all_tests_content_title);
            allTestsContent = itemView.findViewById(R.id.all_tests_content);
            parent = itemView;
        }
    }
}
