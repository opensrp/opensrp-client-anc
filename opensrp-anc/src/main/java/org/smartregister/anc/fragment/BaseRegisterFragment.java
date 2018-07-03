package org.smartregister.anc.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.ybq.android.spinkit.style.FadingCircle;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.BaseRegisterActivity;
import org.smartregister.anc.activity.HomeRegisterActivity;
import org.smartregister.anc.contract.RegisterFragmentContract;
import org.smartregister.anc.event.SyncEvent;
import org.smartregister.anc.helper.LocationHelper;
import org.smartregister.anc.provider.RegisterProvider;
import org.smartregister.anc.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.anc.servicemode.CustomServiceModeOption;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.Utils;
import org.smartregister.anc.view.LocationPickerView;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.cursoradapter.CursorCommonObjectFilterOption;
import org.smartregister.cursoradapter.CursorCommonObjectSort;
import org.smartregister.cursoradapter.CursorSortOption;
import org.smartregister.cursoradapter.SecuredNativeSmartRegisterCursorAdapterFragment;
import org.smartregister.cursoradapter.SmartRegisterPaginatedCursorAdapter;
import org.smartregister.domain.FetchStatus;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.dialog.DialogOption;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;

import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by keyman on 26/06/2018.
 */

public abstract class BaseRegisterFragment extends SecuredNativeSmartRegisterCursorAdapterFragment implements RegisterFragmentContract.View, SyncStatusBroadcastReceiver.SyncStatusListener {

    public static String TOOLBAR_TITLE = BaseRegisterActivity.class.getPackage() + ".toolbarTitle";

    public static final String DIALOG_TAG = "DIALOG_TAG";

    protected RegisterActionHandler registerActionHandler = new RegisterActionHandler();

    protected RegisterFragmentContract.Presenter presenter;

    private LocationPickerView facilitySelection;

    private static final String TAG = BaseRegisterFragment.class.getCanonicalName();
    private Snackbar syncStatusSnackbar;
    private View rootView;
    public static final String CLICK_VIEW_NORMAL = "click_view_normal";
    public static final String CLICK_VIEW_DOSAGE_STATUS = "click_view_dosage_status";

