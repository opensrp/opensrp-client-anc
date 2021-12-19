package org.smartregister.anc.library.presenter;

import android.database.Cursor;
import android.database.CursorJoiner;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.library.contract.AdvancedSearchContract;
import org.smartregister.anc.library.cursor.AdvancedMatrixCursor;
import org.smartregister.anc.library.interactor.AdvancedSearchInteractor;
import org.smartregister.anc.library.model.AdvancedSearchModel;
import org.smartregister.anc.library.util.CreateRemoteLocalCursorUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.domain.Response;

import java.lang.ref.WeakReference;
import java.util.Map;

public class AdvancedSearchPresenter extends RegisterFragmentPresenter
        implements AdvancedSearchContract.Presenter, AdvancedSearchContract.InteractorCallBack {

    public static final String TABLE_NAME = DBConstantsUtils.DEMOGRAPHIC_TABLE_NAME;
    private WeakReference<AdvancedSearchContract.View> viewReference;
    private AdvancedSearchContract.Model model;

    public AdvancedSearchPresenter(AdvancedSearchContract.View view, String viewConfigurationIdentifier) {
        super(view, viewConfigurationIdentifier);
        this.viewReference = new WeakReference<>(view);
        model = new AdvancedSearchModel();
        interactor = new AdvancedSearchInteractor();
    }

    public void search(String firstName, String lastName, String ancId, String edd, String dob, String phoneNumber,
                       String alternateContact, boolean isLocal) {
        String searchCriteria =
                model.createSearchString(firstName, lastName, ancId, edd, dob, phoneNumber, alternateContact);
        if (StringUtils.isBlank(searchCriteria)) {
            return;
        }

        getView().updateSearchCriteria(searchCriteria);

        Map<String, String> editMap =
                model.createEditMap(firstName, lastName, ancId, edd, dob, phoneNumber, alternateContact, isLocal);
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
                Map<String, String> localMap =
                        model.createEditMap(firstName, lastName, ancId, edd, dob, phoneNumber, alternateContact, true);
                if (localMap != null && !localMap.isEmpty()) {
                    localQueryInitialize(localMap);
                }
            }
            interactor.search(editMap, this, ancId);

        }
    }

    protected AdvancedSearchContract.View getView() {
        if (viewReference != null) return viewReference.get();
        else return null;
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
            AdvancedMatrixCursor remoteLocalCursor = new AdvancedMatrixCursor(
                    new String[]{DBConstantsUtils.KeyUtils.ID_LOWER_CASE, DBConstantsUtils.KeyUtils.RELATIONAL_ID, DBConstantsUtils.KeyUtils.FIRST_NAME,
                            DBConstantsUtils.KeyUtils.LAST_NAME, DBConstantsUtils.KeyUtils.DOB,DBConstantsUtils.KeyUtils.EDD, DBConstantsUtils.KeyUtils.ANC_ID,
                            DBConstantsUtils.KeyUtils.PHONE_NUMBER, DBConstantsUtils.KeyUtils.ALT_NAME,DBConstantsUtils.KeyUtils.NEXT_CONTACT,DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE});

            CursorJoiner joiner =
                    new CursorJoiner(matrixCursor, new String[]{DBConstantsUtils.KeyUtils.ANC_ID, DBConstantsUtils.KeyUtils.ID_LOWER_CASE},
                            cursor, new String[]{DBConstantsUtils.KeyUtils.ANC_ID, DBConstantsUtils.KeyUtils.ID_LOWER_CASE});
            for (CursorJoiner.Result joinerResult : joiner) {
                switch (joinerResult) {
                    case BOTH:
                        CreateRemoteLocalCursorUtils createRemoteLocalCursorUtils = new CreateRemoteLocalCursorUtils(matrixCursor, true);
                        remoteLocalCursor.addRow(new Object[]{createRemoteLocalCursorUtils.getId(),
                                createRemoteLocalCursorUtils.getRelationalId(), createRemoteLocalCursorUtils.getFirstName(),
                                createRemoteLocalCursorUtils.getLastName(), createRemoteLocalCursorUtils.getDob(),
                                createRemoteLocalCursorUtils.getEdd(),
                                createRemoteLocalCursorUtils.getAncId(), createRemoteLocalCursorUtils.getPhoneNumber(),
                                createRemoteLocalCursorUtils.getAltName(),
                                createRemoteLocalCursorUtils.getNextContact(),
                                createRemoteLocalCursorUtils.getNextContactDate()
                        });
                        break;
                    case RIGHT:
                        CreateRemoteLocalCursorUtils localCreateRemoteLocalCursorUtils = new CreateRemoteLocalCursorUtils(cursor, false);
                        remoteLocalCursor.addRow(new Object[]{localCreateRemoteLocalCursorUtils.getId(),
                                localCreateRemoteLocalCursorUtils.getRelationalId(), localCreateRemoteLocalCursorUtils.getFirstName(),
                                localCreateRemoteLocalCursorUtils.getLastName(), localCreateRemoteLocalCursorUtils.getDob(),
                                localCreateRemoteLocalCursorUtils.getEdd(),
                                localCreateRemoteLocalCursorUtils.getAncId(), localCreateRemoteLocalCursorUtils.getPhoneNumber(),
                                localCreateRemoteLocalCursorUtils.getAltName(),localCreateRemoteLocalCursorUtils.getNextContact(),
                                localCreateRemoteLocalCursorUtils.getNextContactDate()});

                        break;
                    case LEFT:
                        createRemoteLocalCursorUtils = new CreateRemoteLocalCursorUtils(matrixCursor, true);
                        remoteLocalCursor.addRow(new Object[]{createRemoteLocalCursorUtils.getId(),
                                createRemoteLocalCursorUtils.getRelationalId(), createRemoteLocalCursorUtils.getFirstName(),
                                createRemoteLocalCursorUtils.getLastName(), createRemoteLocalCursorUtils.getDob(),
                                createRemoteLocalCursorUtils.getEdd(),
                                createRemoteLocalCursorUtils.getAncId(), createRemoteLocalCursorUtils.getPhoneNumber(),
                                createRemoteLocalCursorUtils.getAltName(),
                                createRemoteLocalCursorUtils.getNextContact(),
                                createRemoteLocalCursorUtils.getNextContactDate()
                        });
                        break;
                    default:
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

    public void setInteractor(AdvancedSearchContract.Interactor interactor) {
        this.interactor = interactor;
    }

    public void setModel(AdvancedSearchContract.Model model) {
        this.model = model;
    }
}
