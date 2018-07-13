package org.smartregister.anc.task;

import android.os.AsyncTask;

import org.joda.time.LocalDate;
import org.joda.time.Weeks;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.smartregister.anc.contract.ProfileContract;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ndegwamartin on 13/07/2018.
 */
public class FetchProfileDataTask extends AsyncTask<Void, Integer, Map<String, String>> {

    private CommonPersonObjectClient commonPersonObjectClient;
    private ProfileContract.View view;
    private DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-d");

    public FetchProfileDataTask(ProfileContract.View view, CommonPersonObjectClient commonPersonObjectClient) {
        this.commonPersonObjectClient = commonPersonObjectClient;
        this.view = view;
    }

    protected Map<String, String> doInBackground(Void... parms) {
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put(DBConstants.KEY.FIRST_NAME, "Charity");
        stringMap.put(DBConstants.KEY.LAST_NAME, "Otala");
        stringMap.put(DBConstants.KEY.DOB, "1994-07-03");
        stringMap.put(DBConstants.KEY.EDD, "2019-02-21");
        stringMap.put(DBConstants.KEY.ANC_ID, "07564131");

        if (commonPersonObjectClient != null) {
            commonPersonObjectClient.toString();
        }

        //The rest of the code using newCarAsync
        return stringMap;
    }

    protected void onPostExecute(Map<String, String> client) {

        view.setProfileName(client.get(DBConstants.KEY.FIRST_NAME) + " " + client.get(DBConstants.KEY.LAST_NAME));
        view.setProfileAge(String.valueOf(getAgeFromDate(client.get(DBConstants.KEY.DOB))));
        view.setProfileGestationAge(String.valueOf(getGestationAgeFromDate(client.get(DBConstants.KEY.EDD))));
        view.setProfileID(client.get(DBConstants.KEY.ANC_ID));

    }

    private int getAgeFromDate(String dateOfBirth) {
        LocalDate date = formatter.parseLocalDate(dateOfBirth);
        Years age = Years.yearsBetween(date, LocalDate.now());
        return age.getYears();
    }

    private int getGestationAgeFromDate(String expectedDeliveryDate) {
        LocalDate date = formatter.parseLocalDate(expectedDeliveryDate);
        Weeks weeks = Weeks.weeksBetween(LocalDate.now(),date);
        return weeks.getWeeks();
    }
}
