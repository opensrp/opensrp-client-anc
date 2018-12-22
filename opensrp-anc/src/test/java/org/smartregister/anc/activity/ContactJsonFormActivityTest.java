package org.smartregister.anc.activity;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

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

        Mockito.doNothing().when(activitySpy).callSuperWriteValue(DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, DUMMY_STRING_PARAM,false);
        activitySpy.writeValue(DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, DUMMY_STRING_PARAM,false);

        Mockito.verify(activitySpy, Mockito.times(1)).callSuperWriteValue(DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, DUMMY_STRING_PARAM,false);


    }
}
