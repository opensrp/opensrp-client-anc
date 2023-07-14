package org.smartregister.anc.library.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Facts;
import org.json.JSONObject;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.ContactSummaryFinishActivity;
import org.smartregister.anc.library.adapter.ContactSummaryFinishAdapter;
import org.smartregister.anc.library.contract.ProfileContract;
import org.smartregister.anc.library.repository.PatientRepository;
import org.smartregister.anc.library.util.AppExecutors;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.client.utils.constants.JsonFormConstants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import timber.log.Timber;

public class LoadContactSummaryDataTask {
    private final Intent intent;
    private final Context context;
    private final ProfileContract.Presenter mProfilePresenter;
    private final Facts facts;
    private final String baseEntityId;
    private AppExecutors appExecutors;

    public LoadContactSummaryDataTask(Context context, Intent intent, ProfileContract.Presenter mProfilePresenter, Facts facts, String baseEntityId) {
        this.context = context;
        this.intent = intent;
        this.mProfilePresenter = mProfilePresenter;
        this.facts = facts;
        this.baseEntityId = baseEntityId;
    }


    public void execute() {
        appExecutors = new AppExecutors();
        appExecutors.mainThread().execute(this::showDialog);
        appExecutors.diskIO().execute(() -> {
            this.onProcess();
            appExecutors.mainThread().execute(this::finishAdapterOnPostExecute);
        });
    }

    private Void onProcess() {
        try {
            ((ContactSummaryFinishActivity) context).process();
        } catch (Exception e) {
            Timber.e(e, "%s --> loadContactSummaryData", this.getClass().getCanonicalName());
        }

        return null;
    }

    private void showDialog() {
        ((ContactSummaryFinishActivity) context).showProgressDialog(R.string.please_wait_message);
        ((ContactSummaryFinishActivity) context).getProgressDialog().setMessage(String.format(context.getString(R.string.summarizing_contact_number), intent.getExtras().getInt(ConstantsUtils.IntentKeyUtils.CONTACT_NO)) + " data");
        ((ContactSummaryFinishActivity) context).getProgressDialog().show();
    }

    private void finishAdapterOnPostExecute() {
        HashMap<String, String> clientDetails;
        try {
            clientDetails = (HashMap<String, String>) intent.getSerializableExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP);
        } catch (NullPointerException e) {
            clientDetails = new HashMap<>();
        }
        String edd = StringUtils.isNotBlank(facts.get(DBConstantsUtils.KeyUtils.EDD)) ? facts.get(DBConstantsUtils.KeyUtils.EDD) : clientDetails != null ? Utils.reverseHyphenSeperatedValues(clientDetails.get(ConstantsUtils.EDD), "-") : "";
        String visitDate = StringUtils.isNotBlank(facts.get("visit_date")) ? facts.get("visit_date") : null;
        String contactNo = String.valueOf(intent.getExtras().getInt(ConstantsUtils.IntentKeyUtils.CONTACT_NO));

        if (visitDate != null) {
            PatientRepository.updateLastVisitDate(baseEntityId, Utils.reverseHyphenSeperatedValues(visitDate, "-"));
        }

        if (edd != null && ((ContactSummaryFinishActivity) context).saveFinishMenuItem != null) {
            PatientRepository.updateEDDDate(baseEntityId, Utils.reverseHyphenSeperatedValues(edd, "-"));
            ((ContactSummaryFinishActivity) context).saveFinishMenuItem.setEnabled(true);

        } else if (edd == null && contactNo.contains("-")) {
            ((ContactSummaryFinishActivity) context).saveFinishMenuItem.setEnabled(true);
        }

        // Populate facts
        Map<String, Object> factsAsMap = facts.asMap();
        Facts populatedFacts = new Facts();
        for (Map.Entry<String, Object> entry : factsAsMap.entrySet()) {
            String key = entry.getKey();
            Object valueObject = entry.getValue();
            String text = valueObject != null ? Utils.returnTranslatedStringJoinedValue(valueObject.toString()) : "";
            String valueObjectStr = valueObject.toString();
            String value = Utils.getFactInputValue(valueObjectStr);
            populatedFacts.put(key, text);
            populatedFacts.put(key + "_value", value);
            if(StringUtils.isNotBlank(valueObjectStr)  && (valueObjectStr.contains(".")
                    && valueObjectStr.contains(JsonFormConstants.TEXT))
                    || (StringUtils.isNotBlank(valueObjectStr) && valueObjectStr.charAt(0) == '['
                    && valueObjectStr.contains("{") && valueObjectStr.contains(JsonFormConstants.TEXT)) )
                populatedFacts.put(key + "_value", text);
        }

        ContactSummaryFinishAdapter adapter = new ContactSummaryFinishAdapter(context, ((ContactSummaryFinishActivity) context).getYamlConfigList(), populatedFacts);
        adapter.notifyDataSetChanged();
        // set up the RecyclerView
        RecyclerView recyclerView = ((ContactSummaryFinishActivity) context).findViewById(R.id.contact_summary_finish_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        //  ((TextView) findViewById(R.id.section_details)).setText(crazyOutput);
        ((ContactSummaryFinishActivity) context).hideProgressDialog();
        //load profile details
        mProfilePresenter.refreshProfileView(baseEntityId);
        String name = clientDetails.get(DBConstantsUtils.KeyUtils.FIRST_NAME);
        if(clientDetails.get(DBConstantsUtils.KeyUtils.LAST_NAME)!= null)
            name = name +" "+ clientDetails.get(DBConstantsUtils.KeyUtils.LAST_NAME);

        //Create PDF file stuff
        mProfilePresenter.createContactSummaryPdf(name);
    }
}