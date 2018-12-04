package org.smartregister.anc.interactor;

import android.support.annotation.VisibleForTesting;
import android.util.Log;

import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.smartregister.anc.R;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.BaseContactContract;
import org.smartregister.anc.contract.ContactSummarySendContract;
import org.smartregister.anc.model.ContactSummaryModel;
import org.smartregister.anc.repository.PatientRepository;
import org.smartregister.anc.util.AppExecutors;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContactSummaryInteractor extends BaseContactInteractor implements ContactSummarySendContract.Interactor {

    private static String TAG = ContactSummaryInteractor.class.getCanonicalName();

    @VisibleForTesting
    ContactSummaryInteractor(AppExecutors appExecutors) {
        super(appExecutors);
    }

    public ContactSummaryInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void fetchWomanDetails(String baseEntityId, BaseContactContract.InteractorCallback callBack) {
        super.fetchWomanDetails(baseEntityId, callBack);
    }


    @Override
    public void fetchUpcomingContacts(final String entityId, final ContactSummarySendContract.InteractorCallback callback) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                try {

                    Map<String, String> details = PatientRepository.getWomanProfileDetails(entityId);

                    JSONObject rawContactSchedule = new JSONObject(AncApplication.getInstance().getDetailsRepository().getAllDetailsForClient(entityId).get(Constants.DETAILS_KEY.CONTACT_SHEDULE));
                    List<String> contactSchedule = Utils.getListFromString(rawContactSchedule.getString(Constants.DETAILS_KEY.CONTACT_SHEDULE));

                    final List<ContactSummaryModel> contactDates = new ArrayList<>();


                    final Integer lastContact = Integer.valueOf(details.get(DBConstants.KEY.NEXT_CONTACT));
                    Integer lastContactSequence = Integer.valueOf(details.get(DBConstants.KEY.NEXT_CONTACT));

                    String edd = details.get(DBConstants.KEY.EDD);
                    LocalDate localDate = new LocalDate(edd);
                    LocalDate lmpDate = localDate.minusWeeks(Constants.DELIVERY_DATE_WEEKS);

                    for (String contact : contactSchedule) {

                        contactDates.add(new ContactSummaryModel(String.format(AncApplication.getInstance().getApplicationContext().getString(R.string.contact_number), lastContactSequence++), Utils.convertDateFormat(lmpDate.plusWeeks(Integer.valueOf(contact)).toDate(), Utils.CONTACT_SUMMARY_DF)));
                    }

                    getAppExecutors().mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onUpcomingContactsFetched(contactDates, lastContact - 1);
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        };
        getAppExecutors().diskIO().execute(runnable);
    }

}
