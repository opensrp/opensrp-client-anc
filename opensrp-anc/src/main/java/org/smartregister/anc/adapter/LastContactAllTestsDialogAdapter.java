package org.smartregister.anc.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.smartregister.anc.R;
import org.smartregister.anc.domain.TestResults;
import org.smartregister.anc.domain.TestResultsDialog;
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
            createResultList(results, holder);
        }
    }

    private TestResults addHeadingText() {
        return new TestResults("GA", "Date", "Value");
    }

    private void createResultList(List<TestResults> testResultsList, ViewHolder holder) {
        for (TestResults testResults : testResultsList) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ConstraintLayout constraintLayout =
                    (ConstraintLayout) inflater.inflate(R.layout.all_tests_results_dialog_row, null);
            TextView gaText = constraintLayout.findViewById(R.id.all_tests_ga);
            TextView dateText = constraintLayout.findViewById(R.id.all_tests_dates);
            TextView resultText = constraintLayout.findViewById(R.id.all_tests_result);

            gaText.setText(testResults.getGestAge());
            gaText.setTextColor(context.getResources().getColor(R.color.overview_font_right));
            dateText.setText(testResults.getTestDate());
            dateText.setTextColor(context.getResources().getColor(R.color.overview_font_right));
            resultText.setText(testResults.getTestValue());
            resultText.setTextColor(context.getResources().getColor(R.color.overview_font_right));

            if (testResultsList.indexOf(testResults) == 0) {
                gaText.setTypeface(gaText.getTypeface(), Typeface.BOLD);
                dateText.setTypeface(gaText.getTypeface(), Typeface.BOLD);
                resultText.setTypeface(gaText.getTypeface(), Typeface.BOLD);
            }

            holder.allTestsContent.addView(constraintLayout);
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
        private CustomFontTextView allTestsTitle;
        private RelativeLayout allTestsContent;

        ViewHolder(View itemView) {
            super(itemView);
            allTestsTitle = itemView.findViewById(R.id.all_tests_content_title);
            allTestsContent = itemView.findViewById(R.id.all_tests_content);
            parent = itemView;
        }
    }
}
