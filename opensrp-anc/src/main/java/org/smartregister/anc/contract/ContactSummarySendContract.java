package org.smartregister.anc.contract;

import org.smartregister.anc.model.ContactSummaryModel;

import java.util.List;

public interface ContactSummarySendContract {
    interface View {
        void goToClientProfile();

        void displayWomansName(String fullName);

        void displayUpcomingContactDates(List<ContactSummaryModel> models);

        void setProfileImage(String baseEntityId);

        void updateRecordedContact(Integer contactNumber);
    }

    interface Presenter {
        void loadWoman(String entityId);

        void loadUpcomingContacts(String entityId);

        void attachView(ContactSummarySendContract.View view);

        void showWomanProfileImage(String entityId);
    }

    interface Interactor extends BaseContactContract.Interactor {
        void fetchUpcomingContacts(String baseEntityId, InteractorCallback upcomingContactsCallback);
    }

    interface InteractorCallback extends BaseContactContract.InteractorCallback {
        void onUpcomingContactsFetched(List<ContactSummaryModel> upcomingContacts, Integer lastContact);
    }
}
