package org.smartregister.anc.library.interactor;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.widgets.DatePickerFactory;

import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.widget.AncEditTextFactory;
import org.smartregister.anc.library.widget.AncRadioButtonWidgetFactory;
import org.smartregister.anc.library.widget.ExpansionWidgetFactory;

/**
 * Created by ndegwamartin on 30/06/2018.
 */
public class ContactJsonFormInteractor extends JsonFormInteractor {
    private static final ContactJsonFormInteractor INSTANCE = new ContactJsonFormInteractor();

    private ContactJsonFormInteractor() {
        super();
    }

    public static ContactJsonFormInteractor getInstance() {
        return INSTANCE;
    }

    @Override
    protected void registerWidgets() {
        super.registerWidgets();
        map.put(JsonFormConstants.EDIT_TEXT, new AncEditTextFactory());
        map.put(JsonFormConstants.DATE_PICKER, new DatePickerFactory());
        map.put(ConstantsUtils.EXPANSION_PANEL, new ExpansionWidgetFactory());
        map.put(ConstantsUtils.ANC_RADIO_BUTTON, new AncRadioButtonWidgetFactory());
    }
}
