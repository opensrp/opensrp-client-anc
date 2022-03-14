package org.smartregister.anc.library.util;

import static org.smartregister.anc.library.util.Utils.getKeyByValue;
import static org.smartregister.anc.library.util.Utils.getTodayContact;
import static org.smartregister.anc.library.util.Utils.hasPendingRequiredFields;
import static org.smartregister.anc.library.util.Utils.isEmptyMap;
import static org.smartregister.anc.library.util.Utils.processButtonAlertStatus;
import static org.smartregister.anc.library.util.Utils.reverseHyphenSeperatedValues;

import android.content.res.Resources;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.domain.ButtonAlertStatus;
import org.smartregister.anc.library.helper.ECSyncHelper;
import org.smartregister.anc.library.repository.PatientRepository;
import org.smartregister.anc.library.repository.RegisterQueryProvider;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.Location;
import org.smartregister.domain.LocationProperty;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.LocationRepository;
import org.smartregister.sync.ClientProcessorForJava;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.emory.mathcs.backport.java.util.Collections;
import timber.log.Timber;

/**
 * This allows integration of both powermock and robolectric
 * PowerMockIgnore annotations excludes the classes specified as params to avoid having duplicates
 */
@PowerMockRunnerDelegate(RobolectricTestRunner.class)
@PowerMockIgnore({
        "org.powermock.*", "org.mockito.*", "org.robolectric.*", "android.*", "androidx.*",
        "javax.xml.parsers.*", "com.sun.org.apache.xerces.internal.jaxp.*"
})
public class UtilsTest extends BaseUnitTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private Context opensrpContext;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private AncLibrary ancLibrary;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @PrepareForTest({CoreLibrary.class, Context.class})
    @Test
    public void testGetNameWithNullPreferences() {
        CoreLibrary coreLibrary = PowerMockito.mock(CoreLibrary.class);
        Context context = PowerMockito.mock(Context.class);

        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.mockStatic(Context.class);

        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(context);
        PowerMockito.when(context.allSharedPreferences()).thenReturn(null);

        String name = Utils.getPrefferedName();
        Assert.assertNull(name);

    }

    @PrepareForTest({CoreLibrary.class, Context.class})
    @Test
    public void testGetName() {
        String username = "userName1";

        CoreLibrary coreLibrary = PowerMockito.mock(CoreLibrary.class);
        Context context = PowerMockito.mock(Context.class);
        AllSharedPreferences allSharedPreferences = PowerMockito.mock(AllSharedPreferences.class);

        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.mockStatic(Context.class);

        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(context);
        PowerMockito.when(context.allSharedPreferences()).thenReturn(allSharedPreferences);

        PowerMockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn(username);
        Assert.assertNotNull(allSharedPreferences);

        Utils.getPrefferedName();

        Mockito.verify(allSharedPreferences).getANMPreferredName(username);
        Mockito.verify(allSharedPreferences).fetchRegisteredANM();

    }

    @PrepareForTest({StringUtils.class, CoreLibrary.class, Context.class})
    @Test
    public void testGetUserInitialsWithTwoNames() {
        String username = "userName2";
        String preferredName = "Anc Reference";

        CoreLibrary coreLibrary = PowerMockito.mock(CoreLibrary.class);
        Context context = PowerMockito.mock(Context.class);
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);

        PowerMockito.mockStatic(StringUtils.class);
        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.mockStatic(Context.class);

        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(context);
        PowerMockito.when(context.allSharedPreferences()).thenReturn(allSharedPreferences);

        PowerMockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn(username);
        PowerMockito.when(allSharedPreferences.getANMPreferredName(username)).thenReturn(preferredName);

        PowerMockito.when(StringUtils.isNotBlank(preferredName)).thenReturn(true);

        Assert.assertNotNull(allSharedPreferences);
        Assert.assertNotNull(username);
        Assert.assertNotNull(preferredName);

        String initials = Utils.getUserInitials();
        Assert.assertEquals("AR", initials);

        Mockito.verify(allSharedPreferences).getANMPreferredName(username);
        Mockito.verify(allSharedPreferences).fetchRegisteredANM();
    }

    @PrepareForTest({StringUtils.class, CoreLibrary.class, Context.class})
    @Test
    public void testGetUserInitialsWithOneNames() {
        String username = "UserNAME3";
        String preferredName = "Anc";

        CoreLibrary coreLibrary = PowerMockito.mock(CoreLibrary.class);
        Context context = PowerMockito.mock(Context.class);
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);

        PowerMockito.mockStatic(StringUtils.class);
        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.mockStatic(Context.class);

        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(context);
        PowerMockito.when(context.allSharedPreferences()).thenReturn(allSharedPreferences);

        PowerMockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn(username);
        PowerMockito.when(allSharedPreferences.getANMPreferredName(username)).thenReturn(preferredName);

        PowerMockito.when(StringUtils.isNotBlank(preferredName)).thenReturn(true);

        Assert.assertNotNull(allSharedPreferences);
        Assert.assertNotNull(username);
        Assert.assertNotNull(preferredName);


        String initials = Utils.getUserInitials();
        Assert.assertEquals("A", initials);

        Mockito.verify(allSharedPreferences).getANMPreferredName(username);
        Mockito.verify(allSharedPreferences).fetchRegisteredANM();
    }

    @PrepareForTest({CoreLibrary.class, Context.class})
    @Test
    public void testGerPreferredNameWithNullSharePreferences() {
        CoreLibrary coreLibrary = PowerMockito.mock(CoreLibrary.class);
        Context context = PowerMockito.mock(Context.class);

        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.mockStatic(Context.class);

        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(context);
        PowerMockito.when(context.allSharedPreferences()).thenReturn(null);

        String name = Utils.getPrefferedName();
        Assert.assertNull(name);
    }

    @PrepareForTest(StringUtils.class)
    @Test
    public void testDobStringToDateTime() {
        String dobString = "2019-01-23";

        PowerMockito.mockStatic(StringUtils.class);
        PowerMockito.when(StringUtils.isNotBlank(dobString)).thenReturn(true);

        DateTime dobStringToDateTime = Utils.dobStringToDateTime(dobString);
        Assert.assertNotNull(dobStringToDateTime);
    }

    @PrepareForTest(StringUtils.class)
    @Test
    public void testDobStringToDateTimeWithNullStringDate() {
        PowerMockito.mockStatic(StringUtils.class);
        PowerMockito.when(StringUtils.isBlank(null)).thenReturn(true);

        DateTime dobStringToDateTime = Utils.dobStringToDateTime(null);
        Assert.assertEquals(dobStringToDateTime, dobStringToDateTime);
    }

    @Test
    public void testGetListFromString() {
        String stringList = "[30, 34, 36, 38, 40, 41]";
        Assert.assertEquals(Utils.getListFromString("").size(), 0);
        Assert.assertEquals(Utils.getListFromString(stringList).size(), 6);
        Assert.assertEquals(Utils.getListFromString(null).size(), 0);
        Assert.assertEquals(Utils.getListFromString(stringList).get(2), "36");
    }

    @Test
    public void tetGetKeyByValue() {
        Map<String, String> map = new HashMap<>();
        map.put("Key1", "Val1");
        map.put("Key2", "Val2");
        map.put("Key3", "Val3");
        Assert.assertEquals("Key1", getKeyByValue(map, "Val1"));
    }

    @Test
    public void testGetKeyByValueWithEmptyMap() {
        Map<String, String> map = new HashMap<>();
        Assert.assertNull(getKeyByValue(map, "val1"));
    }

    @Test
    public void testReverseHyphenSeparatedValues() {
        Assert.assertEquals("", reverseHyphenSeperatedValues("", "-"));
        Assert.assertEquals("", reverseHyphenSeperatedValues(null, "-"));
        Assert.assertEquals("16-05-2019", reverseHyphenSeperatedValues(" 2019-05-16", "-"));
        Assert.assertEquals("16-05-2019", reverseHyphenSeperatedValues("2019-05-16 ", "-"));
        Assert.assertEquals("16-05-2019", reverseHyphenSeperatedValues("2019-05-16", "-"));
    }

    @Test
    public void testIsEmptyMap() {
        Assert.assertTrue(isEmptyMap(new HashMap<>()));
    }

    @Test
    public void testCreateExpansionPanelValues() throws Exception {
        List list = Collections.unmodifiableList(Arrays.asList("Hepatitis C test:Done today",
                "Hep C test date:28-05-2019",
                "Hep C test type:Anti-HCV rapid diagnostic test (RDT)",
                "Anti-HCV rapid diagnostic test (RDT):Positive"));

        JSONObject mainObject = getMainJsonObject("json_test_forms/expansion_panel_json_array");
        JSONArray expansionValues = mainObject.getJSONArray("expansion_values");
        com.vijay.jsonwizard.utils.Utils utils = new com.vijay.jsonwizard.utils.Utils();
        List<String> result = utils.createExpansionPanelChildren(expansionValues);
        //Compare returned list sizes and the second and last values
        Assert.assertEquals(4, result.size());
        Assert.assertEquals(list.get(1), result.get(1));
        Assert.assertEquals(list.get(3), result.get(3));
    }

    @Test
    public void testHasPendingRequiredFields() throws Exception {
        //Checks if there are required fields that don't have value
        JSONObject mainObject = getMainJsonObject("json_test_forms/test_checkbox_filter_json_form");
        Assert.assertTrue(hasPendingRequiredFields(mainObject));
    }

    @Test
    public void testAllProcessButtonAlertStatus() {
        android.content.Context context = RuntimeEnvironment.application.getApplicationContext();
        Button button = new Button(context);
        TextView textView = new TextView(context);
        ButtonAlertStatus buttonAlertStatus = new ButtonAlertStatus();
        //Default button alert configuration
        buttonAlertStatus.nextContact = 2;
        buttonAlertStatus.gestationAge = 14;
        buttonAlertStatus.buttonText = "Awesome People";

        //Process alert status in_progress
        buttonAlertStatus.buttonAlertStatus = ConstantsUtils.AlertStatusUtils.IN_PROGRESS;
        processButtonAlertStatus(context, button, textView, buttonAlertStatus);
        Assert.assertTrue(assertAlertButtonStatus(context, button, textView, ConstantsUtils.AlertStatusUtils.IN_PROGRESS, buttonAlertStatus));
        //Process alert status  due
        buttonAlertStatus.buttonAlertStatus = ConstantsUtils.AlertStatusUtils.DUE;
        processButtonAlertStatus(context, button, textView, buttonAlertStatus);
        Assert.assertTrue(assertAlertButtonStatus(context, button, textView, ConstantsUtils.AlertStatusUtils.DUE, buttonAlertStatus));
        //Process alert status overdue
        buttonAlertStatus.buttonAlertStatus = ConstantsUtils.AlertStatusUtils.OVERDUE;
        processButtonAlertStatus(context, button, textView, buttonAlertStatus);
        Assert.assertTrue(assertAlertButtonStatus(context, button, textView, ConstantsUtils.AlertStatusUtils.OVERDUE, buttonAlertStatus));
        //Process alert status  not_due
        buttonAlertStatus.buttonAlertStatus = ConstantsUtils.AlertStatusUtils.NOT_DUE;
        processButtonAlertStatus(context, button, textView, buttonAlertStatus);
        Assert.assertTrue(assertAlertButtonStatus(context, button, textView, ConstantsUtils.AlertStatusUtils.NOT_DUE, buttonAlertStatus));
        //Process alert status delivery_due
        buttonAlertStatus.buttonAlertStatus = ConstantsUtils.AlertStatusUtils.DELIVERY_DUE;
        processButtonAlertStatus(context, button, textView, buttonAlertStatus);
        Assert.assertTrue(assertAlertButtonStatus(context, button, textView, ConstantsUtils.AlertStatusUtils.DELIVERY_DUE, buttonAlertStatus));
        //Process alert status  expired
        buttonAlertStatus.buttonAlertStatus = ConstantsUtils.AlertStatusUtils.EXPIRED;
        processButtonAlertStatus(context, button, textView, buttonAlertStatus);
        Assert.assertTrue(assertAlertButtonStatus(context, button, textView, ConstantsUtils.AlertStatusUtils.EXPIRED, buttonAlertStatus));
        //Process alert status  today
        buttonAlertStatus.buttonAlertStatus = ConstantsUtils.AlertStatusUtils.TODAY;
        processButtonAlertStatus(context, button, textView, buttonAlertStatus);
        Assert.assertTrue(assertAlertButtonStatus(context, button, textView, ConstantsUtils.AlertStatusUtils.TODAY, buttonAlertStatus));
    }

    private boolean assertAlertButtonStatus(android.content.Context context, Button button,
                                            TextView textView, String status, ButtonAlertStatus buttonAlertStatus) {
        boolean result;
        switch (status) {
            case ConstantsUtils.AlertStatusUtils.DELIVERY_DUE:
                result = (textView.getVisibility() == (View.GONE)) &&
                        button.getText() == (context.getString(R.string.due_delivery));
                break;
            case ConstantsUtils.AlertStatusUtils.EXPIRED:
                result = (textView.getVisibility() == (View.GONE)) &&
                        button.getText() == (context.getString(R.string.due_delivery));
                break;
            case ConstantsUtils.AlertStatusUtils.TODAY:
                result = (textView.getVisibility() == (View.VISIBLE)) && (button.getVisibility() == (View.GONE)) &&
                        textView.getText().equals(String.format(context.getString(R.string.contact_recorded_today), getTodayContact(String.valueOf(buttonAlertStatus.nextContact)))) &&
                        button.getText().equals(String.format(context.getString(R.string.contact_recorded_today_no_break), getTodayContact(String.valueOf(buttonAlertStatus.nextContact))));
                break;
            case ConstantsUtils.AlertStatusUtils.IN_PROGRESS:
            case ConstantsUtils.AlertStatusUtils.DUE:
            case ConstantsUtils.AlertStatusUtils.NOT_DUE:
            case ConstantsUtils.AlertStatusUtils.OVERDUE:
            default:
                result = (textView.getVisibility() == (View.GONE));
                break;
        }
        return result;
    }

    @Test
    public void testGetGestationAgeFromEDDateWhenDateisZero() {
        int gestAge = Utils.getGestationAgeFromEDDate("0");
        Assert.assertEquals(0, gestAge);
    }

    @Test
    public void testGetGestationAgeFromEDDateToThrowException() {
        int gestAge = Utils.getGestationAgeFromEDDate("10-12-2020");
        Assert.assertEquals(0, gestAge);
    }

    @Test
    public void testGetGestationAgeFromEDDate() {
        int gestAge = Utils.getGestationAgeFromEDDate("2020-08-01");
        Assert.assertThat(gestAge, Matchers.greaterThanOrEqualTo(6));
    }

    @Test
    public void testGetInProgressDisplayTemplateOnRegister() {
        try {
            String displayTemplate = Whitebox.invokeMethod(Utils.class, "getDisplayTemplate", RuntimeEnvironment.application, ConstantsUtils.AlertStatusUtils.IN_PROGRESS, false);
            Assert.assertEquals("CONTACT %1$s\n IN PROGRESS", displayTemplate);
        } catch (Exception e) {
            Timber.e(e, " --> testGetDisplayTemplate");
        }
    }

    @Test
    public void testGetInProgressDisplayTemplateOnProfile() {
        try {
            String displayTemplate = Whitebox.invokeMethod(Utils.class, "getDisplayTemplate", RuntimeEnvironment.application, ConstantsUtils.AlertStatusUtils.IN_PROGRESS, true);
            Assert.assertEquals("CONTACT %1$s · IN PROGRESS", displayTemplate);
        } catch (Exception e) {
            Timber.e(e, " --> testGetDisplayTemplate");
        }
    }

    @Test
    public void testGetNotDueDisplayTemplateOnRegister() {
        try {
            String displayTemplate = Whitebox.invokeMethod(Utils.class, "getDisplayTemplate", RuntimeEnvironment.application, ConstantsUtils.AlertStatusUtils.NOT_DUE, false);
            Assert.assertEquals("CONTACT %1$d\n DUE \n %2$s", displayTemplate);
        } catch (Exception e) {
            Timber.e(e, " --> testGetDisplayTemplate");
        }
    }

    @Test
    public void testGetNotDueDisplayTemplateOnProfile() {
        try {
            String displayTemplate = Whitebox.invokeMethod(Utils.class, "getDisplayTemplate", RuntimeEnvironment.application, ConstantsUtils.AlertStatusUtils.NOT_DUE, true);
            Assert.assertEquals("CONTACT %1$d · DUE · %2$s", displayTemplate);
        } catch (Exception e) {
            Timber.e(e, " --> testGetDisplayTemplate");
        }
    }

    @Test
    public void testGetDefaultDisplayTemplateOnRegister() {
        try {
            String displayTemplate = Whitebox.invokeMethod(Utils.class, "getDisplayTemplate", RuntimeEnvironment.application, ConstantsUtils.AlertStatusUtils.DUE, false);
            Assert.assertEquals("START\nCONTACT %1$s\n%2$s", displayTemplate);
        } catch (Exception e) {
            Timber.e(e, " --> testGetDisplayTemplate");
        }
    }

    @Test
    public void testGetDefaultDisplayTemplateOnProfile() {
        try {
            String displayTemplate = Whitebox.invokeMethod(Utils.class, "getDisplayTemplate", RuntimeEnvironment.application, ConstantsUtils.AlertStatusUtils.DUE, true);
            Assert.assertEquals("START · CONTACT %1$s · %2$s", displayTemplate);
        } catch (Exception e) {
            Timber.e(e, " --> testGetDisplayTemplate");
        }
    }

    @Test
    public void testBuildRepeatingGroupValuesShouldReturnCorrectGroupNo() throws JSONException {
        String strStep1JsonObject = "{\"fields\":[{\"key\":\"previous_visits\",\"type\":\"repeating_group\",\"value\"" +
                ":[{\"key\":\"visit_date\"}]}," +
                "{\"key\":\"visit_date_128040f1b4034311b34b6ea65a81d3aa\",\"value\":\"2020-09-09\"}]}";
        JSONObject step1JsonObject = new JSONObject(strStep1JsonObject);
        HashMap<String, HashMap<String, String>> repeatingGroupNum = Utils.buildRepeatingGroupValues(step1JsonObject.optJSONArray(JsonFormConstants.FIELDS), ConstantsUtils.JsonFormKeyUtils.PREVIOUS_VISITS);
        Assert.assertEquals(1, repeatingGroupNum.size());
    }

    @Test
    @PrepareForTest(PatientRepository.class)
    public void testCreateContactVisitEventShouldCreateEvent() throws Exception {
        Map<String, String> womanDetails = new HashMap<>();
        womanDetails.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT, "2");
        womanDetails.put(DBConstantsUtils.KeyUtils.VISIT_START_DATE, "2020-09-08");
        womanDetails.put(DBConstantsUtils.KeyUtils.BASE_ENTITY_ID, "232-sds-34");
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        Mockito.when(coreLibrary.context()).thenReturn(opensrpContext);
        Mockito.when(opensrpContext.allSharedPreferences()).thenReturn(allSharedPreferences);

        ReflectionHelpers.setStaticField(AncLibrary.class, "instance", ancLibrary);
        Mockito.doReturn(new RegisterQueryProvider()).when(ancLibrary).getRegisterQueryProvider();
        PowerMockito.mockStatic(PatientRepository.class);
        PowerMockito.doNothing().when(PatientRepository.class, "updateContactVisitStartDate",
                Mockito.anyString(), Mockito.anyString());
        Event contactVisitEvent = Utils.createContactVisitEvent(new ArrayList<>(), womanDetails, null);
        Assert.assertNotNull(contactVisitEvent);
        Assert.assertNotNull(contactVisitEvent.getFormSubmissionId());
        Assert.assertEquals(womanDetails.get(DBConstantsUtils.KeyUtils.BASE_ENTITY_ID), contactVisitEvent.getBaseEntityId());
        Assert.assertEquals(ConstantsUtils.EventTypeUtils.CONTACT_VISIT, contactVisitEvent.getEventType());
        Assert.assertFalse(contactVisitEvent.getDetails().isEmpty());
        Assert.assertEquals("Contact 2", contactVisitEvent.getDetails().get(ConstantsUtils.CONTACT));
    }

    @Test
    @PrepareForTest(PatientRepository.class)
    public void testCreatePreviousVisitFromGroupShouldPassCorrectArgs() throws Exception {
        String baseEntityId = "089sd-342";
        String previous_visits_map = "{\"269b6b6d1ece4781b58bf91eb05a740e\":{\"visit_date\":\"26-10-2019\"},\"1cd33bfbf4594e619841472933e34c3f\":{\"visit_date\":\"26-02-2020\"}}";

        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        Mockito.when(coreLibrary.context()).thenReturn(opensrpContext);
        Mockito.when(opensrpContext.allSharedPreferences()).thenReturn(allSharedPreferences);
        Date date = new Date();
        Mockito.when(allSharedPreferences.fetchLastUpdatedAtDate(0)).thenReturn(date.getTime());

        ReflectionHelpers.setStaticField(AncLibrary.class, "instance", ancLibrary);
        Mockito.doReturn(new RegisterQueryProvider()).when(ancLibrary).getRegisterQueryProvider();

        ECSyncHelper ecSyncHelper = Mockito.mock(ECSyncHelper.class);
        Mockito.doNothing().when(ecSyncHelper).addEvent(Mockito.anyString(), Mockito.any(JSONObject.class));

        ClientProcessorForJava clientProcessorForJava = Mockito.mock(ClientProcessorForJava.class);
        Mockito.when(ancLibrary.getClientProcessorForJava()).thenReturn(clientProcessorForJava);

        Mockito.doReturn(ecSyncHelper).when(ancLibrary).getEcSyncHelper();
        PowerMockito.mockStatic(PatientRepository.class);
        PowerMockito.doNothing().when(PatientRepository.class, "updateContactVisitStartDate",
                Mockito.anyString(), Mockito.anyString());
        Utils.createPreviousVisitFromGroup(previous_visits_map, baseEntityId);

        ArgumentCaptor<List<String>> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(ecSyncHelper, Mockito.times(1)).getEvents(listArgumentCaptor.capture());
        Assert.assertNotNull(listArgumentCaptor.getValue());
        Assert.assertEquals(2, listArgumentCaptor.getValue().size());

        Mockito.verify(clientProcessorForJava, Mockito.times(1)).processClient(Mockito.anyList());
        Mockito.verify(allSharedPreferences).saveLastUpdatedAtDate(Mockito.eq(date.getTime()));

    }

    @Test
    public void testGetLocationLocalizedName() {
        LocationProperty property = new LocationProperty();
        property.setName("locationName");
        Location location = new Location();
        location.setProperties(property);

        JsonFormActivity jsonFormActivity = Mockito.mock(JsonFormActivity.class);
        android.content.Context context = Mockito.mock(android.content.Context.class);
        Resources resources = Mockito.mock(Resources.class);
        Mockito.doReturn(resources).when(jsonFormActivity).getResources();
        Mockito.doReturn(context).when(jsonFormActivity).getApplicationContext();
        Mockito.doReturn("").when(context).getPackageName();
        Mockito.doReturn(0).when(resources).getIdentifier(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());


        String locName = Utils.getLocationLocalizedName(location, jsonFormActivity);
        Assert.assertEquals("locationName", locName);
    }

    @Test
    @PrepareForTest(CoreLibrary.class)
    public void testGetCurrentLocation() throws Exception {
        LocationProperty property = new LocationProperty();
        property.setName("defaultLocationName");
        Location location = new Location();
        location.setId("village-id");
        location.setProperties(property);

        JsonFormActivity jsonFormActivity = Mockito.mock(JsonFormActivity.class);
        PowerMockito.mockStatic(CoreLibrary.class);
        CoreLibrary coreLibrary = Mockito.mock(CoreLibrary.class);

        PowerMockito.doReturn(coreLibrary).when(CoreLibrary.class, "getInstance");
        Mockito.doReturn(opensrpContext).when(coreLibrary).context();
        Mockito.doReturn(allSharedPreferences).when(opensrpContext).allSharedPreferences();
        Mockito.doReturn("demo").when(allSharedPreferences).fetchRegisteredANM();
        Mockito.when(allSharedPreferences.fetchUserLocalityId(Mockito.anyString())).thenReturn("default-location-id");

        String form = "{\"count\":\"1\",\"encounter_type\":\"ANC Registration\",\"entity_id\":\"\",\"relational_id\":\"\",\"step1\":{\"title\":\"{{anc_register.step1.title}}\",\"fields\":[{\"key\":\"province\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"province\",\"type\":\"spinner\",\"sub_type\":\"location\",\"hint\":\"Select Province\",\"options\":[],\"v_required\":{\"value\":\"true\",\"err\":\"Please Select\"}},{\"key\":\"district\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"district\",\"type\":\"spinner\",\"sub_type\":\"location\",\"hint\":\"Select District\",\"options\":[],\"v_required\":{\"value\":\"true\",\"err\":\"Please Select\"}},{\"key\":\"subdistrict\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"subdistrict\",\"type\":\"spinner\",\"sub_type\":\"location\",\"hint\":\"Select Sub-District\",\"options\":[],\"v_required\":{\"value\":\"true\",\"err\":\"Please Select\"}},{\"key\":\"health_facility\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"health_facility\",\"type\":\"spinner\",\"sub_type\":\"location\",\"hint\":\"Select Health Facility\",\"options\":[],\"v_required\":{\"value\":\"true\",\"err\":\"Please Select\"}},{\"key\":\"village\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"village\",\"type\":\"spinner\",\"sub_type\":\"location\",\"hint\":\"Select Village\",\"options\":[],\"v_required\":{\"value\":\"true\",\"err\":\"Please Select\"}}]},\"properties_file_name\":\"anc_register\"}";
        Mockito.doReturn(new JSONObject(form)).when(jsonFormActivity).getmJSONObject();

        LocationRepository locationRepository = Mockito.mock(LocationRepository.class);
        Mockito.doReturn(locationRepository).when(opensrpContext).getLocationRepository();
        Mockito.when(locationRepository.getLocationById(Mockito.anyString())).thenReturn(location);

        String locationId = Utils.getCurrentLocation("village", jsonFormActivity);
        Assert.assertEquals("village-id", locationId);
    }


}
