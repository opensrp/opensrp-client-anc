package org.smartregister.anc.job;

import android.content.Intent;
import android.support.annotation.NonNull;

import org.smartregister.anc.service.intent.SettingsSyncIntentService;
import org.smartregister.anc.util.Constants;
import org.smartregister.job.BaseJob;

/**
 * Created by ndegwamartin on 11/09/2018.
 */
public class SyncSettingsServiceJob extends BaseJob {

    public static final String TAG = "SyncSettingsServiceJob";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Intent intent = new Intent(getApplicationContext(), SettingsSyncIntentService.class);
        getApplicationContext().startService(intent);
        return params != null && params.getExtras().getBoolean(Constants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;

    }
}
