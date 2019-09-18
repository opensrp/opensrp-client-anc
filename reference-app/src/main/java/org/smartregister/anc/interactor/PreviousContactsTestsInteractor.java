package org.smartregister.anc.interactor;

import org.smartregister.anc.contract.PreviousContactsTests;
import org.smartregister.anc.task.FetchProfileDataTask;

public class PreviousContactsTestsInteractor implements PreviousContactsTests.Interactor {
    private PreviousContactsTests.Presenter previousContactsTestsPresenter;

    public PreviousContactsTestsInteractor(PreviousContactsTests.Presenter presenter) {
        this.previousContactsTestsPresenter = presenter;
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        if (!isChangingConfiguration) {
            previousContactsTestsPresenter = null;
        }
    }

    @Override
    public void refreshProfileView(String baseEntityId, boolean isForEdit) {
        new FetchProfileDataTask(isForEdit).execute(baseEntityId);
    }


    public PreviousContactsTests.View getProfileView() {
        return previousContactsTestsPresenter.getProfileView();
    }
}
