package org.smartregister.anc.library.contract;

import android.database.Cursor;

import org.smartregister.domain.Response;

import java.util.Map;

public interface AdvancedSearchContract {

    interface Presenter extends RegisterFragmentContract.Presenter {
        void search(String firstName, String lastName, String ancId, String edd, String dob, String phoneNumber,
                    String alternateContact, boolean isLocal);
    }

    interface View extends RegisterFragmentContract.View {
        void switchViews(boolean showList);

        void updateSearchCriteria(String searchCriteriaString);

        String filterAndSortQuery();

        Cursor getRawCustomQueryForAdapter(String query);
    }

    interface Model extends RegisterFragmentContract.Model {

        Map<String, String> createEditMap(String firstName, String lastName, String ancId, String edd, String dob,
                                          String phoneNumber, String alternateContact, boolean isLocal);

        String createSearchString(String firstName, String lastName, String ancId, String edd, String dob,
                                  String phoneNumber, String alternateContact);

        String getMainConditionString(Map<String, String> editMap);

    }


    interface Interactor {
        void search(Map<String, String> editMap, InteractorCallBack callBack, String ancId);
    }

    interface InteractorCallBack {
        void onResultsFound(Response<String> response, String ancId);
    }
}
