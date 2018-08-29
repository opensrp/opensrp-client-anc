package org.smartregister.anc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.smartregister.anc.R;
import org.smartregister.anc.contract.SiteCharacteristicsContract;
import org.smartregister.anc.helper.LocationHelper;
import org.smartregister.anc.presenter.SiteCharacteristicsPresenter;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.JsonFormUtils;

import java.util.Map;

/**
 * Created by ndegwamartin on 28/08/2018.
 */
public class SiteCharacteristicsExitActivity extends BaseActivity implements View.OnClickListener, SiteCharacteristicsContract.View {
    private static final String TAG = SiteCharacteristicsExitActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_characteristics_exit);

        findViewById(R.id.btn_site_characteristics_home_register).setOnClickListener(this);
        findViewById(R.id.btn_back_to_home).setOnClickListener(this);

        presenter = new SiteCharacteristicsPresenter(this);

        String defaultLocation = LocationHelper.getInstance().getOpenMrsLocationName(LocationHelper.getInstance().getDefaultLocation());
        TextView textView = findViewById(R.id.site_characteristics_facility_name);
        textView.setText(getString(R.string.your_site_characteristics_for_facility_name, defaultLocation));


    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btn_site_characteristics_home_register) {

            goToHomeRegisterPage();

        } else {

            presenter.launchSiteCharacteristicsFormForEdit();
        }

    }

    public void goToHomeRegisterPage() {
        Intent intent = new Intent(this, HomeRegisterActivity.class);
        intent.putExtra(Constants.INTENT_KEY.IS_REMOTE_LOGIN, getIntent().getBooleanExtra(Constants.INTENT_KEY.IS_REMOTE_LOGIN, false));
        startActivity(intent);

        finish();//finish this
    }

    @Override
    public void launchSiteCharacteristicsSettingsForm() {
        //InterfaceOverriden
    }

    @Override
    public void goToSiteCharacteristicsExitPage() {
        //Interface overridden
    }

    @Override
    public void launchSiteCharacteristicsSettingsFormForEdit(Map<String, String> characteristics) {

        String formMetadata = JsonFormUtils.getAutoPopulatedSiteCharacteristicsEditFormString(this, characteristics);
        try {

            JsonFormUtils.startFormForEdit(this, JsonFormUtils.REQUEST_CODE_GET_JSON, formMetadata);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
