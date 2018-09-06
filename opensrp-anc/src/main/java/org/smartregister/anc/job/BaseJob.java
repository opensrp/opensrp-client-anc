package org.smartregister.anc.job;

import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;

import org.smartregister.anc.util.Constants;

import java.util.concurrent.TimeUnit;

/**
 * Created by ndegwamartin on 05/09/2018.
 */
public abstract class BaseJob extends Job {

    private static final String TAG = BaseJob.class.getCanonicalName();

    public static void scheduleJob(String jobTag, Long start, Long flex) {

        boolean toReschedule = start < TimeUnit.MINUTES.toMillis(15); //evernote doesn't allow less than 15 mins periodic schedule, keep flag ref for workaround

        PersistableBundleCompat extras = new PersistableBundleCompat();
        extras.putBoolean(Constants.INTENT_KEY.TO_RESCHEDULE, toReschedule);

        JobRequest.Builder jobRequest = new JobRequest.Builder(jobTag).setExtras(extras);

        if (toReschedule) {

            jobRequest.setBackoffCriteria(start, JobRequest.BackoffPolicy.LINEAR).setExact(start);

        } else {

            jobRequest.setPeriodic(TimeUnit.MINUTES.toMillis(start), TimeUnit.MINUTES.toMillis(flex));
        }

        try {

            int jobId = jobRequest.build().schedule();
            Log.d(TAG, "Scheduling job with name " + jobTag + " periodically with JOB ID " + jobId);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * For jobs that need to be started immediately
     */
    public static void scheduleJobImmediately(String jobTag) {

        int jobId = new JobRequest.Builder(jobTag)
                .startNow()
                .build()
                .schedule();

        Log.d(TAG, "Scheduling job with name " + jobTag + " immediately with JOB ID " + jobId);
    }

    @Override
    protected void onReschedule(int newJobId) {

        Log.d(TAG, "Rescheduling job with name " + this.getParams().getTag() + " JOB ID " + newJobId);
    }
}
