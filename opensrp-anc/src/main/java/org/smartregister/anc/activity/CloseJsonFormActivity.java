package org.smartregister.anc.activity;

import android.os.Bundle;

import com.vijay.jsonwizard.activities.JsonFormActivity;

import org.smartregister.anc.R;

public class CloseJsonFormActivity extends JsonFormActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setConfirmCloseMessage(getString(R.string.any_changes_you_make));
    }
}
