package org.smartregister.anc.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.exception.MissingApplicationContextException;
import org.smartregister.anc.service.AncImageUploadSyncService;
import org.smartregister.anc.service.intent.PullUniqueIdsIntentService;
import org.smartregister.anc.util.Constants;
import org.smartregister.configurableviews.service.PullConfigurableViewsIntentService;
import org.smartregister.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by ndegwamartin on 09/04/2018.
 */

public class AlarmReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getCanonicalName();
    private static final int MIN_STAGGER_VALUE_MILLISECS = 2000;
    private static final int MAX_STAGGER_VALUE_MILLISECS = 9000;
    private static final String serviceActionName = "org.smartregister.anc.action.START_SERVICE_ACTION";
    private static final String serviceTypeName = "serviceType";
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onReceive(Context context, Intent alarmIntent) {
        int serviceType = alarmIntent.getIntExtra(serviceTypeName, 0);
        if (!AncApplication.getInstance().getContext().IsUserLoggedOut()) {
            Intent serviceIntent = null;
            switch (serviceType) {
                case Constants.ServiceType.PULL_UNIQUE_IDS:
                    serviceIntent = new Intent(context, PullUniqueIdsIntentService.class);
                    android.util.Log.i(TAG, "Started PULL_UNIQUE_IDS service at: " + dateFormatter.format(new Date()));
                    break;
                case Constants.ServiceType.IMAGE_UPLOAD:
                    serviceIntent = new Intent(context, AncImageUploadSyncService.class);
                    android.util.Log.i(TAG, "Started IMAGE_UPLOAD_SYNC service at: " + dateFormatter.format(new Date()));
                    break;
                case Constants.ServiceType.PULL_VIEW_CONFIGURATIONS:
                    serviceIntent = new Intent(context, PullConfigurableViewsIntentService.class);
                    android.util.Log.i(TAG, "Started VIEW_CONFIGS_SYNC service at: " + dateFormatter.format(new Date()));
                    break;
                default:
                    break;
            }

            if (serviceIntent != null) {
                this.startService(context, serviceIntent);
            }
        }

    }

    private void startService(Context context, Intent serviceIntent) {
        startWakefulService(context, serviceIntent);
    }

    /**
     * @param context
     * @param triggerIteration in minutes
     * @param taskType         a constant from Constants.ServiceType constants denoting the service type
     */
    public static void setAlarm(Context context, long triggerIteration, int taskType) {
        try {
            AlarmManager alarmManager;
            PendingIntent alarmIntent;

            long triggerAt;
            long triggerInterval;
            if (context == null) {
                throw new MissingApplicationContextException(TAG + " Unable to schedule service without app context");
            }

            // Otherwise schedule based on normal interval
            triggerInterval = TimeUnit.MINUTES.toMillis(triggerIteration);
            // set trigger time to be current device time + the interval (frequency).
            int staggerValue = MIN_STAGGER_VALUE_MILLISECS + (new Random().nextInt(MAX_STAGGER_VALUE_MILLISECS));//randomize so that services not launch at exactly the same time
            triggerAt = System.currentTimeMillis() + triggerInterval + staggerValue;

            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent alarmReceiverIntent = new Intent(context, AlarmReceiver.class);

            alarmReceiverIntent.setAction(serviceActionName + taskType);
            alarmReceiverIntent.putExtra(serviceTypeName, taskType);
            alarmIntent = PendingIntent.getBroadcast(context, 0, alarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            try {
                alarmManager.cancel(alarmIntent);
            } catch (Exception e) {
                Log.logError(TAG, e.getMessage());
            }
            //Elapsed real time uses the "time since system boot" as a reference, and real time clock uses UTC (wall clock) time
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAt, triggerInterval, alarmIntent);
        } catch (Exception e) {
            Log.logError(TAG, "Error in setting service Alarm " + e.getMessage());
        }

    }

}
