package org.smartregister.anc.library.activity;

import android.os.Bundle;

import org.smartregister.anc.library.R;
import org.smartregister.view.activity.DynamicJsonFormActivity;

public class EditJsonFormActivity extends DynamicJsonFormActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setConfirmCloseMessage(getString(R.string.any_changes_you_make));
    }
}
