package org.smartregister.anc.library.contract;

import org.jeasy.rules.api.Facts;
import org.smartregister.anc.library.model.Task;

import java.util.List;
import java.util.Map;

/**
 * Created by ndegwamartin on 13/07/2018.
 */
public interface ProfileFragmentContract {

    interface Presenter {
        ProfileFragmentContract.View getProfileView();

        Facts getImmediatePreviousContact(Map<String, String> client, String baseEntityId, String contactNo);

        void getContactTasks(String baseEntityId, String contactNo);
    }

    interface View {
        void setContactTasks(List<Task> contactTasks);
    }

    interface Interactor {
        void onDestroy(boolean isChangingConfiguration);

        void refreshProfileView(String baseEntityId, boolean isForEdit);

        List<Task> getContactTasks(String baseEntityId, String contactNo);
    }
}
