package org.smartregister.anc.library.model;

import androidx.core.util.Pair;

import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.BuildConfig;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.contract.RegisterContract;
import org.smartregister.anc.library.util.ANCJsonFormUtils;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.FormUtils;
import org.smartregister.util.Utils;

import java.util.Date;

@RunWith(PowerMockRunner.class)
public class RegisterModelTest extends BaseUnitTest {
    private RegisterContract.Model model;
    private String jsonString = "{\n" +
            "  \"count\": \"1\",\n" +
            "  \"encounter_type\": \"ANC Registration\",\n" +
            "  \"entity_id\": \"\",\n" +
            "  \"relational_id\": \"\",\n" +
            "  \"metadata\": {\n" +
            "    \"start\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"start\",\n" +
            "      \"openmrs_entity_id\": \"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "      \"value\": \"2018-07-25 04:40:18\"\n" +
            "    },\n" +
            "    \"end\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"end\",\n" +
            "      \"openmrs_entity_id\": \"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "      \"value\": \"2018-07-25 04:40:54\"\n" +
            "    },\n" +
            "    \"today\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"encounter\",\n" +
            "      \"openmrs_entity_id\": \"encounter_date\",\n" +
            "      \"value\": \"25-07-2018\"\n" +
            "    },\n" +
            "    \"deviceid\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"deviceid\",\n" +
            "      \"openmrs_entity_id\": \"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "      \"value\": \"51bd45e1fa36981e\"\n" +
            "    },\n" +
            "    \"subscriberid\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"subscriberid\",\n" +
            "      \"openmrs_entity_id\": \"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "      \"value\": \"310270000000000\"\n" +
            "    },\n" +
            "    \"simserial\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"simserial\",\n" +
            "      \"openmrs_entity_id\": \"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "      \"value\": \"89014103211118510720\"\n" +
            "    },\n" +
            "    \"phonenumber\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"phonenumber\",\n" +
            "      \"openmrs_entity_id\": \"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "      \"value\": \"15555218135\"\n" +
            "    },\n" +
            "    \"encounter_location\": \"44de66fb-e6c6-4bae-92bb-386dfe626eba\",\n" +
            "    \"look_up\": {\n" +
            "      \"entity_id\": \"\",\n" +
            "      \"value\": \"\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"step1\": {\n" +
            "    \"title\": \"Woman Registration\",\n" +
            "    \"fields\": [\n" +
            "      {\n" +
            "        \"key\": \"photo\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"\",\n" +
            "        \"openmrs_entity_id\": \"\",\n" +
            "        \"type\": \"choose_image\",\n" +
            "        \"uploadButtonText\": \"Take a picture of the woman\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"anc_id\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person_identifier\",\n" +
            "        \"openmrs_entity_id\": \"ANC_ID\",\n" +
            "        \"type\": \"barcode\",\n" +
            "        \"barcode_type\": \"qrcode\",\n" +
            "        \"hint\": \"ANC ID *\",\n" +
            "        \"scanButtonText\": \"Scan QR Code\",\n" +
            "        \"v_numeric\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Please enter a valid ANC ID\"\n" +
            "        },\n" +
            "        \"v_required\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Please enter the Woman's ANC ID\"\n" +
            "        },\n" +
            "        \"value\": \"1723154\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"first_name\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person\",\n" +
            "        \"openmrs_entity_id\": \"first_name\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"First name *\",\n" +
            "        \"edit_type\": \"name\",\n" +
            "        \"v_required\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Please enter the first name\"\n" +
            "        },\n" +
            "        \"v_regex\": {\n" +
            "          \"value\": \"[A-Za-z\\\\s.-]*\",\n" +
            "          \"err\": \"Please enter a valid name\"\n" +
            "        },\n" +
            "        \"value\": \"Test_First_Name\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"last_name\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person\",\n" +
            "        \"openmrs_entity_id\": \"last_name\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"Last name *\",\n" +
            "        \"edit_type\": \"name\",\n" +
            "        \"v_required\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Please enter the last name\"\n" +
            "        },\n" +
            "        \"v_regex\": {\n" +
            "          \"value\": \"[A-Za-z\\\\s.-]*\",\n" +
            "          \"err\": \"Please enter a valid name\"\n" +
            "        },\n" +
            "        \"value\": \"Test_Last_Name\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"Sex\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person\",\n" +
            "        \"openmrs_entity_id\": \"gender\",\n" +
            "        \"type\": \"hidden\",\n" +
            "        \"value\": \"female\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"dob\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person\",\n" +
            "        \"openmrs_entity_id\": \"birthdate\",\n" +
            "        \"type\": \"date_picker\",\n" +
            "        \"hint\": \"Date of birth(DOB) *\",\n" +
            "        \"expanded\": false,\n" +
            "        \"duration\": {\n" +
            "          \"label\": \"Age\"\n" +
            "        },\n" +
            "        \"min_date\": \"today-49y\",\n" +
            "        \"max_date\": \"today-15y\",\n" +
            "        \"v_required\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Please enter the date of birth\"\n" +
            "        },\n" +
            "        \"relevance\": {\n" +
            "          \"step1:isDateOfBirthUnknown\": {\n" +
            "            \"type\": \"string\",\n" +
            "            \"ex\": \"equalTo(., \\\"false\\\")\"\n" +
            "          }\n" +
            "        },\n" +
            "        \"value\": \"25-07-2003\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"isDateOfBirthUnknown\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"\",\n" +
            "        \"openmrs_entity_id\": \"\",\n" +
            "        \"type\": \"check_box\",\n" +
            "        \"label\": \"\",\n" +
            "        \"options\": [\n" +
            "          {\n" +
            "            \"key\": \"isDateOfBirthUnknown\",\n" +
            "            \"text\": \"DOB unknown?\",\n" +
            "            \"value\": \"false\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"age\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person_attribute\",\n" +
            "        \"openmrs_entity_id\": \"age_entered\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"Enter Age if DOB unknown *\",\n" +
            "        \"v_numeric\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Number must begin with 0 and must be a total of 10 digits in length\"\n" +
            "        },\n" +
            "        \"v_regex\": {\n" +
            "          \"value\": \"([1][5-9])|([2-4][0-9])|s*\",\n" +
            "          \"err\": \"Number must in the range 15 to 49\"\n" +
            "        },\n" +
            "        \"v_required\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Please enter the woman's age\"\n" +
            "        },\n" +
            "        \"relevance\": {\n" +
            "          \"step1:isDateOfBirthUnknown\": {\n" +
            "            \"type\": \"string\",\n" +
            "            \"ex\": \"equalTo(., \\\"true\\\")\"\n" +
            "          }\n" +
            "        },\n" +
            "        \"value\": \"\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"home_address\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person_attribute\",\n" +
            "        \"openmrs_entity_id\": \"home_address\",\n" +
            "        \"openmrs_data_type\": \"text\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"Home address *\",\n" +
            "        \"v_required\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Please enter the woman's home address\"\n" +
            "        },\n" +
            "        \"v_regex\": {\n" +
            "          \"value\": \"[A-Za-z0-9\\\\s.-]*\",\n" +
            "          \"err\": \"Please enter a valid name\"\n" +
            "        },\n" +
            "        \"value\": \"Test\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"phone_number\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person_attribute\",\n" +
            "        \"openmrs_entity_id\": \"phone_number\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"Mobile phone number *\",\n" +
            "        \"v_numeric\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Number must be a total of 10 digits in length\"\n" +
            "        },\n" +
            "        \"v_regex\": {\n" +
            "          \"value\": \"([0-9]{10})|s*\",\n" +
            "          \"err\": \"Number must be a total of 10 digits in length\"\n" +
            "        },\n" +
            "        \"v_required\": {\n" +
            "          \"value\": true,\n" +
            "          \"err\": \"Please specify the woman's phone number\"\n" +
            "        },\n" +
            "        \"value\": \"0700000000\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"alt_name\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person_attribute\",\n" +
            "        \"openmrs_entity_id\": \"alt_name\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"Alternate contact name\",\n" +
            "        \"edit_type\": \"name\",\n" +
            "        \"look_up\": \"true\",\n" +
            "        \"entity_id\": \"\",\n" +
            "        \"v_regex\": {\n" +
            "          \"value\": \"[A-Za-z\\\\s.-]*\",\n" +
            "          \"err\": \"Please enter a valid VHT name\"\n" +
            "        },\n" +
            "        \"value\": \"Test_Alt_Name\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"alt_phone_number\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person_attribute\",\n" +
            "        \"openmrs_entity_id\": \"alt_phone_number\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"Alternate contact phone number\",\n" +
            "        \"v_numeric\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Number must be a total of 10 digits in length\"\n" +
            "        },\n" +
            "        \"v_regex\": {\n" +
            "          \"value\": \"([0-9]{10})|s*\",\n" +
            "          \"err\": \"Number must be a total of 10 digits in length\"\n" +
            "        },\n" +
            "        \"value\": \"0700000001\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"openmrs_entity\": \"person_attribute\",\n" +
            "        \"openmrs_entity_id\": \"next_contact\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"type\": \"hidden\",\n" +
            "        \"value\": \"2\",\n" +
            "        \"key\": \"next_contact\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"openmrs_entity\": \"person_attribute\",\n" +
            "        \"read_only\": false,\n" +
            "        \"openmrs_entity_id\": \"next_contact_date\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"type\": \"hidden\",\n" +
            "        \"value\": \"2018-08-09\",\n" +
            "        \"key\": \"next_contact_date\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";
    private String json = "{\n" +
            "  \"count\": \"1\",\n" +
            "  \"encounter_type\": \"ANC Registration\",\n" +
            "  \"entity_id\": \"\",\n" +
            "  \"relational_id\": \"\",\n" +
            "  \"metadata\": {\n" +
            "    \"start\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"start\",\n" +
            "      \"openmrs_entity_id\": \"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "    },\n" +
            "    \"end\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"end\",\n" +
            "      \"openmrs_entity_id\": \"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "    },\n" +
            "    \"today\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"encounter\",\n" +
            "      \"openmrs_entity_id\": \"encounter_date\"\n" +
            "    },\n" +
            "    \"deviceid\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"deviceid\",\n" +
            "      \"openmrs_entity_id\": \"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "    },\n" +
            "    \"subscriberid\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"subscriberid\",\n" +
            "      \"openmrs_entity_id\": \"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "    },\n" +
            "    \"simserial\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"simserial\",\n" +
            "      \"openmrs_entity_id\": \"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "    },\n" +
            "    \"phonenumber\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"phonenumber\",\n" +
            "      \"openmrs_entity_id\": \"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "    },\n" +
            "    \"encounter_location\": \"\",\n" +
            "    \"look_up\": {\n" +
            "      \"entity_id\": \"\",\n" +
            "      \"value\": \"\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"step1\": {\n" +
            "    \"title\": \"Woman Registration\",\n" +
            "    \"fields\": [\n" +
            "      {\n" +
            "        \"key\": \"photo\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"\",\n" +
            "        \"openmrs_entity_id\": \"\",\n" +
            "        \"type\": \"choose_image\",\n" +
            "        \"uploadButtonText\": \"Take a picture of the woman\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"anc_id\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person_identifier\",\n" +
            "        \"openmrs_entity_id\": \"ANC_ID\",\n" +
            "        \"type\": \"barcode\",\n" +
            "        \"barcode_type\": \"qrcode\",\n" +
            "        \"hint\": \"ANC ID *\",\n" +
            "        \"value\": \"0\",\n" +
            "        \"scanButtonText\": \"Scan QR Code\",\n" +
            "        \"v_numeric\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Please enter a valid ANC ID\"\n" +
            "        },\n" +
            "        \"v_required\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Please enter the Woman's ANC ID\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"first_name\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person\",\n" +
            "        \"openmrs_entity_id\": \"first_name\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"First name *\",\n" +
            "        \"edit_type\": \"name\",\n" +
            "        \"v_required\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Please enter the first name\"\n" +
            "        },\n" +
            "        \"v_regex\": {\n" +
            "          \"value\": \"[A-Za-z%5C%5Cs%5C.%5C-]*\",\n" +
            "          \"err\": \"Please enter a valid name\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"last_name\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person\",\n" +
            "        \"openmrs_entity_id\": \"last_name\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"Last name *\",\n" +
            "        \"edit_type\": \"name\",\n" +
            "        \"v_required\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Please enter the last name\"\n" +
            "        },\n" +
            "        \"v_regex\": {\n" +
            "          \"value\": \"[A-Za-z%5C%5Cs%5C.%5C-]*\",\n" +
            "          \"err\": \"Please enter a valid name\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"Sex\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person\",\n" +
            "        \"openmrs_entity_id\": \"gender\",\n" +
            "        \"type\": \"hidden\",\n" +
            "        \"value\": \"female\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"dob\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person\",\n" +
            "        \"openmrs_entity_id\": \"birthdate\",\n" +
            "        \"type\": \"date_picker\",\n" +
            "        \"hint\": \"Date of birth(DOB) *\",\n" +
            "        \"expanded\": false,\n" +
            "        \"duration\": {\n" +
            "          \"label\": \"Age\"\n" +
            "        },\n" +
            "        \"min_date\": \"today-49y\",\n" +
            "        \"max_date\": \"today-15y\",\n" +
            "        \"v_required\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Please enter the date of birth\"\n" +
            "        },\n" +
            "        \"relevance\": {\n" +
            "          \"step1:isDateOfBirthUnknown\": {\n" +
            "            \"type\": \"string\",\n" +
            "            \"ex\": \"equalTo(., \\\"false\\\")\"\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"isDateOfBirthUnknown\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"\",\n" +
            "        \"openmrs_entity_id\": \"\",\n" +
            "        \"type\": \"check_box\",\n" +
            "        \"label\": \"\",\n" +
            "        \"options\": [\n" +
            "          {\n" +
            "            \"key\": \"isDateOfBirthUnknown\",\n" +
            "            \"text\": \"DOB unknown?\",\n" +
            "            \"value\": \"false\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"age\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person_attribute\",\n" +
            "        \"openmrs_entity_id\": \"age_entered\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"Enter Age if DOB unknown *\",\n" +
            "        \"v_numeric\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Number must begin with 0 and must be a total of 10 digits in length\"\n" +
            "        },\n" +
            "        \"v_regex\": {\n" +
            "          \"value\": \"([1][5-9])|([2-4][0-9])|%5Cs*\",\n" +
            "          \"err\": \"Number must in the range 15 to 49\"\n" +
            "        },\n" +
            "        \"relevance\": {\n" +
            "          \"step1:isDateOfBirthUnknown\": {\n" +
            "            \"type\": \"string\",\n" +
            "            \"ex\": \"equalTo(., \\\"true\\\")\"\n" +
            "          }\n" +
            "        },\n" +
            "        \"v_required\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Please enter the woman's age\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"home_address\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person_attribute\",\n" +
            "        \"openmrs_entity_id\": \"home_address\",\n" +
            "        \"openmrs_data_type\": \"text\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"Home address *\",\n" +
            "        \"v_required\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Please enter the woman's home address\"\n" +
            "        },\n" +
            "        \"v_regex\": {\n" +
            "          \"value\": \"[A-Za-z0-9%5C%5Cs%5C.%5C-]*\",\n" +
            "          \"err\": \"Please enter a valid name\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"phone_number\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person_attribute\",\n" +
            "        \"openmrs_entity_id\": \"phone_number\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"Mobile phone number *\",\n" +
            "        \"v_numeric\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Number must be a total of 10 digits in length\"\n" +
            "        },\n" +
            "        \"v_regex\": {\n" +
            "          \"value\": \"([0-9]{10})|%5Cs*\",\n" +
            "          \"err\": \"Number must be a total of 10 digits in length\"\n" +
            "        },\n" +
            "        \"v_required\": {\n" +
            "          \"value\": true,\n" +
            "          \"err\": \"Please specify the woman's phone number\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"alt_name\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person_attribute\",\n" +
            "        \"openmrs_entity_id\": \"alt_name\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"Alternate contact name\",\n" +
            "        \"edit_type\": \"name\",\n" +
            "        \"look_up\": \"true\",\n" +
            "        \"entity_id\": \"\",\n" +
            "        \"v_regex\": {\n" +
            "          \"value\": \"[A-Za-z%5C%5Cs%5C.%5C-]*\",\n" +
            "          \"err\": \"Please enter a valid VHT name\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"alt_phone_number\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"person_attribute\",\n" +
            "        \"openmrs_entity_id\": \"alt_phone_number\",\n" +
            "        \"type\": \"edit_text\",\n" +
            "        \"hint\": \"Alternate contact phone number\",\n" +
            "        \"v_numeric\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Number must be a total of 10 digits in length\"\n" +
            "        },\n" +
            "        \"v_regex\": {\n" +
            "          \"value\": \"([0-9]{10})|%5Cs*\",\n" +
            "          \"err\": \"Number must be a total of 10 digits in length\"\n" +
            "        }\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        model = new RegisterModel();
    }

    @Ignore
    @PrepareForTest({CoreLibrary.class, Context.class, LocationHelper.class, Pair.class})
    @Test
    public void testProgressRegistration() {
        CoreLibrary coreLibrary = PowerMockito.mock(CoreLibrary.class);
        Context context = PowerMockito.mock(Context.class);
        AllSharedPreferences allSharedPreferences = PowerMockito.mock(AllSharedPreferences.class);
        LocationHelper locationHelper = PowerMockito.mock(LocationHelper.class);

        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.mockStatic(Context.class);
        PowerMockito.mockStatic(LocationHelper.class);

        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(context);
        PowerMockito.when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        PowerMockito.when(LocationHelper.getInstance()).thenReturn(locationHelper);

        RegisterModel registerModel = (RegisterModel) model;

        String providerId = "PRoviderID";
        String teamId = "TEAmId";
        String team = "TeAM";
        String locationID = "LocationID";

        PowerMockito.doReturn(providerId).when(allSharedPreferences).fetchRegisteredANM();
        PowerMockito.doReturn(locationID).when(allSharedPreferences).fetchDefaultLocalityId(providerId);
        PowerMockito.doReturn(team).when(allSharedPreferences).fetchDefaultTeam(providerId);
        PowerMockito.doReturn(teamId).when(allSharedPreferences).fetchDefaultTeamId(providerId);

        Pair<Client, Event> pair = registerModel.processRegistration(jsonString);
        Assert.assertNotNull(pair);

        Client client = pair.first;
        Event event = pair.second;

        assertRegistrations(client, event);

    }

    private void assertRegistrations(Client client, Event event) {
        Assert.assertNotNull(client);
        Assert.assertNotNull(event);

        Assert.assertEquals(client.getBaseEntityId(), event.getBaseEntityId());

        Assert.assertEquals("Client", client.type());
        Assert.assertEquals(BuildConfig.VERSION_CODE, client.getClientApplicationVersion().intValue());
        Assert.assertEquals(AncLibrary.getInstance().getDatabaseVersion(), client.getClientDatabaseVersion().intValue());

        Assert.assertEquals(BuildConfig.VERSION_CODE, event.getClientApplicationVersion().intValue());
        Assert.assertEquals(AncLibrary.getInstance().getDatabaseVersion(), event.getClientDatabaseVersion().intValue());

        Assert.assertTrue(DateUtils.isSameDay(new Date(), client.getDateCreated()));
        Assert.assertTrue(DateUtils.isSameDay(new Date(), event.getDateCreated()));

        Assert.assertEquals("1723154", client.getIdentifier("ANC_ID"));
        Assert.assertEquals(ANCJsonFormUtils.formatDate("25-07-2003", true), client.getBirthdate());
        Assert.assertEquals("Test_First_Name", client.getFirstName());
        Assert.assertEquals("Test_Last_Name", client.getLastName());
        Assert.assertEquals("female", client.getGender());
        Assert.assertEquals("Test", client.getAttribute("home_address"));
        Assert.assertEquals("0700000000", client.getAttribute("phone_number"));
        Assert.assertEquals("Test_Alt_Name", client.getAttribute("alt_name"));
        Assert.assertEquals("0700000001", client.getAttribute("alt_phone_number"));

        Assert.assertEquals("ANC Registration", event.getEventType());
        Assert.assertEquals("ec_woman", event.getEntityType());
        Assert.assertEquals(ANCJsonFormUtils.formatDate("25-07-2018", true), event.getEventDate());
    }

    @Test
    public void testFormAsJson() throws Exception {
        FormUtils formUtils = Mockito.mock(FormUtils.class);
        RegisterModel registerModel = (RegisterModel) model;

        String formName = "anc_register";
        String entityId = "ENTITY_ID";
        String currentLocationId = "CURRENT_LOCATION_ID";

        JSONObject jsonInMock = new JSONObject(json);

        Assert.assertNotEquals(currentLocationId,
                jsonInMock.getJSONObject(ANCJsonFormUtils.METADATA).getString(ANCJsonFormUtils.ENCOUNTER_LOCATION));
        Assert.assertNotEquals(entityId, entityId(jsonInMock));

        Mockito.doReturn(jsonInMock).when(formUtils).getFormJson(formName);

        JSONObject actualJson = registerModel.getFormAsJson(formName, entityId, currentLocationId);

        Mockito.verify(formUtils).getFormJson(formName);

        Assert.assertEquals(currentLocationId,
                actualJson.getJSONObject(ANCJsonFormUtils.METADATA).getString(ANCJsonFormUtils.ENCOUNTER_LOCATION));

        Assert.assertEquals(entityId, entityId(actualJson));


    }

    private String entityId(JSONObject jsonObject) {
        JSONArray field = ANCJsonFormUtils.fields(jsonObject);
        JSONObject ancId = ANCJsonFormUtils.getFieldJSONObject(field, ConstantsUtils.JsonFormKeyUtils.ANC_ID);
        return ANCJsonFormUtils.getString(ancId, ANCJsonFormUtils.VALUE);
    }

    @PrepareForTest({CoreLibrary.class, Context.class})
    @Test
    public void testGetInitials() {
        CoreLibrary coreLibrary = PowerMockito.mock(CoreLibrary.class);
        Context context = PowerMockito.mock(Context.class);
        AllSharedPreferences allSharedPreferences = PowerMockito.mock(AllSharedPreferences.class);

        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.mockStatic(Context.class);

        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(context);
        PowerMockito.when(context.allSharedPreferences()).thenReturn(allSharedPreferences);

        // Foo Bar ==> FB
        String username = "OpenSRP_USER_NAME";
        String preferredName = "Foo Bar";
        String mockInitials = "FB";

        PowerMockito.when(Utils.getAllSharedPreferences().fetchRegisteredANM()).thenReturn(username);
        PowerMockito.when(Utils.getAllSharedPreferences().getANMPreferredName(ArgumentMatchers.anyString()))
                .thenReturn(preferredName);

        String initials = Utils.getUserInitials();
        Assert.assertEquals(mockInitials, initials);

        Mockito.verify(allSharedPreferences).fetchRegisteredANM();
        Mockito.verify(allSharedPreferences).getANMPreferredName(username);

    }

    @PrepareForTest({CoreLibrary.class, Context.class})
    @Test
    public void testGetInitialsFromThreeNames() {
        CoreLibrary coreLibrary = PowerMockito.mock(CoreLibrary.class);
        Context context = PowerMockito.mock(Context.class);
        AllSharedPreferences allSharedPreferences = PowerMockito.mock(AllSharedPreferences.class);

        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.mockStatic(Context.class);

        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(context);
        PowerMockito.when(context.allSharedPreferences()).thenReturn(allSharedPreferences);

        // Test Foo Bar ==> TF
        String username = "OpenSRP_USER_NAME";
        String preferredName = "Test Foo Bar";
        String initials = "TF";

        Mockito.doReturn(username).when(allSharedPreferences).fetchRegisteredANM();
        Mockito.doReturn(preferredName).when(allSharedPreferences).getANMPreferredName(ArgumentMatchers.anyString());

        String modelInitials = Utils.getUserInitials();
        Assert.assertEquals(modelInitials, initials);

        Mockito.verify(allSharedPreferences).fetchRegisteredANM();
        Mockito.verify(allSharedPreferences).getANMPreferredName(username);
    }

    @PrepareForTest({CoreLibrary.class, Context.class})
    @Test
    public void testGetInitialsFromOneNames() {
        CoreLibrary coreLibrary = PowerMockito.mock(CoreLibrary.class);
        Context context = PowerMockito.mock(Context.class);
        AllSharedPreferences allSharedPreferences = PowerMockito.mock(AllSharedPreferences.class);

        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.mockStatic(Context.class);

        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(context);
        PowerMockito.when(context.allSharedPreferences()).thenReturn(allSharedPreferences);

        // Test ==> T
        String username = "OpenSRP_USER_NAME";
        String preferredName = "Test";
        String initials = "T";

        PowerMockito.doReturn(username).when(allSharedPreferences).fetchRegisteredANM();
        PowerMockito.doReturn(preferredName).when(allSharedPreferences).getANMPreferredName(ArgumentMatchers.anyString());

        String mockInitials = Utils.getUserInitials();
        Assert.assertEquals(mockInitials, initials);

        Mockito.verify(allSharedPreferences).fetchRegisteredANM();
        Mockito.verify(allSharedPreferences).getANMPreferredName(username);
    }

    @PrepareForTest({CoreLibrary.class, Context.class})
    @Test
    public void testGetInitialsWhenPreferredNameIsEmpty() {
        CoreLibrary coreLibrary = PowerMockito.mock(CoreLibrary.class);
        Context context = PowerMockito.mock(Context.class);
        AllSharedPreferences allSharedPreferences = PowerMockito.mock(AllSharedPreferences.class);

        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.mockStatic(Context.class);

        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(context);
        PowerMockito.when(context.allSharedPreferences()).thenReturn(allSharedPreferences);

        // Test Foo Bar ==> TF
        String username = "OpenSRP_USER_NAME";
        String initials = "";

        PowerMockito.doReturn(username).when(allSharedPreferences).fetchRegisteredANM();
        PowerMockito.doReturn(initials).when(allSharedPreferences).getANMPreferredName(ArgumentMatchers.anyString());

        String mockInitials = Utils.getUserInitials();
        Assert.assertNotNull(mockInitials);
        Assert.assertEquals("Me", mockInitials);

        Mockito.verify(allSharedPreferences).fetchRegisteredANM();
        Mockito.verify(allSharedPreferences).getANMPreferredName(username);
    }

    @PrepareForTest(Utils.class)
    @Test
    public void testGetInitialsWhenAllSharedPreferencesIsNull() {
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.getAllSharedPreferences()).thenReturn(null);

        RegisterModel registerModel = (RegisterModel) model;

        String initials = registerModel.getInitials();
        Assert.assertNull(initials);
    }
}