package org.smartregister.anc.application;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.anc.BuildConfig;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.LoginActivity;
import org.smartregister.anc.event.TriggerSyncEvent;
import org.smartregister.anc.event.ViewConfigurationSyncCompleteEvent;
import org.smartregister.anc.helper.ECSyncHelper;
import org.smartregister.anc.receiver.AlarmReceiver;
import org.smartregister.anc.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.anc.repository.AncRepository;
import org.smartregister.anc.repository.UniqueIdRepository;
import org.smartregister.anc.service.intent.PullUniqueIdsIntentService;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.Utils;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.configurableviews.helper.ConfigurableViewsHelper;
import org.smartregister.configurableviews.helper.JsonSpecHelper;
import org.smartregister.configurableviews.repository.ConfigurableViewsRepository;
import org.smartregister.configurableviews.service.PullConfigurableViewsIntentService;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.DrishtiSyncScheduler;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.receiver.TimeChangedBroadcastReceiver;

import id.zelory.compressor.Compressor;

import static org.smartregister.util.Log.logError;
import static org.smartregister.util.Log.logInfo;

/**
 * Created by ndegwamartin on 21/06/2018.
 */
public class AncApplication extends DrishtiApplication implements TimeChangedBroadcastReceiver.OnTimeChangedListener {

    private static JsonSpecHelper jsonSpecHelper;

    private ConfigurableViewsRepository configurableViewsRepository;
    private EventClientRepository eventClientRepository;
    private static CommonFtsObject commonFtsObject;
    private ConfigurableViewsHelper configurableViewsHelper;
    private UniqueIdRepository uniqueIdRepository;

    private ECSyncHelper ecSyncHelper;
    private Compressor compressor;
    private ClientProcessorForJava clientProcessorForJava;

    private static final String TAG = AncApplication.class.getCanonicalName();
    private String password;

    @Override
    public void onCreate() {

        super.onCreate();

        mInstance = this;
        context = Context.getInstance();

        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject());

        //Initialize Modules
        CoreLibrary.init(context);
        ConfigurableViewsLibrary.init(context, getRepository());

        SyncStatusBroadcastReceiver.init(this);
        TimeChangedBroadcastReceiver.init(this);
        TimeChangedBroadcastReceiver.getInstance().addOnTimeChangedListener(this);

        startPullConfigurableViewsIntentService(getApplicationContext());
        try {
            Utils.saveLanguage("en");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        //Initialize JsonSpec Helper
        this.jsonSpecHelper = new JsonSpecHelper(this);

        setUpEventHandling();
    }

    public static synchronized AncApplication getInstance() {
        return (AncApplication) mInstance;
    }

    @Override
    public Repository getRepository() {
        try {
            if (repository == null) {
                repository = new AncRepository(getInstance().getApplicationContext(), context);
                getConfigurableViewsRepository();
            }
        } catch (UnsatisfiedLinkError e) {
            logError("Error on getRepository: " + e);

        }
        return repository;
    }

