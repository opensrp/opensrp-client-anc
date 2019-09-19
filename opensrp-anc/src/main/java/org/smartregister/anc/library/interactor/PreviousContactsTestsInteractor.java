package org.smartregister.anc.library.interactor;

import org.smartregister.anc.library.contract.PreviousContactsTests;
import org.smartregister.anc.library.task.FetchProfileDataTask;

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
