package org.smartregister.anc.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.vijay.jsonwizard.activities.JsonWizardFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.smartregister.anc.R;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.domain.Contact;
import org.smartregister.anc.fragment.ContactJsonFormFragment;
import org.smartregister.anc.model.PartialContact;
import org.smartregister.anc.util.Constants;


/**
 * Created by ndegwamartin on 30/06/2018.
 */
public class ContactJsonFormActivity extends JsonWizardFormActivity {

    private ProgressDialog progressDialog;

    @Override
    public void initializeFormFragment() {
        initializeFormFragmentCore();
    }

    protected void initializeFormFragmentCore() {
        ContactJsonFormFragment contactJsonFormFragment = ContactJsonFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        getSupportFragmentManager().beginTransaction()
                .add(com.vijay.jsonwizard.R.id.container, contactJsonFormFragment).commit();
    }

    public Contact getContact() {
        Form form = getForm();
        if (form instanceof Contact) {
            return (Contact) form;
        }
        return null;
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

        Contact contact = getContact();
        if (contact != null) {
            partialContact.setType(getContact().getFormName());
        }
        partialContact.setFormJsonDraft(currentJsonState());

        AncApplication.getInstance().getPartialContactRepository().savePartialContact(partialContact);
    }
}

