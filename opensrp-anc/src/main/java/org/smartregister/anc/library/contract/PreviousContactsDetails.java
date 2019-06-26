package org.smartregister.anc.library.contract;

import org.jeasy.rules.api.Facts;
import org.json.JSONException;
import org.smartregister.anc.library.model.ContactSummaryModel;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface PreviousContactsDetails {
    interface Presenter {
        PreviousContactsDetails.View getProfileView();

        void loadPreviousContactSchedule(String baseEntityId, String contactNo, String edd);

        void loadPreviousContacts(String baseEntityId, String contactNo) throws IOException, ParseException, JSONException;

    }

    interface View {
        void displayPreviousContactSchedule(List<ContactSummaryModel> schedule);

        void loadPreviousContactsDetails(Map<String, List<Facts>> contactFacts) throws IOException, ParseException;
    }

    interface Interactor {
        void onDestroy(boolean isChangingConfiguration);

        void refreshProfileView(String baseEntityId, boolean isForEdit);
    }
}
