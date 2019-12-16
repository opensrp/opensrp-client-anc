package org.smartregister.anc.library.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.ProfileActivity;
import org.smartregister.anc.library.domain.ButtonAlertStatus;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.view.fragment.BaseProfileFragment;

import java.util.HashMap;

/**
 * Created by ndegwamartin on 12/07/2018.
 */
public class ProfileTasksFragment extends BaseProfileFragment {
    private Button dueButton;
    private ButtonAlertStatus buttonAlertStatus;

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
    }

    @Override
    protected void onCreation() {
        if (getActivity() != null && getActivity().getIntent() != null) {
            HashMap<String, String> clientDetails =
                    (HashMap<String, String>) getActivity().getIntent().getSerializableExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP);
            if (clientDetails != null) {
                buttonAlertStatus = Utils.getButtonAlertStatus(clientDetails, getActivity().getApplicationContext(), true);
            }
        }
    }

    @Override
    protected void onResumption() {
        Utils.processButtonAlertStatus(getActivity(), dueButton, dueButton, buttonAlertStatus);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_profile_tasks, container, false);
        dueButton = fragmentView.findViewById(R.id.profile_overview_due_button);
        if (!ConstantsUtils.AlertStatusUtils.TODAY.equals(buttonAlertStatus.buttonAlertStatus)) {
            dueButton.setOnClickListener((ProfileActivity) getActivity());
        } else {
            dueButton.setEnabled(false);
        }
        return fragmentView;
    }
}
