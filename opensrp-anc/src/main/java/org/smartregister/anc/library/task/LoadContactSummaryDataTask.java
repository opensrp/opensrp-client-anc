package org.smartregister.anc.library.task;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Facts;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.ContactSummaryFinishActivity;
import org.smartregister.anc.library.adapter.ContactSummaryFinishAdapter;
import org.smartregister.anc.library.contract.ProfileContract;
import org.smartregister.anc.library.repository.PatientRepository;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.anc.library.util.Utils;

import java.util.HashMap;

import timber.log.Timber;

public class LoadContactSummaryDataTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private Intent intent;
    private ProfileContract.Presenter mProfilePresenter;
    private Facts facts;
    private String baseEntityId;

    public LoadContactSummaryDataTask(Context context, Intent intent, ProfileContract.Presenter mProfilePresenter, Facts facts, String baseEntityId) {
        this.context = context;
        this.intent = intent;
        this.mProfilePresenter = mProfilePresenter;
        this.facts = facts;
        this.baseEntityId = baseEntityId;
    }


    @Override
    protected Void doInBackground(Void... nada) {
        try {
            ((ContactSummaryFinishActivity) context).process();
        } catch (Exception e) {
            Timber.e(e, "%s --> loadContactSummaryData", this.getClass().getCanonicalName());
        }

        return null;

    }

    @Override
    protected void onPreExecute() {
        ((ContactSummaryFinishActivity) context).showProgressDialog(R.string.please_wait_message);
        ((ContactSummaryFinishActivity) context).getProgressDialog().setMessage(String.format(context.getString(R.string.summarizing_contact_number), intent.getExtras().getInt(ConstantsUtils.IntentKeyUtils.CONTACT_NO)) + " data");
        ((ContactSummaryFinishActivity) context).getProgressDialog().show();
    }

    @Override
    protected void onPostExecute(Void result) {
        HashMap<String, String> clientDetails;
        try {
            clientDetails = (HashMap<String, String>) intent.getSerializableExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP);
        } catch (NullPointerException e) {
            clientDetails = new HashMap<>();
        }
        String edd = StringUtils.isNotBlank(facts.get(DBConstantsUtils.KeyUtils.EDD)) ? facts.get(DBConstantsUtils.KeyUtils.EDD) : clientDetails != null ? Utils.reverseHyphenSeperatedValues(clientDetails.get(ConstantsUtils.EDD), "-") : "";
        String contactNo = String.valueOf(intent.getExtras().getInt(ConstantsUtils.IntentKeyUtils.CONTACT_NO));

        if (edd != null && ((ContactSummaryFinishActivity) context).saveFinishMenuItem != null) {
            PatientRepository.updateEDDDate(baseEntityId, Utils.reverseHyphenSeperatedValues(edd, "-"));
            ((ContactSummaryFinishActivity) context).saveFinishMenuItem.setEnabled(true);

        } else if (edd == null && contactNo.contains("-")) {
            ((ContactSummaryFinishActivity) context).saveFinishMenuItem.setEnabled(true);
        }

        ContactSummaryFinishAdapter adapter =
                new ContactSummaryFinishAdapter(context, ((ContactSummaryFinishActivity) context).getYamlConfigList(), facts);
        adapter.notifyDataSetChanged();

        // set up the RecyclerView
        RecyclerView recyclerView = ((ContactSummaryFinishActivity) context).findViewById(R.id.contact_summary_finish_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        //  ((TextView) findViewById(R.id.section_details)).setText(crazyOutput);
        ((ContactSummaryFinishActivity) context).hideProgressDialog();

        //load profile details

        mProfilePresenter.refreshProfileView(baseEntityId);

        //Create PDF file stuff
        mProfilePresenter.createContactSummaryPdf();
    }
}
