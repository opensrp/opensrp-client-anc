package org.smartregister.anc.interactor;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.widgets.DatePickerFactory;

import org.smartregister.anc.widget.AncEditTextFactory;


/**
 * Created by ndegwamartin on 30/06/2018.
 */
public class AncJsonFormInteractor extends JsonFormInteractor {

    private static final JsonFormInteractor INSTANCE = new AncJsonFormInteractor();

    private AncJsonFormInteractor() {
        super();
    }

    @Override
    protected void registerWidgets() {
        super.registerWidgets();
        map.put(JsonFormConstants.EDIT_TEXT, new AncEditTextFactory());
        map.put(JsonFormConstants.DATE_PICKER, new DatePickerFactory());
    }

    public static JsonFormInteractor getInstance() {
        return INSTANCE;
    }
}
