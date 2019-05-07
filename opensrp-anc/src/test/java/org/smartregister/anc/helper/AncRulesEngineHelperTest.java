package org.smartregister.anc.helper;

import junit.framework.Assert;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.rule.AlertRule;
import org.smartregister.anc.rule.ContactRule;
import org.smartregister.anc.util.Constants;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ndegwamartin on 09/11/2018.
 */
public class AncRulesEngineHelperTest extends BaseUnitTest {

    private static final String ALERT_RULE_FIELD_TODAY_DATE = "todayDate";

    private AncRulesEngineHelper ancRulesEngineHelper;
    private String jsonObject = "{\n" +
            "   \"validate_on_submit\": true,\n" +
            "   \"count\": \"2\",\n" +
            "   \"encounter_type\": \"Tests\",\n" +
            "   \"step1\": {\n" +
            "      \"title\": \"Due\",\n" +
            "      \"next\": \"step2\",\n" +
            "      \"fields\": [\n" +
            "          {\n" +
            "   \"key\": \"symp_sev_preeclampsia\",\n" +
            "   \"type\": \"check_box\",\n" +
            "   \"label\": \"Any symptoms of severe pre-eclampsia?\",\n" +
            "   \"label_text_style\": \"bold\",\n" +
            "   \"text_color\": \"#000000\",\n" +
            "   \"exclusive\": [\n" +
            "      \"none\"\n" +
            "   ],\n" +
            "   \"options\": [\n" +
            "      {\n" +
            "         \"key\": \"none\",\n" +
            "         \"text\": \"None\",\n" +
            "         \"value\": false,\n" +
            "         \"openmrs_entity\": \"\",\n" +
            "         \"openmrs_entity_id\": \"\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"key\": \"severe_headache\",\n" +
            "         \"text\": \"Severe headache\",\n" +
            "         \"value\": false,\n" +
            "         \"openmrs_entity\": \"Headache\",\n" +
            "         \"openmrs_entity_id\": \"139084\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"key\": \"blurred_vision\",\n" +
            "         \"text\": \"Blurred vision\",\n" +
            "         \"value\": false,\n" +
            "         \"openmrs_entity\": \"Blurred vision\",\n" +
            "         \"openmrs_entity_id\": \"147104\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"key\": \"epigastric_pain\",\n" +
            "         \"text\": \"Epigastric pain\",\n" +
            "         \"value\": false,\n" +
            "         \"openmrs_entity\": \"\",\n" +
            "         \"openmrs_entity_id\": \"141128\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"key\": \"dizziness\",\n" +
            "         \"text\": \"Dizziness\",\n" +
            "         \"value\": false,\n" +
            "         \"openmrs_entity\": \"\",\n" +
            "         \"openmrs_entity_id\": \"\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"key\": \"vomiting\",\n" +
            "         \"text\": \"Vomiting\",\n" +
            "         \"value\": false,\n" +
            "         \"openmrs_entity\": \"\",\n" +
            "         \"openmrs_entity_id\": \"\"\n" +
            "      }\n" +
            "   ]\n" +

