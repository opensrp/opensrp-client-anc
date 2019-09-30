package org.smartregister.anc.library.interactor;

import org.smartregister.anc.library.contract.BaseContactContract;
import org.smartregister.anc.library.repository.PatientRepositoryHelper;
import org.smartregister.anc.library.util.AppExecutors;

import java.util.Map;

public abstract class BaseContactInteractor {
    protected AppExecutors appExecutors;

    BaseContactInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    protected void fetchWomanDetails(final String baseEntityId, final BaseContactContract.InteractorCallback callBack) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                final Map<String, String> womanDetails = PatientRepositoryHelper.getWomanProfileDetails(baseEntityId);
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
