package org.smartregister.anc.library.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.model.Task;
import org.smartregister.anc.library.viewholder.ContactTasksViewHolder;

import java.util.List;

import timber.log.Timber;

public class ContactTasksDisplayAdapter extends RecyclerView.Adapter<ContactTasksViewHolder> {
    private List<Task> taskList;
    private LayoutInflater inflater;
    private Context context;

    public ContactTasksDisplayAdapter(List<Task> taskList, Context context) {
        this.taskList = taskList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public ContactTasksViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = inflater.inflate(R.layout.tasks_expansion_panel, viewGroup, false);
        return new ContactTasksViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ContactTasksViewHolder contactTasksViewHolder, int position) {
        try {
            if (taskList != null && taskList.size() > 0) {
                Task task = taskList.get(position);
                JSONObject taskValue = new JSONObject(task.getValue());
                String taskText = taskValue.optString(JsonFormConstants.TEXT, "");
                if (StringUtils.isNotBlank(taskText)) {
                    contactTasksViewHolder.topBarTextView.setText(taskText);
                }
            }
        } catch (JSONException e) {
            Timber.e(e, " --> onBindViewHolder");
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
