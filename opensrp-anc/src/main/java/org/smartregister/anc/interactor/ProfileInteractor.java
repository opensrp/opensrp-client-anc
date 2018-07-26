package org.smartregister.anc.interactor;

import org.smartregister.anc.contract.ProfileContract;
import org.smartregister.anc.task.FetchProfileDataTask;

/**
 * Created by ndegwamartin on 13/07/2018.
 */
public class ProfileInteractor implements ProfileContract.Interactor {
    private ProfileContract.Presenter mProfilePresenter;

    public ProfileInteractor(ProfileContract.Presenter loginPresenter) {
        this.mProfilePresenter = loginPresenter;
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        if (!isChangingConfiguration) {
            mProfilePresenter = null;
        }
    }

    @Override
    public void refreshProfileView(String baseEntityId) {

        new FetchProfileDataTask(false).execute(baseEntityId);

    }


    public ProfileContract.View getProfileView() {
        return mProfilePresenter.getProfileView();
    }
}
