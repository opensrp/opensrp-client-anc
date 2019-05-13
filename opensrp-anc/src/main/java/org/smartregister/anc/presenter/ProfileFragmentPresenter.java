package org.smartregister.anc.presenter;

import android.text.TextUtils;
import android.util.Log;

import org.jeasy.rules.api.Facts;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.ProfileFragmentContract;
import org.smartregister.anc.interactor.ProfileFragmentInteractor;
import org.smartregister.anc.util.Constants;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ndegwamartin on 13/07/2018.
 */
public class ProfileFragmentPresenter implements ProfileFragmentContract.Presenter {
    private static final String TAG = ProfileFragmentPresenter.class.getCanonicalName();

    private WeakReference<ProfileFragmentContract.View> mProfileView;
    private ProfileFragmentContract.Interactor mProfileInteractor;

    public ProfileFragmentPresenter(ProfileFragmentContract.View profileView) {
        mProfileView = new WeakReference<>(profileView);
        mProfileInteractor = new ProfileFragmentInteractor(this);
    }

    public void onDestroy(boolean isChangingConfiguration) {

        mProfileView = null;//set to null on destroy

        // Inform interactor
        if (mProfileInteractor != null) {
            mProfileInteractor.onDestroy(isChangingConfiguration);
        }

        // Activity destroyed set interactor to null
        if (! isChangingConfiguration) {
            mProfileInteractor = null;
        }

    }

    @Override
    public ProfileFragmentContract.View getProfileView() {
        if (mProfileView != null) {
            return mProfileView.get();
        } else {
            return null;
        }
    }

    @Override
    public Facts getImmediatePreviousContact(Map<String, String> clientDetails, String baseEntityId, String contactNo) {
        Facts facts = new Facts();
        try {

            facts = AncApplication.getInstance().getPreviousContactRepository()
                    .getPreviousContactFacts(baseEntityId, contactNo, true);

            Map<String, String> womanDetails =
                    AncApplication.getInstance().getDetailsRepository().getAllDetailsForClient(baseEntityId);
            if (womanDetails != null && womanDetails.containsKey(Constants.DETAILS_KEY.ATTENTION_FLAG_FACTS)) {
                String attentionFlags = womanDetails.get(Constants.DETAILS_KEY.ATTENTION_FLAG_FACTS);
                if (!TextUtils.isEmpty(attentionFlags)) {
                    JSONObject jsonObject = new JSONObject();
                    if (jsonObject.length() > 0) {
                        Iterator<String> keys = jsonObject.keys();

                        while (keys.hasNext()) {
                            String key = keys.next();
                            facts.put(key, jsonObject.get(key));
                        }
                    }
                }
            }


        } catch (JSONException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return facts;
    }
}
