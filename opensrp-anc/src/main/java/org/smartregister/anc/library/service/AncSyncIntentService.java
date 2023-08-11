package org.smartregister.anc.library.service;

import org.smartregister.sync.intent.SyncIntentService;

public class AncSyncIntentService  extends SyncIntentService {

    @Override
    protected void handleSync() {
        doSync();
    }
}
