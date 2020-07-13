package org.smartregister.anc.library.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.customviews.RadioButton;

import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.BaseHomeRegisterActivity;
import org.smartregister.anc.library.contract.AdvancedSearchContract;
import org.smartregister.anc.library.contract.RegisterFragmentContract;
import org.smartregister.anc.library.cursor.AdvancedMatrixCursor;
import org.smartregister.anc.library.helper.DBQueryHelper;
import org.smartregister.anc.library.listener.DatePickerListener;
import org.smartregister.anc.library.presenter.AdvancedSearchPresenter;
import org.smartregister.anc.library.provider.AdvancedSearchProvider;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewFragment;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.job.SyncSettingsServiceJob;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;

import java.util.HashMap;
import java.util.Set;

import timber.log.Timber;

//import androidx.loader.content.CursorLoader;
//mport androidx.loader.content.Loader;

public class AdvancedSearchFragment extends HomeRegisterFragment
        implements AdvancedSearchContract.View, RegisterFragmentContract.View {

    private View listViewLayout;
    private View advancedSearchForm;
    private ImageButton backButton;
    private Button searchButton;
    private Button search;

    private RadioButton outsideInside;
    private RadioButton myCatchment;

    private MaterialEditText ancId;
    private MaterialEditText firstName;
    private MaterialEditText lastName;
    private MaterialEditText edd;
    private MaterialEditText dob;
    private MaterialEditText phoneNumber;
    private MaterialEditText altContactName;

    private TextView searchCriteria;
    private TextView matchingResults;

    private Button qrCodeButton;

    private boolean listMode = false;
    private boolean isLocal = false;

    private BroadcastReceiver connectionChangeReciever;
    private boolean registeredConnectionChangeReceiver = false;
    private AdvancedSearchTextWatcher advancedSearchTextwatcher = new AdvancedSearchTextWatcher();
    private HashMap<String, String> searchFormData = new HashMap<>();

    @Override
    protected void initializePresenter() {
        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new AdvancedSearchPresenter(this, viewConfigurationIdentifier);

    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);

        listViewLayout = view.findViewById(R.id.advanced_search_list);
        listViewLayout.setVisibility(View.GONE);
        advancedSearchForm = view.findViewById(R.id.advanced_search_form);
        backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(registerActionHandler);
        searchButton = view.findViewById(R.id.search);
        qrCodeButton = view.findViewById(R.id.qrCodeButton);

        searchCriteria = view.findViewById(R.id.search_criteria);
        matchingResults = view.findViewById(R.id.matching_results);

        populateFormViews(view);

    }

    @Override
    protected String getMainCondition() {
        return DBQueryHelper.getHomePatientRegisterCondition();
    }

    @Override
    protected void onViewClicked(View view) {
        if (view.getId() == R.id.search) {
            search();
        } else if (view.getId() == R.id.undo_button) {
            ((BaseRegisterActivity) getActivity()).switchToBaseFragment();
            ((BaseRegisterActivity) getActivity()).setSelectedBottomBarMenuItem(R.id.action_clients);
            ((BaseRegisterActivity) getActivity()).setSearchTerm("");
        } else if (view.getId() == R.id.back_button) {
            switchViews(false);
        } else if ((view.getId() == R.id.patient_column || view.getId() == R.id.profile) && view.getTag() != null) {
            Utils.navigateToProfile(getActivity(),
                    (HashMap<String, String>) ((CommonPersonObjectClient) view.getTag()).getColumnmaps());
        } else if (view.getId() == R.id.sync) {
            SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);
            SyncSettingsServiceJob.scheduleJobImmediately(SyncSettingsServiceJob.TAG);
            //Todo add the move to catchment area
        }
    }

    private void search() {
        String fn = firstName.getText().toString();
        String ln = lastName.getText().toString();
        String id = ancId.getText().toString();
        String eddDate = edd.getText().toString();
        String dobDate = dob.getText().toString();
        String pn = phoneNumber.getText().toString();
        String altName = altContactName.getText().toString();

        if (myCatchment.isChecked()) {
            isLocal = true;
        } else if (outsideInside.isChecked()) {
            isLocal = false;
        }

        ((AdvancedSearchContract.Presenter) presenter).search(fn, ln, id, eddDate, dobDate, pn, altName, isLocal);
    }

    @Override
    public void switchViews(boolean showList) {
        if (showList) {
            Utils.hideKeyboard(getActivity());

            advancedSearchForm.setVisibility(View.GONE);
            listViewLayout.setVisibility(View.VISIBLE);
            clientsView.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.VISIBLE);
            searchButton.setVisibility(View.GONE);

            if (titleLabelView != null) {
                titleLabelView.setText(getString(R.string.search_results));
            }

            updateMatchingResults(0);
            showProgressView();
            listMode = true;
        } else {
            clearSearchCriteria();
            advancedSearchForm.setVisibility(View.VISIBLE);
            listViewLayout.setVisibility(View.GONE);
            clientsView.setVisibility(View.INVISIBLE);
            backButton.setVisibility(View.GONE);
            searchButton.setVisibility(View.VISIBLE);

            if (titleLabelView != null) {
                titleLabelView.setText(getString(R.string.advanced_search));
            }


            listMode = false;
        }
    }

    public void updateMatchingResults(int count) {
        if (matchingResults != null) {
            matchingResults.setText(String.format(getString(R.string.matching_results), String.valueOf(count)));
            matchingResults.setVisibility(View.VISIBLE);
        }
    }

    private void clearSearchCriteria() {
        if (searchCriteria != null) {
            searchCriteria.setVisibility(View.GONE);
            searchCriteria.setText("");
        }
    }

    @Override
    public void updateSearchCriteria(String searchCriteriaString) {
        if (searchCriteria != null) {
            searchCriteria.setText(searchCriteriaString);
            searchCriteria.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public String filterAndSortQuery() {
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(mainSelect);

        String query = "";
        try {
            sqb.addCondition(filters);
            query = sqb.orderbyCondition(Sortqueries);
            query = sqb.Endquery(
                    sqb.addlimitandOffset(query, clientAdapter.getCurrentlimit(), clientAdapter.getCurrentoffset()));
        } catch (Exception e) {
            Timber.e(e, " --> filterAndSortQuery");
        }

        return query;
    }

    @Override
    public Cursor getRawCustomQueryForAdapter(String query) {
        return commonRepository().rawCustomQueryForAdapter(query);
    }

    @Override
    public void showNotFoundPopup(String whoAncId) {
        //Todo implement this
    }

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        AdvancedSearchProvider advancedSearchProvider =
                new AdvancedSearchProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler,
                        paginationViewHandler);
        clientAdapter =
                new RecyclerViewPaginatedAdapter(null, advancedSearchProvider, context().commonrepository(this.tablename));
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    public void recalculatePagination(AdvancedMatrixCursor matrixCursor) {
        super.recalculatePagination(matrixCursor);
        updateMatchingResults(clientAdapter.getTotalcount());
    }

    @Override
    public void countExecute() {
        Cursor cursor = null;

        try {
            SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(countSelect);
            String query;

            sqb.addCondition(filters);
            query = sqb.orderbyCondition(Sortqueries);
            query = sqb.Endquery(query);

            Timber.i(query);
            cursor = commonRepository().rawCustomQueryForAdapter(query);
            cursor.moveToFirst();
            clientAdapter.setTotalcount(cursor.getInt(0));
            Timber.v(String.valueOf(clientAdapter.getTotalcount()));

            clientAdapter.setCurrentlimit(20);
            clientAdapter.setCurrentoffset(0);

        } catch (Exception e) {
            Timber.e(e, " --> countExecute");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        updateMatchingResults(clientAdapter.getTotalcount());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == RecyclerViewFragment.LOADER_ID) {// Returns a new CursorLoader
            return new CursorLoader(getActivity()) {
                @Override
                public Cursor loadInBackground() {
                    AdvancedMatrixCursor matrixCursor = ((AdvancedSearchPresenter) presenter).getMatrixCursor();
                    if (isLocal || matrixCursor == null) {
                        String query = filterAndSortQuery();
                        Cursor cursor = commonRepository().rawCustomQueryForAdapter(query);
                        getActivity().runOnUiThread(() -> hideProgressView());

                        return cursor;
                    } else {
                        return matrixCursor;
                    }
                }
            };
        }// An invalid id was passed in
        return null;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            switchViews(false);
            updateSearchLimits();
            resetForm();
        }
    }

    private void updateSearchLimits() {
        if (Utils.isConnectedToNetwork(getActivity())) {
            outsideInside.setChecked(true);
            myCatchment.setChecked(false);
        } else {
            myCatchment.setChecked(true);
            outsideInside.setChecked(false);
        }

        if (connectionChangeReciever == null) {
            connectionChangeReciever = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (!Utils.isConnectedToNetwork(getActivity())) {
                        myCatchment.setChecked(true);
                        outsideInside.setChecked(false);
                    }
                }
            };

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            getActivity().registerReceiver(connectionChangeReciever, intentFilter);
            registeredConnectionChangeReceiver = true;
        }

    }

    private void resetForm() {
        clearSearchCriteria();
        clearMatchingResults();

        ancId.setText("");
        firstName.setText("");
        lastName.setText("");
        edd.setText("");
        dob.setText("");
        phoneNumber.setText("");
        altContactName.setText("");
    }

    private void clearMatchingResults() {
        if (matchingResults != null) {
            matchingResults.setVisibility(View.GONE);
            matchingResults.setText("");
        }
    }

    private void checkTextFields() {
        if (!TextUtils.isEmpty(ancId.getText()) || !TextUtils.isEmpty(firstName.getText()) ||
                !TextUtils.isEmpty(lastName.getText()) || !TextUtils.isEmpty(edd.getText()) ||
                !TextUtils.isEmpty(dob.getText()) || !TextUtils.isEmpty(phoneNumber.getText()) ||
                !TextUtils.isEmpty(altContactName.getText())) {
            search.setEnabled(true);
            search.setTextColor(getResources().getColor(R.color.white));
            search.setOnClickListener(registerActionHandler);
        } else {
            search.setEnabled(false);
            search.setTextColor(getResources().getColor(R.color.contact_complete_grey_border));
        }
    }

    @Override
    public void setupSearchView(View view) {
        // TODO implement this
    }

    @Override
    protected void goBack() {
        if (listMode) {
            switchViews(false);
        } else {
            ((BaseRegisterActivity) getActivity()).switchToBaseFragment();
        }
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.anc_fragment_advanced_search, container, false);
        rootView = view;//handle to the root

        setupViews(view);
        onResumption();
        return view;
    }

    @Override
    public boolean onBackPressed() {
        goBack();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        assignedValuesBeforeBarcode();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (connectionChangeReciever != null && registeredConnectionChangeReceiver) {
            getActivity().unregisterReceiver(connectionChangeReciever);
            registeredConnectionChangeReceiver = false;
        }
    }

    private void assignedValuesBeforeBarcode() {
        if (searchFormData.size() > 0) {
            firstName.setText(searchFormData.get(ConstantsUtils.FIRST_NAME));
            lastName.setText(searchFormData.get(ConstantsUtils.LAST_NAME));
            edd.setText(searchFormData.get(ConstantsUtils.EDD));
            dob.setText(searchFormData.get(ConstantsUtils.DOB));
            phoneNumber.setText(searchFormData.get(ConstantsUtils.PHONE_NUMBER));
            altContactName.setText(searchFormData.get(ConstantsUtils.ALT_CONTACT_NAME));
        }
    }

    private void populateFormViews(View view) {
        search = view.findViewById(R.id.search);
        search.setEnabled(false);
        search.setTextColor(getResources().getColor(R.color.contact_complete_grey_border));

        outsideInside = view.findViewById(R.id.out_and_inside);
        myCatchment = view.findViewById(R.id.my_catchment);

        outsideInside.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!Utils.isConnectedToNetwork(getActivity())) {
                    myCatchment.setChecked(true);
                    outsideInside.setChecked(false);
                } else {
                    myCatchment.setChecked(!isChecked);
                }
            }
        });

        myCatchment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!Utils.isConnectedToNetwork(getActivity())) {
                    myCatchment.setChecked(true);
                    outsideInside.setChecked(false);
                } else {
                    outsideInside.setChecked(!isChecked);
                }
            }
        });

        View outsideInsideLayout = view.findViewById(R.id.out_and_inside_layout);
        outsideInsideLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outsideInside.toggle();
            }
        });

        View myCatchmentLayout = view.findViewById(R.id.my_catchment_layout);
        myCatchmentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCatchment.toggle();
            }
        });


        ancId = view.findViewById(R.id.anc_id);
        ancId.addTextChangedListener(advancedSearchTextwatcher);

        firstName = view.findViewById(R.id.first_name);
        firstName.addTextChangedListener(advancedSearchTextwatcher);

        lastName = view.findViewById(R.id.last_name);
        lastName.addTextChangedListener(advancedSearchTextwatcher);

        edd = view.findViewById(R.id.edd);
        edd.addTextChangedListener(advancedSearchTextwatcher);

        dob = view.findViewById(R.id.dob);
        dob.addTextChangedListener(advancedSearchTextwatcher);

        phoneNumber = view.findViewById(R.id.phone_number);
        phoneNumber.addTextChangedListener(advancedSearchTextwatcher);

        altContactName = view.findViewById(R.id.alternate_contact_name);
        altContactName.addTextChangedListener(advancedSearchTextwatcher);

        setDatePicker(edd);
        setDatePicker(dob);

        qrCodeButton.setOnClickListener(view1 -> {
            if (getActivity() == null) {
                return;
            }
            BaseRegisterActivity baseRegisterActivity = (BaseRegisterActivity) getActivity();
            baseRegisterActivity.startQrCodeScanner();

            ((BaseHomeRegisterActivity) getActivity()).setAdvancedSearch(true);
            ((BaseHomeRegisterActivity) getActivity()).setAdvancedSearchFormData(createSelectedFieldMap());
        });

        resetForm();
    }

    private void setDatePicker(final EditText editText) {
        editText.setOnClickListener(new DatePickerListener(getActivity(), editText, true));
    }

    private HashMap<String, String> createSelectedFieldMap() {
        HashMap<String, String> fields = new HashMap<>();
        fields.put(ConstantsUtils.FIRST_NAME, firstName.getText().toString());
        fields.put(ConstantsUtils.LAST_NAME, lastName.getText().toString());
        fields.put(ConstantsUtils.EDD, edd.getText().toString());
        fields.put(ConstantsUtils.DOB, dob.getText().toString());
        fields.put(ConstantsUtils.PHONE_NUMBER, phoneNumber.getText().toString());
        fields.put(ConstantsUtils.ALT_CONTACT_NAME, altContactName.getText().toString());
        return fields;
    }

    public EditText getAncId() {
        return this.ancId;
    }

    public void setSearchFormData(HashMap<String, String> searchFormData) {
        this.searchFormData = searchFormData;
    }

    private class AdvancedSearchTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //Todo later
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkTextFields();
        }

        @Override
        public void afterTextChanged(Editable s) {
            checkTextFields();
        }
    }
}