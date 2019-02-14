package org.smartregister.anc.activity;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.smartregister.anc.view.AncGenericPopupDialog;

/**
 * Created by ndegwamartin on 04/07/2018.
 */
public class ContactJsonFormActivityTest extends BaseUnitTest {

    @Test
    public void testInitializeFormFragmentShouldCallInitializeFormFragmentCore() {

        ContactJsonFormActivity activity = new ContactJsonFormActivity();
        ContactJsonFormActivity activitySpy = Mockito.spy(activity);

        Assert.assertNotNull(activitySpy);

        Mockito.doNothing().when(activitySpy).initializeFormFragmentCore();
        activitySpy.initializeFormFragment();

        Mockito.verify(activitySpy, Mockito.times(1)).initializeFormFragmentCore();

    }

    @Test
    public void testOnFormFinishShouldCallCallSuperFinishMethodOfParent() {

        ContactJsonFormActivity activity = new ContactJsonFormActivity();
        ContactJsonFormActivity activitySpy = Mockito.spy(activity);
        Assert.assertNotNull(activitySpy);

        Mockito.doNothing().when(activitySpy).callSuperFinish();
        activitySpy.onFormFinish();

        Mockito.verify(activitySpy, Mockito.times(1)).callSuperFinish();

    }

    @Test
    public void testWriteValueShouldCallcallSuperWriteValueWithCorrectParameters() throws JSONException {

        ContactJsonFormActivity activity = new ContactJsonFormActivity();
        ContactJsonFormActivity activitySpy = Mockito.spy(activity);

        Assert.assertNotNull(activitySpy);

        String DUMMY_STRING_PARAM = "dummyStringParam";


        Mockito.doNothing().when(activitySpy)
                .callSuperWriteValue(DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, DUMMY_STRING_PARAM,
                        DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, true);
        activitySpy.writeValue(DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, DUMMY_STRING_PARAM,
                DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, true);
        Mockito.verify(activitySpy, Mockito.times(1))
                .callSuperWriteValue(DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, DUMMY_STRING_PARAM,
                        DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, true);

    }

    @Test
    public void testGetWidgetLabelWithEditText() throws JSONException {
        ContactJsonFormActivity activity = new ContactJsonFormActivity();
        AncGenericPopupDialog genericPopupDialog = new AncGenericPopupDialog();
        ContactJsonFormActivity activitySpy = Mockito.spy(activity);
        Assert.assertNotNull(activitySpy);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "edit_text");
        jsonObject.put("hint", "No body");

        Whitebox.setInternalState(genericPopupDialog, "widgetType", "expansion_panel");
        Whitebox.setInternalState(activity, "genericDialogInterface", genericPopupDialog);
        String result = null;
        try {
            result = Whitebox.invokeMethod(activity, ContactJsonFormActivity.class, "getWidgetLabel", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertEquals("No body", result);


    }

    @Test
    public void testGetWidgetLabelWithDatePicker() throws JSONException {
        ContactJsonFormActivity activity = new ContactJsonFormActivity();
        AncGenericPopupDialog genericPopupDialog = new AncGenericPopupDialog();
        ContactJsonFormActivity activitySpy = Mockito.spy(activity);
        Assert.assertNotNull(activitySpy);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "date_picker");
        jsonObject.put("hint", "No body");

        Whitebox.setInternalState(genericPopupDialog, "widgetType", "expansion_panel");
        Whitebox.setInternalState(activity, "genericDialogInterface", genericPopupDialog);
        String result = null;
        try {
            result = Whitebox.invokeMethod(activity, ContactJsonFormActivity.class, "getWidgetLabel", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertEquals("No body", result);
    }

    @Test
    public void testGetWidgetLabelWithOtherWidgets() throws JSONException {
        ContactJsonFormActivity activity = new ContactJsonFormActivity();
        AncGenericPopupDialog genericPopupDialog = new AncGenericPopupDialog();
        ContactJsonFormActivity activitySpy = Mockito.spy(activity);
        Assert.assertNotNull(activitySpy);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "check_box");
        jsonObject.put("label", "No body");

        Whitebox.setInternalState(genericPopupDialog, "widgetType", "expansion_panel");
        Whitebox.setInternalState(activity, "genericDialogInterface", genericPopupDialog);
        String result = null;
        try {
            result = Whitebox.invokeMethod(activity, ContactJsonFormActivity.class, "getWidgetLabel", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertEquals("No body", result);


    }
}
