package org.smartregister.anc.library.listener;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.fragment.ProfileTasksFragment;
import org.smartregister.anc.library.model.Task;

import java.util.Calendar;

import timber.log.Timber;

public class ContactTaskDisplayClickListener implements View.OnClickListener {
    private ProfileTasksFragment profileTasksFragment;

    public ContactTaskDisplayClickListener(ProfileTasksFragment profileTasksFragment) {
        this.profileTasksFragment = profileTasksFragment;
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            if (view.getId() == R.id.accordion_info_icon) {
                infoAlertDialog(view);
            } else if (view.getId() == R.id.undo_button) {
                undoTasksEntries(view);
            }
        }
    }

    /**
     * Displays the extra info on the expansion panel widget.
     *
     * @param view {@link View}
     */
    private void infoAlertDialog(View view) {
        Context context = ((Context) view.getTag(R.id.accordion_context));
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.AppThemeAlertDialog);
        builderSingle.setTitle((String) view.getTag(R.id.accordion_info_title));
        builderSingle.setMessage((String) view.getTag(R.id.accordion_info_text));
        builderSingle.setIcon(com.vijay.jsonwizard.R.drawable.dialog_info_filled);
        builderSingle.setNegativeButton(context.getResources().getString(R.string.ok),
                (dialog, which) -> dialog.dismiss());

        builderSingle.show();
    }

    /**
     * Intitiates the undo tasks functionality
     *
     * @param view {@link View}
     */
    private void undoTasksEntries(View view) {
        Context context = ((Context) view.getTag(R.id.accordion_context));
        Task task = ((Task) view.getTag(R.id.task_object));
        JSONObject taskValue = ((JSONObject) view.getTag(R.id.accordion_jsonObject));

        if (context != null && task != null && taskValue != null) {
            Task newTask = createTask(removeTestResults(taskValue), task);
            profileTasksFragment.undoTasks(newTask);
        }
    }

    /**
     * Creates the new updated tasks with the the new values
     *
     * @param taskValue {@link JSONObject}
     * @param task      {@link Task}
     * @return task {@link Task}
     */
    private Task createTask(JSONObject taskValue, Task task) {
        Task newTask = new Task();
        newTask.setId(task.getId());
        newTask.setBaseEntityId(task.getBaseEntityId());
        newTask.setKey(task.getKey());
        newTask.setKey(task.getKey());
        newTask.setValue(String.valueOf(taskValue));
        newTask.setContactNo(task.getContactNo());
        newTask.setCreatedAt(Calendar.getInstance().getTimeInMillis());
        return newTask;
    }

    /**
     * Removes the task values & sets it to empty.
     *
     * @param taskValue {@link JSONObject}
     * @return task {@link JSONObject}
     */
    private JSONObject removeTestResults(JSONObject taskValue) {
        JSONObject task = new JSONObject();
        try {
            if (taskValue != null && taskValue.has(JsonFormConstants.VALUE)) {
                task = taskValue.put(JsonFormConstants.VALUE, "");
            }
        } catch (JSONException e) {
            Timber.e(e, " --> removeTestResults");
        }
        return task;
    }
}
