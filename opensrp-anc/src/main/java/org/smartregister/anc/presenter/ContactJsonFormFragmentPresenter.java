package org.smartregister.anc.presenter;

import android.widget.LinearLayout;

import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;
import com.vijay.jsonwizard.utils.ValidationStatus;

import org.smartregister.anc.fragment.ContactJsonFormFragment;

/**
 * Created by keyman on 04/08/18.
 */
public class ContactJsonFormFragmentPresenter extends JsonFormFragmentPresenter {

    public static final String TAG = ContactJsonFormFragmentPresenter.class.getName();
    
    public ContactJsonFormFragmentPresenter(JsonFormFragment formFragment, JsonFormInteractor jsonFormInteractor) {
        super(formFragment, jsonFormInteractor);
    }

    @Override
    public void setUpToolBar() {
        super.setUpToolBar();

    }

    @Override
    public void onNextClick(LinearLayout mainView) {
        ValidationStatus validationStatus = this.writeValuesAndValidate(mainView);
        if (validationStatus.isValid()) {
            JsonFormFragment next = ContactJsonFormFragment.getFormFragment(mStepDetails.optString("next"));
            getView().hideKeyBoard();
            getView().transactThis(next);
        } else {
            validationStatus.requestAttention();
            getView().showToast(validationStatus.getErrorMessage());
        }
    }
}
