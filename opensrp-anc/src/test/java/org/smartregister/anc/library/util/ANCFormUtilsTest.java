package org.smartregister.anc.library.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.model.PartialContact;
import org.smartregister.anc.library.repository.PartialContactRepository;

import java.util.List;

import edu.emory.mathcs.backport.java.util.Arrays;

public class ANCFormUtilsTest extends BaseUnitTest {

    private JSONArray accordionValuesJson;

    @Mock
    private AncLibrary ancLibrary;

    @Mock
    private PartialContactRepository partialContactRepository;

    private String quickCheckForm = "{\"validate_on_submit\":true,\"display_scroll_bars\":true,\"count\":\"1\",\"encounter_type\":\"Quick Check\",\"entity_id\":\"\",\"relational_id\":\"\",\"form_version\":\"0.0.1\",\"step1\":{\"title\":\"Quick Check\",\"fields\":" +
            "[{\"key\":\"contact_reason\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160288AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"," +
            "\"type\":\"native_radio\",\"label\":\"Reason for coming to facility\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"first_contact\"," +
            "\"text\":\"First contact\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165269AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"scheduled_contact\",\"text\":\"Scheduled contact\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1246AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}," +
            "{\"key\":\"specific_complaint\",\"text\":\"Specific complaint\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5219AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":\"true\",\"err\":\"Reason for coming to facility is required\"}}," +
            "{\"key\":\"specific_complaint\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5219AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"check_box\",\"label\":\"Specific complaint(s)\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"exclusive\":[\"dont_know\",\"none\"],\"options\":[{\"key\":\"abnormal_discharge\",\"text\":\"Abnormal vaginal discharge\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"123395AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"altered_skin_color\",\"text\":\"Jaundice\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"136443AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"changes_in_bp\",\"text\":\"Changes in blood pressure\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"155052AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"constipation\",\"text\":\"Constipation\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"996AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"contractions\",\"text\":\"Contractions\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163750AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"cough\",\"text\":\"Cough\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"143264AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"depression\",\"text\":\"Depression\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"119537AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"anxiety\",\"text\":\"Anxiety\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"121543AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"dizziness\",\"text\":\"Dizziness\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"156046AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"domestic_violence\",\"text\":\"Domestic violence\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"141814AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"extreme_pelvic_pain\",\"text\":\"Extreme pelvic pain - can't walk (symphysis pubis dysfunction)\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165270AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"fever\",\"text\":\"Fever\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"140238AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"full_abdominal_pain\",\"text\":\"Full abdominal pain\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"139547AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"flu_symptoms\",\"text\":\"Flu symptoms\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"137162AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"fluid_loss\",\"text\":\"Fluid loss\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"148968AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"headache\",\"text\":\"Headache\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"139084AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"heartburn\",\"text\":\"Heartburn\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"139059AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"leg_cramps\",\"text\":\"Leg cramps\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"135969AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"leg_pain\",\"text\":\"Leg pain\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"114395AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"leg_redness\",\"text\":\"Leg redness\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165215AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"low_back_pain\",\"text\":\"Low back pain\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"116225AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"pelvic_pain\",\"text\":\"Pelvic pain\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"131034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"nausea_vomiting_diarrhea\",\"text\":\"Nausea / vomiting / diarrhea\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"157892AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"no_fetal_movement\",\"text\":\"No fetal movement\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1452AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"oedema\",\"text\":\"Oedema\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"460AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"other_bleeding\",\"text\":\"Other bleeding\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"147241AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"other_pain\",\"text\":\"Other pain\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"114403AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"other_psychological_symptoms\",\"text\":\"Other psychological symptoms\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160198AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"other_skin_disorder\",\"text\":\"Other skin disorder\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"119022AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"other_types_of_violence\",\"text\":\"Other types of violence\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"158358AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"dysuria\",\"text\":\"Pain during urination (dysuria)\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"118771AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"pruritus\",\"text\":\"Pruritus\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"879AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"reduced_fetal_movement\",\"text\":\"Reduced or poor fetal movement\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"113377AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"shortness_of_breath\",\"text\":\"Shortness of breath\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"141600AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"tiredness\",\"text\":\"Tiredness\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"124628AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"trauma\",\"text\":\"Trauma\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"124193AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"bleeding\",\"text\":\"Vaginal bleeding\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"147232AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"visual_disturbance\",\"text\":\"Visual disturbance\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"123074AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"other_specify\",\"text\":\"Other (specify)\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":\"true\",\"err\":\"Specific complain is required\"},\"relevance\":{\"step1:contact_reason\":{\"type\":\"string\",\"ex\":\"equalTo(.,\\\"specific_complaint\\\")\"}}},{\"key\":\"specific_complaint_other\",\"openmrs_entity_parent\":\"5219AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160632AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"normal_edit_text\",\"edit_text_style\":\"bordered\",\"hint\":\"Specify\",\"v_regex\":{\"value\":\"[A-Za-z\\\\s\\\\.\\\\-]*\",\"err\":\"Please enter valid content\"},\"relevance\":{\"step1:specific_complaint\":{\"ex-checkbox\":[{\"or\":[\"other_specify\"]}]}}},{\"key\":\"danger_signs\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160939AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"check_box\",\"label\":\"Danger signs\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"exclusive\":[\"danger_none\"],\"options\":[{\"key\":\"danger_none\",\"text\":\"None\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"danger_bleeding\",\"text\":\"Bleeding vaginally\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"150802AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"central_cyanosis\",\"text\":\"Central cyanosis\",\"label_info_text\":\"Bluish discolouration around the mucous membranes in the mouth, lips and tongue\",\"label_info_title\":\"Central cyanosis\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165216AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"convulsing\",\"text\":\"Convulsing\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"164483AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"danger_fever\",\"text\":\"Fever\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"140238AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"severe_headache\",\"text\":\"Severe headache\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"139081AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"visual_disturbance\",\"text\":\"Visual disturbance\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"123074AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"imminent_delivery\",\"text\":\"Imminent delivery\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"162818AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"labour\",\"text\":\"Labour\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"145AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"looks_very_ill\",\"text\":\"Looks very ill\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163293AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"severe_vomiting\",\"text\":\"Severe vomiting\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"118477AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"severe_pain\",\"text\":\"Severe pain\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163477AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"severe_abdominal_pain\",\"text\":\"Severe abdominal pain\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165271AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"unconscious\",\"text\":\"Unconscious\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"123818AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":\"true\",\"err\":\"Danger signs is required\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"quick_check_relevance_rules.yml\"}}}}]}}";

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
        String itemKey = ANCFormUtils.getObjectKey(new JSONObject());
        Assert.assertNull(itemKey);
    }

    @Test(expected = NullPointerException.class)
    public void testGetKeyWhenObjectIsNull() throws Exception {
        String itemKey = ANCFormUtils.getObjectKey(null);
        Assert.assertNull(itemKey);
    }

    @Test
    public void testCleanValue() {
        String result = ANCFormUtils.cleanValue("[one, two, three]");
        assertEquals(result, "one, two, three");
    }

    @Test
    public void testCleanValueWithoutFirstSquareBrace() {
        String result = ANCFormUtils.cleanValue("one, two, three");
        assertEquals(result, "one, two, three");
    }

    @Test
    public void testValueConverter() {
        String actual = "Done today";
        String entry = "[done_today]";
        String expected = ANCFormUtils.keyToValueConverter(entry);
        assertEquals(expected, actual);
    }

    @Test
    public void testValueConverterWithEmptyString() {
        String actual = "";
        String entry = "";
        String expected = ANCFormUtils.keyToValueConverter(entry);
        assertEquals(expected, actual);
    }

    @Test
    public void testValueConverterWithNullInput() {
        String actual = "";
        String expected = ANCFormUtils.keyToValueConverter(null);
        assertEquals(expected, actual);
    }

    @Test
    public void testRemoveKeyPrefix() {
        String actual = "hiv_positive";
        String result = ANCFormUtils.removeKeyPrefix("step1_hiv_positive", "step1");
        assertEquals(result, actual);
    }

    @Test
    public void testGetListValues() {
        String actual = "one, two, three";
        List<String> list = Arrays.asList(new String[]{"one", "two", "three"});
        String result = ANCFormUtils.getListValuesAsString(list);
        assertEquals(result, actual);
    }

    @Test
    public void testGetListValuesWithNullInput() {
        String actual = "";
        String result = ANCFormUtils.getListValuesAsString(null);
        assertEquals(result, actual);
    }

    @Test
    public void testObtainValueFromAncRadioButtons() throws JSONException {
        String actual = "done_today";
        String result = ANCFormUtils.obtainValue("ultrasound", accordionValuesJson);
        assertEquals(result, actual);
    }

    @Test
    public void testObtainValueFromDatePicker() throws JSONException {
        String actual = "08-04-2019";
        String result = ANCFormUtils.obtainValue("blood_type_test_date", accordionValuesJson);
        assertEquals(result, actual);
    }

    @Test
    public void testObtainValueFromNativeRadioButton() throws JSONException {
        String actual = "ab";
        String result = ANCFormUtils.obtainValue("blood_type", accordionValuesJson);
        assertEquals(result, actual);
    }

    @Test
    public void testObtainValueFromCheckbox() throws JSONException {
        String actual = "[stock_out, expired_stock, other]";
        String result = ANCFormUtils.obtainValue("urine_test_notdone", accordionValuesJson);
        assertEquals(result, actual);
    }

    @Test
    public void testObtainValueFromNumberSelector() throws JSONException {
        String actual = "1";
        String result = ANCFormUtils.obtainValue("no_of_fetuses", accordionValuesJson);
        assertEquals(result, actual);
    }

    @Test
    public void testObtainValueFromHiddenValues() throws JSONException {
        String actual = "39 weeks 6 days";
        String result = ANCFormUtils.obtainValue("ultrasound_gest_age", accordionValuesJson);
        assertEquals(result, actual);
    }

    @Test
    public void testFilterCheckboxValuesWhenFilterOptionsValuesAreDefineAsArray() throws Exception {
        JSONObject mainObject = getMainJsonObject("json_test_forms/test_checkbox_filter_json_form");
        ANCFormUtils.processCheckboxFilteredItems(mainObject);
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
        ANCFormUtils.processCheckboxFilteredItems(mainObject);
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
        ANCFormUtils.processCheckboxFilteredItems(mainObject);
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

    @Test
    public void testGetFormJsonCoreShouldReturnSameFormPassed() throws JSONException {
        PartialContact partialContact = new PartialContact();
        Mockito.when(partialContactRepository.getPartialContact(partialContact)).thenReturn(null);
        Mockito.when(ancLibrary.getPartialContactRepository()).thenReturn(partialContactRepository);
        ReflectionHelpers.setStaticField(AncLibrary.class, "instance", ancLibrary);
        JSONObject form = new JSONObject(quickCheckForm);
        JSONObject result = ANCFormUtils.getFormJsonCore(partialContact, form);
        assertNotNull(result);
        assertEquals(form.toString(), result.toString());
        ReflectionHelpers.setStaticField(AncLibrary.class, "instance", null);
    }

    @Test
    public void testGetFormJsonCoreShouldReturnFormDraft() throws JSONException {
        PartialContact partialContact = new PartialContact();
        PartialContact partialContactResult = new PartialContact();
        JSONObject form = new JSONObject(quickCheckForm);
        partialContactResult.setFormJsonDraft(form.toString());

        Mockito.when(partialContactRepository.getPartialContact(partialContact)).thenReturn(partialContactResult);
        Mockito.when(ancLibrary.getPartialContactRepository()).thenReturn(partialContactRepository);
        ReflectionHelpers.setStaticField(AncLibrary.class, "instance", ancLibrary);

        JSONObject formArg = new JSONObject(quickCheckForm);
        JSONObject global = new JSONObject();
        global.put("contact_no", "1");
        formArg.put(JsonFormConstants.JSON_FORM_KEY.GLOBAL, global);
        JSONObject result = ANCFormUtils.getFormJsonCore(partialContact, formArg);
        assertNotNull(result);
        assertEquals(formArg.toString(), result.toString());
        ReflectionHelpers.setStaticField(AncLibrary.class, "instance", null);
    }
}