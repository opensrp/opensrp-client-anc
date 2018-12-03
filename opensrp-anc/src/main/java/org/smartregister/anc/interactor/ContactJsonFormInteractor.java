package org.smartregister.anc.interactor;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.widgets.DatePickerFactory;

import org.smartregister.anc.util.Constants;
import org.smartregister.anc.widget.AccordionWidgetFactory;
import org.smartregister.anc.widget.AncEditTextFactory;


/**
 * Created by ndegwamartin on 30/06/2018.
 */
public class ContactJsonFormInteractor extends JsonFormInteractor {

    private static final JsonFormInteractor INSTANCE = new ContactJsonFormInteractor();

    private ContactJsonFormInteractor() {
        super();
    }

    @Override
    protected void registerWidgets() {
        super.registerWidgets();
        map.put(JsonFormConstants.EDIT_TEXT, new AncEditTextFactory());
        map.put(JsonFormConstants.DATE_PICKER, new DatePickerFactory());
        map.put(Constants.NATIVE_ACCORDION, new AccordionWidgetFactory());
    }

    public static JsonFormInteractor getInstance() {
        return INSTANCE;
    }
}
