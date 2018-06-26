package org.smartregister.anc.presenter;

import org.smartregister.anc.R;
import org.smartregister.anc.contract.LoginContract;
import org.smartregister.anc.interactor.LoginInteractor;
import org.smartregister.anc.interactor.LoginInteractorImpl;

import java.lang.ref.WeakReference;

/**
 * Created by ndegwamartin on 22/06/2018.
 */
public class LoginPresenter implements LoginContract.Presenter {

    private WeakReference<LoginContract.View> mLoginView;

    private LoginInteractor mLoginInteractor;

    public LoginPresenter(LoginContract.View loginView) {
        mLoginView = new WeakReference<>(loginView);
        mLoginInteractor = new LoginInteractorImpl(this);
    }

    public void onDestroy(boolean isChangingConfiguration) {

        mLoginView = null;//set to null on destroy
        // Inform interactor
        mLoginInteractor.onDestroy(isChangingConfiguration);
        // Activity destroyed set interactor to null
        if (!isChangingConfiguration) {
            mLoginInteractor = null;
        }
    }

    @Override
    public void attemptLogin(String username, String password) {
        // Reset errors.
        getLoginView().resetUsernameError();
        getLoginView().resetPaswordError();

        boolean cancel = false;

        // Check for a valid password, if the user entered one.
        if (!getLoginView().isPasswordValid(password)) {
            getLoginView().setPasswordError(R.string.error_invalid_password);
            cancel = true;
        }

        // Check for a valid username
        if (getLoginView().isEmptyUsername(username)) {
            getLoginView().setUsernameError(R.string.error_field_required);
            cancel = true;
            getLoginView().enableLoginButton(true);
        }

        if (!cancel) {
            mLoginInteractor.login(mLoginView, username, password);


        }
    }

    @Override
    public LoginContract.View getLoginView() {
        if (mLoginView != null)
            return mLoginView.get();
        else
            return null;
    }

}
