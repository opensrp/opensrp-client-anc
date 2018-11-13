package org.smartregister.anc.contract;

import org.json.JSONObject;
import org.smartregister.anc.domain.Contact;

import java.util.Map;

public interface ContactContract {

    interface View {
        void displayPatientName(String patientName);

        void startFormActivity(JSONObject form, Contact contact);

        void startQuickCheck(Contact contact);

        void displayToast(int resourceId);

        String getString(int resourceId);
    }

    interface Presenter {
        void fetchPatient(String baseEntityId);

        void setBaseEntityId(String baseEntityId);

        boolean baseEntityIdExists();

        String getPatientName();

        void startForm(Object tag);

        void onDestroy(boolean isChangingConfiguration);

        void finalizeContactForm(Map<String, String> details);

        void deleteDraft(String baseEntityId);

        void saveFinalJson(String baseEntityId);
    }

    interface Model {
        String extractPatientName(Map<String, String> womanDetails);

        JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception;
    }

    interface Interactor extends BaseContactContract.Interactor {
        void finalizeContactForm(Map<String, String> details);
    }

    interface InteractorCallback extends BaseContactContract.InteractorCallback {
    }

}
