package org.smartregister.anc.library;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusBuilder;
import org.greenrobot.eventbus.meta.SubscriberInfoIndex;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.anc.library.domain.YamlConfig;
import org.smartregister.anc.library.domain.YamlConfigItem;
import org.smartregister.anc.library.helper.AncRulesEngineHelper;
import org.smartregister.anc.library.helper.ECSyncHelper;
import org.smartregister.anc.library.repository.PartialContactRepository;
import org.smartregister.anc.library.repository.PreviousContactRepository;
import org.smartregister.anc.library.sync.BaseAncClientProcessorForJava;
import org.smartregister.anc.library.util.Constants;
import org.smartregister.anc.library.util.FilePath;
import org.smartregister.configurableviews.helper.JsonSpecHelper;
import org.smartregister.domain.Setting;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStreamReader;

import id.zelory.compressor.Compressor;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-02
 */

public class AncLibrary {

    private static final String TAG = AncLibrary.class.getCanonicalName();

    private static AncLibrary instance;
    private static JsonSpecHelper jsonSpecHelper;

    private final Context context;
    private final Repository repository;

    private PartialContactRepository partialContactRepository;
    private PreviousContactRepository previousContactRepository;
    private EventClientRepository eventClientRepository;
    private UniqueIdRepository uniqueIdRepository;
    private DetailsRepository detailsRepository;

    private ECSyncHelper ecSyncHelper;
    private AncRulesEngineHelper ancRulesEngineHelper;

    private ClientProcessorForJava clientProcessorForJava;
    private JSONObject defaultContactFormGlobals = new JSONObject();

    private Compressor compressor;
    private Gson gson;

    private Yaml yaml;

    private SubscriberInfoIndex subscriberInfoIndex;

    private int databaseVersion;

    public static void init(@NonNull Context context, @NonNull Repository repository, int dbVersion) {
        init(context, repository,  dbVersion, null);
    }

    public static void init(@NonNull Context context, @NonNull Repository repository, int dbVersion, @Nullable SubscriberInfoIndex subscriberInfoIndex) {
        if (instance == null) {
            instance = new AncLibrary(context, repository, dbVersion, subscriberInfoIndex);
        }
    }

    public static AncLibrary getInstance() {
        if (instance == null) {
            throw new IllegalStateException(" Instance does not exist!!! Call "
                    + AncLibrary.class.getName()
                    + ".init method in the onCreate method of "
                    + "your Application class ");
        }
        return instance;
    }

    public static JsonSpecHelper getJsonSpecHelper() {
        return getInstance().jsonSpecHelper;
    }

    private AncLibrary(@NonNull Context contextArg, @NonNull Repository repositoryArg, int dbVersion, @Nullable SubscriberInfoIndex subscriberInfoIndex) {
        this.context = contextArg;
        repository = repositoryArg;
        this.subscriberInfoIndex = subscriberInfoIndex;
        this.databaseVersion = dbVersion;

        //Initialize JsonSpec Helper
        this.jsonSpecHelper = new JsonSpecHelper(getApplicationContext());
        setUpEventHandling();

        //initialize configs processor
        initializeYamlConfigs();
    }

    public Repository getRepository() {
        return repository;
    }


    public PartialContactRepository getPartialContactRepository() {
        if (partialContactRepository == null) {
            partialContactRepository = new PartialContactRepository(getRepository());
        }

        return partialContactRepository;
    }

    public PreviousContactRepository getPreviousContactRepository() {
        if (previousContactRepository == null) {
            previousContactRepository = new PreviousContactRepository(getRepository());
        }

        return previousContactRepository;
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

    public AncRulesEngineHelper getAncRulesEngineHelper() {
        if (ancRulesEngineHelper == null) {
            ancRulesEngineHelper = new AncRulesEngineHelper(getApplicationContext());
        }
        return ancRulesEngineHelper;
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
            clientProcessorForJava = BaseAncClientProcessorForJava.getInstance(getApplicationContext());
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

    public Gson getGsonInstance() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    private void setUpEventHandling() {
        try {

            EventBusBuilder eventBusBuilder = EventBus.builder()
                    .addIndex(new org.smartregister.anc.library.ANCEventBusIndex());

            if (subscriberInfoIndex != null) {
                eventBusBuilder.addIndex(subscriberInfoIndex);
            }

            eventBusBuilder.installDefaultEventBus();

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }

    public void populateGlobalSettings() {

        Setting setting = getCharactersitics(Constants.PREF_KEY.SITE_CHARACTERISTICS);
        Setting populationSetting = getCharactersitics(Constants.PREF_KEY.POPULATION_CHARACTERISTICS);

        populateGlobalSettingsCore(setting);
        populateGlobalSettingsCore(populationSetting);
    }

    private void populateGlobalSettingsCore(Setting setting) {
        try {
            JSONObject settingObject = setting != null ? new JSONObject(setting.getValue()) : null;
            if (settingObject != null) {
                JSONArray settingArray = settingObject.getJSONArray(AllConstants.SETTINGS);
                if (settingArray != null) {

                    for (int i = 0; i < settingArray.length(); i++) {

                        JSONObject jsonObject = settingArray.getJSONObject(i);
                        Boolean value = jsonObject.optBoolean(JsonFormConstants.VALUE);
                        JSONObject nullObject = null;
                        if (value != null && !value.equals(nullObject)) {
                            defaultContactFormGlobals.put(jsonObject.getString(JsonFormConstants.KEY), value);
                        } else {

                            defaultContactFormGlobals.put(jsonObject.getString(JsonFormConstants.KEY), false);
                        }
                    }


                }
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public Setting getCharactersitics(String characteristics) {
        return AncLibrary.getInstance().getContext().allSettings().getSetting(characteristics);
    }

    public JSONObject getDefaultContactFormGlobals() {
        return defaultContactFormGlobals;
    }

    public Iterable<Object> readYaml(String filename) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(
                getApplicationContext().getAssets().open((FilePath.FOLDER.CONFIG_FOLDER_PATH + filename)));
        return yaml.loadAll(inputStreamReader);
    }

    private void initializeYamlConfigs() {
        Constructor constructor = new Constructor(YamlConfig.class);
        TypeDescription customTypeDescription = new TypeDescription(YamlConfig.class);
        customTypeDescription.addPropertyParameters(YamlConfigItem.FIELD_CONTACT_SUMMARY_ITEMS, YamlConfigItem.class);
        constructor.addTypeDescription(customTypeDescription);
        yaml = new Yaml(constructor);
    }

    public android.content.Context getApplicationContext() {
        return context.applicationContext();
    }

    public Context getContext() {
        return context;
    }

    public int getDatabaseVersion() {
        return databaseVersion;
    }
}