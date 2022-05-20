package org.smartregister.anc.library.listener;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.ExpansionPanelValuesModel;
import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.fragment.ProfileTasksFragment;
import org.smartregister.anc.library.model.Task;
import org.smartregister.anc.library.util.ANCFormUtils;
import org.smartregister.anc.library.util.ANCJsonFormUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class ContactTaskDisplayClickListener implements View.OnClickListener {
    private final ProfileTasksFragment profileTasksFragment;
    private final ANCFormUtils ANCFormUtils = new ANCFormUtils();

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
            profileTasksFragment.updateTask(newTask);
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
            JSONArray taskValues = getExpansionPanelValues(taskValue, task.getKey());
            Map<String, ExpansionPanelValuesModel> secondaryValuesMap = getSecondaryValues(taskValues);
            JSONArray subFormFields = ANCFormUtils.addExpansionPanelFormValues(loadSubFormFields(taskValue, context).entrySet().iterator().next().getValue(), secondaryValuesMap);
            String taskKey = taskValue.optString(JsonFormConstants.KEY);
            String formTitle = ANCFormUtils.getTranslatedFormTitle(taskKey, context);
            JSONObject form = ANCFormUtils.loadTasksForm(context);
            if (StringUtils.isNotBlank(formTitle)) {
                updateFormTitle(form, formTitle);
            }
            ANCFormUtils.updateFormFields(form, subFormFields);
            // Update form properties file name according to the test fields populated
            ANCFormUtils.updateFormPropertiesFileName(form, taskValue, context);

            profileTasksFragment.startTaskForm(form, task);
        }
    }

    private void addMlsPropertyFile(JSONObject form, String mlsPropertyFile) {
        try {
            if (form != null && StringUtils.isNotBlank(mlsPropertyFile)) {
                form.put(JsonFormConstants.MLS.PROPERTIES_FILE_NAME, mlsPropertyFile);
            }
        } catch (JSONException e) {
            Timber.e(e, " --> add mls property file");
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
        newTask.setValue(String.valueOf(taskValue));
        newTask.setUpdated(true);
        newTask.setComplete(ANCJsonFormUtils.checkIfTaskIsComplete(taskValue));
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

    /**
     * Returns the expansion panel values which were selected from the forms.
     *
     * @param taskValue {@link JSONObject}
     * @param taskKey   {@link String}
     * @return values {@link JSONArray}
     */
    private JSONArray getExpansionPanelValues(JSONObject taskValue, String taskKey) {
        JSONArray values = new JSONArray();
        if (taskValue != null && StringUtils.isNotBlank(taskKey)) {
            JSONArray taskValueArray = new JSONArray();
            taskValueArray.put(taskValue);
            values = ANCFormUtils.loadExpansionPanelValues(taskValueArray, taskKey);
        }

        return values;
    }

    /**
     * Returns a map of the expansion panel values
     *
     * @param secondaryValues {@link JSONArray}
     * @return expansionPanelValuesMap = {@link Map}
     */
    private Map<String, ExpansionPanelValuesModel> getSecondaryValues(JSONArray secondaryValues) {
        Map<String, ExpansionPanelValuesModel> stringExpansionPanelValuesModelMap = new HashMap<>();
        if (secondaryValues != null && secondaryValues.length() > 0) {
            stringExpansionPanelValuesModelMap = ANCFormUtils.createSecondaryValuesMap(secondaryValues);
        }
        return stringExpansionPanelValuesModelMap;
    }

    /**
     * Loads the sub forms using the name on the accordion.  It returns the sub form fields
     *
     * @param taskValue {@link JSONObject}
     * @param context   {@link Context}
     * @return fields  {@link JSONArray}
     */
    private Map<String, JSONArray> loadSubFormFields(JSONObject taskValue, Context context) {
        JSONArray fields = new JSONArray();
        String mlsPropertyFile = "";
        Map<String, JSONArray> stringJSONArrayMap = new HashMap<>();
        try {
            if (taskValue != null && taskValue.has(JsonFormConstants.CONTENT_FORM)) {
                String subFormName = taskValue.getString(JsonFormConstants.CONTENT_FORM);
                JSONObject subForm = FormUtils.getSubFormJson(subFormName, "", context);
                if (subForm.has(JsonFormConstants.CONTENT_FORM)) {
                    fields = subForm.getJSONArray(JsonFormConstants.CONTENT_FORM);
                }
                if (subForm.has(JsonFormConstants.MLS.PROPERTIES_FILE_NAME)) {
                    mlsPropertyFile = subForm.getString(JsonFormConstants.MLS.PROPERTIES_FILE_NAME);
                }

                stringJSONArrayMap.put(mlsPropertyFile, fields);
            }
        } catch (JSONException e) {
            Timber.e(e, " --> loadSubFormFields");
        } catch (Exception e) {
            Timber.e(e, " --> loadSubFormFields");
        }
        return stringJSONArrayMap;
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
            title = taskValue.optString(JsonFormConstants.TEXT);
        }
        return title;
    }

    /**
     * Updates the form step1 title to match the test header
     *
     * @param form  {@link JSONObject}
     * @param title {@link String}
     */
    private void updateFormTitle(JSONObject form, String title) {
        try {
            if (form != null && StringUtils.isNotBlank(title) && form.has(JsonFormConstants.STEP1)) {
                JSONObject stepOne = form.getJSONObject(JsonFormConstants.STEP1);
                stepOne.put(JsonFormConstants.STEP_TITLE, title);
            }
        } catch (JSONException e) {
            Timber.e(e, " --> updateFormTitle");
        }
    }
}