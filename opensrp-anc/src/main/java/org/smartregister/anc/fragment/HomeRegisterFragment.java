package org.smartregister.anc.fragment;


import android.view.View;

import org.smartregister.anc.activity.BaseRegisterActivity;
import org.smartregister.anc.activity.HomeRegisterActivity;
import org.smartregister.anc.helper.DBQueryHelper;
import org.smartregister.anc.presenter.RegisterFragmentPresenter;
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
        presenter = new RegisterFragmentPresenter(this, context(), viewConfigurationIdentifier);
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
        filterStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeRegisterActivity) getActivity()).switchToFragment(1);
            }
        });

    }
}
