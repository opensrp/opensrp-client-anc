package org.smartregister.anc.library.activity;

import android.os.Bundle;

import com.vijay.jsonwizard.activities.FormConfigurationJsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.smartregister.anc.library.R;
import org.smartregister.anc.library.fragment.ANCRegisterFormFragment;

public class EditJsonFormActivity extends AncRegistrationActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setConfirmCloseMessage(getString(R.string.any_changes_you_make));
        super.onCreate(savedInstanceState);
    }
}
