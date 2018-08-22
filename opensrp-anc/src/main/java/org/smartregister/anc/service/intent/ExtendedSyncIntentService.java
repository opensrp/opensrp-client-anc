package org.smartregister.anc.service.intent;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.receiver.AlarmReceiver;
import org.smartregister.anc.util.NetworkUtils;
import org.smartregister.anc.util.ServiceTools;
import org.smartregister.service.ActionService;


public class ExtendedSyncIntentService extends IntentService {

    private Context context;
    private ActionService actionService;
    private static final String TAG = ExtendedSyncIntentService.class.getCanonicalName();

    public ExtendedSyncIntentService() {
        super("ExtendedSyncIntentService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getBaseContext();
        actionService = AncApplication.getInstance().getContext().actionService();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        try {
            boolean wakeup = workIntent.getBooleanExtra(SyncIntentService.WAKE_UP, false);

            if (NetworkUtils.isNetworkAvailable()) {
                actionService.fetchNewActions();

                startSyncValidation(wakeup);
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            AlarmReceiver.completeWakefulIntent(workIntent);
        }
    }


    private void startSyncValidation(boolean wakeup) {
        ServiceTools.startService(context, ValidateIntentService.class, wakeup);
    }


}
