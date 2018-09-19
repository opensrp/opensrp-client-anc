package org.smartregister.anc.service.intent;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.smartregister.Context;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.helper.SyncSettingsServiceHelper;
import org.smartregister.configurableviews.helper.PreferenceHelper;
import org.smartregister.configurableviews.util.Constants;
import org.smartregister.configurableviews.util.Utils;

import java.util.Calendar;

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


                Intent broadCastIntent = new Intent(SettingsSyncIntentService.EVENT_SYNC_COMPLETE); //broadcast useful meta data

                int count = syncSettingsServiceHelper.processIntent();
                if (count > 0) {
                    intent.putExtra(Constants.INTENT_KEY.SYNC_TOTAL_RECORDS, count);
                }

                //Broadcast settings Sync event

                //update last sync time
                String lastSyncTime = Utils.formatDate(Calendar.getInstance().getTime(), "MMM dd HH:mm");
                Utils.writePrefString(this, Constants.INTENT_KEY.LAST_SYNC_TIME_STRING, lastSyncTime);

                broadCastIntent.putExtra(Constants.INTENT_KEY.LAST_SYNC_TIME_STRING, lastSyncTime);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadCastIntent);

            } catch (Exception e) {
                logError(TAG + " Error fetching client settings");
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = AncApplication.getInstance().getContext();
        syncSettingsServiceHelper = new SyncSettingsServiceHelper(getApplicationContext(),
                AncApplication.getInstance().getContext().allSettings(), context.getHttpAgent(),
                context.configuration().dristhiBaseURL(), PreferenceHelper.getInstance(getApplicationContext()),
                AncApplication.getInstance().getPassword() != null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}

