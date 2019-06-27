package org.smartregister.anc.application;

import org.smartregister.anc.R;

/**
 * Created by ndegwamartin on 27/05/2018.
 */

public class TestAncApplication extends AncApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        setTheme(R.style.Theme_AppCompat); //or just R.style.Theme_AppCompat
    }
}
