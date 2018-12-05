package org.smartregister.anc.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONException;
import org.smartregister.anc.R;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.domain.Contact;
import org.smartregister.anc.fragment.ContactJsonFormFragment;
import org.smartregister.anc.model.PartialContact;
import org.smartregister.anc.util.Constants;

import java.io.Serializable;


/**
 * Created by ndegwamartin on 30/06/2018.
 */
public class ContactJsonFormActivity extends JsonFormActivity {

    private static final String CONTACT_STATE = "contactState";
    private Contact contact;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            this.contact = extractContact(getIntent().getSerializableExtra(Constants.JSON_FORM_EXTRA.CONTACT));
        } else {
            this.contact = extractContact(savedInstanceState.getSerializable(CONTACT_STATE));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(CONTACT_STATE, contact);
    }

    @Override
    public void initializeFormFragment() {
        initializeFormFragmentCore();
    }

    @Override
    public void writeValue(String stepName, String key, String value, String openMrsEntityParent, String openMrsEntity, String openMrsEntityId,boolean popup) throws JSONException {
        callSuperWriteValue(stepName, key, value, openMrsEntityParent, openMrsEntity, openMrsEntityId,popup);
    }

    @Override
    public void onFormFinish() {
        callSuperFinish();
    }

    protected void callSuperFinish() {
        super.onFormFinish();
    }

    protected void callSuperWriteValue(String stepName, String key, String value, String openMrsEntityParent, String openMrsEntity, String openMrsEntityId,boolean popup) throws JSONException {
        super.writeValue(stepName, key, value, openMrsEntityParent, openMrsEntity, openMrsEntityId,popup);
    }

    protected void initializeFormFragmentCore() {
        ContactJsonFormFragment contactJsonFormFragment = ContactJsonFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        getSupportFragmentManager().beginTransaction()
                .add(com.vijay.jsonwizard.R.id.container, contactJsonFormFragment).commit();
    }

    private Contact extractContact(Serializable serializable) {
        if (serializable != null && serializable instanceof Contact) {
            return (Contact) serializable;
        }
        return null;
    }

    public Contact getContact() {
        return contact;
    }

    @Override
    public void onBackPressed() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {

                showProgressDialog("Saving contact progress...");
            }

            @Override
            protected Void doInBackground(Void... nada) {

                persistPartial();

                return null;

            }

            @Override
            protected void onPostExecute(Void result) {
                hideProgressDialog();
                ContactJsonFormActivity.this.finish();

            }
        }.execute();

    }

    public void showProgressDialog(String titleIdentifier) {

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle(titleIdentifier);
            progressDialog.setMessage(getString(R.string.please_wait_message));
        }

        if (!isFinishing())
            progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void persistPartial() {
        String baseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);

        PartialContact partialContact = new PartialContact();
        partialContact.setBaseEntityId(baseEntityId);
        partialContact.setContactNo(1);//Hardcoded to remove
        partialContact.setFinalized(false);

        partialContact.setType(getContact().getFormName());
        partialContact.setFormJsonDraft(currentJsonState());

        AncApplication.getInstance().getPartialContactRepository().savePartialContact(partialContact);
    }
}

