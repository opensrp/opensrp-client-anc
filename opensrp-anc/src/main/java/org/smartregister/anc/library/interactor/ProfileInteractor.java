package org.smartregister.anc.library.interactor;

import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.contract.ProfileContract;
import org.smartregister.anc.library.task.FetchProfileDataTask;

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
    public void refreshProfileView(String baseEntityId, boolean isForEdit) {
        new FetchProfileDataTask(isForEdit).execute(baseEntityId);
    }

    @Override
    public String getTaskCount(String baseEntityId) {
        return AncLibrary.getInstance().getContactTasksRepository().getTasksCount(baseEntityId);
    }


    public ProfileContract.View getProfileView() {
        return mProfilePresenter.getProfileView();
    }
}
