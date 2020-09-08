package org.smartregister.anc.library.presenter;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.presenters.JsonWizardFormFragmentPresenter;
import com.vijay.jsonwizard.views.JsonFormFragmentView;
import com.vijay.jsonwizard.widgets.NativeRadioButtonFactory;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.library.fragment.ContactWizardJsonFormFragment;
import org.smartregister.anc.library.util.ANCFormUtils;
import org.smartregister.anc.library.util.ConstantsUtils;

/**
 * Created by keyman on 04/08/18.
 */
public class ContactWizardJsonFormFragmentPresenter extends JsonWizardFormFragmentPresenter {

    public static final String TAG = ContactWizardJsonFormFragmentPresenter.class.getName();

    public ContactWizardJsonFormFragmentPresenter(JsonFormFragment formFragment, JsonFormInteractor jsonFormInteractor) {
        super(formFragment, jsonFormInteractor);
    }

    @Override
    public void setUpToolBar() {
        super.setUpToolBar();

    }

    @Override
    protected boolean moveToNextWizardStep() {
        String nextStep = getFormFragment().getJsonApi().nextStep();
        if (StringUtils.isNotBlank(nextStep)) {
            JsonFormFragment next = ContactWizardJsonFormFragment.getFormFragment(nextStep);
            JsonFormFragmentView jsonFormFragmentView = getView();
            if (jsonFormFragmentView != null) {
                jsonFormFragmentView.hideKeyBoard();
                jsonFormFragmentView.transactThis(next);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        key = (String) view.getTag(com.vijay.jsonwizard.R.id.key);
        type = (String) view.getTag(com.vijay.jsonwizard.R.id.type);
        switch (type) {
            case ConstantsUtils.EXPANSION_PANEL:
                String info = (String) view.getTag(com.vijay.jsonwizard.R.id.label_dialog_info);
                if (!TextUtils.isEmpty(info)) {
                    showInformationDialog(view);
                }
                break;
            case ConstantsUtils.EXTENDED_RADIO_BUTTON:
                showInformationDialog(view);
                break;
            default:
                super.onClick(view);
                break;
        }
    }

    @Override
    protected void nativeRadioButtonClickActions(View view) {
        String type = (String) view.getTag(com.vijay.jsonwizard.R.id.specify_type);
        String specifyWidget = (String) view.getTag(com.vijay.jsonwizard.R.id.specify_widget);
        Log.i(TAG, "The dialog content widget is this: " + specifyWidget);
        if (JsonFormConstants.CONTENT_INFO.equals(type) && specifyWidget.equals(JsonFormConstants.DATE_PICKER)) {
            NativeRadioButtonFactory.showDateDialog(view);
        } else if (JsonFormConstants.CONTENT_INFO.equals(type) && !specifyWidget.equals(JsonFormConstants.DATE_PICKER)) {
            ANCFormUtils formUtils = new ANCFormUtils();
            formUtils.showGenericDialog(view);
        } else if (view.getId() == com.vijay.jsonwizard.R.id.label_edit_button) {
            setRadioViewsEditable(view);
        } else {
            showInformationDialog(view);
        }
    }
}