package org.smartregister.anc.library.job;


import android.content.Intent;

import androidx.annotation.NonNull;

import org.smartregister.anc.library.service.AncSettingsSyncIntentService;
import org.smartregister.job.BaseJob;

public class AncSyncSettingsServiceJob extends BaseJob {

    public static final String TAG = "AncSyncSettingsServiceJob";

    public AncSyncSettingsServiceJob() {
    }

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Intent intent = new Intent(getApplicationContext(), AncSettingsSyncIntentService.class);
        startIntentService(intent);
        return params != null && params.getExtras().getBoolean("to_reschedule", false) ? Result.RESCHEDULE : Result.SUCCESS;
    }
}
