package org.smartregister.anc.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.smartregister.anc.R;
import org.smartregister.anc.event.ViewConfigurationSyncCompleteEvent;
import org.smartregister.anc.presenter.LoginPresenter;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.Utils;
import org.smartregister.task.SaveTeamLocationsTask;
import org.smartregister.view.activity.BaseLoginActivity;
import org.smartregister.view.contract.BaseLoginContract;

import static org.smartregister.util.Log.logInfo;

/**
 * Created by ndegwamartin on 21/06/2018.
 */
public class LoginActivity extends BaseLoginActivity implements BaseLoginContract.View {
    public static final String TAG = BaseLoginActivity.class.getCanonicalName();

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

        if (mLoginPresenter.isSiteCharacteristicsSet()) {
            gotToHomeRegister(remote);
        } else {
            goToSiteCharacteristics(remote);
        }

        finish();
    }

    private void gotToHomeRegister(boolean remote) {
        Intent intent = new Intent(this, HomeRegisterActivity.class);
        intent.putExtra(Constants.INTENT_KEY.IS_REMOTE_LOGIN, remote);
        startActivity(intent);
    }

    private void goToSiteCharacteristics(boolean remote) {
        Intent intent = new Intent(this, SiteCharacteristicsEnterActivity.class);
        intent.putExtra(Constants.INTENT_KEY.IS_REMOTE_LOGIN, remote);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoginPresenter.processViewCustomizations();
        if (!mLoginPresenter.isUserLoggedOut()) {
            goToHome(false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void refreshViews(ViewConfigurationSyncCompleteEvent syncCompleteEvent) {
        if (syncCompleteEvent != null) {
            logInfo("Refreshing Login View...");
            mLoginPresenter.processViewCustomizations();

        }
    }

    @Override
    protected void renderBuildInfo() {
        TextView application_version = findViewById(org.smartregister.R.id.login_build_text_view);
        if (application_version != null) {
            try {
                application_version.setText(String.format(getString(org.smartregister.R.string.app_version), Utils.getVersion(this), Utils.getBuildDate(true)));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }


        ((TextView) findViewById(org.smartregister.R.id.login_user_name_edit_text)).setText("demo");
        ((TextView) findViewById(org.smartregister.R.id.login_password_edit_text)).setText("Amani123");
        findViewById(org.smartregister.R.id.login_login_btn).performClick();
    }
}