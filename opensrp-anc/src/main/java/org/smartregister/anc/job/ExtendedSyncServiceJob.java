package org.smartregister.anc.job;

import android.content.Intent;
import android.support.annotation.NonNull;

import org.smartregister.anc.service.intent.ExtendedSyncIntentService;
import org.smartregister.anc.util.Constants;

/**
 * Created by ndegwamartin on 05/09/2018.
 */
public class ExtendedSyncServiceJob extends BaseJob {

    public static final String TAG = "ExtendedSyncServiceJob";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Intent intent = new Intent(getApplicationContext(), ExtendedSyncIntentService.class);
        getApplicationContext().startService(intent);
        return params != null && params.getExtras().getBoolean(Constants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;
    }
}
