package org.smartregister.anc.library.activity;

import android.content.Context;

import org.json.JSONObject;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.application.AncTestApplication;
import org.smartregister.anc.library.shadows.MyShadowActivity;
import org.smartregister.anc.library.util.ANCJsonFormUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ndegwamartin on 27/03/2018.
 */

@RunWith(RobolectricTestRunner.class)
@Config(application = AncTestApplication.class, shadows = {MyShadowActivity.class})
public abstract class BaseUnitTest {
    protected static final String DUMMY_USERNAME = "myusername";
    protected static final String DUMMY_PASSWORD = "mypassword";
    protected static final String DUMMY_BASE_ENTITY_ID = "00ts-ime-hcla-0tib-0eht-ma0i";
    protected static final String TEST_STRING = "teststring";
    protected static final int DEFAULT_PROFILE_IMAGE_RESOURCE_ID = R.drawable.avatar_woman;
    protected static final String WHO_ANC_ID = "12345678";
    protected static final String GLOBAL_IDENTIFIER = "identifier";
    protected static final String NULL_STRING = null;
    protected static final long DUMMY_LONG = 1000l;
    protected static final Date DUMMY_DATE = Calendar.getInstance().getTime();
    protected static final String DUMMY_JSON = "[\r\n        {\r\n            \"key\": \"site_ipv_assess\",\r\n            \"label\": \"Minimum requirements for IPV assessment\",\r\n            \"value\": \"true\",\r\n            \"description\": \"\\\"Are all of the following in place at your facility: \\r\\n1. A protocol or standard operating procedure for Intimate Partner Violence (IPV); \\r\\n2. A health worker trained on how to ask about IPV and how to provide the minimum response or beyond;\\r\\n3. A private setting; \\r\\n4. A way to ensure confidentiality; \\r\\n5. Time to allow for appropriate disclosure; and\\r\\n6. A system for referral in place. \\\"\"\r\n        },\r\n        {\r\n            \"key\": \"site_anc_hiv\",\r\n            \"label\": \"Generalized HIV epidemic\",\r\n            \"value\": \"true\",\r\n            \"description\": \"Is the HIV prevalence consistently > 1% in pregnant women attending antenatal clinics at your facility?\"\r\n        },\r\n        {\r\n            \"key\": \"site_ultrasound\",\r\n            \"label\": \"Ultrasound available\",\r\n            \"value\": \"false\",\r\n            \"description\": \"Is an ultrasound machine available and functional at your facility and a trained health worker available to use it?\"\r\n        },\r\n        {\r\n            \"key\": \"site_bp_tool\",\r\n            \"label\": \"Automated BP measurement tool\",\r\n            \"value\": \"true\",\r\n            \"description\": \"Does your facility use an automated blood pressure (BP) measurement tool?\"\r\n        }\r\n    ]";
    protected static final String DUMMY_PHONE_NUMBER = "07233244059";
    protected static final int DUMMY_CONTACT_NO = 1;
    protected static final String DUMMY_JSON_OBJECT = "{\n" +
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
            "         \"key\": \"visual_disturbance\",\n" +
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

            "}," +
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
            "                  \"type\": \"extended_radio_button\",\n" +
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
    public static int ASYNC_TIMEOUT = 1000;

    protected JSONObject getMainJsonObject(String filePath) throws Exception {
        Context context = RuntimeEnvironment.application;
        return ANCJsonFormUtils.readJsonFromAsset(context, filePath);
    }
}
