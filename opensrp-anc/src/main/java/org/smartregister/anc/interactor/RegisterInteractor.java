package org.smartregister.anc.interactor;

import android.support.annotation.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.RegisterContract;
import org.smartregister.anc.repository.UniqueIdRepository;
import org.smartregister.anc.util.AppExecutors;

/**
 * Created by keyman 27/06/2018.
 */
public class RegisterInteractor implements RegisterContract.Interactor {


    public static final String TAG = RegisterInteractor.class.getName();

    public enum type {SAVED, UPDATED}


    private AppExecutors appExecutors;

    @VisibleForTesting
    RegisterInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public RegisterInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void getNextUniqueId(final Triple<String, String, String> triple, final RegisterContract.InteractorCallBack callBack) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                UniqueIdRepository uniqueIdRepo = AncApplication.getInstance().getUniqueIdRepository();
                final String entityId = uniqueIdRepo.getNextUniqueId() != null ? uniqueIdRepo.getNextUniqueId().getOpenmrsId() : "";
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (StringUtils.isBlank(entityId)) {
                            callBack.onNoUniqueId();
                        } else {
                            callBack.onUniqueIdFetched(triple, entityId);
                        }
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }


}
