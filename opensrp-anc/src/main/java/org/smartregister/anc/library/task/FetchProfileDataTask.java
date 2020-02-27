package org.smartregister.anc.library.task;

import android.os.AsyncTask;

import org.smartregister.anc.library.event.ClientDetailsFetchedEvent;
import org.smartregister.anc.library.repository.PatientRepository;
import org.smartregister.anc.library.util.Utils;

import java.util.Map;

/**
 * Created by ndegwamartin on 13/07/2018.
 */
public class FetchProfileDataTask extends AsyncTask<String, Integer, Map<String, String>> {

    private boolean isForEdit;

    public FetchProfileDataTask(boolean isForEdit) {
        this.isForEdit = isForEdit;
    }

    protected Map<String, String> doInBackground(String... params) {
        String baseEntityId = params[0];
        return PatientRepository.getWomanProfileDetails(baseEntityId);
    }

    protected void onPostExecute(Map<String, String> client) {
        Utils.postStickyEvent(new ClientDetailsFetchedEvent(client, isForEdit));
    }
}
