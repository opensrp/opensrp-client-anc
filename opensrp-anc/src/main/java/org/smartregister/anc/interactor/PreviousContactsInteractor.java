package org.smartregister.anc.interactor;

import org.smartregister.anc.contract.PreviousContacts;

public class PreviousContactsInteractor implements PreviousContacts.Interactor {
    private PreviousContacts.Presenter previousContactsPresenter;

    public PreviousContactsInteractor(PreviousContacts.Presenter presenter) {
        this.previousContactsPresenter = presenter;
    }
}
