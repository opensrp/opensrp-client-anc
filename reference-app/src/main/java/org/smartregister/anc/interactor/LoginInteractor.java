package org.smartregister.anc.interactor;

import android.content.Context;

import androidx.work.Data;

import org.smartregister.CoreLibrary;
import org.smartregister.anc.BuildConfig;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.domain.LoginResponse;
import org.smartregister.login.interactor.BaseLoginInteractor;
import org.smartregister.sync.wm.worker.DocumentConfigurationWorker;
import org.smartregister.sync.wm.worker.ImageUploadWorkerWorker;
import org.smartregister.sync.wm.worker.P2pProcessRecordsWorker;
import org.smartregister.sync.wm.worker.PullUniqueIdsWorker;
import org.smartregister.sync.wm.worker.SettingsSyncWorker;
import org.smartregister.sync.wm.worker.SyncAllLocationsWorker;
import org.smartregister.sync.wm.worker.SyncWorker;
import org.smartregister.sync.wm.workerrequest.WorkRequest;
import org.smartregister.view.contract.BaseLoginContract;

import java.util.concurrent.TimeUnit;

/**
 * Created by ndegwamartin on 26/06/2018.
 */
public class LoginInteractor extends BaseLoginInteractor implements BaseLoginContract.Interactor {
    public LoginInteractor(BaseLoginContract.Presenter loginPresenter) {
        super(loginPresenter);
    }

    @Override
    protected void scheduleJobsPeriodically() {
        Context applicationContext = CoreLibrary.getInstance().context().applicationContext();
        WorkRequest.runPeriodically(applicationContext, SyncWorker.class, SyncWorker.TAG,
                BuildConfig.DATA_SYNC_DURATION_MINUTES, getFlexValue(BuildConfig.DATA_SYNC_DURATION_MINUTES),
                TimeUnit.MINUTES, Data.EMPTY);
        WorkRequest.runPeriodically(applicationContext, PullUniqueIdsWorker.class, PullUniqueIdsWorker.TAG,
                BuildConfig.PULL_UNIQUE_IDS_MINUTES, getFlexValue(BuildConfig.PULL_UNIQUE_IDS_MINUTES),
                TimeUnit.MINUTES, Data.EMPTY);
        WorkRequest.runPeriodically(applicationContext, ImageUploadWorkerWorker.class, ImageUploadWorkerWorker.TAG,
                BuildConfig.IMAGE_UPLOAD_MINUTES, getFlexValue(BuildConfig.IMAGE_UPLOAD_MINUTES),
                TimeUnit.MINUTES, Data.EMPTY);
        WorkRequest.runPeriodically(applicationContext, SettingsSyncWorker.class, SettingsSyncWorker.TAG,
                BuildConfig.CLIENT_SETTINGS_SYNC_MINUTES, getFlexValue(BuildConfig.CLIENT_SETTINGS_SYNC_MINUTES),
                TimeUnit.MINUTES, Data.EMPTY);
        WorkRequest.runPeriodically(applicationContext, DocumentConfigurationWorker.class, DocumentConfigurationWorker.TAG,
                BuildConfig.CLIENT_SETTINGS_SYNC_MINUTES, getFlexValue(BuildConfig.CLIENT_SETTINGS_SYNC_MINUTES),
                TimeUnit.MINUTES, Data.EMPTY);
        WorkRequest.runPeriodically(applicationContext, P2pProcessRecordsWorker.class, P2pProcessRecordsWorker.TAG,
                BuildConfig.CLIENT_SETTINGS_SYNC_MINUTES, getFlexValue(BuildConfig.CLIENT_SETTINGS_SYNC_MINUTES),
                TimeUnit.MINUTES, Data.EMPTY);
    }

    @Override
    protected void scheduleJobsImmediately() {
        super.scheduleJobsImmediately();
        Context applicationContext = CoreLibrary.getInstance().context().applicationContext();
        WorkRequest.runImmediately(applicationContext, DocumentConfigurationWorker.class, DocumentConfigurationWorker.TAG, Data.EMPTY);
        WorkRequest.runImmediately(applicationContext, SyncAllLocationsWorker.class, SyncAllLocationsWorker.TAG, Data.EMPTY);
    }

    @Override
    protected void processServerSettings(LoginResponse loginResponse) {
        super.processServerSettings(loginResponse);
        AncLibrary.getInstance().populateGlobalSettings();
    }


}
