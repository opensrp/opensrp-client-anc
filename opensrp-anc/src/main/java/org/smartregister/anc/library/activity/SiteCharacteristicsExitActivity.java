package org.smartregister.anc.library.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.smartregister.anc.library.R;
import org.smartregister.anc.library.presenter.CharacteristicsPresenter;
import org.smartregister.location.helper.LocationHelper;

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
        findViewById(R.id.txt_title_label).setOnClickListener(this);

        presenter = new CharacteristicsPresenter(this);

        String defaultLocation =
                LocationHelper.getInstance().getOpenMrsLocationName(LocationHelper.getInstance().getDefaultLocation());
        TextView textView = findViewById(R.id.site_characteristics_facility_name);
        textView.setText(getString(R.string.your_site_characteristics_for_facility_name, defaultLocation));


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_site_characteristics_home_register) {
            goToHomeRegisterPage();
        } else if (view.getId() == R.id.txt_title_label) {
            presenter.launchSiteCharacteristicsFormForEdit();
        } else {
            presenter.launchSiteCharacteristicsFormForEdit();
        }

    }
}