            "},"+
            "         {\n" +
            "            \"key\": \"accordion_ultrasound\",\n" +
            "            \"openmrs_entity_parent\": \"\",\n" +
            "            \"openmrs_entity\": \"\",\n" +
            "            \"openmrs_entity_id\": \"\",\n" +
            "            \"text\": \"Ultrasound test\",\n" +
            "            \"accordion_info_text\": \"An ultrasound is recommended for all women before 24 weeks gestation or even after if deemed necessary (e.g. to identify the number of fetuses, fetal presentation, or placenta location).\",\n" +
            "            \"accordion_info_title\": \"Ultrasound test\",\n" +
            "            \"type\": \"expansion_panel\",\n" +
            "            \"display_bottom_section\": true,\n" +
            "            \"content_form\": \"tests_ultrasound_sub_form\",\n" +
            "            \"container\": \"anc_test\",\n" +
            "            \"relevance\": {\n" +
            "               \"rules-engine\": {\n" +
            "                  \"ex-rules\": {\n" +
            "                     \"rules-file\": \"tests_relevance_rules.yml\"\n" +
            "                  }\n" +
            "               }\n" +
            "            },\n" +
            "            \"is_visible\": true,\n" +
            "            \"value\": [\n" +
            "               {\n" +
            "                  \"key\": \"ultrasound\",\n" +
            "                  \"type\": \"anc_radio_button\",\n" +
            "                  \"label\": \"Ultrasound test\",\n" +
            "                  \"values\": [\n" +
            "                     \"done_today:Done today\"\n" +
            "                  ],\n" +
            "                  \"openmrs_attributes\": {\n" +
            "                     \"openmrs_entity_parent\": \"\",\n" +
            "                     \"openmrs_entity\": \"\",\n" +
            "                     \"openmrs_entity_id\": \"\"\n" +
            "                  },\n" +
            "                  \"value_openmrs_attributes\": [\n" +
            "                     {\n" +
            "                        \"key\": \"ultrasound\",\n" +
            "                        \"openmrs_entity_parent\": \"\",\n" +
            "                        \"openmrs_entity\": \"\",\n" +
            "                        \"openmrs_entity_id\": \"\"\n" +
            "                     }\n" +
            "                  ]\n" +
            "               },\n" +
            "               {\n" +
            "                  \"key\": \"blood_type_test_date\",\n" +
            "                  \"type\": \"date_picker\",\n" +
            "                  \"label\": \"Blood type test date\",\n" +
            "                  \"index\": 2,\n" +
            "                  \"values\": [\n" +
            "                     \"08-04-2019\"\n" +
            "                  ],\n" +
            "                  \"openmrs_attributes\": {\n" +
            "                     \"openmrs_entity_parent\": \"\",\n" +
            "                     \"openmrs_entity\": \"concept\",\n" +
            "                     \"openmrs_entity_id\": \"12005AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "                  }\n" +
            "               },\n" +
            "               {\n" +
            "                  \"key\": \"blood_type\",\n" +
            "                  \"type\": \"native_radio\",\n" +
            "                  \"label\": \"Blood type\",\n" +
            "                  \"index\": 3,\n" +
            "                  \"values\": [\n" +
            "                     \"ab:AB\"\n" +
            "                  ],\n" +
            "                  \"openmrs_attributes\": {\n" +
            "                     \"openmrs_entity_parent\": \"\",\n" +
            "                     \"openmrs_entity\": \"concept\",\n" +
            "                     \"openmrs_entity_id\": \"12006AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "                  },\n" +
            "                  \"value_openmrs_attributes\": [\n" +
            "                     {\n" +
            "                        \"key\": \"blood_type\",\n" +
            "                        \"openmrs_entity_parent\": \"\",\n" +
            "                        \"openmrs_entity\": \"concept\",\n" +
            "                        \"openmrs_entity_id\": \"12009AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "                     }\n" +
            "                  ]\n" +
            "               },\n" +
            "               {\n" +
            "                  \"key\": \"urine_test_notdone\",\n" +
            "                  \"type\": \"check_box\",\n" +
            "                  \"label\": \"Reason\",\n" +
            "                  \"index\": 1,\n" +
            "                  \"values\": [\n" +
            "                     \"stock_out:Stock out:true\",\n" +
            "                     \"expired_stock:Expired stock:true\",\n" +
            "                     \"other:Other (specify):true\"\n" +
            "                  ],\n" +
            "                  \"openmrs_attributes\": {\n" +
            "                     \"openmrs_entity_parent\": \"\",\n" +
            "                     \"openmrs_entity\": \"\",\n" +
            "                     \"openmrs_entity_id\": \"\"\n" +
            "                  },\n" +
            "                  \"value_openmrs_attributes\": [\n" +
            "                     {\n" +
            "                        \"key\": \"urine_test_notdone\",\n" +
            "                        \"openmrs_entity_parent\": \"\",\n" +
            "                        \"openmrs_entity\": \"\",\n" +
            "                        \"openmrs_entity_id\": \"\"\n" +
            "                     },\n" +
            "                     {\n" +
            "                        \"key\": \"urine_test_notdone\",\n" +
            "                        \"openmrs_entity_parent\": \"\",\n" +
            "                        \"openmrs_entity\": \"\",\n" +
            "                        \"openmrs_entity_id\": \"\"\n" +
            "                     },\n" +
            "                     {\n" +
            "                        \"key\": \"urine_test_notdone\",\n" +
            "                        \"openmrs_entity_parent\": \"\",\n" +
            "                        \"openmrs_entity\": \"\",\n" +
            "                        \"openmrs_entity_id\": \"\"\n" +
            "                     }\n" +
            "                  ]\n" +
            "               },\n" +
            "               {\n" +
            "                  \"key\": \"no_of_fetuses\",\n" +
            "                  \"type\": \"numbers_selector\",\n" +
            "                  \"label\": \"No. of fetuses\",\n" +
            "                  \"values\": [\n" +
            "                     \"1\"\n" +
            "                  ],\n" +
            "                  \"openmrs_attributes\": {\n" +
            "                     \"openmrs_entity_parent\": \"\",\n" +
            "                     \"openmrs_entity\": \"\",\n" +
            "                     \"openmrs_entity_id\": \"\"\n" +
            "                  }\n" +
            "               },\n" +
            "               {\n" +
            "                  \"key\": \"ultrasound_gest_age\",\n" +
            "                  \"type\": \"hidden\",\n" +
            "                  \"label\": \"\",\n" +
            "                  \"values\": [\n" +
            "                     \"39 weeks 6 days\"\n" +
            "                  ],\n" +
            "                  \"openmrs_attributes\": {\n" +
            "                     \"openmrs_entity_parent\": \"\",\n" +
            "                     \"openmrs_entity\": \"\",\n" +
            "                     \"openmrs_entity_id\": \"\"\n" +
            "                  }\n" +
            "               },\n" +
            "               {\n" +
            "                  \"key\": \"elly_test\",\n" +
            "                  \"type\": \"edit_text\",\n" +
            "                  \"label\": \"Testing my own rules\",\n" +
            "                  \"values\": [\n" +
            "                     \"12\"\n" +
            "                  ],\n" +
            "                  \"openmrs_attributes\": {\n" +
            "                     \"openmrs_entity_parent\": \"\",\n" +
            "                     \"openmrs_entity\": \"\",\n" +
            "                     \"openmrs_entity_id\": \"\"\n" +
            "                  }\n" +
            "               }\n" +
            "            ]\n" +
            "         },\n" +
            "         {\n" +
            "            \"key\": \"accordion_blood_type\",\n" +
            "            \"openmrs_entity_parent\": \"\",\n" +
            "            \"openmrs_entity\": \"\",\n" +
            "            \"openmrs_entity_id\": \"\",\n" +
            "            \"text\": \"Blood Type test\",\n" +
            "            \"type\": \"expansion_panel\",\n" +
            "            \"display_bottom_section\": true,\n" +
            "            \"content_form\": \"tests_blood_type_sub_form\",\n" +
            "            \"container\": \"anc_test\",\n" +
            "            \"relevance\": {\n" +
            "               \"rules-engine\": {\n" +
            "                  \"ex-rules\": {\n" +
            "                     \"rules-file\": \"tests_relevance_rules.yml\"\n" +
            "                  }\n" +
            "               }\n" +
            "            },\n" +
            "            \"is_visible\": true\n" +
            "         }\n" +
            "      ]\n" +
            "   },\n" +
            "   \"step2\": {\n" +
            "      \"title\": \"Other\",\n" +
            "      \"fields\": [\n" +
            "         {\n" +
            "            \"key\": \"accordion_other_tests\",\n" +
            "            \"openmrs_entity_parent\": \"\",\n" +
            "            \"openmrs_entity\": \"\",\n" +
            "            \"openmrs_entity_id\": \"\",\n" +
            "            \"text\": \"Other Tests\",\n" +
            "            \"accordion_info_text\": \"If any other test was done that is not included here, add it here.\",\n" +
            "            \"accordion_info_title\": \"Other test\",\n" +
            "            \"type\": \"expansion_panel\",\n" +
            "            \"display_bottom_section\": true,\n" +
            "            \"content_form\": \"tests_other_tests_sub_form\",\n" +
            "            \"container\": \"anc_test\"\n" +
            "         }\n" +
            "      ]\n" +
            "   }\n" +
            "}";

    @Mock
    private Rules rules;

    @Mock
    private Facts facts;

    @Mock
    private RulesEngine inferentialRulesEngine;

    @Mock
    private RulesEngine defaultRulesEngine;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ancRulesEngineHelper = new AncRulesEngineHelper(RuntimeEnvironment.application);
    }

    @Test
    public void testRulesEngineHelperConstructorCreatesValidInstance() {
        Assert.assertNotNull(ancRulesEngineHelper);
    }

    @Test
    public void testProcessInferentialRulesInvokesCorrectRulesEngineWithCorrectParameters() {

        Whitebox.setInternalState(ancRulesEngineHelper, "inferentialRulesEngine", inferentialRulesEngine);
        Whitebox.setInternalState(ancRulesEngineHelper, "defaultRulesEngine", defaultRulesEngine);

        ancRulesEngineHelper.processInferentialRules(rules, facts);

        Mockito.verify(inferentialRulesEngine).fire(rules, facts);
        Mockito.verify(defaultRulesEngine, Mockito.times(0)).fire(rules, facts);
    }

    @Test
    public void testProcessDefaultRulesInvokesCorrectRulesEngineWithCorrectParameters() {

        Whitebox.setInternalState(ancRulesEngineHelper, "inferentialRulesEngine", inferentialRulesEngine);
        Whitebox.setInternalState(ancRulesEngineHelper, "defaultRulesEngine", defaultRulesEngine);

        ancRulesEngineHelper.processDefaultRules(rules, facts);

        Mockito.verify(defaultRulesEngine).fire(rules, facts);
        Mockito.verify(inferentialRulesEngine, Mockito.times(0)).fire(rules, facts);
    }

    @Test
    public void testGetButtonAlertStatusReturnsNotDueForUndueDate() {

        //Not due
        AlertRule alertRule = new AlertRule(30, "2019-07-09");
        Whitebox.setInternalState(alertRule, ALERT_RULE_FIELD_TODAY_DATE, new LocalDate("2018-11-09"));

        String buttonAlertStatus = ancRulesEngineHelper.getButtonAlertStatus(alertRule, Constants.RULES_FILE.ALERT_RULES);

        Assert.assertEquals(Constants.ALERT_STATUS.NOT_DUE, buttonAlertStatus);
    }


    @Test
    public void testGetButtonAlertStatusReturnsDueForADueDate() {
        //Due
        AlertRule alertRule = new AlertRule(30, "2018-11-09");
        Whitebox.setInternalState(alertRule, ALERT_RULE_FIELD_TODAY_DATE, new LocalDate("2018-11-09"));

        String buttonAlertStatus = ancRulesEngineHelper.getButtonAlertStatus(alertRule, Constants.RULES_FILE.ALERT_RULES);

        Assert.assertEquals(Constants.ALERT_STATUS.DUE, buttonAlertStatus);
    }


    @Test
    public void testGetButtonAlertStatusReturnsOverDueForOverdueDate() {

        //OverDue
        AlertRule alertRule = new AlertRule(30, "2018-11-01");
        Whitebox.setInternalState(alertRule, ALERT_RULE_FIELD_TODAY_DATE, new LocalDate("2018-11-09"));

        String buttonAlertStatus = ancRulesEngineHelper.getButtonAlertStatus(alertRule, Constants.RULES_FILE.ALERT_RULES);

        Assert.assertEquals(Constants.ALERT_STATUS.OVERDUE, buttonAlertStatus);
    }


    @Test
    public void testGetButtonAlertStatusReturnsDueDeliveryForDueDeliveryDate() {

        //delivery due
        AlertRule alertRule = new AlertRule(40, "2018-11-11");
        Whitebox.setInternalState(alertRule, ALERT_RULE_FIELD_TODAY_DATE, new LocalDate("2018-11-12"));

        String buttonAlertStatus = ancRulesEngineHelper.getButtonAlertStatus(alertRule, Constants.RULES_FILE.ALERT_RULES);

        Assert.assertEquals(Constants.ALERT_STATUS.DELIVERY_DUE, buttonAlertStatus);
    }

    @Test
    public void testGetButtonAlertStatusReturnsExpiredForExpiredDeliveryDate() {

        //expired
        AlertRule alertRule = new AlertRule(40, "2018-11-04");
        Whitebox.setInternalState(alertRule, ALERT_RULE_FIELD_TODAY_DATE, new LocalDate("2018-11-11"));

        String buttonAlertStatus = ancRulesEngineHelper.getButtonAlertStatus(alertRule, Constants.RULES_FILE.ALERT_RULES);

        Assert.assertEquals(Constants.ALERT_STATUS.EXPIRED, buttonAlertStatus);
    }


    @Test
    public void testGetButtonAlertStatusReturnsExpiredOverdueForOverdueDeliveryDate() {

        //Expired Overdue
        AlertRule alertRule = new AlertRule(41, "2018-10-09");
        Whitebox.setInternalState(alertRule, ALERT_RULE_FIELD_TODAY_DATE, new LocalDate("2018-11-09"));

        String buttonAlertStatus = ancRulesEngineHelper.getButtonAlertStatus(alertRule, Constants.RULES_FILE.ALERT_RULES);

        Assert.assertEquals(Constants.ALERT_STATUS.EXPIRED, buttonAlertStatus);
    }

    @Test
    public void testGetContactVisitScheduleInvokesInferentialRulesEngineProcessInferentialRulesWithCorrectParams() {

        ContactRule contactRule = new ContactRule(20, true, DUMMY_BASE_ENTITY_ID);

        AncRulesEngineHelper ancRulesEngineHelperSpy = Mockito.spy(ancRulesEngineHelper);
        List<Integer> scheduleWeeksList = ancRulesEngineHelperSpy.getContactVisitSchedule(contactRule, Constants.RULES_FILE.CONTACT_RULES);

        Assert.assertNotNull(scheduleWeeksList);

        Mockito.verify(ancRulesEngineHelperSpy).processInferentialRules(ArgumentMatchers.any(Rules.class), ArgumentMatchers.any(Facts.class));

    }

    @Test
    public void testGetContactVisitScheduleGeneratesCorrectScheduleAt4Weeks() {

        ContactRule contactRule = new ContactRule(4, true, DUMMY_BASE_ENTITY_ID);

        AncRulesEngineHelper ancRulesEngineHelperSpy = Mockito.spy(ancRulesEngineHelper);
        List<Integer> scheduleWeeksList = ancRulesEngineHelperSpy.getContactVisitSchedule(contactRule, Constants.RULES_FILE.CONTACT_RULES);

        Assert.assertNotNull(scheduleWeeksList);

        Assert.assertEquals(Arrays.asList(new Integer[]{12, 20, 26, 30, 34, 36, 38, 40, 41}), scheduleWeeksList);

    }

    @Test
    public void testGetContactVisitScheduleGeneratesCorrectScheduleAt12Weeks() {

        ContactRule contactRule = new ContactRule(12, true, DUMMY_BASE_ENTITY_ID);

        AncRulesEngineHelper ancRulesEngineHelperSpy = Mockito.spy(ancRulesEngineHelper);
        List<Integer> scheduleWeeksList = ancRulesEngineHelperSpy.getContactVisitSchedule(contactRule, Constants.RULES_FILE.CONTACT_RULES);

        Assert.assertNotNull(scheduleWeeksList);

        Assert.assertEquals(Arrays.asList(new Integer[]{20, 26, 30, 34, 36, 38, 40, 41}), scheduleWeeksList);

    }

    @Test
    public void testGetContactVisitScheduleGeneratesCorrectScheduleAt20Weeks() {

        ContactRule contactRule = new ContactRule(20, true, DUMMY_BASE_ENTITY_ID);

        AncRulesEngineHelper ancRulesEngineHelperSpy = Mockito.spy(ancRulesEngineHelper);
        List<Integer> scheduleWeeksList = ancRulesEngineHelperSpy.getContactVisitSchedule(contactRule, Constants.RULES_FILE.CONTACT_RULES);

        Assert.assertNotNull(scheduleWeeksList);

        Assert.assertEquals(Arrays.asList(new Integer[]{26, 30, 34, 36, 38, 40, 41}), scheduleWeeksList);

    }

    @Test
    public void testGetContactVisitScheduleGeneratesCorrectScheduleAt28Weeks() {

        ContactRule contactRule = new ContactRule(28, true, DUMMY_BASE_ENTITY_ID);

        AncRulesEngineHelper ancRulesEngineHelperSpy = Mockito.spy(ancRulesEngineHelper);
        List<Integer> scheduleWeeksList = ancRulesEngineHelperSpy.getContactVisitSchedule(contactRule, Constants.RULES_FILE.CONTACT_RULES);

        Assert.assertNotNull(scheduleWeeksList);

        Assert.assertEquals(Arrays.asList(new Integer[]{32, 34, 36, 38, 40, 41}), scheduleWeeksList);

    }

    @Test
    public void testGetContactVisitScheduleGeneratesCorrectScheduleAt40Weeks() {

        ContactRule contactRule = new ContactRule(40, true, DUMMY_BASE_ENTITY_ID);

        AncRulesEngineHelper ancRulesEngineHelperSpy = Mockito.spy(ancRulesEngineHelper);
        List<Integer> scheduleWeeksList = ancRulesEngineHelperSpy.getContactVisitSchedule(contactRule, Constants.RULES_FILE.CONTACT_RULES);

        Assert.assertNotNull(scheduleWeeksList);

        Assert.assertEquals(Arrays.asList(new Integer[]{40, 41}), scheduleWeeksList);

    }

    @Test
    public void testStripGaNumber() {
        AncRulesEngineHelper ancRulesEngineHelperSpy = Mockito.spy(ancRulesEngineHelper);
        Assert.assertEquals("12", ancRulesEngineHelperSpy.stripGaNumber("12 Weeks 7 Days"));
        Assert.assertEquals("12", ancRulesEngineHelperSpy.stripGaNumber("12"));
        Assert.assertEquals("12", ancRulesEngineHelperSpy.stripGaNumber("12 Weeks"));
        Assert.assertEquals("12", ancRulesEngineHelperSpy.stripGaNumber("12 Weeks 7"));
    }
    @Test
    public void testGetValueFromCommonInputsFieldInAccordion() throws JSONException {
        AncRulesEngineHelper ancRulesEngineHelperSpy = Mockito.spy(ancRulesEngineHelper);
        ancRulesEngineHelperSpy.setJsonObject(new JSONObject(jsonObject));
        //Test obtaining value for edit_text field
        Assert.assertEquals(ancRulesEngineHelperSpy.getValueFromAccordion("accordion_ultrasound","step1_elly_test"),"12");
        //Test obtaining value for hidden field
        Assert.assertEquals(ancRulesEngineHelperSpy.getValueFromAccordion("accordion_ultrasound","step1_ultrasound_gest_age"),"39 weeks 6 days");
        //Test obtaining value for number_selector field
        Assert.assertEquals(ancRulesEngineHelperSpy.getValueFromAccordion("accordion_ultrasound","step1_no_of_fetuses"),"1");
        //Test obtaining value for date_picker field
        Assert.assertEquals(ancRulesEngineHelperSpy.getValueFromAccordion("accordion_ultrasound","step1_blood_type_test_date"),"08-04-2019");
    }
    @Test
    public void testGetValueFromRadioButtonsFieldInAccordion() throws JSONException {
        AncRulesEngineHelper ancRulesEngineHelperSpy = Mockito.spy(ancRulesEngineHelper);
        ancRulesEngineHelperSpy.setJsonObject(new JSONObject(jsonObject));
        //Test obtaining value for anc_radio_button field
        Assert.assertEquals(ancRulesEngineHelperSpy.getValueFromAccordion("accordion_ultrasound","step1_ultrasound"),"done_today");
        //Test obtaining value for native_radio field
        Assert.assertEquals(ancRulesEngineHelperSpy.getValueFromAccordion("accordion_ultrasound","step1_blood_type"),"ab");

    }
    @Test
    public void testGetValueFromCheckobxFieldInAccordion() throws JSONException {
        AncRulesEngineHelper ancRulesEngineHelperSpy = Mockito.spy(ancRulesEngineHelper);
        ancRulesEngineHelperSpy.setJsonObject(new JSONObject(jsonObject));
        //Test obtaining value for check_box field
        Assert.assertEquals(ancRulesEngineHelperSpy.getValueFromAccordion("accordion_ultrasound","step1_urine_test_notdone"),"[stock_out, expired_stock, other]");

    }
    @Test
    public void testGetValueFromAccordionWithEmptyJson() throws JSONException {
        AncRulesEngineHelper ancRulesEngineHelperSpy = Mockito.spy(ancRulesEngineHelper);
        Assert.assertEquals(ancRulesEngineHelperSpy.getValueFromAccordion("accordion_ultrasound","step1_blood_type_test_date"),"");
    }
    @Test
    public void testGetValueFromAccordionWithMissingStep() throws JSONException {
        AncRulesEngineHelper ancRulesEngineHelperSpy = Mockito.spy(ancRulesEngineHelper);
        Assert.assertEquals(ancRulesEngineHelperSpy.getValueFromAccordion("accordion_ultrasound","step3_blood_type_test_date"),"");
    }
    @Test
    public void testGetValueFromAccordionWithNoValues() throws JSONException {
        AncRulesEngineHelper ancRulesEngineHelperSpy = Mockito.spy(ancRulesEngineHelper);
        Assert.assertEquals(ancRulesEngineHelperSpy.getValueFromAccordion("accordion_other_tests","step2_blood_type_test_date"),"");
    }

    @Test
    public void testFilterCheckboxOptions() throws  JSONException{
        AncRulesEngineHelper ancRulesEngineHelperSpy = Mockito.spy(ancRulesEngineHelper);
        ancRulesEngineHelperSpy.setJsonObject(new JSONObject(jsonObject));
        ancRulesEngineHelperSpy.filterCheckboxValues("step1_symp_sev_preeclampsia","[severe_headache, epigastric_pain, dizziness]");
    }
}
