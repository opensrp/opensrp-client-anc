package org.smartregister.anc.library.contract;

import android.content.Context;

import org.json.JSONObject;
import org.smartregister.anc.library.domain.Contact;

import java.util.HashMap;
import java.util.Map;

public interface ContactContract {

    interface View {
        void displayPatientName(String patientName);

        void startFormActivity(JSONObject form, Contact contact);

        void displayToast(int resourceId);

        String getString(int resourceId);

        void loadGlobals(Contact contact);

    }

    interface Presenter {
        void fetchPatient(String baseEntityId);

        void setBaseEntityId(String baseEntityId);

        boolean baseEntityIdExists();

        String getPatientName();

        void startForm(Object tag);

        void onDestroy(boolean isChangingConfiguration);

        void finalizeContactForm(Map<String, String> details, Context context);

        void deleteDraft(String baseEntityId);

        void saveFinalJson(String baseEntityId);

        int getGestationAge();
    }

    interface Model {
        String extractPatientName(Map<String, String> womanDetails);

        JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception;
    }

    interface Interactor extends BaseContactContract.Interactor {
        HashMap<String, String> finalizeContactForm(Map<String, String> details, Context context);

        int getGestationAge(Map<String, String> details);
    }

    interface InteractorCallback extends BaseContactContract.InteractorCallback {
    }

}
