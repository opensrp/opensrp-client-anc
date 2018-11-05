package org.smartregister.anc.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.R;
import org.smartregister.anc.adapter.ContactAdapter;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.ContactContract;
import org.smartregister.anc.domain.Contact;
import org.smartregister.anc.model.PartialContact;
import org.smartregister.anc.repository.PatientRepository;
import org.smartregister.anc.rule.ContactRule;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.JsonFormUtils;
import org.smartregister.anc.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.view.activity.SecuredActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public abstract class BaseContactActivity extends SecuredActivity {

    protected ContactAdapter contactAdapter;

    protected ContactActionHandler contactActionHandler = new ContactActionHandler();

    protected ContactContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        initializePresenter();

        presenter.setBaseEntityId(getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID));

        setupViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy(isChangingConfigurations());
    }

    protected void setupViews() {
        initializeRecyclerView();

        View cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactActionHandler.onClick(v);
            }
        });
        findViewById(R.id.finalize).setEnabled(true);
        findViewById(R.id.finalize).setOnClickListener(contactActionHandler);

    }

    protected abstract void initializePresenter();

    protected void initializeRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        contactAdapter = new ContactAdapter(this, new ArrayList<Contact>(), contactActionHandler);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(contactAdapter);

        createContacts();
    }

    protected abstract void createContacts();

    protected void startFormActivity(JSONObject form, Contact contact) {
        Intent intent = new Intent(this, ContactJsonFormActivity.class);

        //partial contact exists?

        PartialContact partialContactRequest = new PartialContact();
        partialContactRequest.setBaseEntityId(getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID));
        partialContactRequest.setContactNo(1);
        partialContactRequest.setType(contact.getFormName());

        PartialContact partialContact = AncApplication.getInstance().getPartialContactRepository().getPartialContact(partialContactRequest);

        intent.putExtra(Constants.JSON_FORM_EXTRA.JSON, partialContact != null ? partialContact.getFormJson() : form.toString());

        intent.putExtra(Constants.JSON_FORM_EXTRA.CONTACT, contact);
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID));
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }


    private void displayContactSaveDialog() {

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_contact_save_dialog, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        TextView titleLabel = view.findViewById(R.id.title_label);
        titleLabel.setText(getString(R.string.exit_contact_title));

        String saveChanges = getString(R.string.save_contact);
        Spannable spannable = new SpannableString(saveChanges);
        spannable.setSpan(new RelativeSizeSpan(1.3f), 0, 4
                , Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.contact_save_grey_blue)), 5, saveChanges.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        Button saveButton = view.findViewById(R.id.save_changes);
        saveButton.setText(spannable);

        String closeWithoutSaving = getString(R.string.discard_contact);
        spannable = new SpannableString(closeWithoutSaving);
        spannable.setSpan(new RelativeSizeSpan(1.3f), 0, 7
                , Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.contact_save_grey)), 8, closeWithoutSaving.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        Button closeButton = view.findViewById(R.id.close_without_saving);
        closeButton.setText(spannable);

        Button cancel = view.findViewById(R.id.cancel);

        final AlertDialog dialog = builder.create();

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams param = window.getAttributes();
            param.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            window.setAttributes(param);
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
    }

    private void finalizeForm() {
        try {

//Fire Rules
            CommonPersonObjectClient pc = (CommonPersonObjectClient) getIntent().getExtras().get(Constants.INTENT_KEY.CLIENT);

            presenter.finalizeContactForm(pc);

        } catch (Exception e) {
            Log.e(BaseContactActivity.class.getCanonicalName(), e.getMessage());
        }

    }

    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////

    private class ContactActionHandler implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.cancel_button:
                    displayContactSaveDialog();
                    break;
                case R.id.card_layout:
                    presenter.startForm(view.getTag());
                    break;
                case R.id.finalize:
                    finalizeForm();
                    break;
                default:
                    break;
            }
        }
    }
}
