package org.smartregister.anc.interactor;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.smartregister.anc.activity.BaseUnitTest;

import java.util.Map;

/**
 * Created by ndegwamartin on 04/07/2018.
 */

public class AncJsonFormInteractorTest extends BaseUnitTest {

    @Test
    public void testRegisterWidgetsShouldAddCustomChildWidgetsToInteractorMapCorrectly() {

        AncJsonFormInteractor jsonFormInteractor = (AncJsonFormInteractor) AncJsonFormInteractor.getInstance();

        Map<String, FormWidgetFactory> formWidgetFactoryMap = Whitebox.getInternalState(jsonFormInteractor, "map");
        int formWidgetFactoryMapSizeParent = formWidgetFactoryMap.size(); //Get count of widgets added by super class

        Map<String, FormWidgetFactory> factoryMapSpy = Mockito.spy(formWidgetFactoryMap);
        Whitebox.setInternalState(jsonFormInteractor, "map", factoryMapSpy);
        AncJsonFormInteractor interactorSpy = Mockito.spy(jsonFormInteractor);

        Assert.assertNotNull(interactorSpy);
        interactorSpy.registerWidgets();

        Mockito.verify(factoryMapSpy, Mockito.times(2 + formWidgetFactoryMapSizeParent)).put(ArgumentMatchers.anyString(), ArgumentMatchers.any(FormWidgetFactory.class));

        Assert.assertTrue(factoryMapSpy.containsKey(JsonFormConstants.EDIT_TEXT));
        Assert.assertTrue(factoryMapSpy.containsKey(JsonFormConstants.DATE_PICKER));
    }
}
