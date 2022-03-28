package org.smartregister.anc.library.presenter;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.contract.ContactContract;
import org.smartregister.anc.library.domain.Contact;
import org.smartregister.anc.library.interactor.ContactInteractor;
import org.smartregister.anc.library.model.ContactModel;

import java.lang.ref.WeakReference;
import java.util.Map;

import timber.log.Timber;

public class ContactPresenter implements ContactContract.Presenter, ContactContract.InteractorCallback {

    public static final String TAG = ContactPresenter.class.getName();

    private WeakReference<ContactContract.View> viewReference;
    private ContactContract.Interactor interactor;
    private ContactContract.Model model;

    private String baseEntityId;

    private Map<String, String> details;
    private JSONObject defaultGlobals;

    public ContactPresenter(ContactContract.View contactView) {
        viewReference = new WeakReference<>(contactView);
        interactor = new ContactInteractor();
        model = new ContactModel();
        defaultGlobals = getAncLibrary().getDefaultContactFormGlobals();
    }

    protected AncLibrary getAncLibrary() {
        return AncLibrary.getInstance();
    }

    @Override
    public void onWomanDetailsFetched(Map<String, String> womanDetails) {
        if (womanDetails == null || womanDetails.isEmpty()) {
            return;
        }

        this.details = womanDetails;
        String patientName = model.extractPatientName(womanDetails);
        getView().displayPatientName(patientName);

    }

    private ContactContract.View getView() {
        if (viewReference != null) return viewReference.get();
        else return null;
    }

    // Test methods
    public WeakReference<ContactContract.View> getViewReference() {
        return viewReference;
    }

    public ContactContract.Interactor getInteractor() {
        return interactor;
    }

    public void setInteractor(ContactContract.Interactor interactor) {
        this.interactor = interactor;
    }

    public void setModel(ContactContract.Model model) {
        this.model = model;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
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
        interactor.fetchWomanDetails(baseEntityId, this);
    }


    @Override
    public String getPatientName() {
        if (details == null || details.isEmpty()) {
            return "";
        }
        return model.extractPatientName(details);
    }


    @Override
    public void startForm(Object tag) {
        try {
            if (!(tag instanceof Contact) && getView() == null) {
                return;
            }
            Contact contact = (Contact) tag;
            getView().loadGlobals(contact);
            try {
                String locationId = AncLibrary.getInstance().getContext().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
                JSONObject form = model.getFormAsJson(contact.getFormName(), baseEntityId, locationId);
                if (contact.getGlobals() != null) {
                    for (Map.Entry<String, String> entry : contact.getGlobals().entrySet()) {
                        defaultGlobals.put(entry.getKey(), entry.getValue());
                    }
                }

                if (form != null) {
                    form.put(JsonFormConstants.JSON_FORM_KEY.GLOBAL, defaultGlobals);
                    getView().startFormActivity(form, contact);
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        } catch (Exception e) {
            Timber.e(e);
            getView().displayToast(R.string.error_unable_to_start_form);
        }
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        viewReference = null;//set to null on destroy

        // Activity destroyed set interactor to null
        if (!isChangingConfiguration) {
            interactor = null;
        }
    }


    @Override
    public void finalizeContactForm(Map<String, String> details, Context context) {
        interactor.finalizeContactForm(details, context);
    }


    public void deleteDraft(String baseEntityId) {
        getAncLibrary().getPartialContactRepository().deleteDraftJson(baseEntityId);
    }

    @Override
    public void saveFinalJson(String baseEntityId) {
        getAncLibrary().getPartialContactRepository().saveFinalJson(baseEntityId);
    }

    @Override
    public int getGestationAge() {
        return interactor.getGestationAge(details);
    }


}
