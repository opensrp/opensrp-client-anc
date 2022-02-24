package org.smartregister.anc.library.widget;

import android.os.SystemClock;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.MaterialSpinner;
import com.vijay.jsonwizard.fragments.JsonFormFragment;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.domain.Location;
import org.smartregister.domain.LocationProperty;
import org.smartregister.domain.LocationTag;

import java.util.ArrayList;

@PowerMockRunnerDelegate(RobolectricTestRunner.class)
@PowerMockIgnore({
        "org.powermock.*", "org.mockito.*", "org.robolectric.*", "android.*", "androidx.*",
        "javax.xml.parsers.*", "com.sun.org.apache.xerces.internal.jaxp.*"
})
public class AncSpinnerFactoryTest extends BaseUnitTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private JsonFormFragment jsonFormFragment;

    @Mock
    private JsonFormActivity jsonFormView;

    private ANCSpinnerFactory spinnerFactory;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        spinnerFactory = new ANCSpinnerFactory();
        ReflectionHelpers.setField(spinnerFactory, "jsonFormView", jsonFormView);
        ReflectionHelpers.setField(spinnerFactory, "formFragment", jsonFormFragment);

    }

    @Test
    public void testGenericWidgetLayoutHookback() throws JSONException {
        RelativeLayout view = new RelativeLayout(RuntimeEnvironment.application);
        MaterialSpinner spinner = new MaterialSpinner(RuntimeEnvironment.application);
        view.addView(spinner);

        String form = "{\"count\":\"1\",\"encounter_type\":\"ANC Registration\",\"entity_id\":\"\",\"relational_id\":\"\",\"step1\":{\"title\":\"{{anc_register.step1.title}}\",\"fields\":[{\"key\":\"province\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"province\",\"type\":\"spinner\",\"sub_type\":\"location\",\"hint\":\"Select Province\",\"options\":[],\"v_required\":{\"value\":\"true\",\"err\":\"Please Select\"}},{\"key\":\"district\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"district\",\"type\":\"spinner\",\"sub_type\":\"location\",\"hint\":\"Select District\",\"options\":[],\"v_required\":{\"value\":\"true\",\"err\":\"Please Select\"}},{\"key\":\"subdistrict\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"subdistrict\",\"type\":\"spinner\",\"sub_type\":\"location\",\"hint\":\"Select Sub-District\",\"options\":[],\"v_required\":{\"value\":\"true\",\"err\":\"Please Select\"}},{\"key\":\"health_facility\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"health_facility\",\"type\":\"spinner\",\"sub_type\":\"location\",\"hint\":\"Select Health Facility\",\"options\":[],\"v_required\":{\"value\":\"true\",\"err\":\"Please Select\"}},{\"key\":\"village\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"village\",\"type\":\"spinner\",\"sub_type\":\"location\",\"hint\":\"Select Village\",\"options\":[],\"v_required\":{\"value\":\"true\",\"err\":\"Please Select\"}}]},\"properties_file_name\":\"anc_register\"}";
        spinnerFactory.genericWidgetLayoutHookback(view, new JSONObject(form), jsonFormFragment);

        // Obtain MotionEvent object
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + 100;
        MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, 0f, 0f, 0);
        spinner.dispatchTouchEvent(motionEvent);

        Assert.assertEquals(spinner.getTag(R.id.is_human_action), true);
    }

    @Test
    @PrepareForTest({Utils.class})
    public void testPopulateProvince() throws Exception {
        String province = "{\"key\":\"province\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"province\",\"type\":\"spinner\",\"sub_type\":\"location\",\"hint\":\"Select Province\",\"options\":[],\"v_required\":{\"value\":\"true\",\"err\":\"Please Select\"}}";
        JSONObject provinceOnject = new JSONObject(province);

        LocationTag countryTag = new LocationTag();
        countryTag.setLocationId("02ebbc84-5e29-4cd5-9b79-c594058923e9");
        ArrayList<LocationTag> locationTags = new ArrayList<>();
        locationTags.add(countryTag);

        LocationProperty property = new LocationProperty();
        property.setName("childLocationName");
        Location location = new Location();
        location.setId("location-id");
        location.setProperties(property);
        ArrayList<Location> locations = new ArrayList<>();
        locations.add(location);

        JSONObject step1 = new JSONObject("{\"title\":\"{{anc_register.step1.title}}\",\"fields\":[{\"key\":\"province\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"province\",\"type\":\"spinner\",\"sub_type\":\"location\",\"hint\":\"Select Province\",\"options\":[],\"v_required\":{\"value\":\"true\",\"err\":\"Please Select\"}},{\"key\":\"district\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"district\",\"type\":\"spinner\",\"sub_type\":\"location\",\"hint\":\"Select District\",\"options\":[],\"v_required\":{\"value\":\"true\",\"err\":\"Please Select\"}},{\"key\":\"subdistrict\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"subdistrict\",\"type\":\"spinner\",\"sub_type\":\"location\",\"hint\":\"Select Sub-District\",\"options\":[],\"v_required\":{\"value\":\"true\",\"err\":\"Please Select\"}},{\"key\":\"health_facility\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"health_facility\",\"type\":\"spinner\",\"sub_type\":\"location\",\"hint\":\"Select Health Facility\",\"options\":[],\"v_required\":{\"value\":\"true\",\"err\":\"Please Select\"}},{\"key\":\"village\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"village\",\"type\":\"spinner\",\"sub_type\":\"location\",\"hint\":\"Select Village\",\"options\":[],\"v_required\":{\"value\":\"true\",\"err\":\"Please Select\"}}]}");

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.doReturn(locationTags).when(Utils.class, "getLocationTagsByTagName", "Country");
        PowerMockito.doReturn(locations).when(Utils.class, "getLocationsByParentId", Mockito.anyString());
        PowerMockito.doReturn("location-id").when(Utils.class, "getCurrentLocation", Mockito.anyString(), Mockito.any());
        PowerMockito.doReturn("childLocationfName").when(Utils.class, "getLocationLocalizedName",location, jsonFormView);
        PowerMockito.doReturn(step1).when(jsonFormFragment).getStep(JsonFormConstants.STEP1);

        spinnerFactory.populateProvince(provinceOnject);

        Assert.assertEquals(1, provinceOnject.getJSONArray("options").length());
        Assert.assertEquals("location-id", provinceOnject.getString("value"));
    }
    @Test
    @PrepareForTest({Utils.class})
    public void testPopulateDescendants() throws Exception {
        JSONObject step1 = new JSONObject("{\"title\":\"{{anc_register.step1.title}}\",\"fields\":[{\"key\":\"province\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"province\",\"type\":\"spinner\",\"sub_type\":\"location\",\"hint\":\"Select Province\",\"options\":[],\"v_required\":{\"value\":\"true\",\"err\":\"Please Select\"}},{\"key\":\"district\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"district\",\"type\":\"spinner\",\"sub_type\":\"location\",\"hint\":\"Select District\",\"options\":[],\"v_required\":{\"value\":\"true\",\"err\":\"Please Select\"},\"value\":\"disstrict-loc-id\"},{\"key\":\"subdistrict\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"subdistrict\",\"type\":\"spinner\",\"sub_type\":\"location\",\"hint\":\"Select Sub-District\",\"options\":[],\"v_required\":{\"value\":\"true\",\"err\":\"Please Select\"}},{\"key\":\"health_facility\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"health_facility\",\"type\":\"spinner\",\"sub_type\":\"location\",\"hint\":\"Select Health Facility\",\"options\":[],\"v_required\":{\"value\":\"true\",\"err\":\"Please Select\"}},{\"key\":\"village\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"village\",\"type\":\"spinner\",\"sub_type\":\"location\",\"hint\":\"Select Village\",\"options\":[],\"v_required\":{\"value\":\"true\",\"err\":\"Please Select\"}}]}");

        String subDistrict = "{\"key\":\"subdistrict\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"subdistrict\",\"type\":\"spinner\",\"sub_type\":\"location\",\"hint\":\"Select Sub-District\",\"options\":[],\"v_required\":{\"value\":\"true\",\"err\":\"Please Select\"}}";
        JSONObject subDistrictOnject = new JSONObject(subDistrict);

        LocationProperty property = new LocationProperty();
        property.setName("subDistrictLocationName");
        Location location = new Location();
        location.setId("subDistrict-location-id");
        location.setProperties(property);
        ArrayList<Location> locations = new ArrayList<>();
        locations.add(location);

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.doReturn(locations).when(Utils.class, "getLocationsByParentId", Mockito.anyString());
        PowerMockito.doReturn("subDistrict-location-id").when(Utils.class, "getCurrentLocation", Mockito.anyString(), Mockito.any());
        PowerMockito.doReturn("subDistrictLocationName").when(Utils.class, "getLocationLocalizedName",location, jsonFormView);
        PowerMockito.doReturn(step1).when(jsonFormFragment).getStep(JsonFormConstants.STEP1);

        spinnerFactory.populateDescendants(subDistrictOnject);

        Assert.assertEquals(1, subDistrictOnject.getJSONArray("options").length());
        Assert.assertEquals("subDistrict-location-id", subDistrictOnject.getString("value"));
    }

}

