package org.smartregister.anc.interactor;

import android.content.Context;
import android.util.Log;

import org.joda.time.DateTime;
import org.smartregister.anc.R;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.LoginContract;
import org.smartregister.anc.job.SyncServiceJob;
import org.smartregister.anc.task.RemoteLoginTask;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.NetworkUtils;
import org.smartregister.domain.LoginResponse;
import org.smartregister.domain.TimeStatus;
import org.smartregister.domain.jsonmapping.LoginResponseData;
import org.smartregister.event.Listener;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.service.UserService;

import java.lang.ref.WeakReference;
import java.util.TimeZone;

import static org.smartregister.domain.LoginResponse.NO_INTERNET_CONNECTIVITY;
import static org.smartregister.domain.LoginResponse.UNAUTHORIZED;
import static org.smartregister.domain.LoginResponse.UNKNOWN_RESPONSE;

/**
 * Created by ndegwamartin on 26/06/2018.
 */
public class LoginInteractor implements LoginContract.Interactor {

    private LoginContract.Presenter mLoginPresenter;

    private RemoteLoginTask remoteLoginTask;

    private static final String TAG = LoginInteractor.class.getCanonicalName();

    public LoginInteractor(LoginContract.Presenter loginPresenter) {
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
        loginWithLocalFlag(view, !getSharedPreferences().fetchForceRemoteLogin(), userName, password);
    }

    protected void loginWithLocalFlag(WeakReference<LoginContract.View> view, boolean localLogin, String userName, String password) {

        getLoginView().hideKeyboard();
        getLoginView().enableLoginButton(false);
        if (localLogin) {
            localLogin(view, userName, password);
        } else {
            remoteLogin(userName, password);
        }

        Log.i(getClass().getName(), "Login result finished " + DateTime.now().toString());
    }

    private void localLogin(WeakReference<LoginContract.View> view, String userName, String password) {
        getLoginView().enableLoginButton(true);
        if (getUserService().isUserInValidGroup(userName, password)
                && (!Constants.TIME_CHECK || TimeStatus.OK.equals(getUserService().validateStoredServerTimeZone()))) {
            localLoginWith(userName, password);
        } else {
            loginWithLocalFlag(view, false, userName, password);
        }
    }

    private void localLoginWith(String userName, String password) {

        getUserService().localLogin(userName, password);
        getLoginView().goToHome(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(getClass().getName(), "Starting DrishtiSyncScheduler " + DateTime.now().toString());
                if (NetworkUtils.isNetworkAvailable()) {
                    SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);
                }
                Log.i(getClass().getName(), "Started DrishtiSyncScheduler " + DateTime.now().toString());
            }
        }).start();
    }

    private void remoteLogin(final String userName, final String password) {

        try {
            if (!getSharedPreferences().fetchBaseURL("").isEmpty()) {
                tryRemoteLogin(userName, password, new Listener<LoginResponse>() {

                    public void onEvent(LoginResponse loginResponse) {
                        getLoginView().enableLoginButton(true);
                        if (loginResponse == LoginResponse.SUCCESS) {
                            if (getUserService().isUserInPioneerGroup(userName)) {
                                TimeStatus timeStatus = getUserService().validateDeviceTime(
                                        loginResponse.payload(), Constants.MAX_SERVER_TIME_DIFFERENCE
                                );
                                if (!Constants.TIME_CHECK || timeStatus.equals(TimeStatus.OK)) {
                                    remoteLoginWith(userName, password,
                                            loginResponse.payload());
                                    AncApplication.getInstance().startPullUniqueIdsService();
                                } else {
                                    if (timeStatus.equals(TimeStatus.TIMEZONE_MISMATCH)) {
                                        TimeZone serverTimeZone = mLoginPresenter.getOpenSRPContext().userService()
                                                .getServerTimeZone(loginResponse.payload());
                                        getLoginView().showErrorDialog(getApplicationContext().getString(timeStatus.getMessage(),
                                                serverTimeZone.getDisplayName()));
                                    } else {
                                        getLoginView().showErrorDialog(getApplicationContext().getString(timeStatus.getMessage()));
                                    }
                                }
                            } else {
                                // Valid user from wrong group trying to log in
                                getLoginView().showErrorDialog(getApplicationContext().getString(R.string.unauthorized_group));
                            }
                        } else {
                            if (loginResponse == null) {
                                getLoginView().showErrorDialog("Sorry, your loginWithLocalFlag failed. Please try again");
                            } else {
                                if (loginResponse == NO_INTERNET_CONNECTIVITY) {
                                    getLoginView().showErrorDialog(getApplicationContext().getResources().getString(R.string.no_internet_connectivity));
                                } else if (loginResponse == UNKNOWN_RESPONSE) {
                                    getLoginView().showErrorDialog(getApplicationContext().getResources().getString(R.string.unknown_response));
                                } else if (loginResponse == UNAUTHORIZED) {
                                    getLoginView().showErrorDialog(getApplicationContext().getResources().getString(R.string.unauthorized));
                                } else {
                                    getLoginView().showErrorDialog(loginResponse.message());
                                }
                            }
                        }
                    }
                });
            } else {
                getLoginView().enableLoginButton(true);
                getLoginView().showErrorDialog("OpenSRP Base URL is missing. Please add it in Setting and try again");
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());

            getLoginView().showErrorDialog("Error occurred trying to loginWithLocalFlag in. Please try again...");
        }
    }

    private void tryRemoteLogin(final String userName, final String password, final Listener<LoginResponse> afterLogincheck) {
        if (remoteLoginTask != null && !remoteLoginTask.isCancelled()) {
            remoteLoginTask.cancel(true);
        }
        remoteLoginTask = new RemoteLoginTask(getLoginView(), userName, password, afterLogincheck);
        remoteLoginTask.execute();
    }

    private void remoteLoginWith(String userName, String password, LoginResponseData userInfo) {
        getUserService().remoteLogin(userName, password, userInfo);
        getLoginView().goToHome(true);
        if (NetworkUtils.isNetworkAvailable()) {
            SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);
        }
    }

    public Context getApplicationContext() {
        return AncApplication.getInstance().getApplicationContext();
    }

    public AllSharedPreferences getSharedPreferences() {
        return mLoginPresenter.getOpenSRPContext().allSharedPreferences();
    }

    public LoginContract.View getLoginView() {
        return mLoginPresenter.getLoginView();
    }

    public UserService getUserService() {
        return mLoginPresenter.getOpenSRPContext().userService();
    }
}
