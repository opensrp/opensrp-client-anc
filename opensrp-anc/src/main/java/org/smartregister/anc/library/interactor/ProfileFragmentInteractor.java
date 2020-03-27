package org.smartregister.anc.library.interactor;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.contract.ProfileFragmentContract;
import org.smartregister.anc.library.model.Task;
import org.smartregister.anc.library.task.FetchProfileDataTask;
import org.smartregister.anc.library.util.ANCFormUtils;
import org.smartregister.anc.library.util.Utils;

import java.util.List;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 13/07/2018.
 */
public class ProfileFragmentInteractor implements ProfileFragmentContract.Interactor {
    private ProfileFragmentContract.Presenter mProfileFrgamentPresenter;
    private ANCFormUtils ANCFormUtils = new ANCFormUtils();
    private Utils utils = new Utils();

    public ProfileFragmentInteractor(ProfileFragmentContract.Presenter presenter) {
        this.mProfileFrgamentPresenter = presenter;
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        if (!isChangingConfiguration) {
            mProfileFrgamentPresenter = null;
        }
    }

    @Override
    public void refreshProfileView(String baseEntityId, boolean isForEdit) {
        new FetchProfileDataTask(isForEdit).execute(baseEntityId);
    }

    @Override
    public List<Task> getContactTasks(String baseEntityId, String contactNo) {
        return AncLibrary.getInstance().getContactTasksRepository().getTasks(baseEntityId, null);
    }

    @Override
    public void updateTask(Task task, String contactNo) {
        getProfileView().refreshTasksList(AncLibrary.getInstance().getContactTasksRepository().saveOrUpdateTasks(task));
        savePreviousTaskDetails(contactNo, task);
        createTaskPartialForm(task, contactNo);
    }

    public ProfileFragmentContract.View getProfileView() {
        return mProfileFrgamentPresenter.getProfileView();
    }

    private void savePreviousTaskDetails(String contactNo, Task task) {
        try {
            JSONObject taskValue = new JSONObject(task.getValue());
            if (taskValue.has(JsonFormConstants.VALUE)) {
                JSONArray value = taskValue.getJSONArray(JsonFormConstants.VALUE);
                if (value.length() > 0) {
                    for (int i = 0; i < value.length(); i++) {
                        JSONObject valueObject = value.getJSONObject(i);
                        if (valueObject != null && valueObject.has(JsonFormConstants.KEY) && valueObject.has(JsonFormConstants.TYPE) && valueObject.has(JsonFormConstants.VALUES)) {
                            ANCFormUtils.saveExpansionPanelValues(task.getBaseEntityId(), contactNo, valueObject);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Timber.e(e, " --> savePreviousTestDetails");
        }
    }

    private void createTaskPartialForm(Task task, String contactNo) {
        List<Task> doneTasks = utils.getContactTasksRepositoryHelper().getClosedTasks(task.getBaseEntityId());
        utils.createTasksPartialContainer(task.getBaseEntityId(), getProfileView().getContext(), Integer.parseInt(contactNo) + 1, doneTasks);
    }
}
