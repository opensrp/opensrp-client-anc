package org.smartregister.anc.library.presenter;

import static com.vijay.jsonwizard.constants.JsonFormConstants.FIELDS;
import static com.vijay.jsonwizard.constants.JsonFormConstants.KEYS;
import static com.vijay.jsonwizard.constants.JsonFormConstants.STEP1;
import static com.vijay.jsonwizard.constants.JsonFormConstants.VALUE;
import static com.vijay.jsonwizard.constants.JsonFormConstants.VALUES;
import static org.smartregister.anc.library.util.ConstantsUtils.SpinnerKeyConstants.DISTRICT;
import static org.smartregister.anc.library.util.ConstantsUtils.SpinnerKeyConstants.FACILITY;
import static org.smartregister.anc.library.util.ConstantsUtils.SpinnerKeyConstants.PROVINCE;
import static org.smartregister.anc.library.util.ConstantsUtils.SpinnerKeyConstants.SUB_DISTRICT;
import static org.smartregister.anc.library.util.ConstantsUtils.SpinnerKeyConstants.VILLAGE;
import static org.smartregister.anc.library.widget.ANCSpinnerFactory.descendants;
import static org.smartregister.anc.library.widget.ANCSpinnerFactory.locationSpinners;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.VisibleForTesting;
import androidx.core.util.Pair;

import com.google.gson.Gson;
import com.vijay.jsonwizard.customviews.MaterialSpinner;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.AncRegistrationActivity;
import org.smartregister.anc.library.fragment.ANCRegisterFormFragment;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.domain.Location;
import org.smartregister.util.JsonFormUtils;

import java.util.List;

import timber.log.Timber;

public class ANCJsonFormFragmentPresenter extends JsonFormFragmentPresenter {

    public static final String TAG = ANCJsonFormFragmentPresenter.class.getName();

    private final AncRegistrationActivity jsonFormView;
    private final ANCRegisterFormFragment formFragment;

    public ANCJsonFormFragmentPresenter(ANCRegisterFormFragment formFragment, JsonFormInteractor jsonFormInteractor) {
        super(formFragment, jsonFormInteractor);
        this.formFragment = formFragment;
        jsonFormView = (AncRegistrationActivity) formFragment.getActivity();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        super.onItemSelected(parent, view, position, id);

        performLocationSpinnerAction(parent, position);
    }

    protected void performLocationSpinnerAction(AdapterView<?> parent, int position) {
        String key = (String) parent.getTag(R.id.key);
        String isHumanAction = String.valueOf(parent.getTag(R.id.is_human_action));

        try {
            if (locationSpinners.contains(key) && Boolean.parseBoolean(isHumanAction)) {

                JSONObject field = JsonFormUtils.getFieldJSONObject(formFragment.getStep(STEP1).getJSONArray(FIELDS), key);
                String parentLocationId = field.getString(VALUE);

                if (StringUtils.equals(key, PROVINCE)) {
                    populateLocationSpinner(parentLocationId, DISTRICT, descendants.get(DISTRICT));
                } else if (StringUtils.equals(key, DISTRICT)) {
                    populateLocationSpinner(parentLocationId, SUB_DISTRICT, descendants.get(SUB_DISTRICT));
                } else if (StringUtils.equals(key, SUB_DISTRICT)) {
                    populateLocationSpinner(parentLocationId, FACILITY, descendants.get(FACILITY));
                } else if (StringUtils.equals(key, FACILITY) && position > -1) {
                    populateLocationSpinner(parentLocationId, VILLAGE, null);
                }
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    private void populateLocationSpinner(String parentLocationId, String spinnerKey, List<String> controlsToHide) {
        List<Location> locations = Utils.getLocationsByParentId(parentLocationId);
        String selectedLocation = Utils.getCurrentLocation(spinnerKey, jsonFormView);

        MaterialSpinner spinner = (MaterialSpinner) jsonFormView.getFormDataView(STEP1 + ":" + spinnerKey);
        if (spinner != null) {
            if (locations != null && !locations.isEmpty()) {
                Pair<JSONArray, JSONArray> options = populateLocationOptions(locations);
                ArrayAdapter<String> adapter = getAdapter(options);
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
                    MaterialSpinner spinnerToHide = (MaterialSpinner) jsonFormView.getFormDataView(STEP1 + ":" + control);
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

        for (Location location : locations) {

            codes.put(location.getId());
            String locationName = Utils.getLocationLocalizedName(location, jsonFormView);

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

    @VisibleForTesting
    protected ArrayAdapter<String> getAdapter(Pair<JSONArray, JSONArray> options) {
        return new ArrayAdapter<>(getView().getContext(), R.layout.native_form_simple_list_item_1, new Gson().fromJson(options.second.toString(), String[].class));
    }

}
