package org.smartregister.anc.application;

import org.smartregister.SyncConfiguration;
import org.smartregister.SyncFilter;
import org.smartregister.anc.BuildConfig;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.repository.AllSharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuelgithengi on 10/19/18.
 */
public class AncSyncConfiguration extends SyncConfiguration {
    @Override
    public int getSyncMaxRetries() {
        return BuildConfig.MAX_SYNC_RETRIES;
    }

    @Override
    public SyncFilter getSyncFilterParam() {
        return SyncFilter.TEAM_ID;
    }

    @Override
    public SyncFilter getSettingsSyncFilterParam() {
        return super.getSettingsSyncFilterParam();
    }

    @Override
    public boolean resolveSettings() {
        return BuildConfig.RESOLVE_SETTINGS;
    }

    @Override
    public boolean hasGlobalSettings() {
        return BuildConfig.HAS_GLOBAL_SETTINGS;
    }

    @Override
    public boolean hasExtraSettingsSync() {
        return BuildConfig.HAS_SETTINGS_SYNC;
    }

    @Override
    public List<String> getExtraSettingsParameters() {
        AllSharedPreferences sharedPreferences = AncLibrary.getInstance().getContext().userService().getAllSharedPreferences();
        String providerId = sharedPreferences.fetchRegisteredANM();

        List<String> params = new ArrayList<>();
        params.add(ConstantsUtils.SettingsSyncParams.LOCATION_ID + "=" + sharedPreferences.fetchDefaultLocalityId(providerId));
        return params;
    }

    @Override
    public String getSyncFilterValue() {
        AllSharedPreferences sharedPreferences =
                AncLibrary.getInstance().getContext().userService().getAllSharedPreferences();
        return sharedPreferences.fetchDefaultTeamId(sharedPreferences.fetchRegisteredANM());
    }

    @Override
    public int getUniqueIdSource() {
        return BuildConfig.OPENMRS_UNIQUE_ID_SOURCE;
    }

    @Override
    public int getUniqueIdBatchSize() {
        return BuildConfig.OPENMRS_UNIQUE_ID_BATCH_SIZE;
    }

    @Override
    public int getUniqueIdInitialBatchSize() {
        return BuildConfig.OPENMRS_UNIQUE_ID_INITIAL_BATCH_SIZE;
    }

    @Override
    public boolean isSyncSettings() {
        return BuildConfig.IS_SYNC_SETTINGS;
    }

    @Override
    public SyncFilter getEncryptionParam() {
        return SyncFilter.TEAM;
    }

    @Override
    public boolean updateClientDetailsTable() {
        return true;
    }

    @Override
    public List<String> getSynchronizedLocationTags() {
        return null;
    }

    @Override
    public String getTopAllowedLocationLevel() {
        return null;
    }
}
