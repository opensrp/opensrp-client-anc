package org.smartregister.anc.library.activity;

import android.support.annotation.NonNull;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-17
 */

public class ActivityConfiguration {

    private Class<BaseHomeRegisterActivity> homeRegisterActivityClass;

    public ActivityConfiguration() {
        setHomeRegisterActivityClass(BaseHomeRegisterActivity.class);
    }

    public Class<BaseHomeRegisterActivity> getHomeRegisterActivityClass() {
        return homeRegisterActivityClass;
    }

    public void setHomeRegisterActivityClass(@NonNull Class<BaseHomeRegisterActivity> homeRegisterActivityClass) {
        this.homeRegisterActivityClass = homeRegisterActivityClass;
    }
}
