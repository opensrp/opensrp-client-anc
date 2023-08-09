package org.smartregister.anc.job;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

import org.smartregister.anc.library.job.AncSyncSettingsServiceJob;
import org.smartregister.anc.library.service.AncSyncIntentService;
import org.smartregister.job.DocumentConfigurationServiceJob;
import org.smartregister.job.ExtendedSyncServiceJob;
import org.smartregister.job.ImageUploadServiceJob;
import org.smartregister.job.P2pServiceJob;
import org.smartregister.job.PullUniqueIdsServiceJob;
import org.smartregister.job.SyncAllLocationsServiceJob;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.job.ValidateSyncDataServiceJob;
import org.smartregister.sync.intent.DocumentConfigurationIntentService;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 05/09/2018.
 */
public class AncJobCreator implements JobCreator {
    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        switch (tag) {
            case SyncServiceJob.TAG:
                return new SyncServiceJob(AncSyncIntentService.class);
            case ExtendedSyncServiceJob.TAG:
                return new ExtendedSyncServiceJob();
            case ImageUploadServiceJob.TAG:
                return new ImageUploadServiceJob();
            case PullUniqueIdsServiceJob.TAG:
                return new PullUniqueIdsServiceJob();
            case ValidateSyncDataServiceJob.TAG:
                return new ValidateSyncDataServiceJob();
            case ViewConfigurationsServiceJob.TAG:
                return new ViewConfigurationsServiceJob();
            case AncSyncSettingsServiceJob.TAG:
                return new AncSyncSettingsServiceJob();
            case DocumentConfigurationServiceJob.TAG:
                return new DocumentConfigurationServiceJob(DocumentConfigurationIntentService.class);
            case P2pServiceJob.TAG:
                return new P2pServiceJob();
            case SyncAllLocationsServiceJob.TAG:
                return new SyncAllLocationsServiceJob();
            default:
                Timber.d("Looks like you tried to create a job " + tag + " that is not declared in the Anc Job Creator");
                return null;
        }
    }
}
