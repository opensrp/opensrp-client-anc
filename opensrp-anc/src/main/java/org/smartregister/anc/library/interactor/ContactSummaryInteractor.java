package org.smartregister.anc.library.interactor;

import android.support.annotation.VisibleForTesting;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Facts;
import org.json.JSONObject;
import org.smartregister.anc.library.application.BaseAncApplication;
import org.smartregister.anc.library.contract.BaseContactContract;
import org.smartregister.anc.library.contract.ContactSummarySendContract;
import org.smartregister.anc.library.model.ContactSummaryModel;
import org.smartregister.anc.library.repository.PatientRepository;
import org.smartregister.anc.library.util.AppExecutors;
import org.smartregister.anc.library.util.Constants;
import org.smartregister.anc.library.util.DBConstants;
import org.smartregister.anc.library.util.JsonFormUtils;
import org.smartregister.anc.library.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContactSummaryInteractor extends BaseContactInteractor implements ContactSummarySendContract.Interactor {

    private static String TAG = ContactSummaryInteractor.class.getCanonicalName();
    private JsonFormUtils formUtils = new JsonFormUtils();

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
    public void fetchUpcomingContacts(final String entityId, final String referralContactNo,
                                      final ContactSummarySendContract.InteractorCallback callback) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> details = PatientRepository.getWomanProfileDetails(entityId);

                    Map<String, String> clientDetails =
                            BaseAncApplication.getInstance().getDetailsRepository().getAllDetailsForClient(entityId);
                    JSONObject rawContactSchedule;
                    if (clientDetails.containsKey(Constants.DETAILS_KEY.CONTACT_SCHEDULE)) {
                        rawContactSchedule = new JSONObject(
                                BaseAncApplication.getInstance().getDetailsRepository().getAllDetailsForClient(entityId)
                                        .get(Constants.DETAILS_KEY.CONTACT_SCHEDULE));
                    } else {
                        rawContactSchedule = new JSONObject();
                    }

                    List<String> contactSchedule = new ArrayList<>();
                    if (StringUtils.isEmpty(referralContactNo)) {
                        if (rawContactSchedule.has(Constants.DETAILS_KEY.CONTACT_SCHEDULE)) {
                            contactSchedule =
                                    Utils.getListFromString(
                                            rawContactSchedule.getString(Constants.DETAILS_KEY.CONTACT_SCHEDULE));
                        }
                    } else {
                        int previousContact = getPreviousContactNo(referralContactNo);
                        if (previousContact > 0) {
                            Facts facts =
                                    BaseAncApplication.getInstance().getPreviousContactRepository()
                                            .getImmediatePreviousSchedule(entityId, String.valueOf(previousContact));
                            if (facts != null && facts.asMap().containsKey(Constants.CONTACT_SCHEDULE)) {
                                String schedule = (String) facts.asMap().get(Constants.CONTACT_SCHEDULE);
                                contactSchedule = Utils.getListFromString(schedule);
                            }
                        }
                    }
                    final List<ContactSummaryModel> contactDates;

                    final Integer lastContact = Integer.valueOf(details.get(DBConstants.KEY.NEXT_CONTACT));
                    Integer lastContactSequence = Integer.valueOf(details.get(DBConstants.KEY.NEXT_CONTACT));

                    String edd = details.get(DBConstants.KEY.EDD);
                    contactDates = formUtils.generateNextContactSchedule(edd, contactSchedule, lastContactSequence);

                    getAppExecutors().mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            int contact = lastContact - 1;
                            if (!StringUtils.isEmpty(referralContactNo)) {
                                contact = Integer.parseInt(referralContactNo);
                            }
                            callback.onUpcomingContactsFetched(contactDates, contact);
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        };
        getAppExecutors().diskIO().execute(runnable);
    }

    public int getPreviousContactNo(String referralContact) {
        int contactNo = 0;
        String[] contactArray = referralContact.split("-");
        if (contactArray.length > 0) {
            int currentContact = Integer.parseInt(contactArray[1]);
            contactNo = currentContact - 1;
        }
        return contactNo;
    }

}
