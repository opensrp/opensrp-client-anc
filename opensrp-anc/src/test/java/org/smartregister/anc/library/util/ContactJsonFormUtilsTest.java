package org.smartregister.anc.library.util;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.smartregister.anc.library.activity.BaseUnitTest;

import java.util.List;

import edu.emory.mathcs.backport.java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ContactJsonFormUtilsTest extends BaseUnitTest {

    private JSONArray accordionValuesJson;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        try {
            accordionValuesJson = new JSONArray("[{\"key\":\"ultrasound\",\"type\":\"extended_radio_button\",\"label\":\"Ultrasound test\",\"values\":[\"done_today:Done today\"]," +
                    "\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},\"value_openmrs_attributes\":[{\"key\":\"ultrasound\"," +
                    "\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}]},{\"key\":\"blood_type_test_date\",\"type\":\"date_picker\",\"label\":\"Blood type test date\"," +
                    "\"index\":2,\"values\":[\"08-04-2019\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\"," +
                    "\"openmrs_entity_id\":\"12005AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}},{\"key\":\"blood_type\",\"type\":\"native_radio\"," +
                    "\"label\":\"Blood type\",\"index\":3,\"values\":[\"ab:AB\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\"," +
                    "\"openmrs_entity_id\":\"12006AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"value_openmrs_attributes\":[{\"key\":\"blood_type\"," +
                    "\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"12009AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]}," +
                    "{\"key\":\"urine_test_notdone\",\"type\":\"check_box\",\"label\":\"Reason\",\"index\":1,\"values\":[\"stock_out:Stock out:true\"," +
                    "\"expired_stock:Expired stock:true\",\"other:Other (specify):true\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\"," +
                    "\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},\"value_openmrs_attributes\":[{\"key\":\"urine_test_notdone\",\"openmrs_entity_parent\":\"\"," +
                    "\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"urine_test_notdone\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}," +
                    "{\"key\":\"urine_test_notdone\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}]},{\"key\":\"no_of_fetuses\"," +
                    "\"type\":\"numbers_selector\",\"label\":\"No. of fetuses\",\"values\":[\"1\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\"," +
                    "\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}},{\"key\":\"ultrasound_gest_age\",\"type\":\"hidden\",\"label\":\"\"," +
                    "\"values\":[\"39 weeks 6 days\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}}," +
                    "{\"key\":\"elly_test\",\"type\":\"edit_text\"," +
                    "\"label\":\"Testing my own rules\",\"values\":[\"12\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}}]");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testGetKeyWhenObjectIsEmpty() throws Exception {
        String itemKey = ContactJsonFormUtils.getObjectKey(new JSONObject());
        Assert.assertNull(itemKey);
    }

    @Test(expected = NullPointerException.class)
    public void testGetKeyWhenObjectIsNull() throws Exception {
        String itemKey = ContactJsonFormUtils.getObjectKey(null);
        Assert.assertNull(itemKey);
    }

    @Test
    public void testCleanValue() {
        String result = ContactJsonFormUtils.cleanValue("[one, two, three]");
        assertEquals(result, "one, two, three");
    }

    @Test
    public void testCleanValueWithoutFirstSquareBrace() {
        String result = ContactJsonFormUtils.cleanValue("one, two, three");
        assertEquals(result, "one, two, three");
    }

    @Test
    public void testValueConverter() {
        String actual = "Done today";
        String entry = "[done_today]";
        String expected = ContactJsonFormUtils.keyToValueConverter(entry);
        assertEquals(expected, actual);
    }

    @Test
    public void testValueConverterWithEmptyString() {
        String actual = "";
        String entry = "";
        String expected = ContactJsonFormUtils.keyToValueConverter(entry);
        assertEquals(expected, actual);
    }

    @Test
    public void testValueConverterWithNullInput() {
        String actual = "";
        String expected = ContactJsonFormUtils.keyToValueConverter(null);
        assertEquals(expected, actual);
    }

    @Test
    public void testRemoveKeyPrefix() {
        String actual = "hiv_positive";
        String result = ContactJsonFormUtils.removeKeyPrefix("step1_hiv_positive", "step1");
        assertEquals(result, actual);
    }

    @Test
    public void testGetListValues() {
        String actual = "one, two, three";
        List<String> list = Arrays.asList(new String[]{"one", "two", "three"});
        String result = ContactJsonFormUtils.getListValuesAsString(list);
        assertEquals(result, actual);
    }

    @Test
    public void testGetListValuesWithNullInput() {
        String actual = "";
        String result = ContactJsonFormUtils.getListValuesAsString(null);
        assertEquals(result, actual);
    }

    @Test
    public void testObtainValueFromAncRadioButtons() throws JSONException {
        String actual = "done_today";
        String result = ContactJsonFormUtils.obtainValue("ultrasound", accordionValuesJson);
        assertEquals(result, actual);
    }

    @Test
    public void testObtainValueFromDatePicker() throws JSONException {
        String actual = "08-04-2019";
        String result = ContactJsonFormUtils.obtainValue("blood_type_test_date", accordionValuesJson);
        assertEquals(result, actual);
    }

    @Test
    public void testObtainValueFromNativeRadioButton() throws JSONException {
        String actual = "ab";
        String result = ContactJsonFormUtils.obtainValue("blood_type", accordionValuesJson);
        assertEquals(result, actual);
    }

    @Test
    public void testObtainValueFromCheckbox() throws JSONException {
        String actual = "[stock_out, expired_stock, other]";
        String result = ContactJsonFormUtils.obtainValue("urine_test_notdone", accordionValuesJson);
        assertEquals(result, actual);
    }

    @Test
    public void testObtainValueFromNumberSelector() throws JSONException {
        String actual = "1";
        String result = ContactJsonFormUtils.obtainValue("no_of_fetuses", accordionValuesJson);
        assertEquals(result, actual);
    }

    @Test
    public void testObtainValueFromHiddenValues() throws JSONException {
        String actual = "39 weeks 6 days";
        String result = ContactJsonFormUtils.obtainValue("ultrasound_gest_age", accordionValuesJson);
        assertEquals(result, actual);
    }

    @Test
    public void testFilterCheckboxValuesWhenFilterOptionsValuesAreDefineAsArray() throws Exception {
        JSONObject mainObject = getMainJsonObject("json_test_forms/test_checkbox_filter_json_form");
        ContactJsonFormUtils.processCheckboxFilteredItems(mainObject);
        //Obtain the first checkbox in step1 with key behaviour_persist check if items has been filtered
        JSONObject stepOneBehaviourPersist = mainObject.getJSONObject("step1").getJSONArray("fields").getJSONObject(0);
        JSONArray checkBoxOptions = stepOneBehaviourPersist.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
        assertEquals(4, checkBoxOptions.length());
        assertEquals("none", checkBoxOptions.getJSONObject(0).getString(JsonFormConstants.KEY));
        assertEquals("caffeine_intake", checkBoxOptions.getJSONObject(3).getString(JsonFormConstants.KEY));
        assertTrue(stepOneBehaviourPersist.getBoolean(ConstantsUtils.IS_FILTERED));
        assertTrue(stepOneBehaviourPersist.has(ConstantsUtils.IS_FILTERED));
    }

    @Test
    public void testFilterCheckboxValuesWhenFilterOptionsValueIsNotNone() throws Exception {
        JSONObject mainObject = getMainJsonObject("json_test_forms/test_checkbox_filter_json_form");
        ContactJsonFormUtils.processCheckboxFilteredItems(mainObject);
        //Obtain the first checkbox in step1 with key behaviour_persist check if items has been filtered
        JSONObject stepTwoBehaviourPersist = mainObject.getJSONObject("step2").getJSONArray("fields").getJSONObject(0);
        JSONArray checkBoxOptions = stepTwoBehaviourPersist.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
        assertEquals(1, checkBoxOptions.length());
        assertEquals("none", checkBoxOptions.getJSONObject(0).getString(JsonFormConstants.KEY));
        assertTrue(stepTwoBehaviourPersist.getBoolean(ConstantsUtils.IS_FILTERED));
        assertTrue(stepTwoBehaviourPersist.has(ConstantsUtils.IS_FILTERED));
    }

    @Test
    public void testFilterCheckboxValuesWhenFilterOptionSourceIsDefined() throws Exception {
        JSONObject mainObject = getMainJsonObject("json_test_forms/test_checkbox_filter_json_form");
        ContactJsonFormUtils.processCheckboxFilteredItems(mainObject);
        //Obtain the first checkbox in step1 with key behaviour_persist check if items has been filtered
        JSONObject stepThreePhysSymptomsPersist = mainObject.getJSONObject("step3").getJSONArray("fields").getJSONObject(0);
        JSONArray checkBoxOptions = stepThreePhysSymptomsPersist.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
        assertEquals(3, checkBoxOptions.length());
        assertEquals("none", checkBoxOptions.getJSONObject(0).getString(JsonFormConstants.KEY));
        assertEquals("heartburn", checkBoxOptions.getJSONObject(1).getString(JsonFormConstants.KEY));
        assertEquals("leg_cramps", checkBoxOptions.getJSONObject(2).getString(JsonFormConstants.KEY));
        assertTrue(stepThreePhysSymptomsPersist.getBoolean(ConstantsUtils.IS_FILTERED));
        assertTrue(stepThreePhysSymptomsPersist.has(ConstantsUtils.IS_FILTERED));
    }
}