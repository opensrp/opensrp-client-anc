package org.smartregister.anc.job;

import android.content.Intent;
import android.support.annotation.NonNull;

import org.smartregister.anc.util.Constants;
import org.smartregister.configurableviews.service.PullConfigurableViewsIntentService;

/**
 * Created by ndegwamartin on 06/09/2018.
 */
public class ViewConfigurationsServiceJob extends BaseJob {

    public static final String TAG = "ViewConfigurationsServiceJob";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Intent intent = new Intent(getContext(), PullConfigurableViewsIntentService.class);
        getContext().startService(intent);
        return params.getExtras().getBoolean(Constants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;
    }
}
