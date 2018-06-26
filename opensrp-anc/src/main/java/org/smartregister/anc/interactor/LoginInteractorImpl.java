package org.smartregister.anc.interactor;

import android.content.Context;
import android.util.Log;

import org.joda.time.DateTime;
import org.smartregister.anc.BuildConfig;
import org.smartregister.anc.R;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.LoginContract;
import org.smartregister.anc.receiver.AlarmReceiver;
import org.smartregister.anc.task.RemoteLoginTask;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.NetworkUtils;
import org.smartregister.domain.LoginResponse;
import org.smartregister.domain.TimeStatus;
import org.smartregister.domain.jsonmapping.LoginResponseData;
import org.smartregister.event.Listener;

import java.lang.ref.WeakReference;
import java.util.TimeZone;

import static org.smartregister.anc.activity.LoginActivity.getOpenSRPContext;
import static org.smartregister.domain.LoginResponse.NO_INTERNET_CONNECTIVITY;
import static org.smartregister.domain.LoginResponse.UNAUTHORIZED;
import static org.smartregister.domain.LoginResponse.UNKNOWN_RESPONSE;

/**
 * Created by ndegwamartin on 26/06/2018.
 */
public class LoginInteractorImpl implements LoginInteractor {

    private LoginContract.Presenter mLoginPresenter;

    private RemoteLoginTask remoteLoginTask;

    private static final String TAG = LoginInteractorImpl.class.getCanonicalName();

    public LoginInteractorImpl(LoginContract.Presenter loginPresenter) {
        this.mLoginPresenter = loginPresenter;
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        if (!isChangingConfiguration) {
            mLoginPresenter = null;
        }
    }

    @Override
    public void login(WeakReference<LoginContract.View> view, String userName, String password) {
        login(view, !getOpenSRPContext().allSharedPreferences().fetchForceRemoteLogin(), userName, password);
    }

    private void login(WeakReference<LoginContract.View> view, boolean localLogin, String userName, String password) {

        mLoginPresenter.getLoginView().hideKeyboard();
        mLoginPresenter.getLoginView().enableLoginButton(false);
        if (localLogin) {
            localLogin(view, userName, password);
        } else {
            remoteLogin(userName, password);
        }

        Log.i(getClass().getName(), "Login result finished " + DateTime.now().toString());
    }

    private void localLogin(WeakReference<LoginContract.View> view, String userName, String password) {
        mLoginPresenter.getLoginView().enableLoginButton(true);
        if (getOpenSRPContext().userService().isUserInValidGroup(userName, password)
                && (!Constants.TIME_CHECK || TimeStatus.OK.equals(getOpenSRPContext().userService().validateStoredServerTimeZone()))) {
            localLoginWith(userName, password);
        } else {
            login(view, false, userName, password);
        }
    }

    private void localLoginWith(String userName, String password) {

        getOpenSRPContext().userService().localLogin(userName, password);
        mLoginPresenter.getLoginView().goToHome(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                android.util.Log.i(getClass().getName(), "Starting DrishtiSyncScheduler " + DateTime.now().toString());
                if (NetworkUtils.isNetworkAvailable()) {
                    AlarmReceiver.setAlarm(getApplicationContext(), BuildConfig.AUTO_SYNC_DURATION, Constants.ServiceType.AUTO_SYNC);
                }
                android.util.Log.i(getClass().getName(), "Started DrishtiSyncScheduler " + DateTime.now().toString());
            }
        }).start();
    }

    private void remoteLogin(final String userName, final String password) {

        try {
            if (!getOpenSRPContext().allSharedPreferences().fetchBaseURL("").isEmpty()) {
                tryRemoteLogin(userName, password, new Listener<LoginResponse>() {

                    public void onEvent(LoginResponse loginResponse) {
                        mLoginPresenter.getLoginView().enableLoginButton(true);
                        if (loginResponse == LoginResponse.SUCCESS) {
                            if (getOpenSRPContext().userService().isUserInPioneerGroup(userName)) {
                                TimeStatus timeStatus = getOpenSRPContext().userService().validateDeviceTime(
                                        loginResponse.payload(), Constants.MAX_SERVER_TIME_DIFFERENCE
                                );
                                if (!Constants.TIME_CHECK || timeStatus.equals(TimeStatus.OK)) {
                                    remoteLoginWith(userName, password,
                                            loginResponse.payload());
                                    AncApplication.getInstance().startPullUniqueIdsService();
                                } else {
                                    if (timeStatus.equals(TimeStatus.TIMEZONE_MISMATCH)) {
                                        TimeZone serverTimeZone = getOpenSRPContext().userService()
                                                .getServerTimeZone(loginResponse.payload());
                                        mLoginPresenter.getLoginView().showErrorDialog(getApplicationContext().getString(timeStatus.getMessage(),
                                                serverTimeZone.getDisplayName()));
                                    } else {
                                        mLoginPresenter.getLoginView().showErrorDialog(getApplicationContext().getString(timeStatus.getMessage()));
                                    }
                                }
                            } else {
                                // Valid user from wrong group trying to log in
                                mLoginPresenter.getLoginView().showErrorDialog(getApplicationContext().getString(R.string.unauthorized_group));
                            }
                        } else {
                            if (loginResponse == null) {
                                mLoginPresenter.getLoginView().showErrorDialog("Sorry, your login failed. Please try again");
                            } else {
                                if (loginResponse == NO_INTERNET_CONNECTIVITY) {
                                    mLoginPresenter.getLoginView().showErrorDialog(getApplicationContext().getResources().getString(R.string.no_internet_connectivity));
                                } else if (loginResponse == UNKNOWN_RESPONSE) {
                                    mLoginPresenter.getLoginView().showErrorDialog(getApplicationContext().getResources().getString(R.string.unknown_response));
                                } else if (loginResponse == UNAUTHORIZED) {
                                    mLoginPresenter.getLoginView().showErrorDialog(getApplicationContext().getResources().getString(R.string.unauthorized));
                                } else {
                                    mLoginPresenter.getLoginView().showErrorDialog(loginResponse.message());
                                }
                            }
                        }
                    }
                });
            } else {
                mLoginPresenter.getLoginView().enableLoginButton(true);
                mLoginPresenter.getLoginView().showErrorDialog("OpenSRP Base URL is missing. Please add it in Setting and try again");
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());

            mLoginPresenter.getLoginView().showErrorDialog("Error occurred trying to login in. Please try again...");
        }
    }

    private void tryRemoteLogin(final String userName, final String password, final Listener<LoginResponse> afterLogincheck) {
        if (remoteLoginTask != null && !remoteLoginTask.isCancelled()) {
            remoteLoginTask.cancel(true);
        }
        remoteLoginTask = new RemoteLoginTask(mLoginPresenter.getLoginView(), userName, password, afterLogincheck);
        remoteLoginTask.execute();
    }

    private void remoteLoginWith(String userName, String password, LoginResponseData userInfo) {
        getOpenSRPContext().userService().remoteLogin(userName, password, userInfo);
        mLoginPresenter.getLoginView().goToHome(true);
        if (NetworkUtils.isNetworkAvailable()) {
            AlarmReceiver.setAlarm(getApplicationContext(), BuildConfig.AUTO_SYNC_DURATION, Constants.ServiceType.AUTO_SYNC);
        }
    }

    private Context getApplicationContext() {
        return AncApplication.getInstance().getApplicationContext();
    }
}