    public String getPassword() {
        if (password == null) {
            String username = getContext().userService().getAllSharedPreferences().fetchRegisteredANM();
            password = getContext().userService().getGroupId(username);
        }
        return password;
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

    public static JsonSpecHelper getJsonSpecHelper() {
        return getInstance().jsonSpecHelper;
    }

    public Context getContext() {
        return context;
    }

    protected void cleanUpSyncState() {
        try {
            DrishtiSyncScheduler.stop(getApplicationContext());
            context.allSharedPreferences().saveIsSyncInProgress(false);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onTerminate() {
        logInfo("Application is terminating. Stopping Sync scheduler and resetting isSyncInProgress setting.");
        cleanUpSyncState();
        TimeChangedBroadcastReceiver.destroy(this);
        super.onTerminate();
    }

    public void startPullConfigurableViewsIntentService(android.content.Context context) {
        Intent intent = new Intent(context, PullConfigurableViewsIntentService.class);
        context.startService(intent);
    }

    public static CommonFtsObject createCommonFtsObject() {
        if (commonFtsObject == null) {
            commonFtsObject = new CommonFtsObject(getFtsTables());
            for (String ftsTable : commonFtsObject.getTables()) {
                commonFtsObject.updateSearchFields(ftsTable, getFtsSearchFields());
                commonFtsObject.updateSortFields(ftsTable, getFtsSortFields());
            }
        }
        return commonFtsObject;
    }

    private static String[] getFtsTables() {
        return new String[]{DBConstants.WOMAN_TABLE_NAME};
    }

    private static String[] getFtsSearchFields() {
        return new String[]{DBConstants.KEY.BASE_ENTITY_ID, DBConstants.KEY.FIRST_NAME, DBConstants.KEY.LAST_NAME, DBConstants.KEY.ANC_ID, DBConstants.KEY.DATE_REMOVED, DBConstants.KEY.PHONE_NUMBER};

    }

    private static String[] getFtsSortFields() {
        return new String[]{DBConstants.KEY.BASE_ENTITY_ID, DBConstants.KEY.FIRST_NAME, DBConstants.KEY.LAST_NAME, DBConstants.KEY.LAST_INTERACTED_WITH, DBConstants.KEY.DATE_REMOVED};
    }

    public ConfigurableViewsRepository getConfigurableViewsRepository() {
        if (configurableViewsRepository == null)
            configurableViewsRepository = new ConfigurableViewsRepository(getRepository());
        return configurableViewsRepository;
    }

    public EventClientRepository getEventClientRepository() {
        if (eventClientRepository == null) {
            eventClientRepository = new EventClientRepository(getRepository());
        }
        return eventClientRepository;
    }

    public UniqueIdRepository getUniqueIdRepository() {
        if (uniqueIdRepository == null) {
            uniqueIdRepository = new UniqueIdRepository((AncRepository) getRepository());
        }
        return uniqueIdRepository;
    }

    public ConfigurableViewsHelper getConfigurableViewsHelper() {
        if (configurableViewsHelper == null) {
            configurableViewsHelper = new ConfigurableViewsHelper(getConfigurableViewsRepository(),
                    getJsonSpecHelper(), getApplicationContext());
        }
        return configurableViewsHelper;
    }

    public ECSyncHelper getEcSyncHelper() {
        if (ecSyncHelper == null) {
            ecSyncHelper = ECSyncHelper.getInstance(getApplicationContext());
        }
        return ecSyncHelper;
    }

    public Compressor getCompressor() {
        if (compressor == null) {
            compressor = Compressor.getDefault(getApplicationContext());
        }
        return compressor;
    }

    public ClientProcessorForJava getClientProcessorForJava() {
        if (clientProcessorForJava == null) {
            clientProcessorForJava = ClientProcessorForJava.getInstance(getApplicationContext());
        }
        return clientProcessorForJava;
    }

    private void setUpEventHandling() {

        try {

            EventBus.builder().addIndex(new org.smartregister.anc.ANCEventBusIndex()).installDefaultEventBus();
            LocalBroadcastManager.getInstance(this).registerReceiver(syncCompleteMessageReceiver, new IntentFilter(PullConfigurableViewsIntentService.EVENT_SYNC_COMPLETE));

        } catch
                (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        EventBus.getDefault().register(this);


    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void triggerSync(TriggerSyncEvent event) {
        if (event != null) {
            startPullConfigurableViewsIntentService(this);
            //startSyncService(); call to trigger sync
        }

    }


    // This Broadcast Receiver is the handler called whenever an Intent with an action named PullConfigurableViewsIntentService.EVENT_SYNC_COMPLETE is broadcast.
    private BroadcastReceiver syncCompleteMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(android.content.Context context, Intent intent) {
            // Retrieve the extra data included in the Intent

            int recordsRetrievedCount = intent.getIntExtra(org.smartregister.configurableviews.util.Constants.INTENT_KEY.SYNC_TOTAL_RECORDS, 0);
            if (recordsRetrievedCount > 0) {
                Log.d(TAG, "Total records retrieved " + recordsRetrievedCount);
            }

            Utils.postEvent(new ViewConfigurationSyncCompleteEvent());

            String lastSyncTime = intent.getStringExtra(org.smartregister.configurableviews.util.Constants.INTENT_KEY.LAST_SYNC_TIME_STRING);

            Utils.writePrefString(context, org.smartregister.configurableviews.util.Constants.INTENT_KEY.LAST_SYNC_TIME_STRING, lastSyncTime);

        }
    };


    public void startPullUniqueIdsService() {
        Intent intent = new Intent(getApplicationContext(), PullUniqueIdsIntentService.class);
        getApplicationContext().startService(intent);
    }

    public static void setAlarms(android.content.Context context) {

        AlarmReceiver.setAlarm(context, BuildConfig.IMAGE_UPLOAD_MINUTES, Constants.ServiceType.IMAGE_UPLOAD);
        AlarmReceiver.setAlarm(context, BuildConfig.PULL_UNIQUE_IDS_MINUTES, Constants.ServiceType.PULL_UNIQUE_IDS);
        AlarmReceiver.setAlarm(context, BuildConfig.AUTO_SYNC_DURATION, Constants.ServiceType.AUTO_SYNC);
        AlarmReceiver.setAlarm(context, BuildConfig.SYNC_VIEW_CONFIGURATIONS_MINUTES, Constants.ServiceType.PULL_VIEW_CONFIGURATIONS);
    }

    @Override
    public void onTimeChanged() {
        Utils.showToast(this, this.getString(R.string.device_time_changed));
        context.userService().forceRemoteLogin();
        logoutCurrentUser();
    }

    @Override
    public void onTimeZoneChanged() {
        Utils.showToast(this, this.getString(R.string.device_timezone_changed));
        context.userService().forceRemoteLogin();
        logoutCurrentUser();
    }

}
