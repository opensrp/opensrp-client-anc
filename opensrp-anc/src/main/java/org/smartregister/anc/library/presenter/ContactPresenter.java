package org.smartregister.anc.library.presenter;

import android.content.Context;
import android.util.Log;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.contract.ContactContract;
import org.smartregister.anc.library.domain.Contact;
import org.smartregister.anc.library.interactor.ContactInteractor;
import org.smartregister.anc.library.model.ContactModel;
import org.smartregister.anc.library.model.Task;
import org.smartregister.anc.library.util.ConstantsUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
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
                JSONObject form = model.getFormAsJson(contact.getFormName(), baseEntityId, null);
                if (contact.getGlobals() != null) {
                    for (Map.Entry<String, String> entry : contact.getGlobals().entrySet()) {
                        defaultGlobals.put(entry.getKey(), entry.getValue());
                    }
                }

                if (form != null) {
                    if (ConstantsUtils.JsonFormUtils.ANC_TEST.equals(contact.getFormName()) && contact.getContactNumber() > 1) {
                        List<Task> currentTasks = AncLibrary.getInstance().getContactTasksRepositoryHelper().getClosedTasks(baseEntityId, String.valueOf(contact.getContactNumber() - 1));
                        removeDueTests(form, currentTasks);
                    }

                    form.put(JsonFormConstants.JSON_FORM_KEY.GLOBAL, defaultGlobals);
                    getView().startFormActivity(form, contact);
                }
            } catch (JSONException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            getView().displayToast(R.string.error_unable_to_start_form);
        }
    }

    private void removeDueTests(JSONObject formObject, List<Task> taskList) {
        try {
            Map<String, JSONObject> keys = taskHashMap(taskList);
            if (formObject != null && taskList != null && taskList.size() > 0 && formObject.has(JsonFormConstants.STEP1)) {
                JSONObject dueStep = formObject.getJSONObject(JsonFormConstants.STEP1);
                if (dueStep.has(JsonFormConstants.FIELDS)) {
                    JSONArray fields = dueStep.getJSONArray(JsonFormConstants.FIELDS);
                    for (int i = 0; i < fields.length(); i++) {
                        JSONObject field = fields.getJSONObject(i);
                        if (field != null && field.has(JsonFormConstants.KEY)) {
                            String fieldKey = field.getString(JsonFormConstants.KEY);
                            if (keys.containsKey(fieldKey)) {
                                fields.remove(i);
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Timber.e(e, " --> removeDueTests");
        }
    }

    private Map<String, JSONObject> taskHashMap(List<Task> taskList) {
        Map<String, JSONObject> taskMap = new HashMap<>();
        try {
            if (taskList != null && taskList.size() > 0) {
                for (int i = 0; i < taskList.size(); i++) {
                    Task task = taskList.get(i);
                    String taskKey = task.getKey();
                    JSONObject taskValue = new JSONObject(task.getValue());
                    taskMap.put(taskKey, taskValue);
                }
            }
        } catch (JSONException e) {
            Timber.e(e, " --> taskHashMap");
        }

        return taskMap;
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
        getAncLibrary().getPartialContactRepositoryHelper().deleteDraftJson(baseEntityId);
    }

    @Override
    public void saveFinalJson(String baseEntityId) {
        getAncLibrary().getPartialContactRepositoryHelper().saveFinalJson(baseEntityId);
    }

    @Override
    public int getGestationAge() {
        return interactor.getGestationAge(details);
    }


}
