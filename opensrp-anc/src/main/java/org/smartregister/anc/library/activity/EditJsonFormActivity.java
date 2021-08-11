package org.smartregister.anc.library.activity;

import android.os.Bundle;

import com.vijay.jsonwizard.activities.FormConfigurationJsonFormActivity;

import org.smartregister.anc.library.R;

public class EditJsonFormActivity extends FormConfigurationJsonFormActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setConfirmCloseMessage(getString(R.string.any_changes_you_make));
        super.onCreate(savedInstanceState);
    }
}
