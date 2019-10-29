package org.smartregister.anc.library.shadows;

import android.os.AsyncTask;

import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;
import org.robolectric.shadows.ShadowAsyncTask;
import org.robolectric.shadows.ShadowAsyncTaskBridge;
import org.robolectric.util.ReflectionHelpers;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-30
 */

@Implements(AsyncTask.class)
public class MyShadowAsyncTask<P, Q, R> extends ShadowAsyncTask<P, Q, R> {
    @RealObject
    private AsyncTask actualAsyncTask;

    public AsyncTask execute(P... params) {
        ReflectionHelpers.setField(this, "status", AsyncTask.Status.RUNNING);

        ShadowAsyncTaskBridge<P, Q, R> bridge = ReflectionHelpers.callInstanceMethod(this, "getBridge");
        bridge.onPreExecute();

        R result = ReflectionHelpers.callInstanceMethod(actualAsyncTask, "doInBackground", ReflectionHelpers.ClassParameter.from(Object[].class, params));
        bridge.onPostExecute(result);

        return actualAsyncTask;
    }
}
