package org.smartregister.anc.library.fragment;

import android.os.Bundle;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;

import org.json.JSONObject;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.constants.AncFormConstants;
import org.smartregister.anc.library.interactor.ANCJsonFormInteractor;
import org.smartregister.anc.library.presenter.ANCJsonFormFragmentPresenter;
import org.smartregister.util.JsonFormUtils;

import timber.log.Timber;

public class ANCRegisterFormFragment extends JsonFormFragment {


    @Override
    protected JsonFormFragmentPresenter createPresenter() {
        return new ANCJsonFormFragmentPresenter(this, ANCJsonFormInteractor.getInstance());
    }

    public static JsonFormFragment getFormFragment(String stepName) {
        ANCRegisterFormFragment jsonFormFragment = new ANCRegisterFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(JsonFormConstants.JSON_FORM_KEY.STEPNAME, stepName);
        jsonFormFragment.setArguments(bundle);

        return jsonFormFragment;
    }

}
