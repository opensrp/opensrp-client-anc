package org.smartregister.anc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.smartregister.anc.R;
import org.smartregister.anc.contract.SiteCharacteristicsContract;
import org.smartregister.anc.presenter.SiteCharacteristicsPresenter;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.JsonFormUtils;

/**
 * Created by ndegwamartin on 27/08/2018.
 */
public class SiteCharacteristicsActivity extends BaseActivity implements View.OnClickListener, SiteCharacteristicsContract.View {

    private static final String TAG = SiteCharacteristicsActivity.class.getCanonicalName();
    private SiteCharacteristicsContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_characteristics);

        findViewById(R.id.btn_site_characteristics).setOnClickListener(this);

        presenter = new SiteCharacteristicsPresenter(this);

    }

    @Override
    public void onClick(View view) {

        presenter.launchSiteCharacteristicsForm();
    }

    @Override
    public void launchSiteCharacteristicsSettingsForm() {

        JsonFormUtils.launchSiteCharacteristicsForm(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra("json");
                Log.d("JSONResult", jsonString);

                presenter.saveSiteCharacteristics(jsonString);

            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }

        }
    }

    @Override
    public void goToHomeRegisterPage() {
        Intent intent = new Intent(this, HomeRegisterActivity.class);
        intent.putExtra(Constants.INTENT_KEY.IS_REMOTE_LOGIN, getIntent().getBooleanExtra(Constants.INTENT_KEY.IS_REMOTE_LOGIN, false));
        startActivity(intent);

        finish();//finish this
    }
}
