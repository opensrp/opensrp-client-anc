package org.smartregister.anc.library.activity;

import android.app.Activity;
import android.support.annotation.NonNull;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-17
 */

public class ActivityConfiguration {

    private Class<? extends BaseHomeRegisterActivity> homeRegisterActivityClass;
    private Class<? extends Activity> landingPageActivityClass;


    public ActivityConfiguration() {
        setHomeRegisterActivityClass(BaseHomeRegisterActivity.class);
        setLandingPageActivityClass(BaseHomeRegisterActivity.class);
    }

    public Class<? extends BaseHomeRegisterActivity> getHomeRegisterActivityClass() {
        return homeRegisterActivityClass;
    }

    public void setHomeRegisterActivityClass(@NonNull Class<? extends BaseHomeRegisterActivity> homeRegisterActivityClass) {
        this.homeRegisterActivityClass = homeRegisterActivityClass;
    }

    public Class<? extends Activity> getLandingPageActivityClass() {
        return landingPageActivityClass;
    }

    public void setLandingPageActivityClass(Class<? extends Activity> landingPageActivityClass) {
        this.landingPageActivityClass = landingPageActivityClass;
    }
}
