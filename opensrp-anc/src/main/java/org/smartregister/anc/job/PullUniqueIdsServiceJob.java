package org.smartregister.anc.job;

import android.content.Intent;
import android.support.annotation.NonNull;

import org.smartregister.anc.service.intent.PullUniqueIdsIntentService;
import org.smartregister.anc.util.Constants;

/**
 * Created by ndegwamartin on 06/09/2018.
 */
public class PullUniqueIdsServiceJob extends BaseJob {

    public static final String TAG = "PullUniqueIdsServiceJob";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Intent intent = new Intent(getApplicationContext(), PullUniqueIdsIntentService.class);
        getApplicationContext().startService(intent);
        return params != null && params.getExtras().getBoolean(Constants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;
    }
}
