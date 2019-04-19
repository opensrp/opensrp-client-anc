package org.smartregister.anc.contract;

import org.smartregister.anc.domain.LastContactDetailsWrapper;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface PreviousContactsTests {
    interface Presenter {
        PreviousContactsTests.View getProfileView();

        void loadPreviousContactsTest(String baseEntityId, String contactNo, String lastContactRecordDate)
        throws ParseException, IOException;
    }

    interface View {
        void setUpContactTestsDetailsRecycler(List<LastContactDetailsWrapper> lastContactDetailsTestsWrapperList);
    }

    interface Interactor {
        void onDestroy(boolean isChangingConfiguration);

        void refreshProfileView(String baseEntityId, boolean isForEdit);
    }
}
