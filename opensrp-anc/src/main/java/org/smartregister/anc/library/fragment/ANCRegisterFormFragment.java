package org.smartregister.anc.library.fragment;

import android.os.Bundle;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;

import org.smartregister.anc.library.interactor.ANCJsonFormInteractor;
import org.smartregister.anc.library.presenter.ANCJsonFormFragmentPresenter;

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
