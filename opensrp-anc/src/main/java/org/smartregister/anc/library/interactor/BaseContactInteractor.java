package org.smartregister.anc.library.interactor;

import org.smartregister.anc.library.contract.BaseContactContract;
import org.smartregister.anc.library.repository.PatientRepository;
import org.smartregister.anc.library.util.AppExecutors;

import java.util.Map;

public abstract class BaseContactInteractor {
    protected AppExecutors appExecutors;

    BaseContactInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    protected void fetchWomanDetails(final String baseEntityId, final BaseContactContract.InteractorCallback callBack) {
        Runnable runnable = () -> {
            final Map<String, String> womanDetails = PatientRepository.getWomanProfileDetails(baseEntityId);
            appExecutors.mainThread().execute(() -> callBack.onWomanDetailsFetched(womanDetails));
        };

        appExecutors.diskIO().execute(runnable);
    }

    public AppExecutors getAppExecutors() {
        return appExecutors;
    }
}
