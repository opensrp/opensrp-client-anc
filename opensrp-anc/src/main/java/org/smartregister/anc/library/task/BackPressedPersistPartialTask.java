package org.smartregister.anc.library.task;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import org.smartregister.anc.library.activity.ContactJsonFormActivity;
import org.smartregister.anc.library.domain.Contact;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.ANCFormUtils;

public class BackPressedPersistPartialTask extends AsyncTask<Void, Void, Void> {
    private Contact contact;
    private ContactJsonFormActivity contactJsonFormActivity;
    private Intent intent;
    private String currentJsonState;

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
            currentContact.setJsonForm(currentJsonState);
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