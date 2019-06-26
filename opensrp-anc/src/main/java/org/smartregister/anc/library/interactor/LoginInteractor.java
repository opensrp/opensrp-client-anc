package org.smartregister.anc.library.interactor;

import org.smartregister.anc.library.BuildConfig;
import org.smartregister.anc.library.application.BaseAncApplication;
import org.smartregister.domain.LoginResponse;
import org.smartregister.job.ImageUploadServiceJob;
import org.smartregister.job.PullUniqueIdsServiceJob;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.job.SyncSettingsServiceJob;
import org.smartregister.login.interactor.BaseLoginInteractor;
import org.smartregister.view.contract.BaseLoginContract;

import java.util.concurrent.TimeUnit;

/**
 * Created by ndegwamartin on 26/06/2018.
 */
public class LoginInteractor extends BaseLoginInteractor implements BaseLoginContract.Interactor {

    public static final String TAG = LoginInteractor.class.getCanonicalName();

    public LoginInteractor(BaseLoginContract.Presenter loginPresenter) {
        super(loginPresenter);
    }

    @Override
    protected void scheduleJobsPeriodically() {
        SyncServiceJob.scheduleJob(SyncServiceJob.TAG, TimeUnit.MINUTES.toMillis(BuildConfig.DATA_SYNC_DURATION_MINUTES),
                getFlexValue(BuildConfig.DATA_SYNC_DURATION_MINUTES));

        PullUniqueIdsServiceJob
                .scheduleJob(SyncServiceJob.TAG, TimeUnit.MINUTES.toMillis(BuildConfig.PULL_UNIQUE_IDS_MINUTES),
                        getFlexValue(BuildConfig.PULL_UNIQUE_IDS_MINUTES));

        ImageUploadServiceJob
                .scheduleJob(ImageUploadServiceJob.TAG, TimeUnit.MINUTES.toMillis(BuildConfig.IMAGE_UPLOAD_MINUTES),
                        getFlexValue(BuildConfig.IMAGE_UPLOAD_MINUTES));

        SyncSettingsServiceJob
                .scheduleJob(SyncSettingsServiceJob.TAG, TimeUnit.MINUTES.toMillis(BuildConfig.CLIENT_SETTINGS_SYNC_MINUTES),
                        getFlexValue(BuildConfig.CLIENT_SETTINGS_SYNC_MINUTES));
    }

    @Override
    protected void scheduleJobsImmediately() {
        super.scheduleJobsImmediately();
    }

    @Override
    protected void processServerSettings(LoginResponse loginResponse) {

        super.processServerSettings(loginResponse);
        BaseAncApplication.getInstance().populateGlobalSettings();
    }


}
