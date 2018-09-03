package org.smartregister.anc.activity;

import android.app.AlertDialog;
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

import org.smartregister.anc.R;
import org.smartregister.anc.adapter.ContactAdapter;
import org.smartregister.anc.contract.ContactContract;
import org.smartregister.anc.domain.Contact;
import org.smartregister.anc.util.Constants;
import org.smartregister.view.activity.SecuredActivity;

import java.util.ArrayList;

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

    }

    protected abstract void initializePresenter();

    protected void initializeRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        contactAdapter = new ContactAdapter(this, new ArrayList<Contact>());

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(contactAdapter);

        createContacts();
    }

    protected abstract void createContacts();

    private void displayContactSaveDialog() {

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_contact_save_dialog, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        TextView titleLabel = view.findViewById(R.id.title_label);
        titleLabel.setText(String.format(getString(R.string.exit_contact_with), presenter.getPatientName()));

        String saveChanges = getString(R.string.save_changes);
        Spannable spannable = new SpannableString(saveChanges);
        spannable.setSpan(new RelativeSizeSpan(1.3f), 0, 13
                , Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.contact_save_grey)), 14, saveChanges.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        Button saveButton = view.findViewById(R.id.save_changes);
        saveButton.setText(spannable);

        String closeWithoutSaving = getString(R.string.close_without_saving);
        spannable = new SpannableString(closeWithoutSaving);
        spannable.setSpan(new RelativeSizeSpan(1.3f), 0, 21
                , Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.contact_save_grey)), 22, closeWithoutSaving.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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
                default:
                    break;
            }
        }
    }
}
