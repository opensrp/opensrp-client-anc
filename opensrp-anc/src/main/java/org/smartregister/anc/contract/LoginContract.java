package org.smartregister.anc.contract;

import android.app.Activity;

import org.smartregister.Context;

import java.lang.ref.WeakReference;

/**
 * Created by ndegwamartin on 22/06/2018.
 */
public interface LoginContract {

    interface Presenter {

        void attemptLogin(String username, String password);

        View getLoginView();

        void onDestroy(boolean isChangingConfiguration);

        boolean isUserLoggedOut();

        void processViewCustomizations();

        void positionViews();

        void setLanguage();

        Context getOpenSRPContext();

    }

    interface View {

        void setUsernameError(int resourceId);

        void resetUsernameError();

        void setPasswordError(int resourceId);

        void resetPaswordError();

        void showProgress(final boolean show);

        void hideKeyboard();

        void showErrorDialog(String message);

        void enableLoginButton(boolean isClickable);

        void goToHome(boolean isRemote);

        Activity getActivityContext();
    }

    interface Interactor {

        void onDestroy(boolean isChangingConfiguration);

        void login(WeakReference<View> view, String userName, String password);
    }

    interface Model {

        boolean isEmptyUsername(String username);

        boolean isPasswordValid(String password);

        Context getOpenSRPContext();

        boolean isUserLoggedOut();

    }
}
