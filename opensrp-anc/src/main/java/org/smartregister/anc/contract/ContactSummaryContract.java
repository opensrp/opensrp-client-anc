package org.smartregister.anc.contract;

import org.smartregister.anc.model.ContactSummaryModel;

import java.util.List;

public interface ContactSummaryContract {
    interface View {
        void goToClientProfile();

        void displayWomansName(String fullName);
        void displayUpcomingContactDates(List<ContactSummaryModel> models);
    }

    interface Presenter {
        void loadWoman(String entityId);
        void loadUpcomingContacts(String entityId);
        void attachView(ContactSummaryContract.View view);
    }

    interface Interactor extends BaseContactContract.Interactor{
        void fetchUpcomingContacts(String entityId, InteractorCallback upcomingContactsCallback);
    }
    interface InteractorCallback extends BaseContactContract.InteractorCallback {
        void onUpcomingContactsFetched(List<ContactSummaryModel> upcomingContacts);
    }
}
