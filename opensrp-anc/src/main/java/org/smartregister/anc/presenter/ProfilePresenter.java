package org.smartregister.anc.presenter;

import android.content.Intent;
import android.util.Log;
import android.util.Pair;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartregister.anc.R;
import org.smartregister.anc.contract.ProfileContract;
import org.smartregister.anc.contract.RegisterContract;
import org.smartregister.anc.interactor.ProfileInteractor;
import org.smartregister.anc.interactor.RegisterInteractor;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.JsonFormUtils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.repository.AllSharedPreferences;

import java.lang.ref.WeakReference;

/**
 * Created by ndegwamartin on 13/07/2018.
 */
public class ProfilePresenter implements ProfileContract.Presenter, RegisterContract.InteractorCallBack {

    private static final String TAG = ProfilePresenter.class.getCanonicalName();

    private WeakReference<ProfileContract.View> mProfileView;
    private ProfileContract.Interactor mProfileInteractor;
    private RegisterContract.Interactor mRegisterInteractor;

    public ProfilePresenter(ProfileContract.View loginView) {
        mProfileView = new WeakReference<>(loginView);
        mProfileInteractor = new ProfileInteractor(this);
        mRegisterInteractor = new RegisterInteractor();
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {

        mProfileView = null;//set to null on destroy

        // Inform interactor
        mProfileInteractor.onDestroy(isChangingConfiguration);
        mRegisterInteractor.onDestroy(isChangingConfiguration);

        // Activity destroyed set interactor to null
        if (!isChangingConfiguration) {
            mProfileInteractor = null;
            mRegisterInteractor = null;
        }

    }

    @Override
    public void refreshProfileView(String baseEntityId) {
        mProfileInteractor.refreshProfileView(baseEntityId);
    }

    @Override
    public ProfileContract.View getProfileView() {
        if (mProfileView != null) {
            return mProfileView.get();
        } else {
            return null;
        }
    }

    @Override
    public void processFormDetailsSave(Intent data, AllSharedPreferences allSharedPreferences) {
        try {
            getProfileView().showProgressDialog();
            String jsonString = data.getStringExtra("json");
            Log.d("JSONResult", jsonString);

            JSONObject form = new JSONObject(jsonString);
            if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Constants.EventType.CLOSE) || form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Constants.EventType.UPDATE_REGISTRATION)) {

                Pair<Client, Event> values = JsonFormUtils.processRegistrationForm(jsonString, allSharedPreferences.fetchRegisteredANM());
                mRegisterInteractor.saveRegistration(values, jsonString, true, this);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }


    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {
        //Overriden
    }


    @Override
    public void onNoUniqueId() {
        getProfileView().displayToast(R.string.no_openmrs_id);

    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {

        this.refreshProfileView(getProfileView().getIntentString(Constants.INTENT_KEY.BASE_ENTITY_ID));

        getProfileView().hideProgressDialog();

        getProfileView().displayToast(isEdit ? R.string.registration_info_updated : R.string.new_registration_saved);
    }
}
