package org.smartregister.anc.library.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vijay.jsonwizard.activities.FormConfigurationJsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.ProfileActivity;
import org.smartregister.anc.library.adapter.ContactTasksDisplayAdapter;
import org.smartregister.anc.library.contract.ProfileFragmentContract;
import org.smartregister.anc.library.domain.ButtonAlertStatus;
import org.smartregister.anc.library.model.Task;
import org.smartregister.anc.library.presenter.ProfileFragmentPresenter;
import org.smartregister.anc.library.util.ANCFormUtils;
import org.smartregister.anc.library.util.ANCJsonFormUtils;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.view.fragment.BaseProfileFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 12/07/2018.
 */
public class ProfileTasksFragment extends BaseProfileFragment implements ProfileFragmentContract.View {
    private Button dueButton;
    private ButtonAlertStatus buttonAlertStatus;
    private ProfileFragmentContract.Presenter presenter;
    private String baseEntityId;
    private List<Task> taskList = new ArrayList<>();
    private String contactNo;
    private View noHealthRecordLayout;
    private TextView tasksLayoutHeader;
    private RecyclerView recyclerView;
    private HashMap<String, String> clientDetails;
    private Task currentTask;
    private ANCFormUtils formUtils = new ANCFormUtils();

    public static ProfileTasksFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        ProfileTasksFragment fragment = new ProfileTasksFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializePresenter();
    }

    protected void initializePresenter() {
        if (getActivity() == null || getActivity().getIntent() == null) {
            return;
        }
        presenter = new ProfileFragmentPresenter(this);
    }

    @Override
    protected void onCreation() {
        if (getActivity() != null && getActivity().getIntent() != null) {
            clientDetails = (HashMap<String, String>) getActivity().getIntent().getSerializableExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP);
            if (clientDetails != null) {
                contactNo = String.valueOf(Utils.getTodayContact(clientDetails.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT)));
                buttonAlertStatus = Utils.getButtonAlertStatus(clientDetails, getActivity(), true);
            }
        }
    }

    @Override
    protected void onResumption() {
        Utils.processButtonAlertStatus(getActivity(), dueButton, buttonAlertStatus);
        if (getActivity() != null && getActivity().getIntent() != null) {
            baseEntityId = getActivity().getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID);
        }
        getPresenter().getContactTasks(baseEntityId, contactNo);
        attachTasksRecyclerView();
    }

    @Override
    public void setContactTasks(List<Task> contactTasks) {
        toggleViews(contactTasks);
        setTaskList(contactTasks);
    }

    @Override
    public void updateTask(Task task) {
        getPresenter().updateTask(task, contactNo);
    }

    @Override
    public void refreshTasksList(boolean refresh) {
        if (refresh) {
            onResumption();
        }
    }

    public ProfileFragmentContract.Presenter getPresenter() {
        return presenter;
    }

    /**
     * Attaches the tasks display adapter to the tasks display recycler view
     */
    private void attachTasksRecyclerView() {
        ContactTasksDisplayAdapter adapter = new ContactTasksDisplayAdapter(getTaskList(), getActivity(), this);
        adapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    /**
     * Toggles the views between the recycler view & the 'no health data' section
     *
     * @param taskList {@link List}
     */
    private void toggleViews(List<Task> taskList) {
        if (taskList.size() > 0) {
            noHealthRecordLayout.setVisibility(View.GONE);
            tasksLayoutHeader.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            noHealthRecordLayout.setVisibility(View.VISIBLE);
            tasksLayoutHeader.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    /**
     * Starts the different tasks forms.
     *
     * @param jsonForm {@link JSONObject}
     */
    public void startTaskForm(JSONObject jsonForm, Task task) {
        setCurrentTask(task);

        Intent intent = new Intent(getActivity(), FormConfigurationJsonFormActivity.class);
        intent.putExtra(ConstantsUtils.JsonFormExtraUtils.JSON, String.valueOf(jsonForm));
        intent.putExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID, baseEntityId);
        intent.putExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP, clientDetails);
        intent.putExtra(ConstantsUtils.IntentKeyUtils.CONTACT_NO, contactNo);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, getForm());
        startActivityForResult(intent, ANCJsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    /**
     * Creates the form object required to display the form.
     *
     * @return form {@link Form}
     */
    @NotNull
    private Form getForm() {
        Form form = new Form();
        form.setName(getString(R.string.contact_tasks_form));
        form.setWizard(false);
        form.setSaveLabel(getString(R.string.save));
        form.setHideSaveLabel(true);
        form.setBackIcon(R.drawable.ic_back);
        return form;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            String jsonString = data.getStringExtra(ConstantsUtils.IntentKeyUtils.JSON);
            if (StringUtils.isNotBlank(jsonString)) {
                JSONObject form = new JSONObject(jsonString);
                JSONArray accordionValues = createAccordionValues(form);
                Task newTask = updateTaskValue(accordionValues);
                updateTask(newTask);
            }
        } catch (Exception e) {
            Timber.e(e, " --> onActivityResult");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_profile_tasks, container, false);
        noHealthRecordLayout = fragmentView.findViewById(R.id.no_health_data_recorded_profile_task_layout);
        tasksLayoutHeader = fragmentView.findViewById(R.id.test_tasks_sub_header);
        recyclerView = fragmentView.findViewById(R.id.tasks_display_recyclerview);

        dueButton = ((ProfileActivity) getActivity()).getDueButton();
        if (!ConstantsUtils.AlertStatusUtils.TODAY.equals(buttonAlertStatus.buttonAlertStatus)) {
            dueButton.setOnClickListener((ProfileActivity) getActivity());
        } else {
            dueButton.setEnabled(false);
        }
        return fragmentView;
    }

    private JSONArray createAccordionValues(JSONObject form) {
        JSONArray values = new JSONArray();
        if (form != null) {
            JSONArray fields = ANCFormUtils.fields(form, JsonFormConstants.STEP1);
            values = formUtils.createExpansionPanelValues(fields);
        }
        return values;
    }

    private Task updateTaskValue(JSONArray values) {
        Task newTask = getCurrentTask();
        try {
            if (values != null && values.length() > 0 && newTask != null) {
                JSONObject newValue = new JSONObject(newTask.getValue());
                newValue.put(JsonFormConstants.VALUE, values);
                newTask.setValue(String.valueOf(newValue));
                newTask.setUpdated(true);
                newTask.setComplete(ANCJsonFormUtils.checkIfTaskIsComplete(newValue));
            }
        } catch (JSONException e) {
            Timber.e(e, " --> updateTaskValue");
        }
        return newTask;
    }

    public Task getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(Task currentTask) {
        this.currentTask = currentTask;
    }
}
