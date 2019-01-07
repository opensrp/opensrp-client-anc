package org.smartregister.anc.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.customviews.RadioButton;

import org.smartregister.anc.R;
import org.smartregister.anc.contract.AdvancedSearchContract;
import org.smartregister.anc.contract.RegisterFragmentContract;
import org.smartregister.anc.cursor.AdvancedMatrixCursor;
import org.smartregister.anc.helper.DBQueryHelper;
import org.smartregister.anc.listener.DatePickerListener;
import org.smartregister.anc.presenter.AdvancedSearchPresenter;
import org.smartregister.anc.provider.AdvancedSearchProvider;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.anc.util.Utils;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;

import java.util.Set;

public class AdvancedSearchFragment extends HomeRegisterFragment implements AdvancedSearchContract.View, RegisterFragmentContract.View {

    private View listViewLayout;
    private View advancedSearchForm;
    private ImageButton backButton;
    private Button searchButton;

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

    @Override
    protected void initializePresenter() {
        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new AdvancedSearchPresenter(this, viewConfigurationIdentifier);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_advanced_search, container, false);
        rootView = view;//handle to the root

        setupViews(view);
        onResumption();
        return view;
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

    @Override
    public void onPause() {
        super.onPause();

        if (connectionChangeReciever != null && registeredConnectionChangeReceiver) {
            getActivity().unregisterReceiver(connectionChangeReciever);
            registeredConnectionChangeReceiver = false;
        }
    }

    @Override
    public boolean onBackPressed() {
        goBack();
        return true;
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
    protected void onViewClicked(View view) {
        if (view.getId() == R.id.search) {
            search();
        } else if (view.getId() == R.id.undo_button) {
            ((BaseRegisterActivity) getActivity()).switchToBaseFragment();
            ((BaseRegisterActivity) getActivity()).setSelectedBottomBarMenuItem(R.id.action_clients);
            ((BaseRegisterActivity) getActivity()).setSearchTerm("");
        } else if (view.getId() == R.id.back_button) {
            switchViews(false);
        }
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
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        AdvancedSearchProvider advancedSearchProvider = new AdvancedSearchProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, advancedSearchProvider, context().commonrepository(this.tablename));
        clientsView.setAdapter(clientAdapter);
    }

    private void populateFormViews(View view) {
        Button search = view.findViewById(R.id.search);

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
        firstName = view.findViewById(R.id.first_name);
        lastName = view.findViewById(R.id.last_name);
        edd = view.findViewById(R.id.edd);
        dob = view.findViewById(R.id.dob);
        phoneNumber = view.findViewById(R.id.phone_number);
        altContactName = view.findViewById(R.id.alternate_contact_name);

        setDatePicker(edd);
        setDatePicker(dob);

        search.setOnClickListener(registerActionHandler);

        qrCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() == null) {
                    return;
                }

                BaseRegisterActivity baseRegisterActivity = (BaseRegisterActivity) getActivity();
                baseRegisterActivity.startQrCodeScanner();
            }
        });

        resetForm();
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


            //updateMatchingResults(0);
            showProgressView();
            listMode = true;
        } else {
            //clearSearchCriteria();
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

    private void clearSearchCriteria() {
        if (searchCriteria != null) {
            searchCriteria.setVisibility(View.GONE);
            searchCriteria.setText("");
        }
    }

    private void clearMatchingResults() {
        if (matchingResults != null) {
            matchingResults.setVisibility(View.GONE);
            matchingResults.setText("");
        }
    }


    public void updateMatchingResults(int count) {
        if (matchingResults != null) {
            matchingResults.setText(String.format(getString(R.string.matching_results), String.valueOf(count)));
            matchingResults.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateSearchCriteria(String searchCriteriaString) {
        if (searchCriteria != null) {
            searchCriteria.setText(searchCriteriaString);
            searchCriteria.setVisibility(View.VISIBLE);
        }
    }


    private void setDatePicker(final EditText editText) {
        editText.setOnClickListener(new DatePickerListener(getActivity(), editText, true));
    }

    @Override
    public void setupSearchView(View view) {
        // TODO implement this
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return null;
    }

    @Override
    protected String getMainCondition() {
        return DBQueryHelper.getHomePatientRegisterCondition();
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
    public void recalculatePagination(AdvancedMatrixCursor matrixCursor) {
        super.recalculatePagination(matrixCursor);
        updateMatchingResults(clientAdapter.getTotalcount());
    }

    @Override
    public void showNotFoundPopup(String whoAncId) {
        //Todo implement this
    }

    @Override
    public void countExecute() {
        Cursor c = null;

        try {
            SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(countSelect);
            String query = "";

            sqb.addCondition(filters);
            query = sqb.orderbyCondition(Sortqueries);
            query = sqb.Endquery(query);

            Log.i(getClass().getName(), query);
            c = commonRepository().rawCustomQueryForAdapter(query);
            c.moveToFirst();
            clientAdapter.setTotalcount(c.getInt(0));
            Log.v("total count here", "" + clientAdapter.getTotalcount());

            clientAdapter.setCurrentlimit(20);
            clientAdapter.setCurrentoffset(0);

        } catch (Exception e) {
            Log.e(getClass().getName(), e.toString(), e);
        } finally {
            if (c != null) {
                c.close();
            }
        }

        updateMatchingResults(clientAdapter.getTotalcount());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ID:
                // Returns a new CursorLoader
                return new CursorLoader(getActivity()) {
                    @Override
                    public Cursor loadInBackground() {
                        AdvancedMatrixCursor matrixCursor = ((AdvancedSearchPresenter) presenter).getMatrixCursor();
                        if (isLocal || matrixCursor == null) {
                            String query = filterandSortQuery();
                            Cursor cursor = commonRepository().rawCustomQueryForAdapter(query);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideProgressView();
                                }
                            });

                            return cursor;
                        } else {
                            return matrixCursor;
                        }
                    }
                };
            default:
                // An invalid id was passed in
                return null;
        }
    }

    private String filterandSortQuery() {
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(mainSelect);

        String query = "";
        try {
            sqb.addCondition(filters);
            query = sqb.orderbyCondition(Sortqueries);
            query = sqb.Endquery(sqb.addlimitandOffset(query, clientAdapter.getCurrentlimit(), clientAdapter.getCurrentoffset()));
        } catch (Exception e) {
            Log.e(getClass().getName(), e.toString(), e);
        }

        return query;
    }

    public EditText getAncId() {
        return this.ancId;
    }
}