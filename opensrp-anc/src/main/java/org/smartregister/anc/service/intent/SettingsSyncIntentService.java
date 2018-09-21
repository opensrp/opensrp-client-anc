package org.smartregister.anc.service.intent;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.smartregister.Context;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.helper.SyncSettingsServiceHelper;
import org.smartregister.configurableviews.util.Constants;

import static org.smartregister.util.Log.logError;

/**
 * Created by ndegwamartin on 14/09/2018.
 */
public class SettingsSyncIntentService extends IntentService {
    public static final String SETTINGS_URL = "/rest/settings/sync";

    private static final String TAG = SettingsSyncIntentService.class.getCanonicalName();

    protected SyncSettingsServiceHelper syncSettingsServiceHelper;

    public static final String EVENT_SYNC_COMPLETE = "event_sync_complete";

    public SettingsSyncIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("ssssssss", "In Settings Sync Intent Service...");
        if (intent != null) {
            try {

                int count = syncSettingsServiceHelper.processIntent();
                if (count > 0) {
                    intent.putExtra(Constants.INTENT_KEY.SYNC_TOTAL_RECORDS, count);
                }

            } catch (Exception e) {
                logError(TAG + " Error fetching client settings");
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = AncApplication.getInstance().getContext();
        syncSettingsServiceHelper = new SyncSettingsServiceHelper(getApplicationContext(), context.configuration().dristhiBaseURL(), context.getHttpAgent());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}

