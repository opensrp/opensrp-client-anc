package org.smartregister.anc.contract;

import org.json.JSONObject;
import org.smartregister.anc.domain.Contact;

import java.util.Map;

public interface ContactContract {

    interface View {
        void displayPatientName(String patientName);

        void startFormActivity(JSONObject form);

        void displayToast(int resourceId);
    }

    interface Presenter {
        void fetchPatient(String baseEntityId);

        void setBaseEntityId(String baseEntityId);

        boolean baseEntityIdExists();

        String getPatientName();

        void startForm(Object tag);

        void onDestroy(boolean isChangingConfiguration);
    }

    interface Model {
        String extractPatientName(Map<String, String> womanDetails);

        JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception;
    }

    interface Interactor {
        void fetchWomanDetails(String baseEntityId, InteractorCallBack callBack);
    }

    interface InteractorCallBack {
        void onWomanDetailsFetched(Map<String, String> womanDetails);
    }
}
