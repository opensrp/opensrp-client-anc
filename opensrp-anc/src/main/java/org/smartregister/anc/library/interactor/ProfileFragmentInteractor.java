package org.smartregister.anc.library.interactor;

import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.contract.ProfileFragmentContract;
import org.smartregister.anc.library.model.Task;
import org.smartregister.anc.library.task.FetchProfileDataTask;

import java.util.List;

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

    @Override
    public List<Task> getContactTasks(String baseEntityId, String contactNo) {
        return AncLibrary.getInstance().getContactTasksRepositoryHelper().getTasks(baseEntityId, null);
    }


    public ProfileFragmentContract.View getProfileView() {
        return mProfileFrgamentPresenter.getProfileView();
    }
}
