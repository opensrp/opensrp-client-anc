package org.smartregister.anc.interactor;

import android.support.annotation.VisibleForTesting;

import org.smartregister.anc.contract.ContactContract;
import org.smartregister.anc.repository.PatientRepository;
import org.smartregister.anc.util.AppExecutors;

import java.util.Map;

/**
 * Created by keyman 28/07/2018.
 */
public class ContactInteractor implements ContactContract.Interactor {


    public static final String TAG = ContactInteractor.class.getName();

    public enum type {SAVED, UPDATED}


    private AppExecutors appExecutors;

    @VisibleForTesting
    ContactInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public ContactInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void fetchWomanDetails(final String baseEntityId, final ContactContract.InteractorCallBack callBack) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                final Map<String, String> womanDetails = PatientRepository.getWomanProfileDetails(baseEntityId);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onWomanDetailsFetched(womanDetails);
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

}
