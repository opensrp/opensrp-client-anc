package org.smartregister.anc.library.task;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.ContactSummaryFinishActivity;
import org.smartregister.anc.library.activity.ContactSummarySendActivity;
import org.smartregister.anc.library.contract.ProfileContract;
import org.smartregister.anc.library.repository.PatientRepository;
import org.smartregister.anc.library.util.ConstantsUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import timber.log.Timber;

public class FinalizeContactTask extends AsyncTask<Void, Void, Void> {
    private final Context context;
    private final ProfileContract.Presenter mProfilePresenter;
    private final Intent intent;
    private HashMap<String, String> newWomanProfileDetails;

    public FinalizeContactTask(WeakReference<Context> context, ProfileContract.Presenter mProfilePresenter, Intent intent) {
        this.context = context.get();
        this.mProfilePresenter = mProfilePresenter;
        this.intent = intent;
    }

    @Override
    protected void onPreExecute() {
        ((ContactSummaryFinishActivity) context).showProgressDialog(R.string.please_wait_message);
        ((ContactSummaryFinishActivity) context).getProgressDialog().setMessage(
                String.format(context.getString(R.string.finalizing_contact),
                        intent.getExtras().getInt(ConstantsUtils.IntentKeyUtils.CONTACT_NO)) + " data");
        ((ContactSummaryFinishActivity) context).getProgressDialog().show();
    }
    @Override
    protected Void doInBackground(Void... nada) {
        try {
            HashMap<String, String> womanProfileDetails = (HashMap<String, String>) PatientRepository
                    .getWomanProfileDetails(intent.getExtras().getString(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID));
            int contactNo = intent.getExtras().getInt(ConstantsUtils.IntentKeyUtils.CONTACT_NO);
            if (contactNo < 0) {
                womanProfileDetails.put(ConstantsUtils.REFERRAL, String.valueOf(contactNo));
            }
            newWomanProfileDetails = mProfilePresenter.saveFinishForm(womanProfileDetails, context);
        } catch (Exception e) {
            Timber.e(e);
        }

        return null;

    }



    @Override
    protected void onPostExecute(Void result) {
        ((ContactSummaryFinishActivity) context).hideProgressDialog();
        Intent contactSummaryIntent =
                new Intent(context, ContactSummarySendActivity.class);
        contactSummaryIntent.putExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID,
                intent.getExtras().getString(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID));
        contactSummaryIntent.putExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP, newWomanProfileDetails);
        context.startActivity(contactSummaryIntent);
        ((ContactSummaryFinishActivity) context).finish();
    }
}
