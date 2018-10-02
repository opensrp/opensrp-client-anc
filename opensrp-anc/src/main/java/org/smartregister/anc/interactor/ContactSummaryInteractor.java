package org.smartregister.anc.interactor;

import android.support.annotation.VisibleForTesting;

import org.smartregister.anc.contract.BaseContactContract;
import org.smartregister.anc.contract.ContactSummaryContract;
import org.smartregister.anc.model.ContactSummaryModel;
import org.smartregister.anc.util.AppExecutors;

import java.util.ArrayList;
import java.util.List;

public class ContactSummaryInteractor extends BaseContactInteractor implements ContactSummaryContract.Interactor{

    @VisibleForTesting
    ContactSummaryInteractor(AppExecutors appExecutors) {
        super(appExecutors);
    }

    public ContactSummaryInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void fetchWomanDetails(String baseEntityId, BaseContactContract.InteractorCallback callBack) {
        super.fetchWomanDetails(baseEntityId,callBack);
    }


    @Override
    public void fetchUpcomingContacts(String entityId, final ContactSummaryContract.InteractorCallback callback) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<ContactSummaryModel> contactDates = new ArrayList<>();
                contactDates.add(new ContactSummaryModel("Contact 2", "12 August 2018"));
                contactDates.add(new ContactSummaryModel("Contact 3", "16 September 2018"));
                contactDates.add(new ContactSummaryModel("Contact 4", "25 October 2018"));
                getAppExecutors().mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onUpcomingContactsFetched(contactDates);
                    }
                });
            }
        };
        getAppExecutors().diskIO().execute(runnable);
    }
}
