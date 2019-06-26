package org.smartregister.anc.library.activity;

import android.os.Bundle;

import com.vijay.jsonwizard.activities.JsonFormActivity;

import org.smartregister.anc.library.R;

public class EditJsonFormActivity extends JsonFormActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setConfirmCloseMessage(getString(R.string.any_changes_you_make));
    }
}
