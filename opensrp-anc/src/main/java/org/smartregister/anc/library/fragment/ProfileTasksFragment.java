package org.smartregister.anc.library.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.smartregister.anc.library.R;
import org.smartregister.view.fragment.BaseProfileFragment;

/**
 * Created by ndegwamartin on 12/07/2018.
 */
public class ProfileTasksFragment extends BaseProfileFragment {
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
        //Overriden
    }

    @Override
    protected void onResumption() {
        //Overriden
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_profile_tasks, container, false);
        return fragmentView;
    }
}
