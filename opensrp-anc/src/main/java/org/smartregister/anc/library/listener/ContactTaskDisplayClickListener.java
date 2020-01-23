package org.smartregister.anc.library.listener;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.fragment.ProfileTasksFragment;
import org.smartregister.anc.library.model.Task;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.ContactJsonFormUtils;
import org.smartregister.util.FormUtils;

import java.util.Calendar;

import timber.log.Timber;

public class ContactTaskDisplayClickListener implements View.OnClickListener {
    private ProfileTasksFragment profileTasksFragment;
    private FormUtils formUtils;

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
            } else {
                prepFormForLaunch(view);
            }
        }
    }

    /**
     * This performs all the necessary calculations to get the form ready for launch. This updates the title,
     * Adds the new form fields
     *
     * @param view {@link View}
     */
    private void prepFormForLaunch(View view) {
        Context context = ((Context) view.getTag(R.id.accordion_context));
        Task task = ((Task) view.getTag(R.id.task_object));
        JSONObject taskValue = ((JSONObject) view.getTag(R.id.accordion_jsonObject));

        if (context != null && task != null && taskValue != null) {
            JSONArray subFormFields = loadSubFormFields(taskValue, context);
            String formTitle = getFormTitle(taskValue);
            JSONObject form = loadTasksForm(context);
            updateFormTitle(form, formTitle);
            updateFormFields(form, subFormFields);

            profileTasksFragment.startTaskForm(form);
        }
    }

    /**
     * Updates the form step1 title to match the test header
     *
     * @param form  {@link JSONObject}
     * @param title {@link String}
     */
    private void updateFormTitle(JSONObject form, String title) {
        try {
            if (form != null && StringUtils.isNotBlank(title)) {
                if (form.has(JsonFormConstants.STEP1)) {
                    JSONObject stepOne = form.getJSONObject(JsonFormConstants.STEP1);
                    stepOne.put(JsonFormConstants.STEP_TITLE, title);
                }
            }
        } catch (JSONException e) {
            Timber.e(e, " --> updateFormTitle");
        }
    }

    /**
     * Add the sub form fields to the main form for loading.
     *
     * @param form   {@link JSONObject}
     * @param fields {@link JSONArray}
     */
    private void updateFormFields(JSONObject form, JSONArray fields) {
        try {
            if (form != null && fields != null && fields.length() > 0) {
                if (form.has(JsonFormConstants.STEP1)) {
                    JSONObject stepOne = form.getJSONObject(JsonFormConstants.STEP1);
                    stepOne.put(JsonFormConstants.FIELDS, fields);
                }
            }
        } catch (JSONException e) {
            Timber.e(e, " --> updateFormFields");
        }
    }

    /**
     * Loads the sub forms using the name on the accordion.  It returns the sub form fields
     *
     * @param taskValue {@link JSONObject}
     * @param context   {@link Context}
     * @return fields  {@link JSONArray}
     */
    private JSONArray loadSubFormFields(JSONObject taskValue, Context context) {
        JSONArray fields = new JSONArray();
        try {
            if (taskValue != null && taskValue.has(JsonFormConstants.CONTENT_FORM)) {
                String subFormName = taskValue.getString(JsonFormConstants.CONTENT_FORM);
                JSONObject subForm = ContactJsonFormUtils.getSubFormJson(subFormName, "", context);
                if (subForm.has(JsonFormConstants.CONTENT_FORM)) {
                    fields = subForm.getJSONArray(JsonFormConstants.CONTENT_FORM);
                }
            }
        } catch (JSONException e) {
            Timber.e(e, " --> loadSubFormFields");
        } catch (Exception e) {
            Timber.e(e, " --> loadSubFormFields");
        }
        return fields;
    }

    /**
     * Get the form title for the accordion text
     *
     * @param taskValue {@link JSONObject}
     * @return title {@link String}
     */
    private String getFormTitle(JSONObject taskValue) {
        String title = "";
        if (taskValue != null && taskValue.has(JsonFormConstants.TEXT)) {
            title = taskValue.optString(JsonFormConstants.TEXT, "");
        }
        return title;
    }

    /**
     * Loads the main contact tasks form. Returns a JSONObject of the form.
     *
     * @param context {@link Context}
     * @return jsonForm {@link JSONObject}
     */
    private JSONObject loadTasksForm(Context context) {
        JSONObject form = new JSONObject();
        try {
            formUtils = new FormUtils(context);
            form = formUtils.getFormJson(ConstantsUtils.JsonFormUtils.ANC_TEST_TASKS);
        } catch (Exception e) {
            Timber.e(e, " --> loadTasksForm");
        }
        return form;
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
        if (taskValue != null && taskValue.has(JsonFormConstants.VALUE)) {
            taskValue.remove(JsonFormConstants.VALUE);
            task = taskValue;
        }
        return task;
    }
}
