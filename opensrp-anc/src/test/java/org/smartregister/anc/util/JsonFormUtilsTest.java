package org.smartregister.anc.util;

import android.graphics.Bitmap;

import junit.framework.Assert;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.skyscreamer.jsonassert.JSONAssert;
import org.smartregister.anc.application.AncApplication;

import java.io.File;

import id.zelory.compressor.Compressor;

/**
 * Created by ndegwamartin on 13/11/2018.
 */

@RunWith(PowerMockRunner.class)
public class JsonFormUtilsTest {
    private static final String DUMMY_BASE_ENTITY_ID = "00ts-ime-hcla-0tib-0eht-ma0i";
    private static final String DUMMY_LOCATION_ID = "dummy-location-id-2018";
    private JSONObject formObject;

    @Mock
    AncApplication ancApplication;

    @Mock
    Compressor compressor;

    @Mock
    Bitmap bitmap;

    @Before
    public void setUp() throws JSONException {

        MockitoAnnotations.initMocks(this);

        formObject = new JSONObject();
        JSONObject metadataObject = new JSONObject();
        JSONArray fieldsObject = new JSONArray();


        JSONObject fieldObject = new JSONObject();
        fieldObject.put(JsonFormUtils.KEY, DBConstants.KEY.ANC_ID);
        fieldObject.put(JsonFormUtils.VALUE, "");
        fieldObject.put(JsonFormUtils.OPENMRS_ENTITY, "");
        fieldObject.put(JsonFormUtils.OPENMRS_ENTITY_ID, "");
        fieldObject.put(JsonFormUtils.OPENMRS_ENTITY_PARENT, "");

        fieldsObject.put(fieldObject);
        JSONObject step1Object = new JSONObject();
        step1Object.put(JsonFormUtils.FIELDS, fieldsObject);

        formObject.put(JsonFormUtils.METADATA, metadataObject);
        formObject.put(JsonFormUtils.STEP1, step1Object);
    }

    @Test
    public void testGetFormAsJsonInjectsCurrentLocationIDinFormCorrectly() throws Exception {


        JsonFormUtils.getFormAsJson(formObject, "random-form-name", DUMMY_BASE_ENTITY_ID, DUMMY_LOCATION_ID);

        JSONObject resultObject = JsonFormUtils.getFormAsJson(null, Constants.JSON_FORM.ANC_REGISTER, DUMMY_BASE_ENTITY_ID, DUMMY_LOCATION_ID);
        Assert.assertNull(resultObject);

        resultObject = JsonFormUtils.getFormAsJson(formObject, Constants.JSON_FORM.ANC_REGISTER, DUMMY_BASE_ENTITY_ID, DUMMY_LOCATION_ID);

        JSONArray field = JsonFormUtils.fields(resultObject);
        JsonFormUtils.getFieldJSONObject(field, DBConstants.KEY.ANC_ID);

        Assert.assertNotNull(resultObject);
        Assert.assertEquals(DUMMY_LOCATION_ID, resultObject.getJSONObject(JsonFormUtils.METADATA).get(JsonFormUtils.ENCOUNTER_LOCATION));
    }

    @Test
    public void testGetFormAsJsonInjectsANCIDInRegisterFormCorrectly() throws Exception {

        JSONObject resultObject = JsonFormUtils.getFormAsJson(formObject, Constants.JSON_FORM.ANC_REGISTER, DUMMY_BASE_ENTITY_ID, DUMMY_LOCATION_ID);

        JSONArray field = JsonFormUtils.fields(resultObject);
        JsonFormUtils.getFieldJSONObject(field, DBConstants.KEY.ANC_ID);

        Assert.assertNotNull(resultObject);
        Assert.assertEquals(DUMMY_BASE_ENTITY_ID.replaceAll("-", ""), JsonFormUtils.getFieldJSONObject(field, DBConstants.KEY.ANC_ID).get(JsonFormUtils.VALUE));
    }

    @Test
    public void testGetFormAsJsonInjectsEntityIDinCloseFormCorrectly() throws Exception {

        formObject.put(JsonFormUtils.ENTITY_ID, "");
        JSONObject resultObject = JsonFormUtils.getFormAsJson(formObject, Constants.JSON_FORM.ANC_CLOSE, DUMMY_BASE_ENTITY_ID, DUMMY_LOCATION_ID);

        JSONArray field = JsonFormUtils.fields(resultObject);
        JsonFormUtils.getFieldJSONObject(field, DBConstants.KEY.ANC_ID);

        Assert.assertNotNull(resultObject);
        Assert.assertEquals(DUMMY_BASE_ENTITY_ID, resultObject.getString(JsonFormUtils.ENTITY_ID));
    }

    @Test
    public void testValidateParametersReturnsCorrectResult() throws Exception {

        String jsonFormObjectString = formObject.toString();
        JSONArray jsonFormObjectFields = JsonFormUtils.fields(formObject);

        Triple<Boolean, JSONObject, JSONArray> validatedResult = JsonFormUtils.validateParameters(jsonFormObjectString);

        Assert.assertNotNull(validatedResult);
        Assert.assertTrue(validatedResult.getLeft());

        Assert.assertNotNull(validatedResult.getMiddle());
        JSONAssert.assertEquals(formObject, validatedResult.getMiddle(), false);

        Assert.assertNotNull(validatedResult.getRight());
        JSONAssert.assertEquals(jsonFormObjectFields, validatedResult.getRight(), false);

    }

    @Test
    @Ignore
    @PrepareForTest({AncApplication.class,JsonFormUtils.class})
    public void testSaveImageInvokesSaveStaticImageToDiskWithCorrectParams() throws Exception {


        String PROVIDER_ID = "dummy-provider-id";

        PowerMockito.mockStatic(AncApplication.class);
        PowerMockito.when(AncApplication.getInstance()).thenReturn(ancApplication);
        PowerMockito.when(ancApplication.getCompressor()).thenReturn(compressor);
        PowerMockito.when(compressor.compressToBitmap(ArgumentMatchers.any(File.class))).thenReturn(bitmap);


        PowerMockito.doNothing().when(JsonFormUtils.class, "saveStaticImageToDisk",ArgumentMatchers.eq(bitmap), ArgumentMatchers.eq(PROVIDER_ID), ArgumentMatchers.eq(DUMMY_BASE_ENTITY_ID));

        JsonFormUtils.saveImage(PROVIDER_ID, DUMMY_BASE_ENTITY_ID, "filepath/images/folder/location.jpg");

        PowerMockito.verifyPrivate(JsonFormUtils.class).invoke("saveStaticImageToDisk", bitmap, PROVIDER_ID, DUMMY_BASE_ENTITY_ID);


    }

}
