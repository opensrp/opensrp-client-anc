package org.smartregister.anc.library.interactor;

import com.google.common.collect.ImmutableMap;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.domain.Setting;
import org.smartregister.repository.AllSettings;

import java.util.Map;

/**
 * Created by ndegwamartin on 30/08/2018.
 */
public class CharacteristicsInteractorTest extends BaseUnitTest {


    @Mock
    private AllSettings allSettings;

    @Mock
    private AncLibrary ancLibrary;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionHelpers.setStaticField(AncLibrary.class, "instance", ancLibrary);
    }

    @Test
    public void testSaveCharacteristicsSavesCorrectKeyValuesToAllSettingsRepository() throws JSONException {

        CharacteristicsInteractor interactor = new CharacteristicsInteractor();

        CharacteristicsInteractor interactorSpy = Mockito.spy(interactor);

        Map<String, String> testSettings = ImmutableMap.of(TEST_STRING, TEST_STRING);
        Setting setting = new Setting();
        String SETTING_DUMP_JSON = "{\n" +
                "  \"_id\": \"1\",\n" +
                "  \"_rev\": \"v1\",\n" +
                "  \"type\": \"SettingConfiguration\",\n" +
                "  \"identifier\": \"site_characteristics\",\n" +
                "  \"LOCATION_ID\": \"\",\n" +
                "  \"PROVIDER_ID\": \"\",\n" +
                "  \"TEAM_ID\": \"\",\n" +
                "  \"dateCreated\": \"1970-10-04T10:17:09.993+03:00\",\n" +
                "  \"serverVersion\": 1,\n" +
                "  \"settings\": [\n" +
                "    {\n" +
                "      \"key\": \"site_ipv_assess\",\n" +
                "      \"label\": \"Minimum requirements for IPV assessment\",\n" +
                "      \"value\": null,\n" +
                "      \"description\": \"Are all of the following in place at your facility: \\r\\n\\ta. A protocol or standard operating procedure for Intimate Partner Violence (IPV); \\r\\n\\tb. A health worker trained on how to ask about IPV and how to provide the minimum response or beyond;\\r\\n\\tc. A private setting; \\r\\n\\td. A way to ensure confidentiality; \\r\\n\\te. Time to allow for appropriate disclosure; and\\r\\n\\tf. A system for referral in place. \"\n" +
                "    },\n" +
                "    {\n" +
                "      \"key\": \"site_anc_hiv\",\n" +
                "      \"label\": \"Generalized HIV epidemic\",\n" +
                "      \"value\": null,\n" +
                "      \"description\": \"Is the HIV prevalence consistently > 1% in pregnant women attending antenatal clinics at your facility?\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"key\": \"site_ultrasound\",\n" +
                "      \"label\": \"Ultrasound available\",\n" +
                "      \"value\": null,\n" +
                "      \"description\": \"Is an ultrasound machine available and functional at your facility and a trained health worker available to use it?\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"key\": \"site_bp_tool\",\n" +
                "      \"label\": \"Automated BP measurement tool\",\n" +
                "      \"value\": null,\n" +
                "      \"description\": \"Does your facility use an automated blood pressure (BP) measurement tool?\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        setting.setValue(SETTING_DUMP_JSON);

        Mockito.doReturn(setting).when(allSettings).getSetting(ArgumentMatchers.anyString());

        Mockito.doNothing().when(allSettings).put(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());

        Mockito.doReturn(allSettings).when(interactorSpy).getAllSettingsRepo();

        interactorSpy.saveSiteCharacteristics(testSettings);

        Mockito.verify(allSettings).putSetting(setting);


    }
}
