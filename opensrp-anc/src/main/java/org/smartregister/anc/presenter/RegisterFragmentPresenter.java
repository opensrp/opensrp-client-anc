package org.smartregister.anc.presenter;

import com.google.gson.JsonArray;
import com.google.gson.annotations.JsonAdapter;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.R;
import org.smartregister.anc.contract.AdvancedSearchContract;
import org.smartregister.anc.contract.RegisterFragmentContract;
import org.smartregister.anc.cursor.AdvancedMatrixCursor;
import org.smartregister.anc.interactor.AdvancedSearchInteractor;
import org.smartregister.anc.model.RegisterFramentModel;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.domain.Response;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class RegisterFragmentPresenter implements RegisterFragmentContract.Presenter, AdvancedSearchContract.InteractorCallBack {

    private WeakReference<RegisterFragmentContract.View> viewReference;

    private RegisterFragmentContract.Model model;

    private RegisterConfiguration config;

    protected AdvancedSearchContract.Interactor interactor;

    protected AdvancedMatrixCursor matrixCursor;

    protected Set<org.smartregister.configurableviews.model.View> visibleColumns = new TreeSet<>();
    private String viewConfigurationIdentifier;

    public RegisterFragmentPresenter(RegisterFragmentContract.View view, String viewConfigurationIdentifier) {
        this.viewReference = new WeakReference<>(view);
        this.model = new RegisterFramentModel();
        this.viewConfigurationIdentifier = viewConfigurationIdentifier;
        this.config = model.defaultRegisterConfiguration();

        this.interactor = new AdvancedSearchInteractor();
    }

    @Override
    public void processViewConfigurations() {
        if (StringUtils.isBlank(viewConfigurationIdentifier)) {
            return;
        }

        ViewConfiguration viewConfiguration = model.getViewConfiguration(viewConfigurationIdentifier);
        if (viewConfiguration != null) {
            config = (RegisterConfiguration) viewConfiguration.getMetadata();
            setVisibleColumns(model.getRegisterActiveColumns(viewConfigurationIdentifier));
        }

        if (config.getSearchBarText() != null && getView() != null) {
            getView().updateSearchBarHint(config.getSearchBarText());
        }
    }

    @Override
    public void initializeQueries(String mainCondition) {
        String tableName = DBConstants.WOMAN_TABLE_NAME;

        String countSelect = model.countSelect(tableName, mainCondition);
        String mainSelect = model.mainSelect(tableName, mainCondition);

        getView().initializeQueryParams(tableName, countSelect, mainSelect);
        getView().initializeAdapter(visibleColumns);

        getView().countExecute();
        getView().filterandSortInInitializeQueries();

        getView().refresh();
    }

    @Override
    public void updateInitials() {
        String initials = model.getInitials();
        if (initials != null) {
            getView().updateInitialsText(initials);
        }
    }

    @Override
    public void startSync() {
        //ServiceTools.startSyncService(getActivity());
    }

    @Override
    public void updateSortAndFilter(List<Field> filterList, Field sortField) {
        String filterText = model.getFilterText(filterList, getView().getString(R.string.filter));
        String sortText = model.getSortText(sortField);

        getView().updateFilterAndFilterStatus(filterText, sortText);
    }

    @Override
    public void searchGlobally(String ancId) {
        getView().showProgressView();

        Map<String, String> editMap = model.createEditMap(ancId);
        interactor.search(editMap, this);
    }

    @Override
    public void onResultsFound(Response<String> response, String ancId) {
        JSONArray jsonArray = model.getJsonArray(response);
        
        if (response == null || jsonArray.length() <= 0) {
		  getView().showNotFoundPopup(ancId);
	    } else {
		    matrixCursor = model.createMatrixCursor(response);
		
		    getView().recalculatePagination(matrixCursor);
		
		    getView().filterandSortInInitializeQueries();
		    getView().refresh();
		
		    getView().hideProgressView();
	    }
    }

    protected RegisterFragmentContract.View getView() {
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
    }

    private void setVisibleColumns(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        this.visibleColumns = visibleColumns;
    }

    public void setModel(RegisterFragmentContract.Model model) {
        this.model = model;
    }

    public void setMatrixCursor(AdvancedMatrixCursor matrixCursor) {
        this.matrixCursor = matrixCursor;
    }

    public AdvancedMatrixCursor getMatrixCursor() {
        return matrixCursor;
    }

    public void setInteractor(AdvancedSearchContract.Interactor interactor) {
        this.interactor = interactor;
    }
}