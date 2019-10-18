package org.smartregister.anc.library.interactor;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import org.smartregister.anc.library.widget.AncEditTextFactory;

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
    }
}
