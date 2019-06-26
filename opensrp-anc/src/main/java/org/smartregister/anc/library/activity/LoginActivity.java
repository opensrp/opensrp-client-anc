package org.smartregister.anc.library.activity;

import android.content.Intent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.event.ViewConfigurationSyncCompleteEvent;
import org.smartregister.anc.library.util.Constants;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.anc.library.presenter.LoginPresenter;
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

        if (mLoginPresenter.isServerSettingsSet()) {
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
}