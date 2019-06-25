package org.smartregister.anc.interactor;

import org.smartregister.anc.contract.PreviousContactsDetails;
import org.smartregister.anc.task.FetchProfileDataTask;

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
