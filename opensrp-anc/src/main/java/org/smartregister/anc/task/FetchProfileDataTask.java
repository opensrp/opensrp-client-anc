package org.smartregister.anc.task;

import android.os.AsyncTask;

import org.joda.time.LocalDate;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.smartregister.anc.contract.ProfileContract;
import org.smartregister.anc.event.ClientDetailsFetchedEvent;
import org.smartregister.anc.repository.PatientRepository;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.Utils;

import java.util.Map;

/**
 * Created by ndegwamartin on 13/07/2018.
 */
public class FetchProfileDataTask extends AsyncTask<String, Integer, Map<String, String>> {

    private ProfileContract.View view;
    private DateTimeFormatter formatter = DateTimeFormat.forPattern(Constants.SQLITE_DATE_TIME_FORMAT);

    public FetchProfileDataTask(ProfileContract.View view) {
        this.view = view;
    }

    protected Map<String, String> doInBackground(String... params) {
        String baseEntityId = params[0];
        return PatientRepository.getWomanProfileDetails(baseEntityId);
    }

    protected void onPostExecute(Map<String, String> client) {

        if (view != null) {//null when we want to trigger a data fetch event only other wise bind to view

            view.setProfileName(client.get(DBConstants.KEY.FIRST_NAME) + " " + client.get(DBConstants.KEY.LAST_NAME));
            view.setProfileAge(String.valueOf(Utils.getAgeFromDate(client.get(DBConstants.KEY.DOB))));
            view.setProfileGestationAge(client.containsKey(DBConstants.KEY.EDD) ? String.valueOf(getGestationAgeFromDate(client.get(DBConstants.KEY.EDD))) : null);
            view.setProfileID(client.get(DBConstants.KEY.ANC_ID));
            view.setProfileImage(client.get(DBConstants.KEY.BASE_ENTITY_ID));
        } else {

//Just a data fetch so we broadcast
            Utils.postEvent(new ClientDetailsFetchedEvent(client));
        }
    }


    private int getGestationAgeFromDate(String expectedDeliveryDate) {
        LocalDate date = formatter.withOffsetParsed().parseLocalDate(expectedDeliveryDate);
        Weeks weeks = Weeks.weeksBetween(LocalDate.now(), date);
        return weeks.getWeeks();
    }
}
