package org.smartregister.anc.library.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import org.jeasy.rules.api.Facts;
import org.json.JSONObject;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.ContactSummaryFinishActivity;
import org.smartregister.anc.library.adapter.ContactSummaryFinishAdapter;
import org.smartregister.anc.library.contract.ProfileContract;
import org.smartregister.anc.library.domain.YamlConfig;
import org.smartregister.anc.library.model.PartialContact;
import org.smartregister.anc.library.repository.PatientRepositoryHelper;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.ContactJsonFormUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.anc.library.util.FilePathUtils;
import org.smartregister.anc.library.util.Utils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class LoadContactSummaryDataTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private Intent intent;
    private ProfileContract.Presenter mProfilePresenter;
    private Facts facts;
    private List<YamlConfig> yamlConfigList;
    private String baseEntityId;

    public LoadContactSummaryDataTask(Context context, Intent intent, ProfileContract.Presenter mProfilePresenter, Facts facts, List<YamlConfig> yamlConfigList, String baseEntityId) {
        this.context = context;
        this.intent = intent;
        this.mProfilePresenter = mProfilePresenter;
        this.facts = facts;
        this.yamlConfigList = yamlConfigList;
        this.baseEntityId = baseEntityId;
    }



    @Override
    protected Void doInBackground(Void... nada) {
        try {
           process();
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

        String edd = facts.get(DBConstantsUtils.KeyUtils.EDD);
        String contactNo = String.valueOf(intent.getExtras().getInt(ConstantsUtils.IntentKeyUtils.CONTACT_NO));

        if (edd != null && ((ContactSummaryFinishActivity) context).saveFinishMenuItem != null) {
            PatientRepositoryHelper.updateEDDDate(baseEntityId, Utils.reverseHyphenSeperatedValues(edd, "-"));
            ((ContactSummaryFinishActivity) context).saveFinishMenuItem.setEnabled(true);

        } else if (edd == null && contactNo.contains("-")) {
            ((ContactSummaryFinishActivity) context).saveFinishMenuItem.setEnabled(true);
        }

        ContactSummaryFinishAdapter adapter =
                new ContactSummaryFinishAdapter(context, yamlConfigList, facts);
        adapter.notifyDataSetChanged();

        // set up the RecyclerView
        RecyclerView recyclerView = ((ContactSummaryFinishActivity) context).findViewById(R.id.contact_summary_finish_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        //  ((TextView) findViewById(R.id.section_details)).setText(crazyOutput);
        ((ContactSummaryFinishActivity) context).hideProgressDialog();

        //load profile details

        mProfilePresenter.refreshProfileView(baseEntityId);
    }

    private void process() throws Exception {
        //Get actual Data
        JSONObject object;

        List<PartialContact> partialContacts = ((ContactSummaryFinishActivity) context).getPartialContactRepository()
                .getPartialContacts(intent.getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID),
                        intent.getIntExtra(ConstantsUtils.IntentKeyUtils.CONTACT_NO, 1));

        if (partialContacts != null && !partialContacts.isEmpty()) {
            for (PartialContact partialContact : partialContacts) {
                if (partialContact.getFormJsonDraft() != null || partialContact.getFormJson() != null) {
                    object = new JSONObject(partialContact.getFormJsonDraft() != null ? partialContact.getFormJsonDraft() :
                            partialContact.getFormJson());
                    ContactJsonFormUtils.processRequiredStepsField(facts, object);
                }
            }
        }

        Iterable<Object> ruleObjects = AncLibrary.getInstance().readYaml(FilePathUtils.FileUtils.CONTACT_SUMMARY);

        yamlConfigList = new ArrayList<>();
        for (Object ruleObject : ruleObjects) {
            YamlConfig yamlConfig = (YamlConfig) ruleObject;
            yamlConfigList.add(yamlConfig);
        }
    }
}
