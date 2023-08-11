package org.smartregister.anc.library.service;

import android.content.Intent;

import org.smartregister.domain.FetchStatus;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.sync.intent.SettingsSyncIntentService;

public class AncSettingsSyncIntentService extends SettingsSyncIntentService {

    @Override
    protected void onHandleIntent(Intent intent) {
        sendBroadcastSyncStarted();
        boolean isSuccessfulSync = processSettings(intent);
        if (isSuccessfulSync) {
            SyncServiceJob.scheduleJobImmediately("SyncServiceJob");
        }
    }

    private void sendBroadcastSyncStarted() {
        Intent intent = new Intent();
        intent.setAction("sync_status");
        intent.putExtra("fetch_status", FetchStatus.fetchStarted);
        sendBroadcast(intent);
    }
}
