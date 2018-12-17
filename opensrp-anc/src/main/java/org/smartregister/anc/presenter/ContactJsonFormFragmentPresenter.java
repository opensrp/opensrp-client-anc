package org.smartregister.anc.presenter;

import android.support.v7.widget.AppCompatRadioButton;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;
import com.vijay.jsonwizard.utils.ValidationStatus;
import org.smartregister.anc.fragment.ContactJsonFormFragment;
import org.smartregister.anc.util.Constants;

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
			JsonFormFragment next = ContactJsonFormFragment.getFormFragment(mStepDetails.optString(Constants.NEXT));
			getView().hideKeyBoard();
			getView().transactThis(next);
		} else {
			validationStatus.requestAttention();
			getView().showToast(validationStatus.getErrorMessage());
		}
	}
	
	@Override
	public void onClick(View view) {
		key = (String)view.getTag(com.vijay.jsonwizard.R.id.key);
		type = (String)view.getTag(com.vijay.jsonwizard.R.id.type);
		switch (type) {
			case Constants.NATIVE_ACCORDION:
				String info = (String)view.getTag(com.vijay.jsonwizard.R.id.label_dialog_info);
				if (!TextUtils.isEmpty(info)) {
					showInformationDialog(view);
				}
				break;
			case Constants.ANC_RADIO_BUTTON:
				showInformationDialog(view);
				break;
			default:
				super.onClick(view);
				break;
		}
	}
}