    private TextView initialsTextView;
    private ProgressBar syncProgressBar;
    protected TextView filterStatus;
    protected TextView sortStatus;

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.DefaultOptionsProvider() {


            @Override
            public ServiceModeOption serviceMode() {
                return new CustomServiceModeOption(null, "Name", new int[]{
                        R.string.name, R.string.opensrp_id, R.string.dose_d
                }, new int[]{4, 3, 2});
            }

            @Override
            public FilterOption villageFilter() {
                return new CursorCommonObjectFilterOption("no village filter", "");
            }

            @Override
            public SortOption sortOption() {
                return new CursorCommonObjectSort(getResources().getString(R.string.alphabetical_sort), DBConstants.KEY.LAST_INTERACTED_WITH + " DESC");
            }

            @Override
            public String nameInShortFormForTitle() {
                return context().getStringResource(R.string.anc);
            }
        };
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
                return new DialogOption[]{};
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[]{
                };
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{
                        new CursorCommonObjectSort(getResources().getString(R.string.alphabetical_sort), DBConstants.KEY.FIRST_NAME),
                        new CursorCommonObjectSort(getResources().getString(R.string.opensrp_id), DBConstants.KEY.OPENSRP_ID)
                };
            }

            @Override
            public String searchHint() {
                return context().getStringResource(R.string.str_search_hint);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_register, container, false);
        rootView = view;//handle to the root

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.register_toolbar);
        AppCompatActivity activity = ((AppCompatActivity) getActivity());

        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(activity.getIntent().getStringExtra(TOOLBAR_TITLE));
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        activity.getSupportActionBar().setLogo(R.drawable.round_white_background);
        activity.getSupportActionBar().setDisplayUseLogoEnabled(false);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        setupViews(view);

        return view;
    }

    protected abstract void initializePresenter();

    protected void updateSearchView() {
        if (getSearchView() != null) {
            getSearchView().removeTextChangedListener(textWatcher);
            getSearchView().addTextChangedListener(textWatcher);
            getSearchView().setOnKeyListener(hideKeyboard);
        }
    }

    @Override
    public void updateSearchBarHint(String searchBarText) {
        getSearchView().setHint(searchBarText);
    }

    public void setSearchTerm(String searchText) {
        if (getSearchView() != null) {
            getSearchView().setText(searchText);
        }
    }

    public void onQRCodeSucessfullyScanned(String qrCode) {
        Log.i(TAG, "QR code: " + qrCode);
        if (StringUtils.isNotBlank(qrCode)) {
            filter(qrCode.replace("-", ""), "", getMainCondition());
        }
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        clientsView.setVisibility(View.VISIBLE);
        clientsProgressView.setVisibility(View.INVISIBLE);
        view.findViewById(R.id.sorted_by_bar).setVisibility(View.GONE);

        presenter.processViewConfigurations();
        presenter.initializeQueries(getMainCondition());
        updateSearchView();
        setServiceModeViewDrawableRight(null);

        // QR Code
        View qrCode = view.findViewById(R.id.scan_qr_code);
        qrCode.setOnClickListener(registerActionHandler);

        // Initials
        initialsTextView = view.findViewById(R.id.name_initials);
        presenter.updateInitials();

        // Location
        facilitySelection = view.findViewById(R.id.facility_selection);
        if (facilitySelection != null) {
            facilitySelection.init();
        }

        // Progress bar
        syncProgressBar = view.findViewById(R.id.sync_progress_bar);
        if (syncProgressBar != null) {
            FadingCircle circle = new FadingCircle();
            syncProgressBar.setIndeterminateDrawable(circle);
        }

        // Search button
        View searchButton = view.findViewById(R.id.search_button);
        if (searchButton != null) {
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null) {
                        ((BaseRegisterActivity) getActivity()).displayToast("TODO: go to advanced search page");
                    }
                }
            });
        }

        // Sort and Filter
        filterStatus = view.findViewById(R.id.filter_status);
        sortStatus = view.findViewById(R.id.sort_status);
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        renderView();
    }

    private void renderView() {
        getDefaultOptionsProvider();
        if (isPausedOrRefreshList()) {
            presenter.initializeQueries(getMainCondition());
        }
        updateSearchView();
        presenter.processViewConfigurations();
        updateLocationText();
        refreshSyncProgressSpinner();
    }

    @Override
    public void initializeQueryParams(String tableName, String countSelect, String mainSelect) {
        this.tablename = tableName;
        this.mainCondition = getMainCondition();
        this.countSelect = countSelect;
        this.mainSelect = mainSelect;
        this.Sortqueries = ((CursorSortOption) getDefaultOptionsProvider().sortOption()).sort();

        BaseRegisterFragment.currentlimit = 20;
        // BaseRegisterFragment.currentoffset = 0;
    }

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        RegisterProvider registerProvider = new RegisterProvider(getActivity(), visibleColumns, registerActionHandler);
        clientAdapter = new SmartRegisterPaginatedCursorAdapter(getActivity(), null, registerProvider, context().commonrepository(this.tablename));
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    public void updateInitialsText(String initials) {
        initialsTextView.setText(initials);
    }

    public void filter(String filterString, String joinTableString, String mainConditionString) {
        getSearchCancelView().setVisibility(isEmpty(filterString) ? View.INVISIBLE : View.VISIBLE);

        this.filters = filterString;
        this.joinTable = joinTableString;
        this.mainCondition = mainConditionString;

        CountExecute();
        filterandSortExecute();
    }

    @Override
    public void updateFilterAndFilterStatus(String filterText, String sortText) {
        if (filterStatus != null) {
            filterStatus.setText(Html.fromHtml(filterText));
        }

        if (sortStatus != null) {
            sortStatus.setText(Html.fromHtml(sortText));
        }
    }

    public void updateSortAndFilter(List<Field> filterList, Field sortField) {
        presenter.updateSortAndFilter(filterList, sortField);
    }

    protected final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            //Overriden Do something before Text Changed
        }

        @Override
        public void onTextChanged(final CharSequence cs, int start, int before, int count) {
            filter(cs.toString(), "", getMainCondition());
        }

        @Override
        public void afterTextChanged(Editable editable) {
            //Overriden Do something after Text Changed
        }
    };

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        return null;
    }

    @Override
    protected void onInitialization() {//Implement Abstract Method
    }

    @Override
    protected void startRegistration() {
        ((HomeRegisterActivity) getActivity()).startFormActivity(Constants.JSON_FORM.ANC_REGISTRATION, null, null);
    }

    @Override
    protected void onCreation() {
        initializePresenter();

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            boolean isRemote = extras.getBoolean(Constants.IS_REMOTE_LOGIN);
            if (isRemote) {
                presenter.startSync();
            }
        }
    }

    protected abstract String getMainCondition();

    private void goToPatientDetailActivity(CommonPersonObjectClient patient, boolean launchDialog) {
        /*Map<String, String> patientDetails = patient.getDetails();
        Intent intent = null;
        String registerToken = "";
        intent = new Intent(getActivity(), PatientDetailActivity.class);
        registerToken = Constants.VIEW_CONFIGS.HOME_REGISTER;

        String registerTitle = Utils.readPrefString(getActivity(), TOOLBAR_TITLE + registerToken, "");
        intent.putExtra(Constants.INTENT_KEY.REGISTER_TITLE, registerTitle);
        intent.putExtra(Constants.INTENT_KEY.PATIENT_DETAIL_MAP, (HashMap) patientDetails);
        intent.putExtra(Constants.INTENT_KEY.CLIENT_OBJECT, patient);
        intent.putExtra(Constants.INTENT_KEY.OPENSRP_ID, patientDetails.get(Constants.INTENT_KEY.OPENSRP_ID));
        intent.putExtra(Constants.INTENT_KEY.LAUNCH_VACCINE_DIALOG, launchDialog);
        startActivity(intent);*/
    }

    class RegisterActionHandler implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.scan_qr_code) {
                ((HomeRegisterActivity) getActivity()).startQrCodeScanner();
            } else if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == CLICK_VIEW_NORMAL) {
                goToPatientDetailActivity((CommonPersonObjectClient) view.getTag(), false);
            } else if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == CLICK_VIEW_DOSAGE_STATUS) {
                goToPatientDetailActivity((CommonPersonObjectClient) view.getTag(), true);
            }

        }
    }

    protected void updateLocationText() {
        if (facilitySelection != null) {
            facilitySelection.setText(LocationHelper.getInstance().getOpenMrsReadableName(
                    facilitySelection.getSelectedItem()));
            String locationId = LocationHelper.getInstance().getOpenMrsLocationId(facilitySelection.getSelectedItem());
            context().allSharedPreferences().savePreference(Constants.CURRENT_LOCATION_ID, locationId);

        }
    }

    public LocationPickerView getFacilitySelection() {
        return facilitySelection;
    }


    private void registerSyncStatusBroadcastReceiver() {
        SyncStatusBroadcastReceiver.getInstance().addSyncStatusListener(this);
    }

    private void unregisterSyncStatusBroadcastReceiver() {
        SyncStatusBroadcastReceiver.getInstance().removeSyncStatusListener(this);
    }

    @Override
    public void onSyncInProgress(FetchStatus fetchStatus) {
        Utils.postEvent(new SyncEvent(fetchStatus));
        refreshSyncStatusViews(fetchStatus);
    }

    @Override
    public void onSyncStart() {
        refreshSyncStatusViews(null);
    }


    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {
        refreshSyncStatusViews(fetchStatus);
    }

    private void refreshSyncStatusViews(FetchStatus fetchStatus) {


        if (SyncStatusBroadcastReceiver.getInstance().isSyncing()) {
            if (syncStatusSnackbar != null) syncStatusSnackbar.dismiss();
            syncStatusSnackbar = Snackbar.make(rootView, R.string.syncing,
                    Snackbar.LENGTH_LONG);
            syncStatusSnackbar.show();
        } else {
            if (fetchStatus != null) {
                if (syncStatusSnackbar != null) syncStatusSnackbar.dismiss();
                if (fetchStatus.equals(FetchStatus.fetchedFailed)) {
                    syncStatusSnackbar = Snackbar.make(rootView, R.string.sync_failed, Snackbar.LENGTH_INDEFINITE);
                    syncStatusSnackbar.setActionTextColor(getResources().getColor(R.color.snackbar_action_color));
                    syncStatusSnackbar.setAction(R.string.retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            presenter.startSync();
                        }
                    });
                } else if (fetchStatus.equals(FetchStatus.fetched)
                        || fetchStatus.equals(FetchStatus.nothingFetched)) {

                    setRefreshList(true);
                    renderView();

                    syncStatusSnackbar = Snackbar.make(rootView, R.string.sync_complete, Snackbar.LENGTH_LONG);
                } else if (fetchStatus.equals(FetchStatus.noConnection)) {
                    syncStatusSnackbar = Snackbar.make(rootView, R.string.sync_failed_no_internet, Snackbar.LENGTH_LONG);
                }
                syncStatusSnackbar.show();
            }

        }

        refreshSyncProgressSpinner();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerSyncStatusBroadcastReceiver();
    }

    @Override
    public void onPause() {
        unregisterSyncStatusBroadcastReceiver();
        super.onPause();
    }

    protected View.OnKeyListener hideKeyboard = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                Utils.hideKeyboard(getActivity());
                return true;
            }
            return false;
        }
    };

    private void refreshSyncProgressSpinner() {
        if (SyncStatusBroadcastReceiver.getInstance().isSyncing()) {
            syncProgressBar.setVisibility(View.VISIBLE);
            initialsTextView.setVisibility(View.GONE);
        } else {
            syncProgressBar.setVisibility(View.GONE);
            initialsTextView.setVisibility(View.VISIBLE);
        }
    }

}



