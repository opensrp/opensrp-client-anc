package org.smartregister.anc.library.interactor;


import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import org.smartregister.anc.library.widget.ANCSpinnerFactory;


public class ANCJsonFormInteractor extends JsonFormInteractor {

    private static final ANCJsonFormInteractor instance = new ANCJsonFormInteractor();

    private ANCJsonFormInteractor() {
        super();
    }

    public static ANCJsonFormInteractor getInstance() {
        return instance;
    }


    @Override
    protected void registerWidgets() {
        super.registerWidgets();
        map.put(JsonFormConstants.SPINNER, new ANCSpinnerFactory());
    }

}
