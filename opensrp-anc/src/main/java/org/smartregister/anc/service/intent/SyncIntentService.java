package org.smartregister.anc.service.intent;

import org.smartregister.anc.BuildConfig;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.util.Constants;
import org.smartregister.repository.AllSharedPreferences;


public class SyncIntentService extends org.smartregister.sync.intent.SyncIntentService {
    @Override
    public int getSyncMaxRetries() {
        return BuildConfig.MAX_SYNC_RETRIES;
    }

    @Override
    public String getSyncFilterParam() {
        return Constants.SyncFilters.FILTER_TEAM_ID;
    }

    @Override
    public String getSyncFilterValue() {
        AllSharedPreferences sharedPreferences = AncApplication.getInstance().getContext().userService().getAllSharedPreferences();
        return sharedPreferences.fetchDefaultTeamId(sharedPreferences.fetchRegisteredANM());
    }
}
