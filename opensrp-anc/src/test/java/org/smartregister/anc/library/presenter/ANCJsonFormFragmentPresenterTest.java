package org.smartregister.anc.library.presenter;

import android.view.View;
import android.widget.ArrayAdapter;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.MaterialSpinner;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

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
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.AncRegistrationActivity;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.fragment.ANCRegisterFormFragment;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.domain.Location;
import org.smartregister.domain.LocationProperty;

import java.util.ArrayList;

@PowerMockRunnerDelegate(RobolectricTestRunner.class)
@PowerMockIgnore({
        "org.powermock.*", "org.mockito.*", "org.robolectric.*", "android.*", "androidx.*",
        "javax.xml.parsers.*", "com.sun.org.apache.xerces.internal.jaxp.*"
})
public class ANCJsonFormFragmentPresenterTest extends BaseUnitTest {

    private ANCJsonFormFragmentPresenter fragmentPresenter;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private ANCRegisterFormFragment formFragment;
    @Mock
    private AncRegistrationActivity jsonFormView;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        JsonFormInteractor jsonFormInteractor = Mockito.mock(JsonFormInteractor.class);
        Mockito.doReturn(jsonFormView).when(formFragment).getActivity();
        Mockito.doReturn(jsonFormView).when(formFragment).getJsonApi();
        fragmentPresenter = new ANCJsonFormFragmentPresenter(formFragment, jsonFormInteractor);
    }

    @PrepareForTest({Utils.class})
    @Test
    public void testOnOptionsItemSelected() throws Exception {
        PowerMockito.mockStatic(Utils.class);
        ANCJsonFormFragmentPresenter spyPresenter = Mockito.spy(fragmentPresenter);

        MaterialSpinner spinnerProvince = new MaterialSpinner(RuntimeEnvironment.application);
        spinnerProvince.setTag(R.id.is_human_action, true);
        spinnerProvince.setTag(R.id.key, "province");

        MaterialSpinner spinnerDistrict = new MaterialSpinner(RuntimeEnvironment.application);
        spinnerDistrict.setVisibility(View.GONE);
        MaterialSpinner spinnerSubDistrict = new MaterialSpinner(RuntimeEnvironment.application);
        spinnerSubDistrict.setVisibility(View.VISIBLE);
        MaterialSpinner spinnerfacility = new MaterialSpinner(RuntimeEnvironment.application);
        spinnerfacility.setVisibility(View.VISIBLE);
        MaterialSpinner spinnerVillage = new MaterialSpinner(RuntimeEnvironment.application);
        spinnerVillage.setVisibility(View.VISIBLE);

        Mockito.doReturn(spinnerDistrict).when(jsonFormView).getFormDataView("step1:district");
        Mockito.doReturn(spinnerSubDistrict).when(jsonFormView).getFormDataView("step1:subdistrict");
        Mockito.doReturn(spinnerfacility).when(jsonFormView).getFormDataView("step1:health_facility");
        Mockito.doReturn(spinnerVillage).when(jsonFormView).getFormDataView("step1:village");

        JSONObject step1 = new JSONObject("{\"title\":\"{{anc_register.step1.title}}\",\"fields\":[{\"key\":\"province\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"province\",\"type\":\"spinner\",\"sub_type\":\"location\",\"hint\":\"Select Province\",\"options\":[],\"v_required\":{\"value\":\"true\",\"err\":\"Please Select\"},\"value\":\"anc-province-id\"},{\"key\":\"district\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"district\",\"type\":\"spinner\",\"sub_type\":\"location\",\"hint\":\"Select District\",\"options\":[],\"v_required\":{\"value\":\"true\",\"err\":\"Please Select\"}},{\"key\":\"subdistrict\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"subdistrict\",\"type\":\"spinner\",\"sub_type\":\"location\",\"hint\":\"Select Sub-District\",\"options\":[],\"v_required\":{\"value\":\"true\",\"err\":\"Please Select\"}},{\"key\":\"health_facility\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"health_facility\",\"type\":\"spinner\",\"sub_type\":\"location\",\"hint\":\"Select Health Facility\",\"options\":[],\"v_required\":{\"value\":\"true\",\"err\":\"Please Select\"}},{\"key\":\"village\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"village\",\"type\":\"spinner\",\"sub_type\":\"location\",\"hint\":\"Select Village\",\"options\":[],\"v_required\":{\"value\":\"true\",\"err\":\"Please Select\"}}]}");

        Mockito.doReturn(step1).when(formFragment).getStep(JsonFormConstants.STEP1);

        LocationProperty property = new LocationProperty();
        property.setName("childLocationName");
        Location location = new Location();
        location.setId("location-id");
        location.setProperties(property);
        ArrayList<Location> locations = new ArrayList<>();
        locations.add(location);

        PowerMockito.doReturn(locations).when(Utils.class, "getLocationsByParentId", Mockito.anyString());
        PowerMockito.doReturn("location-id").when(Utils.class, "getCurrentLocation", Mockito.anyString(), Mockito.any());
        PowerMockito.doReturn("childLocationName").when(Utils.class, "getLocationLocalizedName",location, jsonFormView);

        Mockito.doReturn(Mockito.mock(ArrayAdapter.class)).when(spyPresenter).getAdapter(Mockito.any());

        spyPresenter.performLocationSpinnerAction(spinnerProvince, 1);

        Assert.assertEquals(View.VISIBLE, spinnerDistrict.getVisibility());

    }

}
