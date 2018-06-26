package org.smartregister.anc.interactor;

import org.smartregister.anc.contract.LoginContract;

import java.lang.ref.WeakReference;

/**
 * Created by ndegwamartin on 26/06/2018.
 */
public interface LoginInteractor {
    void onDestroy(boolean isChangingConfiguration);

    void login(WeakReference<LoginContract.View> view, String userName, String password);
}
