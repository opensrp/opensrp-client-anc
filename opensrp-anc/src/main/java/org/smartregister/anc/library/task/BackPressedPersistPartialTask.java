package org.smartregister.anc.library.task;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import org.smartregister.anc.library.activity.ContactJsonFormActivity;
import org.smartregister.anc.library.domain.Contact;
import org.smartregister.anc.library.util.ANCFormUtils;
import org.smartregister.anc.library.util.ConstantsUtils;

public class BackPressedPersistPartialTask extends AsyncTask<Void, Void, Void> {
    private final Contact contact;
    private final ContactJsonFormActivity contactJsonFormActivity;
    private final Intent intent;
    private final String currentJsonState;
    private final ANCFormUtils ancFormUtils = new ANCFormUtils();

    public BackPressedPersistPartialTask(Contact contact, Context context, Intent intent, String currentJsonState) {
        this.contact = contact;
        this.contactJsonFormActivity = (ContactJsonFormActivity) context;
        this.intent = intent;
        this.currentJsonState = currentJsonState;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (intent != null) {
            int contactNo = intent.getIntExtra(ConstantsUtils.IntentKeyUtils.CONTACT_NO, 0);
            Contact currentContact = contact;
            currentContact.setJsonForm(ancFormUtils.addFormDetails(currentJsonState));
            currentContact.setContactNumber(contactNo);
            ANCFormUtils.persistPartial(intent.getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID), currentContact);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        contactJsonFormActivity.finish();
    }
}