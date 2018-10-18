package org.smartregister.anc.interactor;

import com.google.common.collect.ImmutableMap;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.domain.Setting;
import org.smartregister.repository.AllSettings;

import java.util.Map;

/**
 * Created by ndegwamartin on 30/08/2018.
 */
public class CharacteristicsInteractorTest extends BaseUnitTest {


    @Mock
    private AllSettings allSettings;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSaveCharacteristicsSavesCorrectKeyValuesToAllSettingsRepository() throws JSONException {

        CharacteristicsInteractor interactor = new CharacteristicsInteractor();

        CharacteristicsInteractor interactorSpy = Mockito.spy(interactor);

        Map<String, String> testSettings = ImmutableMap.of(TEST_STRING, TEST_STRING);
        Setting setting = new Setting();
        setting.setValue(DUMMY_JSON);

        Mockito.doReturn(setting).when(allSettings).getSetting(ArgumentMatchers.anyString());

        Mockito.doNothing().when(allSettings).put(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());

        Mockito.doReturn(allSettings).when(interactorSpy).getAllSettingsRepo();

        interactorSpy.saveSiteCharacteristics(testSettings);

        Mockito.verify(allSettings).putSetting(setting);


    }
}
