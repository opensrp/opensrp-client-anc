package org.smartregister.anc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.smartregister.anc.R;
import org.smartregister.anc.contract.SiteCharacteristicsContract;
import org.smartregister.anc.helper.LocationHelper;
import org.smartregister.anc.presenter.SiteCharacteristicsPresenter;
import org.smartregister.anc.util.Constants;

/**
 * Created by ndegwamartin on 28/08/2018.
 */
public class SiteCharacteristicsExitActivity extends BaseActivity implements View.OnClickListener, SiteCharacteristicsContract.View {

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
}
