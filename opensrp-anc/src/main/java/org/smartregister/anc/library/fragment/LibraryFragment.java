package org.smartregister.anc.library.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.BaseHomeRegisterActivity;
import org.smartregister.anc.library.adapter.LibraryContentAdapter;
import org.smartregister.anc.library.model.LibraryContent;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends org.smartregister.view.fragment.LibraryFragment {
    protected Toolbar mToolbar;
    private RecyclerView contentLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            ((BaseHomeRegisterActivity) getActivity()).setLibrary(false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootLayout = inflater.inflate(R.layout.fragment_library, container, false);
        setUpViews(rootLayout);
        return rootLayout;
    }

    private void setUpViews(View rootLayout) {
        mToolbar = rootLayout.findViewById(R.id.library_toolbar);
        mToolbar.findViewById(R.id.close_library).setOnClickListener(view -> onBackPressed());
        contentLayout = rootLayout.findViewById(R.id.layout_attach_recycler_view);
    }

    public void onBackPressed() {
        ((BaseHomeRegisterActivity) getActivity()).setLibrary(false);
        ((BaseRegisterActivity) getActivity()).switchToBaseFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        attachLayoutContentAdapter();
    }

    private void attachLayoutContentAdapter() {
        LibraryContentAdapter libraryContentAdapter = new LibraryContentAdapter(getLibraryContent(), getActivity());
        libraryContentAdapter.notifyDataSetChanged();
        contentLayout.setLayoutManager(new LinearLayoutManager(getActivity()));
        contentLayout.setAdapter(libraryContentAdapter);
    }

    private List<LibraryContent> getLibraryContent() {
        List<LibraryContent> libraryContents = new ArrayList<>();
        if (getActivity() != null && getActivity().getResources() != null) {
            LibraryContent birthEmergencyPlan = new LibraryContent(getActivity().getResources().getString(R.string.birth_and_emergency_plan));
            LibraryContent physicalActivity = new LibraryContent(getActivity().getResources().getString(R.string.physical_activity));
            LibraryContent balancedNutrition = new LibraryContent(getActivity().getResources().getString(R.string.balanced_nutrition));

            libraryContents.add(birthEmergencyPlan);
            libraryContents.add(balancedNutrition);
            libraryContents.add(physicalActivity);
        }
        return libraryContents;
    }
}
