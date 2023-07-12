package org.smartregister.anc.application;

import static org.smartregister.util.Log.logError;
import static org.smartregister.util.Log.logInfo;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.evernote.android.job.JobManager;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.vijay.jsonwizard.NativeFormLibrary;

import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.P2POptions;
import org.smartregister.anc.ANCEventBusIndex;
import org.smartregister.anc.BuildConfig;
import org.smartregister.anc.activity.LoginActivity;
import org.smartregister.anc.library.AppConfig;
import org.smartregister.anc.job.AncJobCreator;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.auth.AncCoreAuthorizationService;
import org.smartregister.anc.library.sync.BaseAncClientProcessorForJava;
import org.smartregister.anc.library.util.ANCFailSafeRecalledID;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.anc.repository.AncRepository;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.Repository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.DrishtiSyncScheduler;
import org.smartregister.util.LangUtils;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.receiver.TimeChangedBroadcastReceiver;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 21/06/2018.
 */
public class AncApplication extends DrishtiApplication implements TimeChangedBroadcastReceiver.OnTimeChangedListener {
    private static CommonFtsObject commonFtsObject;

    public static CommonFtsObject createCommonFtsObject() {
        if (commonFtsObject == null) {
            commonFtsObject = new CommonFtsObject(getFtsTables());
            for (String ftsTable : commonFtsObject.getTables()) {
                commonFtsObject.updateSearchFields(ftsTable, getFtsSearchFields(ftsTable));
                commonFtsObject.updateSortFields(ftsTable, getFtsSortFields(ftsTable));
            }
        }
        return commonFtsObject;
    }

    private static String[] getFtsTables() {
        return new String[]{DBConstantsUtils.DEMOGRAPHIC_TABLE_NAME, DBConstantsUtils.WOMAN_DETAILS_TABLE_NAME};
    }

    private static String[] getFtsSearchFields(String tableName) {
        if (tableName.equals(DBConstantsUtils.DEMOGRAPHIC_TABLE_NAME)) {
            return new String[]{DBConstantsUtils.KeyUtils.FIRST_NAME, DBConstantsUtils.KeyUtils.LAST_NAME, DBConstantsUtils.KeyUtils.ANC_ID};
        } else if (tableName.equals(DBConstantsUtils.WOMAN_DETAILS_TABLE_NAME)) {
            return new String[]{DBConstantsUtils.KeyUtils.NEXT_CONTACT};
        } else {
            return null;
        }

    }

    private static String[] getFtsSortFields(String tableName) {
        if (tableName.equals(DBConstantsUtils.DEMOGRAPHIC_TABLE_NAME)) {
            return new String[]{DBConstantsUtils.KeyUtils.BASE_ENTITY_ID, DBConstantsUtils.KeyUtils.FIRST_NAME, DBConstantsUtils.KeyUtils.LAST_NAME,
                    DBConstantsUtils.KeyUtils.LAST_INTERACTED_WITH, DBConstantsUtils.KeyUtils.DATE_REMOVED};
        } else if (tableName.equals(DBConstantsUtils.WOMAN_DETAILS_TABLE_NAME)) {
            return new String[]{DBConstantsUtils.KeyUtils.NEXT_CONTACT};
        } else {
            return null;
        }
    }

    public static synchronized AncApplication getInstance() {
        return (AncApplication) DrishtiApplication.mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject());

        //Initialize Modules
        P2POptions p2POptions = new P2POptions(true);
        p2POptions.setAuthorizationService(new AncCoreAuthorizationService());
        ANCFailSafeRecalledID recalledID = new ANCFailSafeRecalledID();
        p2POptions.setRecalledIdentifier(recalledID);
        CoreLibrary.init(context, new AncSyncConfiguration(), BuildConfig.BUILD_TIMESTAMP, p2POptions);
        AncLibrary.init(context, BuildConfig.DATABASE_VERSION, new ANCEventBusIndex());
        ConfigurableViewsLibrary.init(context);

        SyncStatusBroadcastReceiver.init(this);
        TimeChangedBroadcastReceiver.init(this);
        TimeChangedBroadcastReceiver.getInstance().addOnTimeChangedListener(this);
        LocationHelper.init(Utils.ALLOWED_LEVELS, Utils.DEFAULT_LOCATION_LEVEL);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);


        //init Job Manager
        JobManager.create(this).addJobCreator(new AncJobCreator());

        // Set default language
        setDefaultLanguage();

        //Only integrate Flurry Analytics for  production. Remove negation to test in debug
        if (!BuildConfig.DEBUG) {
            new FlurryAgent.Builder()
                    .withLogEnabled(true)
                    .withCaptureUncaughtExceptions(true)
                    .withContinueSessionMillis(10000)
                    .withLogLevel(Log.VERBOSE)
                    .build(this, BuildConfig.FLURRY_API_KEY);
        }
        NativeFormLibrary
                .getInstance()
                .setClientFormDao(CoreLibrary.getInstance().context().getClientFormRepository());

    }

    private void setDefaultLanguage() {
        try {
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
            String current = allSharedPreferences.getPreferences().getString(AllConstants.LANGUAGE_PREFERENCE_KEY,null);
            if(current==null) {
                Utils.saveLanguage(AppConfig.DefaultLocale.getLanguage());
                current = AppConfig.DefaultLocale.getLanguage();
            }
            else Utils.saveLanguage(current);
            LangUtils.setAppLocale(this,current);
            AncLibrary.getInstance().notifyAppContextChange();
        } catch (Exception e) {
            Timber.e(e, " --> saveLanguage");
        }
    }

    @Override
    public void logoutCurrentUser() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getApplicationContext().startActivity(intent);
        context.userService().logoutSession();
    }

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public Repository getRepository() {
        try {
            if (repository == null) {
                repository = new AncRepository(getInstance().getApplicationContext(), context);
            }
        } catch (UnsatisfiedLinkError e) {
            logError("Error on getRepository: " + e);

        }
        return repository;
    }

    @NonNull
    @Override
    public ClientProcessorForJava getClientProcessor() {
        return BaseAncClientProcessorForJava.getInstance(this);
    }

    @Override
    public void onTerminate() {
        logInfo("Application is terminating. Stopping Sync scheduler and resetting isSyncInProgress setting.");
        cleanUpSyncState();
        TimeChangedBroadcastReceiver.destroy(this);
        super.onTerminate();
    }

    protected void cleanUpSyncState() {
        try {
            DrishtiSyncScheduler.stop(getApplicationContext());
            context.allSharedPreferences().saveIsSyncInProgress(false);
        } catch (Exception e) {
            Timber.e(e, " --> cleanUpSyncState");
        }
    }

    public Context getContext() {
        return context;
    }

    @Override
    public void onTimeChanged() {
        Utils.showToast(this, this.getString(org.smartregister.anc.library.R.string.device_time_changed));
        context.userService().getAllSharedPreferences().saveForceRemoteLogin(false, context.allSharedPreferences().fetchRegisteredANM());
        logoutCurrentUser();
    }

    @Override
    public void onTimeZoneChanged() {
        Utils.showToast(this, this.getString(org.smartregister.anc.library.R.string.device_timezone_changed));
        context.userService().getAllSharedPreferences().saveForceRemoteLogin(false, context.allSharedPreferences().fetchRegisteredANM());
        logoutCurrentUser();
    }
}
