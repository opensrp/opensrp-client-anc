package org.smartregister.anc.library.presenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.core.util.Pair;

import org.apache.commons.lang3.tuple.Triple;
import org.jeasy.rules.api.Facts;
import org.json.JSONObject;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.contract.ProfileContract;
import org.smartregister.anc.library.contract.RegisterContract;
import org.smartregister.anc.library.interactor.ContactInteractor;
import org.smartregister.anc.library.interactor.ProfileInteractor;
import org.smartregister.anc.library.interactor.RegisterInteractor;
import org.smartregister.anc.library.util.ANCJsonFormUtils;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.repository.AllSharedPreferences;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 13/07/2018.
 */
public class ProfilePresenter implements ProfileContract.Presenter, RegisterContract.InteractorCallBack {
    private WeakReference<ProfileContract.View> mProfileView;
    private ProfileContract.Interactor mProfileInteractor;
    private RegisterContract.Interactor mRegisterInteractor;
    private ContactInteractor contactInteractor;

    public ProfilePresenter(ProfileContract.View profileView) {
        mProfileView = new WeakReference<>(profileView);
        mProfileInteractor = new ProfileInteractor(this);
        mRegisterInteractor = new RegisterInteractor();
        contactInteractor = new ContactInteractor();
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {

        mProfileView = null;//set to null on destroy

        // Inform interactor
        if (mProfileInteractor != null) {
            mProfileInteractor.onDestroy(isChangingConfiguration);
        }

        if (mRegisterInteractor != null) {
            mRegisterInteractor.onDestroy(isChangingConfiguration);
        }

        // Activity destroyed set interactor to null
        if (!isChangingConfiguration) {
            mProfileInteractor = null;
            mRegisterInteractor = null;
            contactInteractor = null;
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
    public void setBaseEntityRegister(String baseEntityId) {
        /**
         * Overrriden because the implemented contract requires it.  It can be used to set the base enitty id for a user.
         */
    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        this.refreshProfileView(getProfileView().getIntentString(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID));
        getProfileView().hideProgressDialog();
        getProfileView().displayToast(isEdit ? R.string.registration_info_updated : R.string.new_registration_saved);
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
    public void fetchProfileData(String baseEntityId) {
        mProfileInteractor.refreshProfileView(baseEntityId, true);
    }

    @Override
    public void refreshProfileView(String baseEntityId) {
        mProfileInteractor.refreshProfileView(baseEntityId, false);
    }

    @Override
    public void processFormDetailsSave(Intent data, AllSharedPreferences allSharedPreferences) {
        try {
            String jsonString = data.getStringExtra(ConstantsUtils.IntentKeyUtils.JSON);
            Timber.d(jsonString);
            if (jsonString != null) {
                JSONObject form = new JSONObject(jsonString);
                getProfileView().showProgressDialog(
                        form.getString(ANCJsonFormUtils.ENCOUNTER_TYPE).equals(ConstantsUtils.EventTypeUtils.CLOSE) ?
                                R.string.removing_dialog_title : R.string.saving_dialog_title);

                if (form.getString(ANCJsonFormUtils.ENCOUNTER_TYPE).equals(ConstantsUtils.EventTypeUtils.UPDATE_REGISTRATION)) {
                    Pair<Client, Event> values = ANCJsonFormUtils.processRegistrationForm(allSharedPreferences, jsonString);
                    mRegisterInteractor.saveRegistration(values, jsonString, true, this);
                } else if (form.getString(ANCJsonFormUtils.ENCOUNTER_TYPE).equals(ConstantsUtils.EventTypeUtils.CLOSE)) {
                    mRegisterInteractor.removeWomanFromANCRegister(jsonString, allSharedPreferences.fetchRegisteredANM());
                } else {
                    getProfileView().hideProgressDialog();
                }
            }
        } catch (Exception e) {
            Timber.e(e, " --> processFormDetailsSave");
        }
    }


    @Override
    public void refreshProfileTopSection(Map<String, String> client) {
        if (client != null) {
            ProfileContract.View profile = getProfileView();
            try {
                // Retrieve client's data
                String ancId = client.get(DBConstantsUtils.KeyUtils.ANC_ID);
                String entityId = client.get(DBConstantsUtils.KeyUtils.BASE_ENTITY_ID);
                String name = client.get(DBConstantsUtils.KeyUtils.FIRST_NAME) + " " + client.get(DBConstantsUtils.KeyUtils.LAST_NAME);
                String age = String.valueOf(Utils.getAgeFromDate(client.get(DBConstantsUtils.KeyUtils.DOB)));
                String recordDate = client.get(DBConstantsUtils.KeyUtils.LAST_VISIT_DATE);
                String lastVisit = client.get(DBConstantsUtils.KeyUtils.LAST_VISIT_DATE);
                String phoneNumber = client.get(DBConstantsUtils.KeyUtils.PHONE_NUMBER);
                String nextContact = client.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT);
                Integer nextContactNo = nextContact != null ? new Integer(nextContact) : null;
                Integer currentContactNo = (nextContactNo != null || nextContactNo >= 0) ? nextContactNo - 1 : null;
                // Update text in UI
                profile.setProfileImage(entityId);
                profile.setProfileID(ancId);
                profile.setProfileName(name);
                profile.setProfileAge(age);

				// Load contact details when she has previous records
				if (recordDate != null && currentContactNo != null) {
                    String edd = client.get(DBConstantsUtils.KeyUtils.EDD);
                    String actualEdd = Utils.getActualEDD(edd, recordDate, lastVisit);
					String ga = String.valueOf(Utils.getLastContactGA(edd, lastVisit));
					profile.setProfileGestationAge(ga);
					profile.setPhoneNumber(phoneNumber);
				}


            } catch (Exception e) {
                getProfileView().setProfileGestationAge("0");
            }
        }
    }

    @Override
    public HashMap<String, String> saveFinishForm(Map<String, String> client, Context context) {
        return contactInteractor.finalizeContactForm(client, context);
    }

    @Override
    public void getTaskCount(String baseEntityId) {
        getProfileView().setTaskCount(mProfileInteractor.getTaskCount(baseEntityId));
    }

    @Override
    public void createContactSummaryPdf(String womanName) {
        getProfileView().createContactSummaryPdf(womanName);
    }
}