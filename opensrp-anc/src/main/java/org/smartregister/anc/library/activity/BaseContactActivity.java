package org.smartregister.anc.library.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.adapter.ContactAdapter;
import org.smartregister.anc.library.contract.ContactContract;
import org.smartregister.anc.library.domain.Contact;
import org.smartregister.anc.library.model.PartialContact;
import org.smartregister.anc.library.model.Task;
import org.smartregister.anc.library.util.ANCJsonFormUtils;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.view.activity.SecuredActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

//import androidx.recyclerview.widget.GridLayoutManager;

public abstract class BaseContactActivity extends SecuredActivity {
    protected ContactAdapter contactAdapter;
    protected ContactActionHandler contactActionHandler = new ContactActionHandler();
    protected ContactContract.Presenter presenter;
    protected Integer contactNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        initializePresenter();
        presenter.setBaseEntityId(getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID));
        setupViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        createContacts();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy(isChangingConfigurations());
    }

    protected abstract void createContacts();

    protected abstract void initializePresenter();

    protected void setupViews() {
        initializeRecyclerView();
        View cancelButton = findViewById(R.id.undo_button);
        cancelButton.setOnClickListener(v -> contactActionHandler.onClick(v));
        findViewById(R.id.finalize_contact).setOnClickListener(contactActionHandler);
    }

    protected void initializeRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        contactAdapter = new ContactAdapter(this, new ArrayList<>(), contactActionHandler);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(contactAdapter);
    }

    protected void startFormActivity(JSONObject form, Contact contact) {
        Intent intent = new Intent(this, ContactJsonFormActivity.class);
        formStartActions(form, contact, intent);
    }

    private void formStartActions(JSONObject form, Contact contact, Intent intent) {
        try {
            intent.putExtra(ConstantsUtils.JsonFormExtraUtils.JSON, getUpdatedForm(form, contact, getPartialContact(contact)));
            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, contact);
            intent.putExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID,
                    getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID));
            intent.putExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP, getIntent().getSerializableExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP));
            intent.putExtra(ConstantsUtils.IntentKeyUtils.FORM_NAME, contact.getFormName());
            intent.putExtra(ConstantsUtils.IntentKeyUtils.CONTACT_NO, contactNo);
            intent.putExtra(JsonFormConstants.PERFORM_FORM_TRANSLATION, true);
            startActivityForResult(intent, ANCJsonFormUtils.REQUEST_CODE_GET_JSON);
        } catch (JSONException e) {
            Timber.e(e, " --> formStartActions");
        }
    }

    private String getUpdatedForm(JSONObject form, Contact contact, PartialContact partialContactRequest) throws JSONException {
        JSONObject jsonForm = new JSONObject(getFormJson(partialContactRequest, form));
        if (ConstantsUtils.JsonFormUtils.ANC_TEST.equals(contact.getFormName()) && contact.getContactNumber() > 1) {
            List<Task> currentTasks = AncLibrary.getInstance().getContactTasksRepository().getClosedTasks(getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID));
            jsonForm = removeDueTests(jsonForm, currentTasks);
        }
        return String.valueOf(jsonForm);
    }

    @NotNull
    private PartialContact getPartialContact(Contact contact) {
        PartialContact partialContactRequest = new PartialContact();
        partialContactRequest.setBaseEntityId(getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID));
        partialContactRequest.setContactNo(contact.getContactNumber());
        partialContactRequest.setType(contact.getFormName());
        return partialContactRequest;
    }

    protected abstract String getFormJson(PartialContact partialContactRequest, JSONObject jsonForm);

    private JSONObject removeDueTests(JSONObject formObject, List<Task> taskList) {
        JSONObject form = new JSONObject();
        try {
            Map<String, JSONObject> keys = taskHashMap(taskList);
            if (formObject != null && taskList != null && taskList.size() > 0 && formObject.has(JsonFormConstants.STEP1)) {
                JSONObject dueStep = formObject.getJSONObject(JsonFormConstants.STEP1);
                if (dueStep.has(JsonFormConstants.FIELDS)) {
                    JSONArray fields = dueStep.getJSONArray(JsonFormConstants.FIELDS);
                    for (int i = 0; i < fields.length(); i++) {
                        JSONObject field = fields.getJSONObject(i);
                        if (field != null && field.has(JsonFormConstants.KEY)) {
                            String fieldKey = field.getString(JsonFormConstants.KEY);
                            if (keys.containsKey(fieldKey) && ANCJsonFormUtils.checkIfTaskIsComplete(keys.get(fieldKey))) {
                                fields.remove(i);
                            }
                        }
                    }

                    dueStep.put(JsonFormConstants.FIELDS, fields);
                    form = formObject;
                }
            } else {
                form = formObject;
            }
        } catch (JSONException e) {
            Timber.e(e, " --> removeDueTests");
        }

        return form;
    }

    private Map<String, JSONObject> taskHashMap(List<Task> taskList) {
        Map<String, JSONObject> taskMap = new HashMap<>();
        try {
            if (taskList != null && taskList.size() > 0) {
                for (int i = 0; i < taskList.size(); i++) {
                    Task task = taskList.get(i);
                    String taskKey = task.getKey();
                    JSONObject taskValue = new JSONObject(task.getValue());
                    taskMap.put(taskKey, taskValue);
                }
            }
        } catch (JSONException e) {
            Timber.e(e, " --> taskHashMap");
        }

        return taskMap;
    }

    private void displayContactSaveDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_contact_save_dialog, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        TextView titleLabel = view.findViewById(R.id.title_label);
        titleLabel.setText(getString(R.string.exit_contact_title));

        String saveChanges = getString(R.string.save_contact);
        //For future usage
        /*
        Spannable spannable = new SpannableString(saveChanges);
        spannable.setSpan(new RelativeSizeSpan(1.3f), 0, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.contact_save_grey_blue)), 5,
                saveChanges.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
*/

        Button saveButton = view.findViewById(R.id.save_changes);
        saveButton.setText(saveChanges);

        String closeWithoutSaving = getString(R.string.discard_contact);

        //For future usage
      /*
        spannable = new SpannableString(closeWithoutSaving);
        spannable.setSpan(new RelativeSizeSpan(1.3f), 0, 6, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.contact_save_grey)), 7,
                closeWithoutSaving.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                */

        Button closeButton = view.findViewById(R.id.close_without_saving);
        closeButton.setText(closeWithoutSaving);

        Button cancel = view.findViewById(R.id.cancel);

        final AlertDialog dialog = builder.create();

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams param = window.getAttributes();
            param.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            window.setAttributes(param);
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        onSave(saveButton, dialog);
        onCancel(closeButton, cancel, dialog);

        dialog.show();
    }

    private void onCancel(Button closeButton, Button cancel, AlertDialog dialog) {
        cancel.setOnClickListener(v -> dialog.dismiss());
        closeButton.setOnClickListener(v -> {
            dialog.dismiss();
            presenter.deleteDraft(getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID));
            goToMainRegister();
        });
    }

    private void onSave(Button saveButton, AlertDialog dialog) {
        saveButton.setOnClickListener(v -> {
            dialog.dismiss();
            presenter.saveFinalJson(getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID));
            goToMainRegister();
        });
    }

    public void goToMainRegister() {
        Intent intent = new Intent(getActivity(), AncLibrary.getInstance().getActivityConfiguration().getHomeRegisterActivityClass());
        startActivity(intent);
    }

    private Activity getActivity() {
        return this;
    }

    public void startForms(View view) {
        presenter.startForm(view.getTag());
    }

    ////////////////////////////////////////////////////////////////
    // Inner classesC
    ////////////////////////////////////////////////////////////////

    private class ContactActionHandler implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.undo_button) {
                displayContactSaveDialog();
            } else if (i == R.id.card_layout) {
                startForms(view);
            } else if (i == R.id.finalize_contact) {
                Utils.finalizeForm(getActivity(),
                        (HashMap<String, String>) getIntent().getSerializableExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP),
                        false);
            }
        }
    }
}
