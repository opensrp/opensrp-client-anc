package org.smartregister.anc.util;

import android.graphics.Bitmap;

import junit.framework.Assert;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.skyscreamer.jsonassert.JSONAssert;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.domain.Photo;
import org.smartregister.domain.ProfileImage;
import org.smartregister.domain.form.FormLocation;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.ImageRepository;
import org.smartregister.util.FormUtils;
import org.smartregister.view.LocationPickerView;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private AncApplication ancApplication;

    @Mock
    private Compressor compressor;

    @Mock
    private Bitmap bitmap;

    @Mock
    private File file;

    @Mock
    private OutputStream outputStream;

    @Mock
    private Context context;

    @Mock
    private android.content.Context applicationContext;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private FormUtils formUtils;

    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private LocationPickerView locationPickerView;

    @Mock
    private LocationHelper locationHelper;

    @Mock
    private Photo photo;

    private final String registerFormJsonString = "{\r\n\t\"count\": \"1\",\r\n\t\"encounter_type\": \"ANC Registration\",\r\n\t\"entity_id\": \"\",\r\n\t\"relational_id\": \"\",\r\n\t\"metadata\": {\r\n\t\t\"start\": {\r\n\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\"openmrs_entity\": \"concept\",\r\n\t\t\t\"openmrs_data_type\": \"start\",\r\n\t\t\t\"openmrs_entity_id\": \"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\r\n\t\t},\r\n\t\t\"end\": {\r\n\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\"openmrs_entity\": \"concept\",\r\n\t\t\t\"openmrs_data_type\": \"end\",\r\n\t\t\t\"openmrs_entity_id\": \"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\r\n\t\t},\r\n\t\t\"today\": {\r\n\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\"openmrs_entity\": \"encounter\",\r\n\t\t\t\"openmrs_entity_id\": \"encounter_date\"\r\n\t\t},\r\n\t\t\"deviceid\": {\r\n\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\"openmrs_entity\": \"concept\",\r\n\t\t\t\"openmrs_data_type\": \"deviceid\",\r\n\t\t\t\"openmrs_entity_id\": \"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\r\n\t\t},\r\n\t\t\"subscriberid\": {\r\n\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\"openmrs_entity\": \"concept\",\r\n\t\t\t\"openmrs_data_type\": \"subscriberid\",\r\n\t\t\t\"openmrs_entity_id\": \"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\r\n\t\t},\r\n\t\t\"simserial\": {\r\n\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\"openmrs_entity\": \"concept\",\r\n\t\t\t\"openmrs_data_type\": \"simserial\",\r\n\t\t\t\"openmrs_entity_id\": \"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\r\n\t\t},\r\n\t\t\"phonenumber\": {\r\n\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\"openmrs_entity\": \"concept\",\r\n\t\t\t\"openmrs_data_type\": \"phonenumber\",\r\n\t\t\t\"openmrs_entity_id\": \"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\r\n\t\t},\r\n\t\t\"encounter_location\": \"\",\r\n\t\t\"look_up\": {\r\n\t\t\t\"entity_id\": \"\",\r\n\t\t\t\"value\": \"\"\r\n\t\t}\r\n\t},\r\n\t\"step1\": {\r\n\t\t\"title\": \"ANC Registration\",\r\n\t\t\"fields\": [{\r\n\t\t\t\t\"key\": \"photo\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"\",\r\n\t\t\t\t\"openmrs_entity_id\": \"\",\r\n\t\t\t\t\"type\": \"choose_image\",\r\n\t\t\t\t\"uploadButtonText\": \"Take a picture of the woman\"\r\n\t\t\t},{\r\n\t\t\t\t\"key\": \"edd\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"\",\r\n\t\t\t\t\"openmrs_entity_id\": \"\",\r\n\t\t\t\t\"type\": \"edit_text\",\r\n\t\t\t\t\"value\": \"\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"key\": \"anc_id\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"person_identifier\",\r\n\t\t\t\t\"openmrs_entity_id\": \"ANC_ID\",\r\n\t\t\t\t\"type\": \"barcode\",\r\n\t\t\t\t\"barcode_type\": \"qrcode\",\r\n\t\t\t\t\"hint\": \"ANC ID *\",\r\n\t\t\t\t\"value\": \"0\",\r\n\t\t\t\t\"scanButtonText\": \"Scan QR Code\",\r\n\t\t\t\t\"v_numeric\": {\r\n\t\t\t\t\t\"value\": \"true\",\r\n\t\t\t\t\t\"err\": \"Please enter a valid ANC ID\"\r\n\t\t\t\t},\r\n\t\t\t\t\"v_required\": {\r\n\t\t\t\t\t\"value\": \"true\",\r\n\t\t\t\t\t\"err\": \"Please enter the Woman's ANC ID\"\r\n\t\t\t\t}\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"key\": \"first_name\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"person\",\r\n\t\t\t\t\"openmrs_entity_id\": \"first_name\",\r\n\t\t\t\t\"type\": \"edit_text\",\r\n\t\t\t\t\"hint\": \"First name *\",\r\n\t\t\t\t\"edit_type\": \"name\",\r\n\t\t\t\t\"v_required\": {\r\n\t\t\t\t\t\"value\": \"true\",\r\n\t\t\t\t\t\"err\": \"Please enter the first name\"\r\n\t\t\t\t},\r\n\t\t\t\t\"v_regex\": {\r\n\t\t\t\t\t\"value\": \"[A-Za-z]\",\r\n\t\t\t\t\t\"err\": \"Please enter a valid name\"\r\n\t\t\t\t}\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"key\": \"last_name\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"person\",\r\n\t\t\t\t\"openmrs_entity_id\": \"last_name\",\r\n\t\t\t\t\"type\": \"edit_text\",\r\n\t\t\t\t\"hint\": \"Last name *\",\r\n\t\t\t\t\"edit_type\": \"name\",\r\n\t\t\t\t\"v_required\": {\r\n\t\t\t\t\t\"value\": \"true\",\r\n\t\t\t\t\t\"err\": \"Please enter the last name\"\r\n\t\t\t\t},\r\n\t\t\t\t\"v_regex\": {\r\n\t\t\t\t\t\"value\": \"[A-Za-z]\",\r\n\t\t\t\t\t\"err\": \"Please enter a valid name\"\r\n\t\t\t\t}\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"key\": \"gender\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"person\",\r\n\t\t\t\t\"openmrs_entity_id\": \"gender\",\r\n\t\t\t\t\"type\": \"hidden\",\r\n\t\t\t\t\"value\": \"female\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"key\": \"dob\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"person\",\r\n\t\t\t\t\"openmrs_entity_id\": \"birthdate\",\r\n\t\t\t\t\"type\": \"date_picker\",\r\n\t\t\t\t\"hint\": \"Date of birth (DOB) *\",\r\n\t\t\t\t\"expanded\": false,\r\n\t\t\t\t\"duration\": {\r\n\t\t\t\t\t\"label\": \"Age\"\r\n\t\t\t\t},\r\n\t\t\t\t\"min_date\": \"today-49y\",\r\n\t\t\t\t\"max_date\": \"today-15y\",\r\n\t\t\t\t\"v_required\": {\r\n\t\t\t\t\t\"value\": \"true\",\r\n\t\t\t\t\t\"err\": \"Please enter the date of birth\"\r\n\t\t\t\t},\r\n\t\t\t\t\"relevance\": {\r\n\t\t\t\t\t\"step1:dob_unknown\": {\r\n\t\t\t\t\t\t\"ex-checkbox\": [{\r\n\t\t\t\t\t\t\t\"not\": [\r\n\t\t\t\t\t\t\t\t\"dob_unknown\"\r\n\t\t\t\t\t\t\t]\r\n\t\t\t\t\t\t}]\r\n\t\t\t\t\t}\r\n\t\t\t\t}\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"key\": \"dob_unknown\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"person\",\r\n\t\t\t\t\"openmrs_entity_id\": \"birthdateApprox\",\r\n\t\t\t\t\"type\": \"check_box\",\r\n\t\t\t\t\"label\": \"\",\r\n\t\t\t\t\"options\": [{\r\n\t\t\t\t\t\"key\": \"dob_unknown\",\r\n\t\t\t\t\t\"text\": \"DOB unknown?\",\r\n\t\t\t\t\t\"text_size\": \"18px\",\r\n\t\t\t\t\t\"value\": \"false\"\r\n\t\t\t\t}]\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"key\": \"age\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"person_attribute\",\r\n\t\t\t\t\"openmrs_entity_id\": \"age\",\r\n\t\t\t\t\"type\": \"edit_text\",\r\n\t\t\t\t\"hint\": \"Age *\",\r\n\t\t\t\t\"v_numeric\": {\r\n\t\t\t\t\t\"value\": \"true\",\r\n\t\t\t\t\t\"err\": \"Number must begin with 0 and must be a total of 10 digits in length\"\r\n\t\t\t\t},\r\n\t\t\t\t\"v_min\": {\r\n\t\t\t\t\t\"value\": \"15\",\r\n\t\t\t\t\t\"err\": \"Age must be equal or greater than 15\"\r\n\t\t\t\t},\r\n\t\t\t\t\"v_max\": {\r\n\t\t\t\t\t\"value\": \"49\",\r\n\t\t\t\t\t\"err\": \"Age must be equal or less than 49\"\r\n\t\t\t\t},\r\n\t\t\t\t\"relevance\": {\r\n\t\t\t\t\t\"step1:dob_unknown\": {\r\n\t\t\t\t\t\t\"ex-checkbox\": [{\r\n\t\t\t\t\t\t\t\"and\": [\r\n\t\t\t\t\t\t\t\t\"dob_unknown\"\r\n\t\t\t\t\t\t\t]\r\n\t\t\t\t\t\t}]\r\n\t\t\t\t\t}\r\n\t\t\t\t},\r\n\t\t\t\t\"v_required\": {\r\n\t\t\t\t\t\"value\": true,\r\n\t\t\t\t\t\"err\": \"Please enter the woman's age\"\r\n\t\t\t\t}\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"key\": \"home_address\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"person_attribute\",\r\n\t\t\t\t\"openmrs_entity_id\": \"home_address\",\r\n\t\t\t\t\"openmrs_data_type\": \"text\",\r\n\t\t\t\t\"type\": \"edit_text\",\r\n\t\t\t\t\"hint\": \"Home address *\",\r\n\t\t\t\t\"v_required\": {\r\n\t\t\t\t\t\"value\": \"true\",\r\n\t\t\t\t\t\"err\": \"Please enter the woman's home address\"\r\n\t\t\t\t},\r\n\t\t\t\t\"v_regex\": {\r\n\t\t\t\t\t\"value\": \"[A-Za-z0-9]\",\r\n\t\t\t\t\t\"err\": \"Please enter a valid name\"\r\n\t\t\t\t}\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"key\": \"phone_number\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"person_attribute\",\r\n\t\t\t\t\"openmrs_entity_id\": \"phone_number\",\r\n\t\t\t\t\"type\": \"edit_text\",\r\n\t\t\t\t\"hint\": \"Mobile phone number *\",\r\n\t\t\t\t\"v_numeric\": {\r\n\t\t\t\t\t\"value\": \"true\",\r\n\t\t\t\t\t\"err\": \"Number must be a total of 10 digits in length\"\r\n\t\t\t\t},\r\n\t\t\t\t\"v_regex\": {\r\n\t\t\t\t\t\"value\": \"([0-9]{10})\",\r\n\t\t\t\t\t\"err\": \"Number must be a total of 10 digits in length\"\r\n\t\t\t\t},\r\n\t\t\t\t\"v_required\": {\r\n\t\t\t\t\t\"value\": true,\r\n\t\t\t\t\t\"err\": \"Please specify the woman's phone number\"\r\n\t\t\t\t}\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"key\": \"reminders\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"person_attribute\",\r\n\t\t\t\t\"openmrs_entity_id\": \"reminders\",\r\n\t\t\t\t\"openmrs_data_type\": \"select one\",\r\n\t\t\t\t\"type\": \"spinner\",\r\n\t\t\t\t\"label_info_text\": \"Does she want to receive reminders for care and messages regarding her health throughout her pregnancy?\",\r\n\t\t\t\t\"hint\": \"Reminders throughout pregnancy? *\",\r\n\t\t\t\t\"values\": [\r\n\t\t\t\t\t\"Yes\",\r\n\t\t\t\t\t\"No\"\r\n\t\t\t\t],\r\n\t\t\t\t\"v_required\": {\r\n\t\t\t\t\t\"value\": true,\r\n\t\t\t\t\t\"err\": \"Please select whether the woman has agreed to receiving reminder notifications\"\r\n\t\t\t\t}\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"key\": \"alt_name\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"person_attribute\",\r\n\t\t\t\t\"openmrs_entity_id\": \"alt_name\",\r\n\t\t\t\t\"type\": \"edit_text\",\r\n\t\t\t\t\"hint\": \"Alternate contact name\",\r\n\t\t\t\t\"edit_type\": \"name\",\r\n\t\t\t\t\"look_up\": \"true\",\r\n\t\t\t\t\"entity_id\": \"\",\r\n\t\t\t\t\"v_regex\": {\r\n\t\t\t\t\t\"value\": \"[A-Za-z]\",\r\n\t\t\t\t\t\"err\": \"Please enter a valid VHT name\"\r\n\t\t\t\t}\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"key\": \"alt_phone_number\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"person_attribute\",\r\n\t\t\t\t\"openmrs_entity_id\": \"alt_phone_number\",\r\n\t\t\t\t\"type\": \"edit_text\",\r\n\t\t\t\t\"hint\": \"Alternate contact phone number\",\r\n\t\t\t\t\"v_numeric\": {\r\n\t\t\t\t\t\"value\": \"true\",\r\n\t\t\t\t\t\"err\": \"Number must be a total of 10 digits in length\"\r\n\t\t\t\t},\r\n\t\t\t\t\"v_regex\": {\r\n\t\t\t\t\t\"value\": \"([0-9]{10})\",\r\n\t\t\t\t\t\"err\": \"Number must be a total of 10 digits in length\"\r\n\t\t\t\t}\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"key\": \"next_contact\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"person_attribute\",\r\n\t\t\t\t\"openmrs_entity_id\": \"next_contact\",\r\n\t\t\t\t\"type\": \"hidden\",\r\n\t\t\t\t\"value\": \"\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"key\": \"next_contact_date\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"person_attribute\",\r\n\t\t\t\t\"openmrs_entity_id\": \"next_contact_date\",\r\n\t\t\t\t\"type\": \"hidden\",\r\n\t\t\t\t\"value\": \"\"\r\n\t\t\t}\r\n\t\t]\r\n\t}\r\n}";

    private final String expectedProcessedJson = "{\"metadata\":{\"look_up\":{\"entity_id\":\"\",\"value\":\"\"},\"encounter_location\":\"\",\"today\":{\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\",\"openmrs_entity_parent\":\"\"},\"start\":{\"openmrs_data_type\":\"start\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"},\"phonenumber\":{\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"},\"subscriberid\":{\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"},\"simserial\":{\"openmrs_data_type\":\"simserial\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"},\"end\":{\"openmrs_data_type\":\"end\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"},\"deviceid\":{\"openmrs_data_type\":\"deviceid\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}},\"count\":\"1\",\"entity_id\":\"00ts-ime-hcla-0tib-0eht-ma0i\",\"relational_id\":\"\",\"encounter_type\":\"Update ANC Registration\",\"step1\":{\"title\":\"ANC Registration\",\"fields\":[{\"uploadButtonText\":\"Take a picture of the woman\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"openmrs_entity_parent\":\"\",\"type\":\"choose_image\",\"value\":\"\\/images\\/00ts-ime-hcla-0tib-0eht-ma0i\",\"key\":\"photo\"},{\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"openmrs_entity_parent\":\"\",\"type\":\"edit_text\",\"value\":\"19-10-2018\",\"key\":\"edd\"},{\"openmrs_entity\":\"person_identifier\",\"hint\":\"ANC ID *\",\"openmrs_entity_id\":\"ANC_ID\",\"v_required\":{\"err\":\"Please enter the Woman's ANC ID\",\"value\":\"true\"},\"barcode_type\":\"qrcode\",\"openmrs_entity_parent\":\"\",\"v_numeric\":{\"err\":\"Please enter a valid ANC ID\",\"value\":\"true\"},\"type\":\"barcode\",\"value\":\"00tsimehcla0tib0ehtma0i\",\"key\":\"anc_id\",\"scanButtonText\":\"Scan QR Code\"},{\"openmrs_entity\":\"person\",\"read_only\":false,\"hint\":\"First name *\",\"openmrs_entity_id\":\"first_name\",\"edit_type\":\"name\",\"v_required\":{\"err\":\"Please enter the first name\",\"value\":\"true\"},\"openmrs_entity_parent\":\"\",\"type\":\"edit_text\",\"value\":\"First Name\",\"key\":\"first_name\",\"v_regex\":{\"err\":\"Please enter a valid name\",\"value\":\"[A-Za-z]\"}},{\"openmrs_entity\":\"person\",\"read_only\":false,\"hint\":\"Last name *\",\"openmrs_entity_id\":\"last_name\",\"edit_type\":\"name\",\"v_required\":{\"err\":\"Please enter the last name\",\"value\":\"true\"},\"openmrs_entity_parent\":\"\",\"type\":\"edit_text\",\"value\":\"Last Name\",\"key\":\"last_name\",\"v_regex\":{\"err\":\"Please enter a valid name\",\"value\":\"[A-Za-z]\"}},{\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"gender\",\"openmrs_entity_parent\":\"\",\"type\":\"hidden\",\"value\":\"female\",\"key\":\"gender\"},{\"max_date\":\"today-15y\",\"openmrs_entity_id\":\"birthdate\",\"openmrs_entity_parent\":\"\",\"type\":\"date_picker\",\"relevance\":{\"step1:dob_unknown\":{\"ex-checkbox\":[{\"not\":[\"dob_unknown\"]}]}},\"duration\":{\"label\":\"Age\"},\"expanded\":false,\"openmrs_entity\":\"person\",\"min_date\":\"today-49y\",\"hint\":\"Date of birth (DOB) *\",\"v_required\":{\"err\":\"Please enter the date of birth\",\"value\":\"true\"},\"value\":\"09-08-1995\",\"key\":\"dob\"},{\"openmrs_entity\":\"person\",\"read_only\":false,\"openmrs_entity_id\":\"birthdateApprox\",\"options\":[{\"text_size\":\"18px\",\"text\":\"DOB unknown?\",\"value\":\"false\",\"key\":\"dob_unknown\"}],\"openmrs_entity_parent\":\"\",\"label\":\"\",\"type\":\"check_box\",\"key\":\"dob_unknown\"},{\"v_min\":{\"err\":\"Age must be equal or greater than 15\",\"value\":\"15\"},\"openmrs_entity_id\":\"age\",\"openmrs_entity_parent\":\"\",\"type\":\"edit_text\",\"relevance\":{\"step1:dob_unknown\":{\"ex-checkbox\":[{\"and\":[\"dob_unknown\"]}]}},\"v_max\":{\"err\":\"Age must be equal or less than 49\",\"value\":\"49\"},\"openmrs_entity\":\"person_attribute\",\"read_only\":false,\"hint\":\"Age *\",\"v_required\":{\"err\":\"Please enter the woman's age\",\"value\":true},\"v_numeric\":{\"err\":\"Number must begin with 0 and must be a total of 10 digits in length\",\"value\":\"true\"},\"value\":23,\"key\":\"age\"},{\"openmrs_data_type\":\"text\",\"openmrs_entity\":\"person_attribute\",\"hint\":\"Home address *\",\"openmrs_entity_id\":\"home_address\",\"v_required\":{\"err\":\"Please enter the woman's home address\",\"value\":\"true\"},\"openmrs_entity_parent\":\"\",\"type\":\"edit_text\",\"value\":\"Roysambu\",\"key\":\"home_address\",\"v_regex\":{\"err\":\"Please enter a valid name\",\"value\":\"[A-Za-z0-9]\"}},{\"openmrs_entity\":\"person_attribute\",\"hint\":\"Mobile phone number *\",\"openmrs_entity_id\":\"phone_number\",\"v_required\":{\"err\":\"Please specify the woman's phone number\",\"value\":true},\"openmrs_entity_parent\":\"\",\"v_numeric\":{\"err\":\"Number must be a total of 10 digits in length\",\"value\":\"true\"},\"type\":\"edit_text\",\"key\":\"phone_number\",\"v_regex\":{\"err\":\"Number must be a total of 10 digits in length\",\"value\":\"([0-9]{10})\"}},{\"openmrs_data_type\":\"select one\",\"openmrs_entity\":\"person_attribute\",\"hint\":\"Reminders throughout pregnancy? *\",\"values\":[\"Yes\",\"No\"],\"openmrs_entity_id\":\"reminders\",\"v_required\":{\"err\":\"Please select whether the woman has agreed to receiving reminder notifications\",\"value\":true},\"openmrs_entity_parent\":\"\",\"type\":\"spinner\",\"label_info_text\":\"Does she want to receive reminders for care and messages regarding her health throughout her pregnancy?\",\"key\":\"reminders\"},{\"look_up\":\"true\",\"openmrs_entity\":\"person_attribute\",\"hint\":\"Alternate contact name\",\"openmrs_entity_id\":\"alt_name\",\"edit_type\":\"name\",\"openmrs_entity_parent\":\"\",\"type\":\"edit_text\",\"entity_id\":\"\",\"key\":\"alt_name\",\"v_regex\":{\"err\":\"Please enter a valid VHT name\",\"value\":\"[A-Za-z]\"}},{\"openmrs_entity\":\"person_attribute\",\"hint\":\"Alternate contact phone number\",\"openmrs_entity_id\":\"alt_phone_number\",\"openmrs_entity_parent\":\"\",\"v_numeric\":{\"err\":\"Number must be a total of 10 digits in length\",\"value\":\"true\"},\"type\":\"edit_text\",\"key\":\"alt_phone_number\",\"v_regex\":{\"err\":\"Number must be a total of 10 digits in length\",\"value\":\"([0-9]{10})\"}},{\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"next_contact\",\"openmrs_entity_parent\":\"\",\"type\":\"hidden\",\"value\":\"\",\"key\":\"next_contact\"},{\"openmrs_entity\":\"person_attribute\",\"read_only\":false,\"openmrs_entity_id\":\"next_contact_date\",\"openmrs_entity_parent\":\"\",\"type\":\"hidden\",\"value\":\"2018-08-09\",\"key\":\"next_contact_date\"}]},\"current_opensrp_id\":\"00tsimehcla0tib0ehtma0i\"}";

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
    @PrepareForTest({AncApplication.class, FileUtil.class, DrishtiApplication.class})
    public void testSaveImageInvokesSaveStaticImageToDiskWithCorrectParams() throws Exception {


        String PROVIDER_ID = "dummy-provider-id";

        PowerMockito.mockStatic(AncApplication.class);
        PowerMockito.when(AncApplication.getInstance()).thenReturn(ancApplication);
        PowerMockito.when(ancApplication.getCompressor()).thenReturn(compressor);
        PowerMockito.when(compressor.compressToBitmap(ArgumentMatchers.any(File.class))).thenReturn(bitmap);

        PowerMockito.when(ancApplication.getContext()).thenReturn(context);
        PowerMockito.when(context.imageRepository()).thenReturn(imageRepository);


        PowerMockito.mockStatic(DrishtiApplication.class);
        PowerMockito.when(DrishtiApplication.getAppDir()).thenReturn("/images");


        PowerMockito.mockStatic(FileUtil.class);
        PowerMockito.when(FileUtil.createFileFromPath(ArgumentMatchers.anyString())).thenReturn(file);
        PowerMockito.when(file.exists()).thenReturn(true);
        PowerMockito.when(FileUtil.createFileOutputStream(file)).thenReturn(outputStream);

        JsonFormUtils.saveImage(PROVIDER_ID, DUMMY_BASE_ENTITY_ID, "filepath/images/folder/location.jpg");

        Mockito.verify(imageRepository).add(ArgumentMatchers.any(ProfileImage.class));


    }

    @Test
    @PrepareForTest({FormUtils.class, CoreLibrary.class, LocationHelper.class, ImageUtils.class})
    public void testGetAutoPopulatedJsonEditFormStringInjectsValuesCorrectlyInForm() throws Exception {

        String firstName = "First Name";
        String lastName = "Last Name";

        Map<String, String> details = new HashMap<>();
        details.put(DBConstants.KEY.FIRST_NAME, firstName);
        details.put(DBConstants.KEY.LAST_NAME, lastName);
        details.put(DBConstants.KEY.EDD, "2018-10-19");
        details.put(DBConstants.KEY.NEXT_CONTACT_DATE, "2018-08-09");
        details.put(DBConstants.KEY.BASE_ENTITY_ID, DUMMY_BASE_ENTITY_ID);
        details.put(DBConstants.KEY.ANC_ID, DUMMY_BASE_ENTITY_ID);
        details.put(DBConstants.KEY.DOB, "1995-08-9");
        details.put(DBConstants.KEY.DOB_UNKNOWN, "false");
        details.put(DBConstants.KEY.HOME_ADDRESS, "Roysambu");
        details.put(DBConstants.KEY.AGE, "20");

        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(context);
        PowerMockito.when(context.applicationContext()).thenReturn(applicationContext);

        PowerMockito.mock(LocationPickerView.class);
        PowerMockito.whenNew(LocationPickerView.class).withArguments(applicationContext).thenReturn(locationPickerView);

        PowerMockito.mockStatic(FormUtils.class);
        PowerMockito.when(FormUtils.getInstance(applicationContext)).thenReturn(formUtils);

        PowerMockito.mockStatic(LocationHelper.class);
        PowerMockito.when(LocationHelper.getInstance()).thenReturn(locationHelper);

        ArrayList<String> allLevels = new ArrayList<>();
        allLevels.add("Country");
        allLevels.add("Province");
        allLevels.add("District");
        allLevels.add("City/Town");
        allLevels.add("Health Facility");
        allLevels.add(Utils.HOME_ADDRESS);

        ArrayList<String> healthFacilities = new ArrayList<>();
        healthFacilities.add(Utils.HOME_ADDRESS);

        List<FormLocation> formLocations = new ArrayList<>();
        FormLocation formLocation = new FormLocation();
        formLocation.key = DBConstants.KEY.HOME_ADDRESS;
        formLocation.name = details.get(DBConstants.KEY.HOME_ADDRESS);
        formLocations.add(formLocation);


        List<String> locations = new ArrayList<>();
        locations.add(details.get(Utils.HOME_ADDRESS));
        PowerMockito.when(locationHelper.generateDefaultLocationHierarchy(ArgumentMatchers.eq(healthFacilities))).thenReturn(locations);
        PowerMockito.when(locationHelper.generateLocationHierarchyTree(ArgumentMatchers.eq(false), ArgumentMatchers.eq(healthFacilities))).thenReturn(formLocations);
        PowerMockito.when(locationHelper.getOpenMrsLocationId(ArgumentMatchers.anyString())).thenReturn(DUMMY_LOCATION_ID);

        PowerMockito.mockStatic(ImageUtils.class);
        PowerMockito.when(ImageUtils.profilePhotoByClientID(DUMMY_BASE_ENTITY_ID)).thenReturn(photo);
        PowerMockito.when(photo.getFilePath()).thenReturn("/images/" + DUMMY_BASE_ENTITY_ID);

        JSONObject registerForm = new JSONObject(registerFormJsonString);
        PowerMockito.when(formUtils.getFormJson(Constants.JSON_FORM.ANC_REGISTER)).thenReturn(registerForm);

        String resultJsonFormString = JsonFormUtils.getAutoPopulatedJsonEditRegisterFormString(applicationContext, details);

        Assert.assertNotNull(resultJsonFormString);
        JSONAssert.assertEquals(expectedProcessedJson, resultJsonFormString, false);

    }

}
