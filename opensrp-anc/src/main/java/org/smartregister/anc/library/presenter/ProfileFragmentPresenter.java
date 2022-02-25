package org.smartregister.anc.library.presenter;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.utils.NativeFormLangUtils;

import org.jeasy.rules.api.Facts;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.contract.ProfileFragmentContract;
import org.smartregister.anc.library.interactor.ProfileFragmentInteractor;
import org.smartregister.anc.library.model.Task;
import org.smartregister.anc.library.util.ConstantsUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

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

    @SuppressLint("NewApi")
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
                        String attentionFlagValue = jsonObject.optString(key);
                        if (attentionFlagValue.length() != 0 && attentionFlagValue.charAt(0) == '{' && attentionFlagValue.charAt(0) == '[') {
                            if (attentionFlagValue.charAt(0) == '[') {
                                if (org.smartregister.anc.library.util.Utils.checkJsonArrayString(attentionFlags)) {
                                    JSONArray object = new JSONArray(attentionFlagValue);
                                    List<String> list = new ArrayList<>();
                                    for (int i = 0; i < jsonObject.length(); i++) {
                                        list.add(object.optJSONObject(i).optString(JsonFormConstants.TEXT));
                                    }
                                    facts.put(key, list);
                                } else {
                                    facts.put(key, attentionFlagValue);
                                }

                            } else {
                                JSONObject attentionFlagObject = new JSONObject(attentionFlagValue);
                                String translated_text, text;
                                text = attentionFlagObject.optString(JsonFormConstants.TEXT);
                                translated_text = !text.isEmpty() ? NativeFormLangUtils.translateDatabaseString(text, AncLibrary.getInstance().getApplicationContext()) : "";
                                facts.put(key, translated_text);
                            }

                        } else if (key.endsWith(ConstantsUtils.KeyUtils.VALUE) && attentionFlagValue.contains(",") && attentionFlagValue.contains(JsonFormConstants.TEXT)) {
                            List<String> attentionFlagValueArray = Arrays.asList(attentionFlagValue.split(","));
                            List<String> translatedList = new ArrayList<>();
                            for (int i = 0; i < attentionFlagValueArray.size(); i++) {
                                String textToTranslate = attentionFlagValueArray.get(i), translatedText;
                                ResourceBundle resourceBundle = ResourceBundle.getBundle(textToTranslate.split("\\.")[0], Locale.getDefault());
                                Timber.i("Resource Bundle %s", resourceBundle.getBaseBundleName());
                                translatedText = NativeFormLangUtils.translateDatabaseString(textToTranslate, AncLibrary.getInstance().getApplicationContext());
                                translatedList.add(translatedText);
                            }
                            facts.put(key, String.join(",", translatedList));
                        } else {
                            facts.put(key, jsonObject.get(key));
                        }

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
