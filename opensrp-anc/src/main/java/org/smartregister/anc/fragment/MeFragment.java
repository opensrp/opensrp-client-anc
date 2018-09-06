package org.smartregister.anc.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.smartregister.anc.R;
import org.smartregister.anc.activity.PopulationCharacteristicsActivity;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.MeContract;
import org.smartregister.anc.helper.LocationHelper;
import org.smartregister.anc.presenter.MePresenter;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.view.LocationPickerView;

public class MeFragment extends Fragment implements MeContract.View {
    private MeFragmentActionHandler meFragmentActionHandler = new MeFragmentActionHandler();
    private MeContract.Presenter presenter;

    private TextView initials;
    private TextView userName;
    private TextView synced_data;
    private TextView application_version;
    private TextView location_text;

    private RelativeLayout me_location_section;
    private RelativeLayout me_pop_characteristics_section;
    private RelativeLayout site_characteristics_section;
    private RelativeLayout setting_section;
    private RelativeLayout logout_section;
    private RelativeLayout me_location_selection_section;

    private ImageView locationRightCaret;
    private ImageView locationDownCaret;

    private LocationPickerView facilitySelection;

    private Boolean selected = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializePresenter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_me, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpViews(view);
        setClickListeners();
        presenter.updateInitials();
        presenter.updateName();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateLocationText();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLocationText();
    }

    private void setUpViews(View view) {
        initials = view.findViewById(R.id.initials);
        userName = view.findViewById(R.id.user_name);
        me_location_section = view.findViewById(R.id.me_location_section);
        me_pop_characteristics_section = view.findViewById(R.id.me_pop_characteristics_section);
        site_characteristics_section = view.findViewById(R.id.site_characteristics_section);
        setting_section = view.findViewById(R.id.setting_section);
        logout_section = view.findViewById(R.id.logout_section);
        synced_data = view.findViewById(R.id.synced_data);
        locationRightCaret = view.findViewById(R.id.locationRightCaret);
        locationDownCaret = view.findViewById(R.id.locationDownCaret);
        me_location_selection_section = view.findViewById(R.id.me_location_selection_section);
        facilitySelection = view.findViewById(R.id.facility_selection);
        if (facilitySelection != null) {
            facilitySelection.init();
        }
        location_text = view.findViewById(R.id.location_text);
        application_version = view.findViewById(R.id.application_version);
        if (application_version != null) {
            try {
                application_version.setText(String.format(getString(R.string.app_version), getVersion(), presenter.getBuildDate()));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void setClickListeners() {
        me_location_section.setOnClickListener(meFragmentActionHandler);
        me_pop_characteristics_section.setOnClickListener(meFragmentActionHandler);
        site_characteristics_section.setOnClickListener(meFragmentActionHandler);
        setting_section.setOnClickListener(meFragmentActionHandler);
        logout_section.setOnClickListener(meFragmentActionHandler);
    }

    private void initializePresenter() {
        presenter = new MePresenter(this);
    }

    @Override
    public void updateInitialsText(String userInitials) {
        if (initials != null) {
            initials.setText(userInitials);
        }
    }

    @Override
    public void updateNameText(String name) {
        if (userName != null) {
            userName.setText(name);
        }
    }

    private String getVersion() throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        return packageInfo.versionName;
    }

    protected void updateLocationText() {
        if (facilitySelection != null) {
            facilitySelection.setText(LocationHelper.getInstance().getOpenMrsReadableName(facilitySelection.getSelectedItem()));
            String locationId = LocationHelper.getInstance().getOpenMrsLocationId(facilitySelection.getSelectedItem());
            AncApplication.getInstance().getContext().allSharedPreferences().savePreference(Constants.CURRENT_LOCATION_ID, locationId);

            location_text.setText(String.format(getString(R.string.me_page_location_text), LocationHelper.getInstance().getOpenMrsReadableName(
                    facilitySelection.getSelectedItem())));
        }
    }

    public LocationPickerView getFacilitySelection() {
        return facilitySelection;
    }

    /**
     * Handles the Click actions on any of the section in the page.
     */
    private class MeFragmentActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.logout_section:
                    AncApplication.getInstance().logoutCurrentUser();
                    break;
                case R.id.setting_section:
                    //ToDO Add the functionality for the setting page after that is decided.
                    break;
                case R.id.site_characteristics_section:
                    break;
                case R.id.me_pop_characteristics_section:
                    getContext().startActivity(new Intent(getContext(), PopulationCharacteristicsActivity.class));
                    break;
                case R.id.me_location_section:
                    if (selected) {
                        selected = false;
                        me_location_selection_section.setVisibility(View.GONE);
                        locationDownCaret.setVisibility(View.GONE);
                        locationRightCaret.setVisibility(View.VISIBLE);
                    } else {
                        selected = true;
                        me_location_selection_section.setVisibility(View.VISIBLE);
                        locationDownCaret.setVisibility(View.VISIBLE);
                        locationRightCaret.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }

    }
}
