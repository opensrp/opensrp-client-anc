package org.smartregister.anc.library.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jeasy.rules.api.Facts;
import org.json.JSONArray;
import org.json.JSONException;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.contract.PreviousContactsTests;
import org.smartregister.anc.library.domain.TestResults;
import org.smartregister.anc.library.domain.TestResultsDialog;
import org.smartregister.anc.library.domain.YamlConfigItem;
import org.smartregister.anc.library.domain.YamlConfigWrapper;
import org.smartregister.anc.library.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ndegwamartin on 04/12/2018.
 */
public class ProfileOverviewAdapter extends RecyclerView.Adapter<ProfileOverviewAdapter.ViewHolder> {

    private List<YamlConfigWrapper> mData;
    private LayoutInflater mInflater;
    private Facts facts;
    private Context context;
    private AllTestClickListener allTestClickListener = new AllTestClickListener();
    private PreviousContactsTests.Presenter presenter;
    private String baseEntityId;
    private String contactNo;

    // data is passed into the constructor
    public ProfileOverviewAdapter(Context context, List<YamlConfigWrapper> data, Facts facts) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.facts = facts;
        this.context = context;
    }

    public ProfileOverviewAdapter(Context context, List<YamlConfigWrapper> data, Facts facts,
                                  PreviousContactsTests.Presenter presenter, String baseEntityId, String contactNo) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.facts = facts;
        this.context = context;
        this.presenter = presenter;
        this.baseEntityId = baseEntityId;
        this.contactNo = contactNo;
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

        if (!TextUtils.isEmpty(mData.get(position).getGroup())) {

            holder.sectionHeader.setText(processUnderscores(mData.get(position).getGroup()));
            holder.sectionHeader.setVisibility(View.VISIBLE);

        } else {
            holder.sectionHeader.setVisibility(View.GONE);

        }

        if (!TextUtils.isEmpty(mData.get(position).getSubGroup())) {
            holder.subSectionHeader.setText(processUnderscores(mData.get(position).getSubGroup()));
            holder.subSectionHeader.setVisibility(View.VISIBLE);
        } else {
            holder.subSectionHeader.setVisibility(View.GONE);

        }

        if (mData.get(position).getYamlConfigItem() != null) {

            YamlConfigItem yamlConfigItem = mData.get(position).getYamlConfigItem();

            Template template = getTemplate(yamlConfigItem.getTemplate());
            String output = Utils.fillTemplate(template.detail, this.facts);

            holder.sectionDetailTitle.setText(template.title);
            holder.sectionDetails.setText(output);//Perhaps refactor to use Json Form Parser Implementation

            if (AncLibrary.getInstance().getAncRulesEngineHelper().getRelevance(facts, yamlConfigItem.getIsRedFont())) {
                holder.sectionDetailTitle.setTextColor(context.getResources().getColor(R.color.overview_font_red));
                holder.sectionDetails.setTextColor(context.getResources().getColor(R.color.overview_font_red));
            } else {
                holder.sectionDetailTitle.setTextColor(context.getResources().getColor(R.color.overview_font_left));
                holder.sectionDetails.setTextColor(context.getResources().getColor(R.color.overview_font_right));
            }

            holder.sectionDetailTitle.setVisibility(View.VISIBLE);
            holder.sectionDetails.setVisibility(View.VISIBLE);

        } else {
            holder.sectionDetailTitle.setVisibility(View.GONE);
            holder.sectionDetails.setVisibility(View.GONE);
        }

        /*if (!TextUtils.isEmpty(mData.get(position).getTestResults())) {
            holder.allTestResultsButton.setVisibility(View.VISIBLE);
            holder.allTestResultsButton.setTag(R.id.test_results, mData.get(position).getTestResults());
        } else {
            holder.allTestResultsButton.setVisibility(View.GONE);
            holder.allTestResultsButton.setTag(R.id.test_results, "");
        }*/
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    private String processUnderscores(String string) {
        return string.replace("_", " ").toUpperCase();
    }

    private Template getTemplate(String rawTemplate) {
        Template template = new Template();

        if (rawTemplate.contains(":")) {
            String[] templateArray = rawTemplate.split(":");
            if (templateArray.length > 1) {
                template.title = templateArray[0].trim();
                template.detail = templateArray[1].trim();
            }
        } else {
            template.title = rawTemplate;
        }

        return template;

    }

    private List<TestResultsDialog> getTestData(JSONArray jsonArrayKeys) throws JSONException {
        List<TestResultsDialog> allResultKeys = new ArrayList<>();
        for (int i = 0; i < jsonArrayKeys.length(); i++) {
            String[] keys = getTestKeyAndTitle(jsonArrayKeys.getString(i));
            if (keys.length > 2) {
                List<TestResults> testResults = presenter.loadAllTestResults(baseEntityId, keys[0], keys[1], contactNo);
                if (testResults.size() > 0) {
                    TestResultsDialog testResultsDialog = new TestResultsDialog(keys[1], testResults);
                    allResultKeys.add(testResultsDialog);
                }
            }
        }
        return allResultKeys;
    }

    private String[] getTestKeyAndTitle(String textKey) {
        return textKey.split(":");
    }

    // stores and recycles views as they are scrolled off screen
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
            allTestResultsButton.setOnClickListener(allTestClickListener);
            parent = itemView;
        }
    }

    private class Template {
        public String title = "";
        public String detail = "";
    }

    private class AllTestClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            try {
                if (view.getId() == R.id.all_test_results_button) {
                    String textKey = (String) view.getTag(R.id.test_results);
                    if (!TextUtils.isEmpty(textKey)) {
                        JSONArray jsonArrayKeys = new JSONArray(textKey);
                        List<TestResultsDialog> testResultsDialogs = getTestData(jsonArrayKeys);
                        if (testResultsDialogs.size() > 0) {
                            displayUndoDialog(testResultsDialogs);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void displayUndoDialog(List<TestResultsDialog> testResultsDialogList) {
            Activity activity = (Activity) context;
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogLayout = inflater.inflate(R.layout.all_tests_results_dialog, null);

            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setView(dialogLayout);

            final ImageView cancel = dialogLayout.findViewById(R.id.all_test_popup_cancel);

            setUpRecyclerView(testResultsDialogList, dialogLayout);

            final AlertDialog dialog = builder.create();

            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams param = window.getAttributes();
                param.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
                window.setAttributes(param);
                window.setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, 8);
            }

            cancel.setOnClickListener(v -> dialog.dismiss());

            dialog.show();
        }

        private void setUpRecyclerView(List<TestResultsDialog> testResultsDialogList, View dialogLayout) {
            RelativeLayout relativeLayout = dialogLayout.findViewById(R.id.all_test_content_display_layout);
            LastContactAllTestsDialogAdapter adapter = new LastContactAllTestsDialogAdapter(context, testResultsDialogList);
            adapter.notifyDataSetChanged();
            RecyclerView recyclerView = relativeLayout.findViewById(R.id.all_test_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(adapter);
        }
    }

}
