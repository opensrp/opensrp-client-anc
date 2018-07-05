package org.smartregister.anc.activity;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Created by ndegwamartin on 04/07/2018.
 */
public class AncJsonFormActivityTest extends BaseUnitTest {

    @Test
    public void testInitializeFormFragmentShouldCallInitializeFormFragmentCore() {

        AncJsonFormActivity activity = new AncJsonFormActivity();
        AncJsonFormActivity activitySpy = Mockito.spy(activity);

        Assert.assertNotNull(activitySpy);

        Mockito.doNothing().when(activitySpy).initializeFormFragmentCore();
        activitySpy.initializeFormFragment();

        Mockito.verify(activitySpy, Mockito.times(1)).initializeFormFragmentCore();

    }

    @Test
    public void testOnFormFinishShouldCallCallSuperFinishMethodOfParent() {

        AncJsonFormActivity activity = new AncJsonFormActivity();
        AncJsonFormActivity activitySpy = Mockito.spy(activity);
        Assert.assertNotNull(activitySpy);

        Mockito.doNothing().when(activitySpy).callSuperFinish();
        activitySpy.onFormFinish();

        Mockito.verify(activitySpy, Mockito.times(1)).callSuperFinish();

    }

    @Test
    public void testWriteValueShouldCallcallSuperWriteValueWithCorrectParameters() throws JSONException {

        AncJsonFormActivity activity = new AncJsonFormActivity();
        AncJsonFormActivity activitySpy = Mockito.spy(activity);

        Assert.assertNotNull(activitySpy);

        String DUMMY_STRING_PARAM = "dummyStringParam";

        Mockito.doNothing().when(activitySpy).callSuperWriteValue(DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, DUMMY_STRING_PARAM);
        activitySpy.writeValue(DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, DUMMY_STRING_PARAM);

        Mockito.verify(activitySpy, Mockito.times(1)).callSuperWriteValue(DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, DUMMY_STRING_PARAM, DUMMY_STRING_PARAM);


    }
}
