package org.smartregister.anc.library.interactor;

import org.smartregister.anc.library.contract.PreviousContactsDetails;
import org.smartregister.anc.library.task.FetchProfileDataTask;

public class PreviousContactsDetailsInteractor implements PreviousContactsDetails.Interactor {
    private PreviousContactsDetails.Presenter previousContactsPresenter;

    public PreviousContactsDetailsInteractor(PreviousContactsDetails.Presenter presenter) {
        this.previousContactsPresenter = presenter;
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        if (!isChangingConfiguration) {
            previousContactsPresenter = null;
        }
    }

    @Override
    public void refreshProfileView(String baseEntityId, boolean isForEdit) {
        new FetchProfileDataTask(isForEdit).execute(baseEntityId);
    }


    public PreviousContactsDetails.View getProfileView() {
        return previousContactsPresenter.getProfileView();
    }
}
