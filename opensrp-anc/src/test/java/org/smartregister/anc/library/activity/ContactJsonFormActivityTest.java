package org.smartregister.anc.library.activity;

import android.app.ProgressDialog;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.ExpansionPanelGenericPopupDialog;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.smartregister.anc.library.domain.Contact;

/**
 * Created by ndegwamartin on 04/07/2018.
 */
public class ContactJsonFormActivityTest extends BaseUnitTest {

    @Mock
    private ProgressDialog progressDialog;

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
    public void testGetContact() {
        ContactJsonFormActivity activity = new ContactJsonFormActivity();
        ContactJsonFormActivity formActivity = Mockito.spy(activity);
        Assert.assertNotNull(formActivity);

        Contact form = Mockito.spy(new Contact());
        Mockito.when(formActivity.getForm()).thenReturn(form);
        Assert.assertNotNull(form);

        Contact contact = formActivity.getContact();
        Assert.assertNotNull(contact);

        Mockito.verify(formActivity, Mockito.times(1)).getForm();
    }

    @Test
    public void testGetNullContact() {
        ContactJsonFormActivity activity = new ContactJsonFormActivity();
        ContactJsonFormActivity formActivity = Mockito.spy(activity);
        Assert.assertNotNull(formActivity);

        Form form = Mockito.spy(new Form());
        Mockito.when(formActivity.getForm()).thenReturn(form);
        Assert.assertNotNull(form);

        Contact contact = formActivity.getContact();
        Assert.assertNull(contact);

        Mockito.verify(formActivity, Mockito.times(1)).getForm();
    }

    @Test
    public void testHideProgressDialog() {
        ContactJsonFormActivity activity = new ContactJsonFormActivity();
        ContactJsonFormActivity formActivity = Mockito.spy(activity);
        Assert.assertNotNull(formActivity);

        progressDialog = Mockito.mock(ProgressDialog.class);
        Whitebox.setInternalState(formActivity, "progressDialog", progressDialog);
        Assert.assertNotNull(progressDialog);

        formActivity.hideProgressDialog();
        Mockito.verify(progressDialog, Mockito.times(1)).dismiss();
    }

    @Test
    @Ignore
    public void testShowProgressDialog() {
        ContactJsonFormActivity activity = new ContactJsonFormActivity();
        ContactJsonFormActivity formActivity = Mockito.spy(activity);
        Assert.assertNotNull(formActivity);

        progressDialog = null;
        Whitebox.setInternalState(formActivity, "progressDialog", progressDialog);
        Assert.assertNull(progressDialog);

        formActivity.showProgressDialog("My Progress Dialog");
        Mockito.verify(progressDialog, Mockito.times(1)).setTitle("My Progress Dialog");
    }

    @Test
    public void testReturnWithFormFieldsForTheMainForm() throws JSONException {
        ContactJsonFormActivity activity = new ContactJsonFormActivity();
        ContactJsonFormActivity formActivity = Mockito.spy(activity);
        Assert.assertNotNull(formActivity);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonFormConstants.FIELDS, new JSONArray());

        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray = Whitebox.invokeMethod(activity, ContactJsonFormActivity.class, "returnWithFormFields", jsonObject,
                    false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertEquals(jsonArray.length(), 0);
    }

    @Test
    public void testReturnWithFormFieldsForThePopup() throws JSONException {
        ContactJsonFormActivity activity = new ContactJsonFormActivity();
        ContactJsonFormActivity formActivity = Mockito.spy(activity);
        Assert.assertNotNull(formActivity);

        ExpansionPanelGenericPopupDialog genericPopupDialog = new ExpansionPanelGenericPopupDialog();

        Whitebox.setInternalState(genericPopupDialog, "parentKey", "parent_keys_expansion");
        Whitebox.setInternalState(genericPopupDialog, "widgetType", "expansion_panel");
        Whitebox.setInternalState(activity, "genericDialogInterface", genericPopupDialog);

        JSONObject jsonObject = new JSONObject();

        JSONArray parentArray = new JSONArray();
        JSONObject itemObject = new JSONObject();
        itemObject.put(JsonFormConstants.KEY, "parent_keys_expansion");
        itemObject.put(JsonFormConstants.CONTENT_FORM, "content_form");
        parentArray.put(itemObject);
        jsonObject.put(JsonFormConstants.FIELDS, parentArray);

        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray =
                    Whitebox.invokeMethod(activity, ContactJsonFormActivity.class, "returnWithFormFields", jsonObject, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertEquals(jsonArray.length(), 0);
    }
}
