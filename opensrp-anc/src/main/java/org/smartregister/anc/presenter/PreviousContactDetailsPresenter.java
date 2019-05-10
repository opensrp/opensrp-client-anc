package org.smartregister.anc.presenter;

import android.text.TextUtils;
import android.util.Log;

import org.jeasy.rules.api.Facts;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.PreviousContactsDetails;
import org.smartregister.anc.interactor.PreviousContactsDetailsInteractor;
import org.smartregister.anc.model.ContactSummaryModel;
import org.smartregister.anc.model.PreviousContactsSummaryModel;
import org.smartregister.anc.repository.PreviousContactRepository;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.JsonFormUtils;
import org.smartregister.anc.util.Utils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by ndegwamartin on 13/07/2018.
 */
public class PreviousContactDetailsPresenter implements PreviousContactsDetails.Presenter {
    private static final String TAG = PreviousContactDetailsPresenter.class.getCanonicalName();
    private JsonFormUtils formUtils = new JsonFormUtils();

    private WeakReference<PreviousContactsDetails.View> mProfileView;
    private PreviousContactsDetails.Interactor mProfileInteractor;

    public PreviousContactDetailsPresenter(PreviousContactsDetails.View profileView) {
        mProfileView = new WeakReference<>(profileView);
        mProfileInteractor = new PreviousContactsDetailsInteractor(this);
    }

    public void onDestroy(boolean isChangingConfiguration) {
        mProfileView = null;//set to null on destroy

        // Inform interactor
        if (mProfileInteractor != null) {
            mProfileInteractor.onDestroy(isChangingConfiguration);
        }

        // Activity destroyed set interactor to null
        if (!isChangingConfiguration) {
            mProfileInteractor = null;
        }
    }

    @Override
    public PreviousContactsDetails.View getProfileView() {
        if (mProfileView != null) {
            return mProfileView.get();
        } else {
            return null;
        }
    }

    @Override
    public void loadPreviousContactSchedule(String baseEntityId, String contactNo, String edd) {
        try {
            Facts immediatePreviousSchedule = getPreviousContactRepository()
                    .getImmediatePreviousSchedule(baseEntityId, contactNo);
            String contactScheduleString = "";
            if (immediatePreviousSchedule != null) {
                Map<String, Object> scheduleMap = immediatePreviousSchedule.asMap();
                for (Map.Entry<String, Object> entry : scheduleMap.entrySet()) {
                    if (Constants.CONTACT_SCHEDULE.equals(entry.getKey())) {
                        contactScheduleString = entry.getValue().toString();
                    }
                }
            }

            List<String> scheduleList = new ArrayList<>();
            if (!TextUtils.isEmpty(contactScheduleString)) {
                scheduleList = Utils.getListFromString(contactScheduleString);
            }

            Date lastContactEdd = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(edd);
            String formattedEdd = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(lastContactEdd);
            List<ContactSummaryModel> schedule =
                    formUtils.generateNextContactSchedule(formattedEdd, scheduleList, Integer.valueOf(contactNo));

            getProfileView().displayPreviousContactSchedule(schedule);
        } catch (ParseException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    @Override
    public void loadPreviousContacts(String baseEntityId, String contactNo) {
        List<PreviousContactsSummaryModel> allContactsFacts = getPreviousContactRepository()
                .getPreviousContactsFacts(baseEntityId);
        LinkedHashMap<String, List<Facts>> filteredContacts = new LinkedHashMap<>();

        if (allContactsFacts != null && allContactsFacts.size() > 0 && !TextUtils.isEmpty(contactNo)) {
            Collections.reverse(allContactsFacts);
            for (PreviousContactsSummaryModel previousContactsSummaryModel : allContactsFacts) {
                String currentContactInLoop = previousContactsSummaryModel.getContactNumber();
                List<Facts> factsList = filteredContacts.get(currentContactInLoop);
                if (factsList != null && factsList.size() > 0) {
                    factsList.add(previousContactsSummaryModel.getVisitFacts());
                    filteredContacts.put(currentContactInLoop, factsList);
                } else {
                    List<Facts> newList = new ArrayList<>();
                    newList.add(previousContactsSummaryModel.getVisitFacts());
                    filteredContacts.put(currentContactInLoop, newList);
                }
            }
        }

        try {
            getProfileView().loadPreviousContactsDetails(filteredContacts);
        } catch (IOException |
                ParseException e) {
            e.printStackTrace();
        }

    }

    private PreviousContactRepository getPreviousContactRepository() {
        return AncApplication.getInstance().getPreviousContactRepository();
    }
}
