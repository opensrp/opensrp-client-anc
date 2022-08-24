package org.smartregister.anc.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.BaseHomeRegisterActivity;
import org.smartregister.anc.library.activity.SiteCharacteristicsEnterActivity;
import org.smartregister.anc.library.event.ViewConfigurationSyncCompleteEvent;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.anc.presenter.LoginPresenter;
import org.smartregister.task.SaveTeamLocationsTask;
import org.smartregister.view.activity.BaseLoginActivity;
import org.smartregister.view.contract.BaseLoginContract;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 21/06/2018.
 */
public class LoginActivity extends BaseLoginActivity implements BaseLoginContract.View {

    @Override
    protected void onResume() {
        super.onResume();
        mLoginPresenter.processViewCustomizations();
        if (!mLoginPresenter.isUserLoggedOut()) {
            goToHome(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        setUpViews();
    }

    private void setUpViews() {
        TextView formReleaseTextView = findViewById(R.id.manifest_text_view);
        if (StringUtils.isNotBlank(new Utils().getManifestVersion(this))) {
            formReleaseTextView.setText(new Utils().getManifestVersion(this));
        } else {
            formReleaseTextView.setVisibility(View.GONE);
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initializePresenter() {
        mLoginPresenter = new LoginPresenter(this);
    }

    @Override
    public void goToHome(boolean remote) {
        if (remote) {
            Utils.startAsyncTask(new SaveTeamLocationsTask(), null);
        }

        if (mLoginPresenter.isServerSettingsSet()) {
            gotToHomeRegister(remote);
        } else {
            goToSiteCharacteristics(remote);
        }

        finish();
    }

    private void gotToHomeRegister(boolean remote) {
        Intent intent = new Intent(this, BaseHomeRegisterActivity.class);
        intent.putExtra(ConstantsUtils.IntentKeyUtils.IS_REMOTE_LOGIN, remote);
        startActivity(intent);
    }

    private void goToSiteCharacteristics(boolean remote) {
        Intent intent = new Intent(this, SiteCharacteristicsEnterActivity.class);
        intent.putExtra(ConstantsUtils.IntentKeyUtils.IS_REMOTE_LOGIN, remote);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void refreshViews(ViewConfigurationSyncCompleteEvent syncCompleteEvent) {
        if (syncCompleteEvent != null) {
            Timber.d("Refreshing Login View...");
            mLoginPresenter.processViewCustomizations();

        }
    }
}