package org.smartregister.anc.interactor;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.smartregister.anc.activity.BaseUnitTest;

import java.util.Map;

/**
 * Created by ndegwamartin on 04/07/2018.
 */

public class ContactJsonFormInteractorTest extends BaseUnitTest {

    @Test
    public void testRegisterWidgetsShouldAddCustomChildWidgetsToInteractorMapCorrectly() {

        ContactJsonFormInteractor jsonFormInteractor = ContactJsonFormInteractor.getInstance();

        Map<String, FormWidgetFactory> formWidgetFactoryMap = Whitebox.getInternalState(jsonFormInteractor, "map");

        Map<String, FormWidgetFactory> factoryMapSpy = Mockito.spy(formWidgetFactoryMap);
        Whitebox.setInternalState(jsonFormInteractor, "map", factoryMapSpy);
        ContactJsonFormInteractor interactorSpy = Mockito.spy(jsonFormInteractor);

        Assert.assertNotNull(interactorSpy);
        interactorSpy.registerWidgets();

        Assert.assertTrue(factoryMapSpy.containsKey(JsonFormConstants.EDIT_TEXT));
        Assert.assertTrue(factoryMapSpy.containsKey(JsonFormConstants.DATE_PICKER));
        Assert.assertTrue(factoryMapSpy.containsKey(JsonFormConstants.EXPANSION_PANEL));
        Assert.assertTrue(factoryMapSpy.containsKey(JsonFormConstants.ANC_RADIO_BUTTON));
    }
}
