package org.smartregister.anc.presenter;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.contract.ContactContract;
import org.smartregister.anc.interactor.ContactInteractor;
import org.smartregister.anc.util.DBConstants;

import java.lang.ref.WeakReference;
import java.util.Map;

public class ContactPresenter implements ContactContract.Presenter, ContactContract.InteractorCallBack {

    private static final String TAG = ProfilePresenter.class.getCanonicalName();

    private WeakReference<ContactContract.View> viewReference;
    private ContactContract.Interactor contactInteractor;

    private String baseEntityId;

    public ContactPresenter(ContactContract.View contactView) {
        viewReference = new WeakReference<>(contactView);
        contactInteractor = new ContactInteractor();
    }

    @Override
    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;

        fetchPatient(baseEntityId);
    }

    @Override
    public boolean baseEntityIdExists() {
        return StringUtils.isNotBlank(baseEntityId);
    }

    @Override
    public void fetchPatient(String baseEntityId) {
        contactInteractor.fetchWomanDetails(baseEntityId, this);
    }

    @Override
    public void onWomanDetailsFetched(Map<String, String> womanDetails) {
        if (womanDetails == null || womanDetails.isEmpty()) {
            return;
        }

        String patientName = extractNames(womanDetails);
        getView().displayPatientName(patientName);

    }

    private String extractNames(Map<String, String> womanDetails) {
        String firstName = womanDetails.get(DBConstants.KEY.FIRST_NAME);
        String lastName = womanDetails.get(DBConstants.KEY.LAST_NAME);

        String patientName = "";
        if (StringUtils.isBlank(firstName) && StringUtils.isBlank(lastName)) {
            return patientName;
        }

        if (StringUtils.isBlank(firstName)) {
            patientName = lastName;
        } else if (StringUtils.isBlank(lastName)) {
            patientName = firstName;
        } else {
            patientName = firstName + " " + lastName;
        }
        return patientName;
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        viewReference = null;//set to null on destroy

        // Activity destroyed set interactor to null
        if (!isChangingConfiguration) {
            contactInteractor = null;
        }
    }

    private ContactContract.View getView() {
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
    }

}
