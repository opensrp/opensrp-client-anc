package org.smartregister.anc.task;

import android.os.AsyncTask;

import org.smartregister.anc.application.AncApplication;

/**
 * Created by ndegwamartin on 26/06/2018.
 */

public class SaveTeamLocationsTask extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {
        AncApplication.getLocationHelper().locationIdsFromHierarchy();
        return null;
    }
}