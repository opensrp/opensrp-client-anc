package org.smartregister.anc.fragment;


import android.view.View;

import org.smartregister.anc.R;
import org.smartregister.anc.helper.DBQueryHelper;
import org.smartregister.anc.util.Constants;

/**
 * Created by keyman on 26/06/2018.
 */

public class HomeRegisterFragment extends BaseRegisterFragment {

    @Override
    protected void populateClientListHeaderView(View view) {
        //View headerLayout = getLayoutInflater(null).inflate(R.layout.register_home_list_header, null);
        //populateClientListHeaderView(view, headerLayout, Constants.CONFIGURATION.HOME_REGISTER_HEADER);
    }

    @Override
    protected String getMainCondition() {
        return DBQueryHelper.getHomePatientRegisterCondition();
    }

    @Override
    protected String[] getAdditionalColumns(String tableName) {
        return new String[]{};
    }

    /*public LocationPickerView getLocationPickerView() {
        return getFacilitySelection();
    }*/


}
