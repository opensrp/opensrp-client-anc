package org.smartregister.anc.library.application;


import org.smartregister.anc.library.R;
import org.smartregister.view.activity.DrishtiApplication;

/**
 * Created by ndegwamartin on 27/05/2018.
 */

public class TestAncApplication extends DrishtiApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        setTheme(R.style.Theme_AppCompat); //or just R.style.Theme_AppCompat
    }

    @Override
    public void logoutCurrentUser() {

    }
}
