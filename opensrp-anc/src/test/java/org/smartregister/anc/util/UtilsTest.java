package org.smartregister.anc.util;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.domain.ButtonAlertStatus;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.emory.mathcs.backport.java.util.Collections;

import static org.smartregister.anc.util.Utils.getKeyByValue;
import static org.smartregister.anc.util.Utils.getTodayContact;
import static org.smartregister.anc.util.Utils.hasPendingRequiredFields;
import static org.smartregister.anc.util.Utils.isEmptyMap;
import static org.smartregister.anc.util.Utils.processButtonAlertStatus;
import static org.smartregister.anc.util.Utils.reverseHyphenSeperatedValues;

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
        String dobString = ArgumentMatchers.anyString();

        PowerMockito.mockStatic(StringUtils.class);
        PowerMockito.when(StringUtils.isNotBlank(dobString)).thenReturn(true);

        DateTime dobStringToDateTime = Utils.dobStringToDateTime(dobString);
        Assert.assertEquals(dobStringToDateTime, dobStringToDateTime);
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
        Assert.assertEquals(org.smartregister.anc.util.Utils.getListFromString("").size(), 0);
        Assert.assertEquals(org.smartregister.anc.util.Utils.getListFromString(stringList).size(), 6);
        Assert.assertEquals(org.smartregister.anc.util.Utils.getListFromString(null).size(), 0);
        Assert.assertEquals(org.smartregister.anc.util.Utils.getListFromString(stringList).get(2), "36");
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
        org.smartregister.anc.util.Utils utils = new org.smartregister.anc.util.Utils();
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
        Assert.assertFalse(hasPendingRequiredFields(mainObject));
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
        buttonAlertStatus.buttonAlertStatus = Constants.ALERT_STATUS.IN_PROGRESS;
        processButtonAlertStatus(context, button, textView, buttonAlertStatus);
        Assert.assertTrue(assertAlertButtonStatus(context, button, textView, Constants.ALERT_STATUS.IN_PROGRESS, buttonAlertStatus));
        //Process alert status  due
        buttonAlertStatus.buttonAlertStatus = Constants.ALERT_STATUS.DUE;
        processButtonAlertStatus(context, button, textView, buttonAlertStatus);
        Assert.assertTrue(assertAlertButtonStatus(context, button, textView, Constants.ALERT_STATUS.DUE, buttonAlertStatus));
        //Process alert status overdue
        buttonAlertStatus.buttonAlertStatus = Constants.ALERT_STATUS.OVERDUE;
        processButtonAlertStatus(context, button, textView, buttonAlertStatus);
        Assert.assertTrue(assertAlertButtonStatus(context, button, textView, Constants.ALERT_STATUS.OVERDUE, buttonAlertStatus));
        //Process alert status  not_due
        buttonAlertStatus.buttonAlertStatus = Constants.ALERT_STATUS.NOT_DUE;
        processButtonAlertStatus(context, button, textView, buttonAlertStatus);
        Assert.assertTrue(assertAlertButtonStatus(context, button, textView, Constants.ALERT_STATUS.NOT_DUE, buttonAlertStatus));
        //Process alert status delivery_due
        buttonAlertStatus.buttonAlertStatus = Constants.ALERT_STATUS.DELIVERY_DUE;
        processButtonAlertStatus(context, button, textView, buttonAlertStatus);
        Assert.assertTrue(assertAlertButtonStatus(context, button, textView, Constants.ALERT_STATUS.DELIVERY_DUE, buttonAlertStatus));
        //Process alert status  expired
        buttonAlertStatus.buttonAlertStatus = Constants.ALERT_STATUS.EXPIRED;
        processButtonAlertStatus(context, button, textView, buttonAlertStatus);
        Assert.assertTrue(assertAlertButtonStatus(context, button, textView, Constants.ALERT_STATUS.EXPIRED, buttonAlertStatus));
        //Process alert status  today
        buttonAlertStatus.buttonAlertStatus = Constants.ALERT_STATUS.TODAY;
        processButtonAlertStatus(context, button, textView, buttonAlertStatus);
        Assert.assertTrue(assertAlertButtonStatus(context, button, textView, Constants.ALERT_STATUS.TODAY, buttonAlertStatus));
    }

    private boolean assertAlertButtonStatus(android.content.Context context, Button button,
                                            TextView textView, String status, ButtonAlertStatus buttonAlertStatus) {
        boolean result;
        switch (status) {
            case Constants.ALERT_STATUS.IN_PROGRESS:
                result = (textView.getVisibility() == (View.GONE));

                break;
            case Constants.ALERT_STATUS.DUE:
                result = (textView.getVisibility() == (View.GONE));

                break;
            case Constants.ALERT_STATUS.OVERDUE:
                result = (textView.getVisibility() == (View.GONE));

                break;
            case Constants.ALERT_STATUS.NOT_DUE:
                result = (textView.getVisibility() == (View.GONE));
                break;
            case Constants.ALERT_STATUS.DELIVERY_DUE:
                result = (textView.getVisibility() == (View.GONE)) &&
                        button.getText() == (context.getString(R.string.due_delivery));
                break;
            case Constants.ALERT_STATUS.EXPIRED:
                result = (textView.getVisibility() == (View.GONE)) &&
                        button.getText() == (context.getString(R.string.due_delivery));
                break;
            case Constants.ALERT_STATUS.TODAY:
                result = (textView.getVisibility() == (View.VISIBLE)) && (button.getVisibility() == (View.GONE)) &&
                        textView.getText().equals(String.format(context.getString(R.string.contact_recorded_today),
                                getTodayContact(String.valueOf(buttonAlertStatus.nextContact)))) &&
                        button.getText().equals( String.format(context.getString(R.string.contact_recorded_today_no_break),
                                getTodayContact(String.valueOf(buttonAlertStatus.nextContact))));
                break;
            default:
                result = (textView.getVisibility() == (View.GONE));
                break;
        }
        return result;
    }
}
