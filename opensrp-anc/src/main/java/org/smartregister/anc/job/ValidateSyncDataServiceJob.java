package org.smartregister.anc.job;

import android.content.Intent;
import android.support.annotation.NonNull;

import org.smartregister.anc.service.intent.ValidateIntentService;
import org.smartregister.anc.util.Constants;

/**
 * Created by ndegwamartin on 06/09/2018.
 */
public class ValidateSyncDataServiceJob extends BaseJob {

    public static final String TAG = "ValidateSyncDataServiceJob";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Intent intent = new Intent(getApplicationContext(), ValidateIntentService.class);
        getApplicationContext().startService(intent);
        return params != null && params.getExtras().getBoolean(Constants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;
    }
}
