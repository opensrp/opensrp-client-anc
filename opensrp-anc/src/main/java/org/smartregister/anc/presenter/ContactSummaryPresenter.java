package org.smartregister.anc.presenter;

import org.smartregister.anc.contract.ContactSummarySendContract;
import org.smartregister.anc.model.ContactSummaryModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactSummaryPresenter implements ContactSummarySendContract.Presenter,
        ContactSummarySendContract.InteractorCallback {

    private WeakReference<ContactSummarySendContract.View> contactConfirmationView;
    private ContactSummarySendContract.Interactor contactSummaryInteractor;
    private ContactSummaryModel contactSummaryModel;
    private Map<String, String> womanDetails = new HashMap<>();
    private List<ContactSummaryModel> upcomingContacts =  new ArrayList<>();


    public ContactSummaryPresenter(ContactSummarySendContract.Interactor interactor) {
        this.contactSummaryInteractor = interactor;
        this.contactSummaryModel = new ContactSummaryModel();
    }

    private void setWomansName() {
        getView().displayWomansName(contactSummaryModel.extractPatientName(this.womanDetails));
    }
    private void addUpcomingContactsToView(){
        getView().displayUpcomingContactDates(this.upcomingContacts);
    }

    public Map<String, String> getWomanDetails() {
        return womanDetails;
    }

    public List<ContactSummaryModel> getUpcomingContacts() {
        return upcomingContacts;
    }

    public ContactSummarySendContract.View getView() {
        return contactConfirmationView.get();
    }
    @Override
    public void loadWoman(String entityId) {
        contactSummaryInteractor.fetchWomanDetails(entityId, this);
    }

    @Override
    public void loadUpcomingContacts(String entityId) {
        contactSummaryInteractor.fetchUpcomingContacts(entityId,this);

    }

    @Override
    public void attachView(ContactSummarySendContract.View view) {
        this.contactConfirmationView = new WeakReference<>(view);
    }

    @Override
    public void showWomanProfileImage(String clientEntityId) {
        if (clientEntityId == null || clientEntityId.isEmpty()){
            return;
        }
        getView().setProfileImage(clientEntityId);
    }

    @Override
    public void onWomanDetailsFetched(Map<String, String> womanDetails) {
        if (womanDetails == null || womanDetails.isEmpty()) {
            return;
        }
        this.womanDetails = womanDetails;
        setWomansName();
    }

    @Override
    public void onUpcomingContactsFetched(List<ContactSummaryModel> upcomingContacts, Integer lastContact) {
        if(upcomingContacts == null || upcomingContacts.isEmpty()){
            return;
        }
        this.upcomingContacts.addAll(upcomingContacts);
        addUpcomingContactsToView();
        getView().updateRecordedContact(lastContact);
    }
}
