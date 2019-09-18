package org.smartregister.anc.interactor;

import org.smartregister.anc.contract.ProfileFragmentContract;
import org.smartregister.anc.task.FetchProfileDataTask;

/**
 * Created by ndegwamartin on 13/07/2018.
 */
public class ProfileFragmentInteractor implements ProfileFragmentContract.Interactor {
    private ProfileFragmentContract.Presenter mProfileFrgamentPresenter;

    public ProfileFragmentInteractor(ProfileFragmentContract.Presenter presenter) {
        this.mProfileFrgamentPresenter = presenter;
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        if (!isChangingConfiguration) {
            mProfileFrgamentPresenter = null;
        }
    }

    @Override
    public void refreshProfileView(String baseEntityId, boolean isForEdit) {
        new FetchProfileDataTask(isForEdit).execute(baseEntityId);
    }


    public ProfileFragmentContract.View getProfileView() {
        return mProfileFrgamentPresenter.getProfileView();
    }
}
