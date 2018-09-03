package org.smartregister.anc.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.smartregister.anc.R;
import org.smartregister.anc.helper.LocationHelper;
import org.smartregister.anc.presenter.CharacteristicsPresenter;

/**
 * Created by ndegwamartin on 28/08/2018.
 */
public class SiteCharacteristicsExitActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_characteristics_exit);

        findViewById(R.id.btn_site_characteristics_home_register).setOnClickListener(this);
        findViewById(R.id.btn_back_to_home).setOnClickListener(this);

        presenter = new CharacteristicsPresenter(this);

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
}
