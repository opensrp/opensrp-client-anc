package org.smartregister.anc.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.smartregister.anc.R;
import org.smartregister.anc.contract.SiteCharacteristicsContract;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.JsonFormUtils;

import java.util.Map;

/**
 * Created by ndegwamartin on 27/08/2018.
 */
public abstract class BaseActivity extends AppCompatActivity implements SiteCharacteristicsContract.View {

    protected ProgressDialog progressDialog;
    protected SiteCharacteristicsContract.Presenter presenter;
    private static final String TAG = BaseActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void showProgressDialog(int saveMessageStringIdentifier) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle(getString(saveMessageStringIdentifier));
            progressDialog.setMessage(getString(R.string.please_wait_message));
        }
        if (!isFinishing())
            progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
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
    public void launchSiteCharacteristicsSettingsForm() {

        JsonFormUtils.launchSiteCharacteristicsForm(this);
    }

    @Override
    public void goToSiteCharacteristicsExitPage() {
        Intent intent = new Intent(this, SiteCharacteristicsExitActivity.class);
        intent.putExtra(Constants.INTENT_KEY.IS_REMOTE_LOGIN, getIntent().getBooleanExtra(Constants.INTENT_KEY.IS_REMOTE_LOGIN, false));
        startActivity(intent);

        finish();//finish this
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
