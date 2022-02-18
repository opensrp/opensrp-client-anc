package org.smartregister.anc.library.view;

import static com.vijay.jsonwizard.constants.JsonFormConstants.FIELDS;
import static com.vijay.jsonwizard.constants.JsonFormConstants.HIDDEN;
import static com.vijay.jsonwizard.constants.JsonFormConstants.KEY;
import static com.vijay.jsonwizard.constants.JsonFormConstants.STEP1;
import static com.vijay.jsonwizard.constants.JsonFormConstants.STEP_TITLE;
import static com.vijay.jsonwizard.constants.JsonFormConstants.TEXT;
import static com.vijay.jsonwizard.constants.JsonFormConstants.VALUE;
import static org.smartregister.anc.library.constants.AncFormConstants.FormKeyConstants.LOCATION_SUB_TYPE;
import static org.smartregister.anc.library.constants.AncFormConstants.FormKeyConstants.SUB_TYPE;
import static org.smartregister.anc.library.constants.AncFormConstants.SpinnerKeyConstants.DISTRICT;
import static org.smartregister.anc.library.constants.AncFormConstants.SpinnerKeyConstants.FACILITY;
import static org.smartregister.anc.library.constants.AncFormConstants.SpinnerKeyConstants.PROVINCE;
import static org.smartregister.anc.library.constants.AncFormConstants.SpinnerKeyConstants.SUB_DISTRICT;
import static org.smartregister.anc.library.constants.AncFormConstants.SpinnerKeyConstants.VILLAGE;
import static org.smartregister.anc.library.util.ANCJsonFormUtils.ENCOUNTER_TYPE;
import static org.smartregister.util.JsonFormUtils.getFieldValue;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.widgets.SpinnerFactory;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.domain.Location;
import org.smartregister.domain.LocationTag;
import org.smartregister.util.JsonFormUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class ANCSpinnerFactory extends SpinnerFactory {

    private final HashSet<String> locationSpinners = new HashSet<String>() {
        {
            add(PROVINCE);
            add(DISTRICT);
            add(SUB_DISTRICT);
            add(FACILITY);
            add(VILLAGE);
        }
    };
    private final Map<String, List<String>> descendants = new HashMap<String, List<String>>() {
        {
            put(PROVINCE, Arrays.asList(DISTRICT, SUB_DISTRICT, FACILITY, VILLAGE));
            put(DISTRICT, Arrays.asList(SUB_DISTRICT, FACILITY, VILLAGE));
            put(SUB_DISTRICT, Arrays.asList(FACILITY, VILLAGE));
            put(FACILITY, Arrays.asList(VILLAGE));
            put(VILLAGE, null);
        }
    };
    private final Map<String, String> parents = new HashMap<String, String>() {
        {
            put(DISTRICT, PROVINCE);
            put(SUB_DISTRICT, DISTRICT);
            put(FACILITY, SUB_DISTRICT);
            put(VILLAGE, FACILITY);
        }
    };
    private JsonFormFragment formFragment;
    private JsonFormActivity jsonFormView;

    public ANCSpinnerFactory() {
        super();
    }

    @Override
    public void genericWidgetLayoutHookback(View view, JSONObject jsonObject, JsonFormFragment formFragment) {
        super.genericWidgetLayoutHookback(view, jsonObject, formFragment);
        View materialView = ((RelativeLayout) view).getChildAt(0);
        materialView.setOnTouchListener((v, event) -> {
            materialView.setTag(R.id.is_human_action, true);
            return false;
        });
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment,
                                       JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception {
        this.formFragment = formFragment;
        jsonFormView = (JsonFormActivity) formFragment.getActivity();

        try {
            if (jsonObject.has(SUB_TYPE)
                    && jsonObject.getString(SUB_TYPE)
                    .equalsIgnoreCase(LOCATION_SUB_TYPE)
                    && jsonObject.has(ConstantsUtils.JsonFormKeyUtils.OPTIONS)
                    && jsonObject.getJSONArray(ConstantsUtils.JsonFormKeyUtils.OPTIONS).length() <= 0) {

                String stepTitle = formFragment.getStep(JsonFormConstants.STEP1).get(STEP_TITLE).toString();
                if (stepTitle.equals(ConstantsUtils.EventTypeUtils.REGISTRATION)
                        && StringUtils.endsWithIgnoreCase(jsonObject.getString(KEY), "province")) {
                    populateProvince(jsonObject);
                } else {
                    populateDescendants(jsonObject);
                }
            }
        } catch (JSONException e) {
            Timber.e(e);
        }

        return super.getViewsFromJson(stepName, context, formFragment, jsonObject, listener, popup);
    }

    private void populateProvince(JSONObject jsonObject) {
        List<LocationTag> tags = Utils.getLocationTagsByTagName("Country");
        // TODO: Remove static country ID, current implementation is only done to select proper country in case of more than one countries (dummy data) in locations
        String countryId = (tags != null && tags.size() > 0) ? ((tags.size() > 1) ? "02ebbc84-5e29-4cd5-9b79-c594058923e9" : tags.get(0).getLocationId()) : "";
//        String countryId = (tags != null && tags.size() > 0) ? tags.get(0).getLocationId() : "";

        try {
            populateLocationSpinner(jsonObject, countryId, jsonObject.getString(KEY),
                    descendants.get(jsonObject.getString(KEY)));
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    private void populateDescendants(JSONObject jsonObject) {
        try {
            JSONObject parentField = JsonFormUtils.getFieldJSONObject(getFormStep().getJSONArray(FIELDS),
                    parents.get(jsonObject.getString(KEY)));
            String parentId = parentField.getString(VALUE);

            populateLocationSpinner(jsonObject, parentId, jsonObject.getString(KEY), descendants.get(jsonObject.getString(KEY)));
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    private void populateLocationSpinner(JSONObject jsonObject, String parentLocationId, String spinnerKey,
                                         List<String> controlsToHide) {
        List<Location> locations = Utils.getLocationsByParentId(parentLocationId);
        String selectedLocation = getCurrentLocation(spinnerKey);

        try {
            JSONArray spinnerOptions = jsonObject.getJSONArray("options");

            for (Location entry : locations) {
                String id = entry.getProperties().getName().toLowerCase().trim()
                        .replace(" ", "_")
                        .replace("(", "")
                        .replace(")", "")
                        .replace("-", "_")
                        .replace(":", "_")
                        .replace("'", "")
                        .replace("’", "_");

                int identifier = formFragment.getResources().getIdentifier(id, "string",
                        jsonFormView.getApplicationContext().getPackageName());
                String locationName = entry.getProperties().getName();
                if (identifier != 0) {
                    locationName = jsonFormView.getResources().getString(identifier);
                }

                JSONObject option = new JSONObject();
                option.put(KEY, entry.getId());
                // option.put(TEXT, entry.getProperties().getName());
                option.put(TEXT, locationName);

                spinnerOptions.put(option);

                jsonObject.put(VALUE, selectedLocation);
            }

            JSONObject field = JsonFormUtils.getFieldJSONObject(getFormStep().getJSONArray(FIELDS),
                    jsonObject.getString(KEY));
            field.put(VALUE, selectedLocation);
        } catch (JSONException e) {
            Timber.e(e);
        }

        if (controlsToHide != null && !controlsToHide.isEmpty() && StringUtils.isEmpty(parentLocationId)) {
            for (String control : controlsToHide) {
                try {
                    JSONObject formField = JsonFormUtils.getFieldJSONObject(getFormStep().getJSONArray(FIELDS),
                            control);

                    formField.put(HIDDEN, true);
                } catch (JSONException e) {
                    Timber.e(e);
                }
            }
        }
    }

    private JSONObject getFormStep() {
        return ((JsonFormFragment) formFragment).getStep(STEP1);
    }

    private String getCurrentLocation(String level) {
        String villageID = Utils.getAllSharedPreferences()
                .fetchUserLocalityId(Utils.getAllSharedPreferences().fetchRegisteredANM());
        String address = "";
        String currentLocation = "";

        try {
            JSONObject form = jsonFormView.getmJSONObject();
            if (form.getString(ENCOUNTER_TYPE)
                    .equals(ConstantsUtils.EventTypeUtils.UPDATE_REGISTRATION)) {
                String fieldValue = getFieldValue(form.getJSONObject(STEP1).getJSONArray(FIELDS),
                        level);
                if (fieldValue != null && !fieldValue.equals(""))
                    return fieldValue;
//                }
            }

            Location village = Utils.getLocationById(villageID);
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