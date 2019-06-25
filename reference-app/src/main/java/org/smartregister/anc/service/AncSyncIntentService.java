package org.smartregister.anc.service;

import org.smartregister.anc.application.AncApplication;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.intent.SyncIntentService;

/**
 * Created by ndegwamartin on 11/02/2019.
 */
public class AncSyncIntentService extends SyncIntentService {

    @Override
    protected ClientProcessorForJava getClientProcessor() {
        return AncApplication.getInstance().getClientProcessorForJava();
    }
}
