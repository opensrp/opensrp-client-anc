package org.smartregister.anc.presenter;

import android.database.Cursor;
import android.database.CursorJoiner;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.contract.AdvancedSearchContract;
import org.smartregister.anc.cursor.AdvancedMatrixCursor;
import org.smartregister.anc.interactor.AdvancedSearchInteractor;
import org.smartregister.anc.model.AdvancedSearchModel;
import org.smartregister.anc.util.CreateRemoteLocalCursor;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.domain.Response;

import java.lang.ref.WeakReference;
import java.util.Map;

public class AdvancedSearchPresenter extends RegisterFragmentPresenter
        implements AdvancedSearchContract.Presenter, AdvancedSearchContract.InteractorCallBack {

    private WeakReference<AdvancedSearchContract.View> viewReference;

    private AdvancedSearchContract.Model model;

    public static final String TABLE_NAME = DBConstants.WOMAN_TABLE_NAME;

    public AdvancedSearchPresenter(AdvancedSearchContract.View view, String viewConfigurationIdentifier) {
        super(view, viewConfigurationIdentifier);
        this.viewReference = new WeakReference<>(view);
        model = new AdvancedSearchModel();
        interactor = new AdvancedSearchInteractor();
    }

    public void search(String firstName, String lastName, String ancId, String edd, String dob, String phoneNumber,
                       String alternateContact, boolean isLocal) {
        String searchCriteria = model
                .createSearchString(firstName, lastName, ancId, edd, dob, phoneNumber, alternateContact);
        if (StringUtils.isBlank(searchCriteria)) {
            return;
        }

        getView().updateSearchCriteria(searchCriteria);

        Map<String, String> editMap = model
                .createEditMap(firstName, lastName, ancId, edd, dob, phoneNumber, alternateContact, isLocal);
        if (editMap == null || editMap.isEmpty()) {
            return;
        }

        if (isLocal) {
            getView().showProgressView();
            getView().switchViews(true);
            localQueryInitialize(editMap);

            getView().countExecute();
            getView().filterandSortInInitializeQueries();
            getView().hideProgressView();

        } else {
            getView().showProgressView();
            getView().switchViews(true);
            if (editMap.size() > 0) {
                Map<String, String> localMap = model
                        .createEditMap(firstName, lastName, ancId, edd, dob, phoneNumber, alternateContact, true);
                if (localMap != null && !localMap.isEmpty()) {
                    localQueryInitialize(localMap);
                }
            }
            interactor.search(editMap, this, ancId);

        }
    }

    private void localQueryInitialize(Map<String, String> editMap) {
        String mainCondition = model.getMainConditionString(editMap);

        String countSelect = model.countSelect(TABLE_NAME, mainCondition);
        String mainSelect = model.mainSelect(TABLE_NAME, mainCondition);

        getView().initializeQueryParams(TABLE_NAME, countSelect, mainSelect);
        getView().initializeAdapter(visibleColumns);
    }

    @Override
    public void onResultsFound(Response<String> response, String ancId) {
        matrixCursor = model.createMatrixCursor(response);
        AdvancedMatrixCursor advancedMatrixCursor = getRemoteLocalMatrixCursor(matrixCursor);
        advancedMatrixCursor.moveToFirst();
        getView().recalculatePagination(advancedMatrixCursor);

        getView().filterandSortInInitializeQueries();
        getView().hideProgressView();
    }

    private AdvancedMatrixCursor getRemoteLocalMatrixCursor(AdvancedMatrixCursor matrixCursor) {
        String query = getView().filterAndSortQuery();
        Cursor cursor = getView().getRawCustomQueryForAdapter(query);
        if (cursor != null && cursor.getCount() > 0) {
            AdvancedMatrixCursor remoteLocalCursor = new AdvancedMatrixCursor(new String[]{DBConstants.KEY.ID_LOWER_CASE,
                    DBConstants.KEY.RELATIONAL_ID, DBConstants.KEY.FIRST_NAME, DBConstants.KEY.LAST_NAME, DBConstants.KEY.DOB,
                    DBConstants.KEY.ANC_ID, DBConstants.KEY.PHONE_NUMBER, DBConstants.KEY.ALT_NAME});

            CursorJoiner joiner = new CursorJoiner(matrixCursor, new String[]{DBConstants.KEY.ANC_ID,
                    DBConstants.KEY.ID_LOWER_CASE}, cursor,
                    new String[]{DBConstants.KEY.ANC_ID, DBConstants.KEY.ID_LOWER_CASE});
            for (CursorJoiner.Result joinerResult : joiner) {
                switch (joinerResult) {
                    case BOTH:
                        CreateRemoteLocalCursor createRemoteLocalCursor = new CreateRemoteLocalCursor(matrixCursor, true);
                        remoteLocalCursor
                                .addRow(new Object[]{createRemoteLocalCursor.getId(), createRemoteLocalCursor.getRelationalId(),
                                        createRemoteLocalCursor.getFirstName(), createRemoteLocalCursor.getLastName(),
                                        createRemoteLocalCursor.getDob(), createRemoteLocalCursor.getAncId(),
                                        createRemoteLocalCursor.getPhoneNumber(), createRemoteLocalCursor.getAltName()});
                        break;
                    case RIGHT:
                        CreateRemoteLocalCursor localCreateRemoteLocalCursor = new CreateRemoteLocalCursor(cursor, false);
                        remoteLocalCursor
                                .addRow(new Object[]{localCreateRemoteLocalCursor.getId(), localCreateRemoteLocalCursor.getRelationalId(),
                                        localCreateRemoteLocalCursor.getFirstName(), localCreateRemoteLocalCursor.getLastName(),
                                        localCreateRemoteLocalCursor.getDob(), localCreateRemoteLocalCursor.getAncId(),
                                        localCreateRemoteLocalCursor.getPhoneNumber(), localCreateRemoteLocalCursor.getAltName()});

                        break;
                    case LEFT:
                        createRemoteLocalCursor = new CreateRemoteLocalCursor(matrixCursor, true);
                        remoteLocalCursor
                                .addRow(new Object[]{createRemoteLocalCursor.getId(), createRemoteLocalCursor.getRelationalId(),
                                        createRemoteLocalCursor.getFirstName(), createRemoteLocalCursor.getLastName(),
                                        createRemoteLocalCursor.getDob(), createRemoteLocalCursor.getAncId(),
                                        createRemoteLocalCursor.getPhoneNumber(), createRemoteLocalCursor.getAltName()});
                        break;
                }
            }

            cursor.close();
            matrixCursor.close();
            return remoteLocalCursor;
        } else {
            return matrixCursor;
        }
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
