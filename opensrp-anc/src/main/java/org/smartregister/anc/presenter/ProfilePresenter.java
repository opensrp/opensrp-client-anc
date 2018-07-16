package org.smartregister.anc.presenter;

import org.smartregister.anc.contract.ProfileContract;
import org.smartregister.anc.interactor.ProfileInteractor;

import java.lang.ref.WeakReference;

/**
 * Created by ndegwamartin on 13/07/2018.
 */
public class ProfilePresenter implements ProfileContract.Presenter {

    private WeakReference<ProfileContract.View> mProfileView;
    private ProfileContract.Interactor mProfileInteractor;

    public ProfilePresenter(ProfileContract.View loginView) {
        mProfileView = new WeakReference<>(loginView);
        mProfileInteractor = new ProfileInteractor(this);
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {

        mProfileView = null;//set to null on destroy

        // Inform interactor
        mProfileInteractor.onDestroy(isChangingConfiguration);

        // Activity destroyed set interactor to null
        if (!isChangingConfiguration) {
            mProfileInteractor = null;
        }

    }

    @Override
    public void refreshProfileView(String baseEntityId) {
        mProfileInteractor.refreshProfileView(baseEntityId);
    }

    @Override
    public ProfileContract.View getProfileView() {
        if (mProfileView != null) {
            return mProfileView.get();
        } else {
            return null;
        }
    }

}
