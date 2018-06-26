package org.smartregister.anc.contract;

/**
 * Created by ndegwamartin on 22/06/2018.
 */
public interface LoginContract {

    interface Presenter {

        void attemptLogin(String username, String password);

        View getLoginView();
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

        boolean isPasswordValid(String password);

        boolean isEmptyUsername(String username);
    }

}
