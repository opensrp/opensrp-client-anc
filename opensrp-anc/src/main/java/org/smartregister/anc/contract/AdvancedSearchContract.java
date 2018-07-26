package org.smartregister.anc.contract;

import org.json.JSONArray;
import org.smartregister.anc.cursor.AdvancedMatrixCursor;
import org.smartregister.domain.Response;

import java.util.Map;

public class AdvancedSearchContract {

    public interface Presenter extends RegisterFragmentContract.Presenter {
        void search(String firstName, String lastName, String ancId, String edd, String dob, String phoneNumber, String alternateContact, boolean isLocal);

        AdvancedMatrixCursor getMatrixCursor();
    }

    public interface View extends RegisterFragmentContract.View {
        void switchViews(boolean showList);

        void updateSearchCriteria(String searchCriteriaString);

        void recalculatePagination(AdvancedMatrixCursor matrixCursor);

        void showProgressView();

        void hideProgressView();
    }

    public interface Model extends RegisterFragmentContract.Model {

        Map<String, String> createEditMap(String firstName, String lastName, String ancId, String edd, String dob, String phoneNumber, String alternateContact, boolean isLocal);

        String createSearchString(String firstName, String lastName, String ancId, String edd, String dob, String phoneNumber, String alternateContact);

        String getMainConditionString(Map<String, String> editMap);

        AdvancedMatrixCursor createMatrixCursor(Response<String> response);

    }

    public interface Interactor {
        void search(Map<String, String> editMap, AdvancedSearchContract.InteractorCallBack callBack);
    }

    public interface InteractorCallBack {
        void onResultsFound(Response<String> response);
    }
}
