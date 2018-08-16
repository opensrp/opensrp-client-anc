package org.smartregister.anc.presenter;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.contract.AdvancedSearchContract;
import org.smartregister.anc.interactor.AdvancedSearchInteractor;
import org.smartregister.anc.model.AdvancedSearchModel;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.domain.Response;

import java.lang.ref.WeakReference;
import java.util.Map;

public class AdvancedSearchPresenter extends RegisterFragmentPresenter implements AdvancedSearchContract.Presenter, AdvancedSearchContract.InteractorCallBack {

    private WeakReference<AdvancedSearchContract.View> viewReference;

    private AdvancedSearchContract.Model model;

    public static final String TABLE_NAME = DBConstants.WOMAN_TABLE_NAME;

    public AdvancedSearchPresenter(AdvancedSearchContract.View view, String viewConfigurationIdentifier) {
        super(view, viewConfigurationIdentifier);
        this.viewReference = new WeakReference<>(view);
        model = new AdvancedSearchModel();
        interactor = new AdvancedSearchInteractor();
    }

    public void search(String firstName, String lastName, String ancId, String edd, String dob, String phoneNumber, String alternateContact, boolean isLocal) {
        String searchCriteria = model.createSearchString(firstName, lastName, ancId, edd, dob, phoneNumber, alternateContact);
        if (StringUtils.isBlank(searchCriteria)) {
            return;
        }

        getView().updateSearchCriteria(searchCriteria);

        Map<String, String> editMap = model.createEditMap(firstName, lastName, ancId, edd, dob, phoneNumber, alternateContact, isLocal);
        if (editMap == null || editMap.isEmpty()) {
            return;
        }

        if (isLocal) {
            getView().showProgressView();
            getView().switchViews(true);

            String mainCondition = model.getMainConditionString(editMap);

            String countSelect = model.countSelect(TABLE_NAME, mainCondition);
            String mainSelect = model.mainSelect(TABLE_NAME, mainCondition);

            getView().initializeQueryParams(TABLE_NAME, countSelect, mainSelect);
            getView().initializeAdapter(visibleColumns);

            getView().countExecute();
            getView().filterandSortInInitializeQueries();

            getView().refresh();

            getView().hideProgressView();

        } else {
            getView().showProgressView();
            getView().switchViews(true);

            interactor.search(editMap, this);

        }
    }

    @Override
    public void onResultsFound(Response<String> response, String ancId) {
        matrixCursor = model.createMatrixCursor(response);

        getView().recalculatePagination(matrixCursor);

        getView().filterandSortInInitializeQueries();
        getView().refresh();

        getView().hideProgressView();
    }

    protected AdvancedSearchContract.View getView() {
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
    }

    public void setModel(AdvancedSearchContract.Model model) {
        this.model = model;
    }

    public void setInteractor(AdvancedSearchContract.Interactor interactor) {
        this.interactor = interactor;
    }
}
