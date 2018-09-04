package org.smartregister.anc.presenter;

import android.util.Log;
import android.widget.LinearLayout;

import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;
import com.vijay.jsonwizard.utils.ValidationStatus;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.fragment.AncJsonFormFragment;

/**
 * Created by keyman on 04/08/18.
 */
public class AncJsonFormFragmentPresenter extends JsonFormFragmentPresenter {

    public static final String TAG = AncJsonFormFragmentPresenter.class.getName();

    private static final String TITLE = "title";

    public AncJsonFormFragmentPresenter(JsonFormFragment formFragment, JsonFormInteractor jsonFormInteractor) {
        super(formFragment, jsonFormInteractor);
    }

    @Override
    public void setUpToolBar() {
        super.setUpToolBar();

        String jsonString = getView().getCurrentJsonState();
        if (StringUtils.isNotBlank(jsonString)) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                if (jsonObject.has(TITLE)) {
                    String title = jsonObject.getString(TITLE);
                    getView().setActionBarTitle(title);
                }
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
        }

    }

    @Override
    public void onNextClick(LinearLayout mainView) {
        ValidationStatus validationStatus = this.writeValuesAndValidate(mainView);
        if (validationStatus.isValid()) {
            JsonFormFragment next = AncJsonFormFragment.getFormFragment(mStepDetails.optString("next"));
            getView().hideKeyBoard();
            getView().transactThis(next);
        } else {
            validationStatus.requestAttention();
            getView().showToast(validationStatus.getErrorMessage());
        }
    }
}
