package org.smartregister.anc.interactor;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.widgets.DatePickerFactory;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.widget.AccordionWidgetFactory;
import org.smartregister.anc.widget.AncEditTextFactory;
import org.smartregister.anc.widget.AncRadioButtonWidgetFactory;

/**
 * Created by ndegwamartin on 30/06/2018.
 */
public class ContactJsonFormInteractor extends JsonFormInteractor {
	
	private static final ContactJsonFormInteractor INSTANCE = new ContactJsonFormInteractor();
	
	private ContactJsonFormInteractor() {
		super();
	}
	
	@Override
	protected void registerWidgets() {
		map.put(JsonFormConstants.EDIT_TEXT, new AncEditTextFactory());
		map.put(JsonFormConstants.DATE_PICKER, new DatePickerFactory());
		map.put(Constants.NATIVE_ACCORDION, new AccordionWidgetFactory());
		map.put(Constants.ANC_RADIO_BUTTON, new AncRadioButtonWidgetFactory());
		super.registerWidgets();
	}
	
	public static ContactJsonFormInteractor getInstance() {
		return INSTANCE;
	}
}
