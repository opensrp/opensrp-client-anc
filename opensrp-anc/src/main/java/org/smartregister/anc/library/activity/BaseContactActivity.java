package org.smartregister.anc.library.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONObject;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.adapter.ContactAdapter;
import org.smartregister.anc.library.contract.ContactContract;
import org.smartregister.anc.library.domain.Contact;
import org.smartregister.anc.library.model.PartialContact;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.JsonFormUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.view.activity.SecuredActivity;

import java.util.ArrayList;
import java.util.HashMap;

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

        contactAdapter = new ContactAdapter(this, new ArrayList<Contact>(), contactActionHandler);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(contactAdapter);
    }

    protected void startFormActivity(JSONObject form, Contact contact) {
        Intent intent = new Intent(this, ContactJsonFormActivity.class);
        formStartActions(form, contact, intent);
    }

    private void formStartActions(JSONObject form, Contact contact, Intent intent) {
        //partial contact exists?
        PartialContact partialContactRequest = new PartialContact();
        partialContactRequest.setBaseEntityId(getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID));
        partialContactRequest.setContactNo(contact.getContactNumber());
        partialContactRequest.setType(contact.getFormName());

        intent.putExtra(ConstantsUtils.JsonFormExtraUtils.JSON, getFormJson(partialContactRequest, form));
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, contact);
        intent.putExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID,
                getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID));
        intent.putExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP, getIntent().getSerializableExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP));
        intent.putExtra(ConstantsUtils.IntentKeyUtils.FORM_NAME, contact.getFormName());
        intent.putExtra(ConstantsUtils.IntentKeyUtils.CONTACT_NO, contactNo);
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    protected abstract String getFormJson(PartialContact partialContactRequest, JSONObject jsonForm);

    private void displayContactSaveDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_contact_save_dialog, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        TextView titleLabel = view.findViewById(R.id.title_label);
        titleLabel.setText(getString(R.string.exit_contact_title));
        String saveChanges = getString(R.string.save_contact);
        Button saveButton = view.findViewById(R.id.save_changes);
        saveButton.setText(saveChanges);
        String closeWithoutSaving = getString(R.string.discard_contact);
        Button closeButton = view.findViewById(R.id.close_without_saving);
        closeButton.setText(closeWithoutSaving);
        Button cancel = view.findViewById(R.id.cancel);

        final AlertDialog dialog = builder.create();

        updateDialogWindow(dialog);
        attachSaveButtonAction(saveButton, dialog);
        cancel.setOnClickListener(v -> dialog.dismiss());
        attachCloseButtonAction(closeButton, dialog);

        dialog.show();
    }

    private void attachCloseButtonAction(Button closeButton, AlertDialog dialog) {
        closeButton.setOnClickListener(v -> {
            dialog.dismiss();
            presenter.deleteDraft(getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID));
            goToMainRegister();
        });
    }

    private void attachSaveButtonAction(Button saveButton, AlertDialog dialog) {
        saveButton.setOnClickListener(v -> {
            dialog.dismiss();
            presenter.saveFinalJson(getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID));
            goToMainRegister();
        });
    }

    private void updateDialogWindow(AlertDialog dialog) {
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams param = window.getAttributes();
            param.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            window.setAttributes(param);
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
    }

    public void goToMainRegister() {
        Intent intent = new Intent(getActivity(), AncLibrary.getInstance().getActivityConfiguration().getHomeRegisterActivityClass());
        startActivity(intent);
    }

    private Activity getActivity() {
        return this;
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
                presenter.startForm(view.getTag());
            } else if (i == R.id.finalize_contact) {
                Utils.finalizeForm(getActivity(),
                        (HashMap<String, String>) getIntent().getSerializableExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP),
                        false);
            }
        }
    }
}
