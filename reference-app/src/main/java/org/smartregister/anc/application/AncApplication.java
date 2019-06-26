package org.smartregister.anc.application;

import android.support.annotation.NonNull;
import android.util.Log;

import com.flurry.android.FlurryAgent;

import org.smartregister.anc.BuildConfig;
import org.smartregister.anc.library.application.BaseAncApplication;
import org.smartregister.anc.library.sync.BaseAncClientProcessorForJava;
import org.smartregister.sync.ClientProcessorForJava;

/**
 * Created by ndegwamartin on 21/06/2018.
 */
public class AncApplication extends BaseAncApplication {


    @Override
    public void onCreate() {
        super.onCreate();

        //Only integrate Flurry Analytics for  production. Remove negation to test in debug
        if (!BuildConfig.DEBUG) {
            new FlurryAgent.Builder()
                    .withLogEnabled(true)
                    .withCaptureUncaughtExceptions(true)
                    .withContinueSessionMillis(10000)
                    .withLogLevel(Log.VERBOSE)
                    .build(this, BuildConfig.FLURRY_API_KEY);
        }

    }

    @NonNull
    @Override
    public ClientProcessorForJava getClientProcessor() {
        return BaseAncClientProcessorForJava.getInstance(this);
    }

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(base);
    }

}
