package org.smartregister.anc.contract;

import java.util.Map;

public class AdvancedSearchContract {

    public interface Presenter extends RegisterFragmentContract.Presenter {
        void search(String firstName, String lastName, String ancId, String edd, String dob, String phoneNumber, String alternateContact, boolean isLocal);
    }

    public interface View extends RegisterFragmentContract.View {
        void switchViews(boolean showList);
    }

    public interface Model extends RegisterFragmentContract.Model {

        Map<String, String> createEditMap(String firstName, String lastName, String ancId, String edd, String dob, String phoneNumber, String alternateContact);

        String getMainConditionString(Map<String, String> editMap);

    }
}
