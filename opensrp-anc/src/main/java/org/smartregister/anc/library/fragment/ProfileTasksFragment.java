package org.smartregister.anc.library.fragment;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.ProfileActivity;
import org.smartregister.anc.library.contract.ProfileFragmentContract;
import org.smartregister.anc.library.domain.ButtonAlertStatus;
import org.smartregister.anc.library.model.Task;
import org.smartregister.anc.library.presenter.ProfileFragmentPresenter;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.view.fragment.BaseProfileFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ndegwamartin on 12/07/2018.
 */
public class ProfileTasksFragment extends BaseProfileFragment implements ProfileFragmentContract.View {
    private Button dueButton;
    private ButtonAlertStatus buttonAlertStatus;
    private ProfileFragmentContract.Presenter presenter;
    private String baseEntityId;
    private List<Task> taskList = new ArrayList<>();
    private HashMap<String, String> clientDetails;
    private String contactNo;
    private View noHealthRecordLayout;
    private ConstraintLayout tasksLayout;

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
            contactNo = String.valueOf(Utils.getTodayContact(clientDetails.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT)));
            if (clientDetails != null) {
                buttonAlertStatus = Utils.getButtonAlertStatus(clientDetails, getActivity().getApplicationContext(), true);
            }
        }
    }

    @Override
    protected void onResumption() {
        Utils.processButtonAlertStatus(getActivity(), dueButton, buttonAlertStatus);
        if (getActivity() != null && getActivity().getIntent() != null) {
            baseEntityId = getActivity().getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID);
        }

        presenter.getContactTasks(baseEntityId, contactNo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_profile_tasks, container, false);
        noHealthRecordLayout = fragmentView.findViewById(R.id.no_health_data_recorded_profile_task_layout);
        tasksLayout = fragmentView.findViewById(R.id.tasks_layout);

        dueButton = ((ProfileActivity) getActivity()).getDueButton();
        if (!ConstantsUtils.AlertStatusUtils.TODAY.equals(buttonAlertStatus.buttonAlertStatus)) {
            dueButton.setOnClickListener((ProfileActivity) getActivity());
        } else {
            dueButton.setEnabled(false);
        }
        return fragmentView;
    }

    @Override
    public void setContactTasks(List<Task> contactTasks) {
        taskList = contactTasks;
        toggleViews();
    }

    private void toggleViews() {
        if (taskList.size() > 0) {
            noHealthRecordLayout.setVisibility(View.GONE);
            tasksLayout.setVisibility(View.VISIBLE);
        } else {
            noHealthRecordLayout.setVisibility(View.VISIBLE);
            tasksLayout.setVisibility(View.GONE);
        }
    }
}
