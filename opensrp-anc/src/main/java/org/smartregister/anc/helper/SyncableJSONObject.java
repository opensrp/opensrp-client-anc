package org.smartregister.anc.helper;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.util.Utils;
import org.smartregister.repository.AllSharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by ndegwamartin on 20/09/2018.
 */
public class SyncableJSONObject extends JSONObject {

    public SyncableJSONObject() {

        try {

            AllSharedPreferences allSharedPreferences = AncApplication.getInstance().getContext().allSharedPreferences();

            String providerId = allSharedPreferences.fetchRegisteredANM();

            put("providerId", providerId);
            put("locationId", allSharedPreferences.fetchDefaultLocalityId(providerId));
            put("teamId", allSharedPreferences.fetchDefaultTeam(providerId));
            put("teamId", allSharedPreferences.fetchDefaultTeamId(providerId));
            put("dateCreated", Utils.convertDateFormat(Calendar.getInstance().getTime(), new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")));

        } catch (JSONException e) {

        }
    }
}
