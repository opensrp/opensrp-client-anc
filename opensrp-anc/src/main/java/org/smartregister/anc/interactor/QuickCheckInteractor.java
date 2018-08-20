package org.smartregister.anc.interactor;

import android.support.annotation.VisibleForTesting;
import android.util.Log;

import org.json.JSONObject;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.QuickCheckContract;
import org.smartregister.anc.helper.ECSyncHelper;
import org.smartregister.anc.presenter.QuickCheckPresenter;
import org.smartregister.anc.util.AppExecutors;
import org.smartregister.anc.util.JsonFormUtils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.repository.AllSharedPreferences;

/**
 * Created by keyman 27/06/2018.
 */
public class QuickCheckInteractor implements QuickCheckContract.Interactor {

    public static final String TAG = QuickCheckInteractor.class.getName();

    private AppExecutors appExecutors;

    private ECSyncHelper syncHelper;

    private AllSharedPreferences allSharedPreferences;

    @VisibleForTesting
    QuickCheckInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public QuickCheckInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void saveQuickCheckEvent(final QuickCheckPresenter.QuickCheck quickCheck, final String baseEntityId, final QuickCheckContract.InteractorCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                try {
                    Event event = JsonFormUtils.createQuickCheckEvent(getAllSharedPreferences(), quickCheck, baseEntityId);
                    JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(event));
                    getSyncHelper().addEvent(baseEntityId, eventJson);

                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.quickCheckSaved(true);
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));

                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.quickCheckSaved(false);
                        }
                    });
                }
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    public void setSyncHelper(ECSyncHelper syncHelper) {
        this.syncHelper = syncHelper;
    }

    public ECSyncHelper getSyncHelper() {
        if (syncHelper == null) {
            syncHelper = ECSyncHelper.getInstance(AncApplication.getInstance().getApplicationContext());
        }
        return syncHelper;
    }

    public void setAllSharedPreferences(AllSharedPreferences allSharedPreferences) {
        this.allSharedPreferences = allSharedPreferences;
    }

    public AllSharedPreferences getAllSharedPreferences() {
        if (allSharedPreferences == null) {
            allSharedPreferences = AncApplication.getInstance().getContext().allSharedPreferences();
        }
        return allSharedPreferences;
    }
}
