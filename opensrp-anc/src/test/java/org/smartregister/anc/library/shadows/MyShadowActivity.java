package org.smartregister.anc.library.shadows;

import android.app.Activity;
import android.content.Intent;

import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowActivity;
import org.smartregister.anc.library.R;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-29
 */

@Implements(Activity.class)
public class MyShadowActivity extends ShadowActivity {

    @Override
    public void callAttach(Intent intent) {
        super.callAttach(intent);
        realActivity.setTheme(R.style.AncAppTheme_NoActionBar);
    }
}
