package org.smartregister.anc.library.activity;

import android.app.Activity;

import androidx.annotation.NonNull;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-17
 */

public class ActivityConfiguration {

    private Class<? extends BaseHomeRegisterActivity> homeRegisterActivityClass;
    private Class<? extends Activity> landingPageActivityClass;
    private Class<? extends Activity> mainContactActivityClass;
    private Class<? extends Activity> profileActivityClass;


    public ActivityConfiguration() {
        setHomeRegisterActivityClass(BaseHomeRegisterActivity.class);
        setLandingPageActivityClass(getHomeRegisterActivityClass());
        setMainContactActivityClass(MainContactActivity.class);
        setProfileActivityClass(ProfileActivity.class);
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

    public Class<? extends Activity> getMainContactActivityClass() {
        return mainContactActivityClass;
    }

    public void setMainContactActivityClass(Class<? extends Activity> mainContactActivityClass) {
        this.mainContactActivityClass = mainContactActivityClass;
    }

    public Class<? extends Activity> getProfileActivityClass() {
        return profileActivityClass;
    }

    public void setProfileActivityClass(Class<? extends Activity> profileActivityClass) {
        this.profileActivityClass = profileActivityClass;
    }
}
