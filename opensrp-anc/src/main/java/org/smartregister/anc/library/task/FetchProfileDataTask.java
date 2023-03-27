package org.smartregister.anc.library.task;

import org.smartregister.anc.library.event.ClientDetailsFetchedEvent;
import org.smartregister.anc.library.repository.PatientRepository;
import org.smartregister.anc.library.util.AppExecutors;
import org.smartregister.anc.library.util.Utils;

import java.util.Map;

/**
 * Created by ndegwamartin on 13/07/2018.
 */
public class FetchProfileDataTask {

    private boolean isForEdit;
    private AppExecutors appExecutors;

    public FetchProfileDataTask(boolean isForEdit) {
        this.isForEdit = isForEdit;
    }

    public void execute(String baseEntityId) {
        appExecutors = new AppExecutors();
        appExecutors.diskIO().execute(() -> {
            Map<String, String> client = this.getWomanDetailsOnBackground(baseEntityId);
            appExecutors.mainThread().execute(() -> postStickEventOnPostExec(client));
        });

    }

    private Map<String, String> getWomanDetailsOnBackground(String baseEntityId) {
        return PatientRepository.getWomanProfileDetails(baseEntityId);
    }

    protected void postStickEventOnPostExec(Map<String, String> client) {
        Utils.postStickyEvent(new ClientDetailsFetchedEvent(client, isForEdit));
    }
}
