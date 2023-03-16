package org.smartregister.anc.library.presenter;

import org.smartregister.anc.library.contract.ContactSummarySendContract;
import org.smartregister.anc.library.model.ContactSummaryModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactSummaryPresenter
        implements ContactSummarySendContract.Presenter, ContactSummarySendContract.InteractorCallback {

    private WeakReference<ContactSummarySendContract.View> contactConfirmationView;
    private ContactSummarySendContract.Interactor contactSummaryInteractor;
    private ContactSummaryModel contactSummaryModel;
    private Map<String, String> womanDetails = new HashMap<>();
    private List<ContactSummaryModel> upcomingContacts = new ArrayList<>();


    public ContactSummaryPresenter(ContactSummarySendContract.Interactor interactor) {
        this.contactSummaryInteractor = interactor;
        this.contactSummaryModel = new ContactSummaryModel();
    }

    public Map<String, String> getWomanDetails() {
        return womanDetails;
    }

    public List<ContactSummaryModel> getUpcomingContacts() {
        return upcomingContacts;
    }

    @Override
    public void loadWoman(String entityId) {
        contactSummaryInteractor.fetchWomanDetails(entityId, this);
    }

    @Override
    public void loadUpcomingContacts(String entityId, String referralContactNo) {
        contactSummaryInteractor.fetchUpcomingContacts(entityId, referralContactNo, this);

    }

    @Override
    public void attachView(ContactSummarySendContract.View view) {
        this.contactConfirmationView = new WeakReference<>(view);
    }

    @Override
    public void showWomanProfileImage(String clientEntityId) {
        if (clientEntityId == null || clientEntityId.isEmpty()) {
            return;
        }
        getView().setProfileImage(clientEntityId);
    }

    public ContactSummarySendContract.View getView() {
        return contactConfirmationView.get();
    }

    @Override
    public void onWomanDetailsFetched(Map<String, String> womanDetails) {
        if (womanDetails == null || womanDetails.isEmpty()) {
            return;
        }
        this.womanDetails = womanDetails;
        setWomansName();
    }

    private void setWomansName() {
        getView().displayPatientName(contactSummaryModel.extractPatientName(this.womanDetails));
    }

    @Override
    public void onUpcomingContactsFetched(List<ContactSummaryModel> upcomingContacts, Integer lastContact) {
        if ((upcomingContacts == null || upcomingContacts.isEmpty()) && lastContact >= 0) {
            return;
        }
        this.upcomingContacts.clear();
        for (ContactSummaryModel contactSummaryModel : upcomingContacts) {
            if (!this.upcomingContacts.contains(contactSummaryModel)) {
                this.upcomingContacts.add(contactSummaryModel);
            }
        }
        addUpcomingContactsToView();
        getView().updateRecordedContact(lastContact);
    }

    private void addUpcomingContactsToView() {
        getView().displayUpcomingContactDates(this.upcomingContacts);
    }
}
