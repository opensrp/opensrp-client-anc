package org.smartregister.anc.job;

import android.content.Intent;
import android.support.annotation.NonNull;

import org.smartregister.anc.util.Constants;
import org.smartregister.service.ImageUploadSyncService;

/**
 * Created by ndegwamartin on 05/09/2018.
 */
public class ImageUploadServiceJob extends BaseJob {

    public static final String TAG = "ImageUploadServiceJob";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Intent intent = new Intent(getContext(), ImageUploadSyncService.class);
        getContext().startService(intent);
        return params.getExtras().getBoolean(Constants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;
    }
}
