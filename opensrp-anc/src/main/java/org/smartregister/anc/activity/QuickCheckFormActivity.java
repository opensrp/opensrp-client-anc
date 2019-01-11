package org.smartregister.anc.activity;

import com.vijay.jsonwizard.activities.JsonWizardFormActivity;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONException;
import org.smartregister.anc.domain.Contact;
import org.smartregister.anc.fragment.QuickCheckFormFragment;

public class QuickCheckFormActivity extends JsonWizardFormActivity {

    @Override
    public void writeValue(String stepName, String key, String value, String openMrsEntityParent, String openMrsEntity,
                           String openMrsEntityId, boolean popup) throws JSONException {
        callSuperWriteValue(stepName, key, value, openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);

    }
    protected void callSuperWriteValue(String stepName, String key, String value, String openMrsEntityParent,
                                       String openMrsEntity, String openMrsEntityId, Boolean popup) throws JSONException {
        super.writeValue(stepName, key, value, openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);


    }

    @Override
    public void onFormFinish() {
        callSuperFinish();
    }

    protected void callSuperFinish() {
        super.onFormFinish();
    }
    @Override
    public void initializeFormFragment() {
        initializeFormFragmentCore();
    }

    @Override
    protected void initializeFormFragmentCore() {
        QuickCheckFormFragment contactJsonFormFragment =  QuickCheckFormFragment.getFormFragment();
        getSupportFragmentManager().beginTransaction()
                .add(com.vijay.jsonwizard.R.id.container, contactJsonFormFragment).commit();
    }

    public Contact getContact() {
        Form form = getForm();
        if (form instanceof Contact) {
            return (Contact) form;
        }
        return null;
    }

}
