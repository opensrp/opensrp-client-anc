package org.smartregister.anc.presenter;

import org.smartregister.anc.contract.PreviousContacts;
import org.smartregister.anc.interactor.ContactInteractor;
import org.smartregister.anc.interactor.PreviousContactsInteractor;

import java.lang.ref.WeakReference;

public class PreviousContactsPresenter implements PreviousContacts.Presenter {
    private static final String TAG = ProfilePresenter.class.getCanonicalName();

    private WeakReference<PreviousContacts.View> previousContactsView;
    private PreviousContacts.Interactor previousContactsInteractor;
    private ContactInteractor contactInteractor;

    public PreviousContactsPresenter(PreviousContacts.View previousContactsView) {
        this.previousContactsView = new WeakReference<>(previousContactsView);
        ;
        this.previousContactsInteractor = new PreviousContactsInteractor(this);
        this.contactInteractor = new ContactInteractor();
    }
}
