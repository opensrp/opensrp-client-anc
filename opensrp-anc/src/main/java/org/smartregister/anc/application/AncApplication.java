package org.smartregister.anc.application;

import android.content.Intent;
import android.util.Log;

import com.evernote.android.job.JobManager;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.LoginActivity;
import org.smartregister.anc.helper.ECSyncHelper;
import org.smartregister.anc.helper.RulesEngineHelper;
import org.smartregister.anc.job.AncJobCreator;
import org.smartregister.anc.repository.AncRepository;
import org.smartregister.anc.repository.PartialContactRepository;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.Utils;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.configurableviews.helper.JsonSpecHelper;
import org.smartregister.domain.Setting;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;
import org.smartregister.repository.UniqueIdRepository;
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

    private static final String TAG = AncApplication.class.getCanonicalName();
    private static JsonSpecHelper jsonSpecHelper;
    private static CommonFtsObject commonFtsObject;
    private EventClientRepository eventClientRepository;
    private UniqueIdRepository uniqueIdRepository;
    private DetailsRepository detailsRepository;
    private ECSyncHelper ecSyncHelper;
    private Compressor compressor;
    private ClientProcessorForJava clientProcessorForJava;
    private String password;
    private PartialContactRepository partialContactRepository;
    private RulesEngineHelper rulesEngineHelper;
    private JSONObject defaultContactFormGlobals = new JSONObject();

    public static synchronized AncApplication getInstance() {
        return (AncApplication) mInstance;
    }

    public static JsonSpecHelper getJsonSpecHelper() {
        return getInstance().jsonSpecHelper;
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
        return new String[]{DBConstants.KEY.BASE_ENTITY_ID, DBConstants.KEY.FIRST_NAME, DBConstants.KEY.LAST_NAME, DBConstants.KEY.ANC_ID,
                DBConstants.KEY.DATE_REMOVED, DBConstants.KEY.PHONE_NUMBER};

    }

    private static String[] getFtsSortFields() {
        return new String[]{DBConstants.KEY.BASE_ENTITY_ID, DBConstants.KEY.FIRST_NAME, DBConstants.KEY.LAST_NAME, DBConstants.KEY
                .LAST_INTERACTED_WITH, DBConstants.KEY.DATE_REMOVED};
    }

    @Override
    public void onCreate() {

        super.onCreate();

        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject());

        //Initialize Modules
        CoreLibrary.init(context, new AncSyncConfiguration());
        ConfigurableViewsLibrary.init(context, getRepository());

        SyncStatusBroadcastReceiver.init(this);
        TimeChangedBroadcastReceiver.init(this);
        TimeChangedBroadcastReceiver.getInstance().addOnTimeChangedListener(this);
        LocationHelper.init(Utils.ALLOWED_LEVELS, Utils.DEFAULT_LOCATION_LEVEL);

        try {
            Utils.saveLanguage("en");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        //Initialize JsonSpec Helper
        this.jsonSpecHelper = new JsonSpecHelper(this);

        setUpEventHandling();

        //init Job Manager
        JobManager.create(this).addJobCreator(new AncJobCreator());
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

    public PartialContactRepository getPartialContactRepository() {
        if (partialContactRepository == null)
            partialContactRepository = new PartialContactRepository(getRepository());
        return partialContactRepository;
    }

    public EventClientRepository getEventClientRepository() {
        if (eventClientRepository == null) {
            eventClientRepository = new EventClientRepository(getRepository());
        }
        return eventClientRepository;
    }

    public UniqueIdRepository getUniqueIdRepository() {
        if (uniqueIdRepository == null) {
            uniqueIdRepository = new UniqueIdRepository(getRepository());
        }
        return uniqueIdRepository;
    }

    public RulesEngineHelper getRulesEngineHelper() {
        if (rulesEngineHelper == null) {
            rulesEngineHelper = new RulesEngineHelper(getApplicationContext());
        }
        return rulesEngineHelper;
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

    public DetailsRepository getDetailsRepository() {
        if (detailsRepository == null) {
            detailsRepository = new DetailsRepository();
            detailsRepository.updateMasterRepository(getRepository());
        }
        return detailsRepository;
    }

    private void setUpEventHandling() {

        try {

            EventBus.builder().addIndex(new org.smartregister.anc.ANCEventBusIndex()).installDefaultEventBus();

        } catch
                (Exception e) {
            Log.e(TAG, e.getMessage());
        }

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

    public void populateGlobalSettings() {

        Setting setting = getCharactersitics(Constants.PREF_KEY.SITE_CHARACTERISTICS);
        Setting populationSetting = getCharactersitics(Constants.PREF_KEY.POPULATION_CHARACTERISTICS);

        populateGlobalSettingsCore(setting);
        populateGlobalSettingsCore(populationSetting);


    }

    private void populateGlobalSettingsCore(Setting setting) {
        try {
            JSONArray settingArray = setting != null ? new JSONArray(setting.getValue()) : null;

            if (settingArray != null) {

                for (int i = 0; i < settingArray.length(); i++) {

                    JSONObject jsonObject = settingArray.getJSONObject(i);
                    Object value = jsonObject.get(JsonFormConstants.VALUE);
                    JSONObject nullObject = null;
                    if (value != null && !value.equals(nullObject)) {
                        defaultContactFormGlobals.put(jsonObject.getString(JsonFormConstants.KEY), jsonObject.getString(JsonFormConstants.VALUE));
                    }
                }


            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public Setting getCharactersitics(String characteristics) {
        return AncApplication.getInstance().getContext().allSettings().getSetting(characteristics);
    }


    public JSONObject getDefaultContactFormGlobals() {
        return defaultContactFormGlobals;
    }

}
