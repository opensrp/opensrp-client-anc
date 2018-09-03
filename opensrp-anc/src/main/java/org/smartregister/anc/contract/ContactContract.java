package org.smartregister.anc.contract;

import java.util.Map;

public interface ContactContract {

    interface View {
        void displayPatientName(String patientName);
    }

    interface Presenter {
        void fetchPatient(String baseEntityId);

        void setBaseEntityId(String baseEntityId);

        boolean baseEntityIdExists();

        String getPatientName();

        void onDestroy(boolean isChangingConfiguration);
    }

    interface Model {
        String extractPatientName(Map<String, String> womanDetails);
    }

    interface Interactor {
        void fetchWomanDetails(String baseEntityId, InteractorCallBack callBack);
    }

    interface InteractorCallBack {
        void onWomanDetailsFetched(Map<String, String> womanDetails);
    }
}
