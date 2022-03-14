package org.smartregister.anc.library.widget;

import static com.vijay.jsonwizard.constants.JsonFormConstants.FIELDS;
import static com.vijay.jsonwizard.constants.JsonFormConstants.HIDDEN;
import static com.vijay.jsonwizard.constants.JsonFormConstants.KEY;
import static com.vijay.jsonwizard.constants.JsonFormConstants.STEP1;
import static com.vijay.jsonwizard.constants.JsonFormConstants.STEP_TITLE;
import static com.vijay.jsonwizard.constants.JsonFormConstants.TEXT;
import static com.vijay.jsonwizard.constants.JsonFormConstants.VALUE;
import static org.smartregister.anc.library.util.ConstantsUtils.FormKeyConstants.LOCATION_SUB_TYPE;
import static org.smartregister.anc.library.util.ConstantsUtils.FormKeyConstants.SUB_TYPE;
import static org.smartregister.anc.library.util.ConstantsUtils.LocationConstants.COUNTRY;
import static org.smartregister.anc.library.util.ConstantsUtils.SpinnerKeyConstants.DISTRICT;
import static org.smartregister.anc.library.util.ConstantsUtils.SpinnerKeyConstants.FACILITY;
import static org.smartregister.anc.library.util.ConstantsUtils.SpinnerKeyConstants.PROVINCE;
import static org.smartregister.anc.library.util.ConstantsUtils.SpinnerKeyConstants.SUB_DISTRICT;
import static org.smartregister.anc.library.util.ConstantsUtils.SpinnerKeyConstants.VILLAGE;

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

    public static final HashSet<String> locationSpinners = new HashSet<String>() {
        {
            add(PROVINCE);
            add(DISTRICT);
            add(SUB_DISTRICT);
            add(FACILITY);
            add(VILLAGE);
        }
    };

    public static final Map<String, List<String>> descendants = new HashMap<String, List<String>>() {
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

                String stepTitle = formFragment.getStep(STEP1).get(STEP_TITLE).toString();
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

    protected void populateProvince(JSONObject jsonObject) {
        String countryId = Utils.getProperties(jsonFormView).getProperty(ConstantsUtils.Properties.DEAFAULT_COUNTRY_ID, "");
        if (StringUtils.isEmpty(countryId)) {
            List<LocationTag> tags = Utils.getLocationTagsByTagName(COUNTRY);
            countryId = (tags != null && tags.size() > 0) ? tags.get(0).getLocationId() : "";
        }
        try {
            populateLocationSpinner(jsonObject, countryId, jsonObject.getString(KEY),
                    descendants.get(jsonObject.getString(KEY)));
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    protected void populateDescendants(JSONObject jsonObject) {
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
        String selectedLocation = Utils.getCurrentLocation(spinnerKey, jsonFormView);

        try {
            JSONArray spinnerOptions = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);

            for (Location entry : locations) {
                String locationName = Utils.getLocationLocalizedName(entry, jsonFormView);

                JSONObject option = new JSONObject();
                option.put(KEY, entry.getId());
                option.put(TEXT, locationName);

                spinnerOptions.put(option);
                jsonObject.put(VALUE, selectedLocation);
            }

            JSONObject field = JsonFormUtils.getFieldJSONObject(getFormStep().getJSONArray(FIELDS),
                    jsonObject.getString(KEY));
            if (field != null)
                field.put(VALUE, selectedLocation);
        } catch (JSONException e) {
            Timber.e(e);
        }

        if (controlsToHide != null && !controlsToHide.isEmpty() && StringUtils.isEmpty(parentLocationId)) {
            for (String control : controlsToHide) {
                try {
                    JSONObject formField = JsonFormUtils.getFieldJSONObject(getFormStep().getJSONArray(FIELDS),
                            control);
                    if (formField != null)
                        formField.put(HIDDEN, true);
                } catch (JSONException e) {
                    Timber.e(e);
                }
            }
        }
    }

    private JSONObject getFormStep() {
        return formFragment.getStep(STEP1);
    }

}