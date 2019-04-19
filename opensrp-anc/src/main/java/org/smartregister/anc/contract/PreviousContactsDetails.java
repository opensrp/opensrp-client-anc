package org.smartregister.anc.contract;

import org.jeasy.rules.api.Facts;
import org.json.JSONException;
import org.smartregister.anc.model.ContactSummaryModel;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface PreviousContactsDetails {
    interface Presenter {
        PreviousContactsDetails.View getProfileView();

        void loadPreviousContactSchedule(String baseEntityId, String contactNo, String edd);

        void loadPreviousContacts(String baseEntityId, String contactNo) throws IOException, ParseException, JSONException;
    }

    interface View {
        void displayPreviousContactSchedule(List<ContactSummaryModel> schedule);

        void loadPreviousContactsTest(Facts attentionFlagsFacts, Facts contactFacts, String specificContactNo)
        throws IOException, ParseException;
    }

    interface Interactor {
        void onDestroy(boolean isChangingConfiguration);

        void refreshProfileView(String baseEntityId, boolean isForEdit);
    }
}
