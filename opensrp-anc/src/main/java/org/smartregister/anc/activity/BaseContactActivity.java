package org.smartregister.anc.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONObject;
import org.smartregister.anc.R;
import org.smartregister.anc.adapter.ContactAdapter;
import org.smartregister.anc.contract.ContactContract;
import org.smartregister.anc.domain.Contact;
import org.smartregister.anc.model.PartialContact;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.JsonFormUtils;
import org.smartregister.anc.util.Utils;
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

        presenter.setBaseEntityId(getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID));

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

    protected void setupViews() {
        initializeRecyclerView();

        View cancelButton = findViewById(R.id.undo_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactActionHandler.onClick(v);
            }
        });
        findViewById(R.id.finalize_contact).setEnabled(true);
        findViewById(R.id.finalize_contact).setOnClickListener(contactActionHandler);

    }

    protected abstract void initializePresenter();

    protected void initializeRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        contactAdapter = new ContactAdapter(this, new ArrayList<Contact>(), contactActionHandler);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(contactAdapter);
    }

    protected abstract void createContacts();

    protected void startFormActivity(JSONObject form, Contact contact) {
        Intent intent = new Intent(this, ContactJsonFormActivity.class);
        formStartActions(form, contact, intent);
    }

    protected void startQuickCheck(JSONObject form, Contact contact) {
        Intent intent = new Intent(this, QuickCheckFormActivity.class);
        formStartActions(form, contact, intent);
    }

    private void formStartActions(JSONObject form, Contact contact, Intent intent) {
        //partial contact exists?
        PartialContact partialContactRequest = new PartialContact();
        partialContactRequest.setBaseEntityId(getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID));
        partialContactRequest.setContactNo(contact.getContactNumber());
        partialContactRequest.setType(contact.getFormName());

        intent.putExtra(Constants.JSON_FORM_EXTRA.JSON, getFormJson(partialContactRequest, form));
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, contact);
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID));
        intent.putExtra(Constants.INTENT_KEY.CLIENT_MAP, getIntent().getSerializableExtra(Constants.INTENT_KEY.CLIENT_MAP));
        intent.putExtra(Constants.INTENT_KEY.FORM_NAME, contact.getFormName());
        intent.putExtra(Constants.INTENT_KEY.CONTACT_NO, contactNo);
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

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                presenter.saveFinalJson(getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID));
                goToMainRegister();
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
                presenter.deleteDraft(getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID));
                goToMainRegister();
            }
        });

        dialog.show();
    }

    private Activity getActivity() {
        return this;
    }

    private void goToMainRegister() {
        Intent intent = new Intent(getActivity(), HomeRegisterActivity.class);
        startActivity(intent);
    }

    protected abstract String getFormJson(PartialContact partialContactRequest, JSONObject jsonForm);

    ////////////////////////////////////////////////////////////////
    // Inner classesC
    ////////////////////////////////////////////////////////////////

    private class ContactActionHandler implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.undo_button:
                    displayContactSaveDialog();
                    break;
                case R.id.card_layout:
                    presenter.startForm(view.getTag());
                    break;
                case R.id.finalize_contact:
                    Utils.finalizeForm(getActivity(), (HashMap<String, String>) getIntent().getSerializableExtra(Constants.INTENT_KEY.CLIENT_MAP));
                    break;
                default:
                    break;
            }
        }
    }
}
