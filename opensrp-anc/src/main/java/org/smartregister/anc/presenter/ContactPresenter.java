package org.smartregister.anc.presenter;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.smartregister.anc.R;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.ContactContract;
import org.smartregister.anc.domain.Contact;
import org.smartregister.anc.interactor.ContactInteractor;
import org.smartregister.anc.model.ContactModel;
import org.smartregister.anc.repository.PatientRepository;
import org.smartregister.anc.rule.ContactRule;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class ContactPresenter implements ContactContract.Presenter, ContactContract.InteractorCallback {

    public static final String TAG = ContactPresenter.class.getName();

    private WeakReference<ContactContract.View> viewReference;
    private ContactContract.Interactor interactor;
    private ContactContract.Model model;

    private String baseEntityId;

    private Map<String, String> details;

    public ContactPresenter(ContactContract.View contactView) {
        viewReference = new WeakReference<>(contactView);
        interactor = new ContactInteractor();
        model = new ContactModel();
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
    public void onWomanDetailsFetched(Map<String, String> womanDetails) {
        if (womanDetails == null || womanDetails.isEmpty()) {
            return;
        }

        this.details = womanDetails;
        String patientName = model.extractPatientName(womanDetails);
        getView().displayPatientName(patientName);

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
            if (tag == null || !(tag instanceof Contact)) {
                return;
            }

            Contact contact = (Contact) tag;
            if (contact.getName().equals(getView().getString(R.string.quick_check))) {
                getView().startQuickCheck(contact);
            } else {
                JSONObject form = model.getFormAsJson(contact.getFormName(), baseEntityId, null);
                getView().startFormActivity(form, contact);
            }

        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
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
    public void finalizeContactForm(CommonPersonObjectClient pc) {
        try {


            String baseEntityId = pc.getCaseId();

            int ga = pc.getColumnmaps().containsKey(DBConstants.KEY.EDD) && pc.getColumnmaps().get(DBConstants.KEY.EDD) != null ? Utils.getGestationAgeFromEDDate(pc.getColumnmaps().get(DBConstants.KEY.EDD)) : 4;
            ContactRule contactRule = new ContactRule(ga, pc.getColumnmaps().get(DBConstants.KEY.NEXT_CONTACT) == null, baseEntityId);
            List<Integer> integerList = AncApplication.getInstance().getRulesEngineHelper().getContactVisitSchedule(contactRule, "contact-rules.yml");

            int nextContactVisitWeeks = integerList.get(0);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.DETAILS_KEY.CONTACT_SHEDULE, integerList);

            //convert String to LocalDate ;
            LocalDate localDate = new LocalDate(pc.getColumnmaps().get(DBConstants.KEY.EDD));
            String nextContactVisitDate = localDate.minusWeeks(Constants.DELIVERY_DATE_WEEKS).plusWeeks(nextContactVisitWeeks).toString();

            Integer nextContact = pc.getColumnmaps().containsKey(DBConstants.KEY.NEXT_CONTACT) && pc.getColumnmaps().get(DBConstants.KEY.NEXT_CONTACT) != null ? Integer.valueOf(pc.getColumnmaps().get(DBConstants.KEY.NEXT_CONTACT)) : 0;
            nextContact += 1;

            PatientRepository.updateContactVisitDetails(baseEntityId, nextContact, nextContactVisitDate);

            AncApplication.getInstance().getDetailsRepository().add(baseEntityId, Constants.DETAILS_KEY.CONTACT_SHEDULE, jsonObject.toString(), Calendar.getInstance().getTimeInMillis());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void deleteDraft(String baseEntityId) {

        getAncApplication().getPartialContactRepository().deleteDraftJson(baseEntityId);
    }

    @Override
    public void saveFinalJson(String baseEntityId) {

        getAncApplication().getPartialContactRepository().saveFinalJson(baseEntityId);
    }

    private ContactContract.View getView() {
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
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

    protected AncApplication getAncApplication() {
        return AncApplication.getInstance();
    }
}
