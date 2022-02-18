package org.smartregister.anc.library.presenter;

import static com.vijay.jsonwizard.constants.JsonFormConstants.FIELDS;
import static com.vijay.jsonwizard.constants.JsonFormConstants.KEYS;
import static com.vijay.jsonwizard.constants.JsonFormConstants.STEP1;
import static com.vijay.jsonwizard.constants.JsonFormConstants.VALUE;
import static com.vijay.jsonwizard.constants.JsonFormConstants.VALUES;
import static org.smartregister.anc.library.constants.AncFormConstants.SpinnerKeyConstants.DISTRICT;
import static org.smartregister.anc.library.constants.AncFormConstants.SpinnerKeyConstants.FACILITY;
import static org.smartregister.anc.library.constants.AncFormConstants.SpinnerKeyConstants.PROVINCE;
import static org.smartregister.anc.library.constants.AncFormConstants.SpinnerKeyConstants.SUB_DISTRICT;
import static org.smartregister.anc.library.constants.AncFormConstants.SpinnerKeyConstants.VILLAGE;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.core.util.Pair;

import com.google.gson.Gson;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.MaterialSpinner;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.AncRegistrationActivity;
import org.smartregister.anc.library.fragment.ANCRegisterFormFragment;
import org.smartregister.anc.library.util.ANCJsonFormUtils;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.domain.Location;
import org.smartregister.util.JsonFormUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class ANCJsonFormFragmentPresenter extends JsonFormFragmentPresenter {

    public static final String TAG = ANCJsonFormFragmentPresenter.class.getName();
    private final HashSet<String> locationSpinners = new HashSet<String>() {
        {
            add(PROVINCE);
            add(DISTRICT);
            add(SUB_DISTRICT);
            add(FACILITY);
            add(VILLAGE);
        }
    };

    private final Map<String, String> children = new HashMap<String, String>() {{
        put(PROVINCE, DISTRICT);
        put(DISTRICT, SUB_DISTRICT);
        put(SUB_DISTRICT, FACILITY);
        put(FACILITY, VILLAGE);
        put(VILLAGE, null);
    }};

    private final Map<String, List<String>> descendants = new HashMap<String, List<String>>() {
        {
            put(PROVINCE, Arrays.asList(DISTRICT, SUB_DISTRICT, FACILITY, VILLAGE));
            put(DISTRICT, Arrays.asList(SUB_DISTRICT, FACILITY, VILLAGE));
            put(SUB_DISTRICT, Arrays.asList(FACILITY, VILLAGE));
            put(FACILITY, Arrays.asList(VILLAGE));
            put(VILLAGE, null);
        }
    };
    private AncRegistrationActivity jsonFormView;
    private ANCRegisterFormFragment formFragment;

    public ANCJsonFormFragmentPresenter(ANCRegisterFormFragment formFragment, JsonFormInteractor jsonFormInteractor) {
        super(formFragment, jsonFormInteractor);
        this.formFragment = formFragment;
        jsonFormView = (AncRegistrationActivity) formFragment.getActivity();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        super.onItemSelected(parent, view, position, id);

        String key = (String) parent.getTag(R.id.key);
        String isHumanAction = String.valueOf(parent.getTag(R.id.is_human_action));

        try {
            if (locationSpinners.contains(key) && Boolean.valueOf(isHumanAction)) {

                JSONObject field = JsonFormUtils.getFieldJSONObject(formFragment.getStep(STEP1).getJSONArray(FIELDS), key);
                String parentLocationId = field.getString(VALUE);

                if (key.equals(PROVINCE)) {
                    populateLocationSpinner(parentLocationId, DISTRICT, descendants.get(DISTRICT));
                } else if (key.equals(DISTRICT)) {
                    populateLocationSpinner(parentLocationId, SUB_DISTRICT, descendants.get(SUB_DISTRICT));
                } else if (key.equals(SUB_DISTRICT)) {
                    populateLocationSpinner(parentLocationId, FACILITY, descendants.get(FACILITY));
                } else if (key.equals(FACILITY) && position > -1) {
                    populateLocationSpinner(parentLocationId, VILLAGE, null);
                }
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    private void populateLocationSpinner(String parentLocationId, String spinnerKey, List<String> controlsToHide) {
        List<Location> locations = Utils.getLocationsByParentId(parentLocationId);
        String selectedLocation = getCurrentLocation(spinnerKey);

        MaterialSpinner spinner = (MaterialSpinner) jsonFormView.getFormDataView(JsonFormConstants.STEP1 + ":" + spinnerKey);
        if (spinner != null) {
            if (locations != null && !locations.isEmpty()) {
                Pair<JSONArray, JSONArray> options = populateLocationOptions(locations);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getView().getContext(), R.layout.native_form_simple_list_item_1, new Gson().fromJson(options.second.toString(), String[].class));
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(formFragment.getCommonListener());
                spinner.setTag(R.id.keys, options.first);
                spinner.setVisibility(View.VISIBLE);
                spinner.setSelection(adapter.getPosition(selectedLocation) + 1);
            } else {
                spinner.setVisibility(View.GONE);
            }

            if (controlsToHide != null && !controlsToHide.isEmpty()) {
                for (String control : controlsToHide) {
                    MaterialSpinner spinnerToHide = (MaterialSpinner) jsonFormView.getFormDataView(JsonFormConstants.STEP1 + ":" + control);
                    spinnerToHide.setVisibility(View.GONE);
                }
            }
        }
    }

    public Pair<JSONArray, JSONArray> populateLocationOptions(List<Location> locations) {
        if (locations == null)
            return null;

        JSONObject field = new JSONObject();
        JSONArray codes = new JSONArray();
        JSONArray values = new JSONArray();

        for (int i = 0; i < locations.size(); i++) {
            codes.put(locations.get(i).getId());

            String id = locations.get(i).getProperties().getName().toLowerCase().trim()
                    .replace(" ", "_")
                    .replace("(", "")
                    .replace(")", "")
                    .replace("-", "_")
                    .replace(":", "_")
                    .replace("'", "")
                    .replace("’", "_");

            int identifier = formFragment.getResources().getIdentifier(id, "string", jsonFormView.getApplicationContext().getPackageName());
            String locationName = locations.get(i).getProperties().getName();
            if (identifier != 0) {
                locationName = jsonFormView.getResources().getString(identifier);
            }

            // values.put(locations.get(i).getProperties().getName());
            values.put(locationName);
        }

        try {
            field.put(KEYS, codes);
            field.put(VALUES, values);
        } catch (JSONException e) {
            Timber.e(e, "Error populating location options");
        }

        return new Pair<>(codes, values);
    }

    private String getCurrentLocation(String level) {
        String villageId = CoreLibrary.getInstance().context().allSharedPreferences().fetchUserLocalityId(CoreLibrary.getInstance().context().allSharedPreferences().fetchRegisteredANM());
        String currentLocation = "";

        try {
            JSONObject form = jsonFormView.getmJSONObject();
            if (form.getString(ANCJsonFormUtils.ENCOUNTER_TYPE).equals(ConstantsUtils.EventTypeUtils.UPDATE_REGISTRATION)) {
                String fieldValue = JsonFormUtils.getFieldValue(form.getJSONObject(STEP1).getJSONArray(FIELDS), level);
                if (fieldValue != null && !fieldValue.equals("")) return fieldValue;
            }

            Location village = Utils.getLocationById(villageId);
            String facilityID = village != null ? village.getProperties().getParentId() : "";
            Location facility = Utils.getLocationById(facilityID);
            Location subDistrict = Utils.getLocationById(facility != null ? facility.getProperties().getParentId() : "");
            Location district = Utils.getLocationById(subDistrict != null ? subDistrict.getProperties().getParentId() : "");
            Location province = Utils.getLocationById(district != null ? district.getProperties().getParentId() : "");
            Location country = Utils.getLocationById(province != null ? province.getProperties().getParentId() : "");

            switch (level.substring(level.lastIndexOf("_") + 1).toUpperCase()) {
                case "COUNTRY":
                    currentLocation = country != null ? country.getId() : "";
                    break;
                case "PROVINCE":
                    currentLocation = province != null ? province.getId() : "";
                    break;
                case "DISTRICT":
                    currentLocation = district != null ? district.getId() : "";
                    break;
                case "SUBDISTRICT":
                    currentLocation = subDistrict != null ? subDistrict.getId() : "";
                    break;
                case "HEALTH_FACILITY":
                case "FACILITY":
                    currentLocation = facility != null ? facility.getId() : "";
                    break;
                case "COMMUNE":
                case "AREA":
                case "VILLAGE":
                default:
                    currentLocation = village != null ? village.getId() : "";
                    break;
            }
        } catch (JSONException e) {
            Timber.e(e, "Error loading current location");
        } catch (Exception e) {
            Timber.e(e, e.getMessage());
        }

        return currentLocation;
    }
}
