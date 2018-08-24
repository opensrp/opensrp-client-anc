package org.smartregister.anc.fragment;

import android.support.design.widget.BottomNavigationView;
import android.view.View;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.BaseRegisterActivity;
import org.smartregister.anc.activity.HomeRegisterActivity;
import org.smartregister.anc.helper.DBQueryHelper;
import org.smartregister.anc.presenter.RegisterFragmentPresenter;
import org.smartregister.anc.util.DisableShitModeBottomNavigation;
import org.smartregister.anc.view.LocationPickerView;

/**
 * Created by keyman on 26/06/2018.
 */

public class HomeRegisterFragment extends BaseRegisterFragment {

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }

        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new RegisterFragmentPresenter(this, viewConfigurationIdentifier);
    }

    @Override
    protected String getMainCondition() {
        return DBQueryHelper.getHomePatientRegisterCondition();
    }

    public LocationPickerView getLocationPickerView() {
        return getFacilitySelection();
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        // QR Code
        View filterImage = view.findViewById(R.id.filter_image_view);
        if (filterImage != null) {
            filterImage.setOnClickListener(registerActionHandler);
        }
        
        View filterText = view.findViewById(R.id.filter_text_view);
        if(filterText != null) {
        	filterText.setOnClickListener(registerActionHandler);
        }

        // Due Button
        View contactButton = view.findViewById(R.id.due_button);
        if (contactButton != null) {
            contactButton.setOnClickListener(registerActionHandler);
        }

        //Risk view
        View attentionFlag = view.findViewById(R.id.risk_layout);
        if (attentionFlag != null) {
            attentionFlag.setOnClickListener(registerActionHandler);
        }
	
	    BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
	        DisableShitModeBottomNavigation.disableShiftMode(bottomNavigationView);
            bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationBarActionHandler);
        }

    }

    @Override
    protected void onViewClicked(View view) {
        if (getActivity() == null) {
            return;
        }

        HomeRegisterActivity homeRegisterActivity = (HomeRegisterActivity) getActivity();

        switch (view.getId()) {
            case R.id.filter_image_view:
                homeRegisterActivity.switchToFragment(2);
                break;
	        case R.id.filter_text_view:
	        	homeRegisterActivity.switchToFragment(2);
	        	break;
            default:
                break;
        }
    }
}
