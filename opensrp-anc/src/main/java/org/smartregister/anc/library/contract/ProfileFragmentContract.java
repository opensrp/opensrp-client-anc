package org.smartregister.anc.library.contract;

import android.content.Context;

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

        void updateTask(Task task, String contactNo);
    }

    interface View {
        void setContactTasks(List<Task> contactTasks);

        void updateTask(Task task);

        void refreshTasksList(boolean refresh);

        Context getContext();
    }

    interface Interactor {
        void onDestroy(boolean isChangingConfiguration);

        void refreshProfileView(String baseEntityId, boolean isForEdit);

        List<Task> getContactTasks(String baseEntityId, String contactNo);

        void updateTask(Task task, String contactNo);
    }
}
