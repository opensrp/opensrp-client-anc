package org.smartregister.anc.library.presenter;

import android.text.TextUtils;

import org.jeasy.rules.api.Facts;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.contract.ProfileFragmentContract;
import org.smartregister.anc.library.interactor.ProfileFragmentInteractor;
import org.smartregister.anc.library.model.Task;
import org.smartregister.anc.library.util.ConstantsUtils;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 13/07/2018.
 */
public class ProfileFragmentPresenter implements ProfileFragmentContract.Presenter {
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
        if (!isChangingConfiguration) {
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
            facts = AncLibrary.getInstance().getPreviousContactRepository().getPreviousContactFacts(baseEntityId, contactNo, true);

            Map<String, Object> factsAsMap = facts.asMap();
            String attentionFlags = "";
            if (factsAsMap.containsKey(ConstantsUtils.DetailsKeyUtils.ATTENTION_FLAG_FACTS)) {
                attentionFlags = (String) factsAsMap.get(ConstantsUtils.DetailsKeyUtils.ATTENTION_FLAG_FACTS);
            }

            if (!TextUtils.isEmpty(attentionFlags)) {
                JSONObject jsonObject = new JSONObject(attentionFlags);
                if (jsonObject.length() > 0) {
                    Iterator<String> keys = jsonObject.keys();

                    while (keys.hasNext()) {
                        String key = keys.next();
                        facts.put(key, jsonObject.get(key));
                    }
                }
            }
        } catch (JSONException e) {
            Timber.e(e);
        }

        return facts;
    }

    @Override
    public void getContactTasks(String baseEntityId, String contactNo) {
        getProfileView().setContactTasks(mProfileInteractor.getContactTasks(baseEntityId, contactNo));
    }

    @Override
    public void updateTask(Task task, String contactNo) {
        mProfileInteractor.updateTask(task, contactNo);
    }
}
