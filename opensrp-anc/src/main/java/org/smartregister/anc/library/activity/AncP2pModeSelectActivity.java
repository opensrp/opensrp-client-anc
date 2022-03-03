package org.smartregister.anc.library.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import org.smartregister.anc.library.R;
import org.smartregister.p2p.activity.P2pModeSelectActivity;
import org.smartregister.util.LangUtils;

public class AncP2pModeSelectActivity extends P2pModeSelectActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.peer_to_peer_activity_title);
    }

    @Override
    protected void attachBaseContext(Context base) {
// get language from prefs
        String lang = LangUtils.getLanguage(base.getApplicationContext());
        Configuration newConfiguration = LangUtils.setAppLocale(base, lang);

        super.attachBaseContext(base);

        applyOverrideConfiguration(newConfiguration);
    }
}