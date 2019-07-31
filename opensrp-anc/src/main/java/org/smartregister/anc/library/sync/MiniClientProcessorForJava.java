package org.smartregister.anc.library.sync;

import android.support.annotation.NonNull;

import java.util.HashSet;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-31
 */

public interface MiniClientProcessorForJava {

    @NonNull
    HashSet<String> getEventType();

    boolean canProcess(@NonNull String eventType);
}
