package org.smartregister.anc.library.activity;

import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.smartregister.anc.library.fragment.ANCRegisterFormFragment;

public class AncRegistrationActivity extends JsonFormActivity {

    @Override
    public synchronized void initializeFormFragment() {
        isFormFragmentInitialized = true;
        ANCRegisterFormFragment formFragment = (ANCRegisterFormFragment) ANCRegisterFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        getSupportFragmentManager().beginTransaction()
                .add(com.vijay.jsonwizard.R.id.container, formFragment).commitAllowingStateLoss();
    }

}
