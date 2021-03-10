package org.smartregister.anc.library.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.contract.SiteCharacteristicsContract;
import org.smartregister.anc.library.util.ANCJsonFormUtils;
import org.smartregister.anc.library.util.ConstantsUtils;

import java.util.Map;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 27/08/2018.
 */
public abstract class BaseActivity extends AppCompatActivity implements SiteCharacteristicsContract.View {

    protected ProgressDialog progressDialog;
    protected SiteCharacteristicsContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ANCJsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra("json");
                Timber.d("JSONResult %s", jsonString);
                presenter.saveSiteCharacteristics(jsonString);
            } catch (Exception e) {
                Timber.e(e);
            }

        }
    }

    @Override
    public void launchSiteCharacteristicsSettingsForm() {
        ANCJsonFormUtils.launchSiteCharacteristicsForm(this);
    }

    public void showProgressDialog(int saveMessageStringIdentifier) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle(getString(saveMessageStringIdentifier));
            progressDialog.setMessage(getString(R.string.please_wait_message));
        }
        if (!isFinishing()) progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void goToLastPage() {

        if (this instanceof SiteCharacteristicsActivity) {
            goToHomeRegisterPage();
        } else {
            goToSiteCharacteristicsExitPage();
        }
    }

    public void goToHomeRegisterPage() {
        Intent intent = new Intent(this, AncLibrary.getInstance().getActivityConfiguration().getLandingPageActivityClass())
                .putExtra(ConstantsUtils.IntentKeyUtils.IS_REMOTE_LOGIN,
                        getIntent().getBooleanExtra(ConstantsUtils.IntentKeyUtils.IS_REMOTE_LOGIN, false));
        startActivity(intent);
        finish();
    }

    public void goToSiteCharacteristicsExitPage() {
        Intent intent = new Intent(this, SiteCharacteristicsExitActivity.class)
                .putExtra(ConstantsUtils.IntentKeyUtils.IS_REMOTE_LOGIN,
                        getIntent().getBooleanExtra(ConstantsUtils.IntentKeyUtils.IS_REMOTE_LOGIN, false));
        startActivity(intent);
        finish();
    }

    @Override
    public void launchSiteCharacteristicsSettingsFormForEdit(Map<String, String> characteristics) {
        String formMetadata = ANCJsonFormUtils.getAutoPopulatedSiteCharacteristicsEditFormString(this, characteristics);
        try {
            ANCJsonFormUtils.startFormForEdit(this, ANCJsonFormUtils.REQUEST_CODE_GET_JSON, formMetadata);
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
