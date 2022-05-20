package org.smartregister.anc.library.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.utils.Utils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.fragment.ProfileTasksFragment;
import org.smartregister.anc.library.model.Task;
import org.smartregister.anc.library.util.ANCFormUtils;
import org.smartregister.anc.library.viewholder.ContactTasksViewHolder;

import java.util.List;

import timber.log.Timber;

public class ContactTasksDisplayAdapter extends RecyclerView.Adapter<ContactTasksViewHolder> {
    private List<Task> taskList;
    private LayoutInflater inflater;
    private Context context;
    private ANCFormUtils ANCFormUtils = new ANCFormUtils();
    private Utils utils = new Utils();
    private ProfileTasksFragment profileTasksFragment;

    public ContactTasksDisplayAdapter(List<Task> taskList, Context context, ProfileTasksFragment profileTasksFragment) {
        this.taskList = taskList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.profileTasksFragment = profileTasksFragment;
    }

    @NonNull
    @Override
    public ContactTasksViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = inflater.inflate(R.layout.tasks_expansion_panel, viewGroup, false);
        return new ContactTasksViewHolder(view, profileTasksFragment);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactTasksViewHolder contactTasksViewHolder, int position) {
        try {
            if (taskList != null && taskList.size() > 0) {
                Task task = taskList.get(position);
                JSONObject taskValue = new JSONObject(task.getValue());
                String taskKey = taskValue.optString(JsonFormConstants.KEY);
                String testName= ANCFormUtils.getTranslatedFormTitle(taskKey, context);
                if (StringUtils.isNotBlank(testName)) {
                    contactTasksViewHolder.topBarTextView.setText(testName);
                }
                updateStatusIcon(taskValue, contactTasksViewHolder);
                showInfoIcon(taskValue, contactTasksViewHolder);
                if (task.isUpdated()) {
                    attachContent(taskValue, contactTasksViewHolder);
                }
                attachFormOpenViewTags(taskValue, task, contactTasksViewHolder);
            }
        } catch (JSONException e) {
            Timber.e(e, " --> onBindViewHolder");
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    /**
     * Update the status image view color and design according to the test status
     *
     * @param taskValue              {@link JSONObject}
     * @param contactTasksViewHolder {@link ContactTasksViewHolder}
     */
    private void updateStatusIcon(JSONObject taskValue, ContactTasksViewHolder contactTasksViewHolder) {
        try {
            if (taskValue.has(JsonFormConstants.VALUE)) {
                JSONArray values = taskValue.getJSONArray(JsonFormConstants.VALUE);
                for (int i = 0; i < values.length(); i++) {
                    JSONObject valueObject = values.getJSONObject(i);
                    if (valueObject != null && valueObject.has(JsonFormConstants.VALUES) && valueObject.has(JsonFormConstants.INDEX) && valueObject.getInt(JsonFormConstants.INDEX) == 0) {
                        JSONArray jsonArray = valueObject.getJSONArray(JsonFormConstants.VALUES);
                        String status = jsonArray.getString(0);
                        if (status.contains(JsonFormConstants.AncRadioButtonOptionTypesUtils.NOT_DONE)) {
                            contactTasksViewHolder.statusImageView.setImageResource(R.drawable.not_done);
                        } else if (status.contains(JsonFormConstants.AncRadioButtonOptionTypesUtils.ORDERED)) {
                            contactTasksViewHolder.statusImageView.setImageResource(R.drawable.ordered);
                        } else if (status.contains(JsonFormConstants.AncRadioButtonOptionTypesUtils.DONE_TODAY) || status.contains(JsonFormConstants.AncRadioButtonOptionTypesUtils.DONE_EARLIER)) {
                            contactTasksViewHolder.statusImageView.setImageResource(R.drawable.done_today);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Timber.e(e, " --> updateStatusIcon");
        }
    }

    /**
     * Checks for any extra info widget,  add the necessary tags before the info icon is displayed to enable the icon clicking
     *
     * @param taskValue              {@link JSONObject}
     * @param contactTasksViewHolder {@link ContactTasksViewHolder}
     */
    private void showInfoIcon(JSONObject taskValue, ContactTasksViewHolder contactTasksViewHolder) {
        try {
            if (taskValue.has(JsonFormConstants.ACCORDION_INFO_TEXT)) {
                String infoText = taskValue.getString(JsonFormConstants.ACCORDION_INFO_TEXT);
                String infoTextTitle = taskValue.getString(JsonFormConstants.ACCORDION_INFO_TITLE);
                if (StringUtils.isNotBlank(infoText)) {
                    contactTasksViewHolder.accordionInfoIcon.setVisibility(View.VISIBLE);
                    contactTasksViewHolder.accordionInfoIcon.setTag(R.id.accordion_context, context);
                    contactTasksViewHolder.accordionInfoIcon.setTag(R.id.accordion_info_title, infoTextTitle);
                    contactTasksViewHolder.accordionInfoIcon.setTag(R.id.accordion_info_text, infoText);
                }
            }
        } catch (JSONException e) {
            Timber.e(e, " --> showInfoIcon");
        }
    }

    /**
     * Displays the earlier selctions made on the tests if any
     *
     * @param taskValue  {@link JSONObject}
     * @param viewHolder {@link ContactTasksViewHolder}
     * @throws JSONException - Throws this in case the operation of the json object fails.
     */
    private void attachContent(JSONObject taskValue, ContactTasksViewHolder viewHolder) throws JSONException {
        JSONArray values = new JSONArray();
        if (taskValue.has(JsonFormConstants.VALUE)) {
            values = taskValue.optJSONArray(JsonFormConstants.VALUE);
            if (values != null && ANCFormUtils.checkValuesContent(values)) {
                viewHolder.contentLayout.setVisibility(View.VISIBLE);
                viewHolder.contentView.setVisibility(View.VISIBLE);
            }
        }
        if (values != null) {
            ANCFormUtils.addValuesDisplay(utils.createExpansionPanelChildren(values), viewHolder.contentView, context);
        }
    }

    private void attachFormOpenViewTags(JSONObject taskValue, Task task, ContactTasksViewHolder contactTasksViewHolder) {
        attachViewTags(taskValue, task, contactTasksViewHolder.expansionHeaderLayout);
        attachViewTags(taskValue, task, contactTasksViewHolder.statusImageView);
        attachViewTags(taskValue, task, contactTasksViewHolder.expansionPanelHeader);
        attachViewTags(taskValue, task, contactTasksViewHolder.topBarTextView);
        attachViewTags(taskValue, task, contactTasksViewHolder.okButton);
    }

    /**
     * Adding the necessary tags on the given views
     *
     * @param taskValue {@link JSONObject}
     * @param task      {@link Task}
     * @param view      {@link View}
     */
    private void attachViewTags(JSONObject taskValue, Task task, View view) {
        if (view.getVisibility() == View.VISIBLE) {
            view.setTag(R.id.accordion_jsonObject, taskValue);
            view.setTag(R.id.accordion_context, context);
            view.setTag(R.id.task_object, task);
        }
    }
}
