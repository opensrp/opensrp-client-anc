package org.smartregister.anc.interactor;

import android.support.annotation.VisibleForTesting;

import org.smartregister.anc.contract.BaseContactContract;
import org.smartregister.anc.contract.ContactContract;
import org.smartregister.anc.util.AppExecutors;

/**
 * Created by keyman 30/07/2018.
 */
public class ContactInteractor extends BaseContactInteractor implements ContactContract.Interactor {

    public static final String TAG = ContactInteractor.class.getName();

    @VisibleForTesting
    ContactInteractor(AppExecutors appExecutors) {
        super(appExecutors);
    }

    public ContactInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void fetchWomanDetails(String baseEntityId, BaseContactContract.InteractorCallback callBack) {
        super.fetchWomanDetails(baseEntityId, callBack);
    }
}
