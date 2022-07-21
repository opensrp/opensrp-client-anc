package org.smartregister.anc.library.activity;

import android.os.Bundle;

import org.smartregister.anc.library.R;

public class EditJsonFormActivity extends AncRegistrationActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setConfirmCloseMessage(getString(R.string.any_changes_you_make));
        super.onCreate(savedInstanceState);
    }
}
