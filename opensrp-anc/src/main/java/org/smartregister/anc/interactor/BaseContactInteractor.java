package org.smartregister.anc.interactor;

import org.smartregister.anc.contract.BaseContactContract;
import org.smartregister.anc.repository.PatientRepository;
import org.smartregister.anc.util.AppExecutors;

import java.util.Map;

public abstract class BaseContactInteractor {
    private AppExecutors appExecutors;

    BaseContactInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    protected void fetchWomanDetails(final String baseEntityId, final BaseContactContract.InteractorCallback callBack) {

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

    public AppExecutors getAppExecutors() {
        return appExecutors;
    }
}
