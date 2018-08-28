package org.smartregister.anc.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import org.smartregister.anc.R;
import org.smartregister.anc.contract.MeContract;
import org.smartregister.anc.listener.BottomNavigationListener;
import org.smartregister.anc.presenter.MePresenter;
import org.smartregister.anc.util.DisableShitModeBottomNavigation;

public class MeFragment extends Fragment implements MeContract.View, BottomNavigationView.OnNavigationItemSelectedListener {
	private MeContract.Presenter presenter;
	private BottomNavigationListener bottomNavigationListener;
	private BottomNavigationView bottomNavigationView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initializePresenter();
	}
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_me, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setUpViews(view);
	}
	
	private void setUpViews(View view) {
		bottomNavigationView = view.findViewById(R.id.bottom_navigation);
		if (bottomNavigationView != null) {
			DisableShitModeBottomNavigation.disableShiftMode(bottomNavigationView);
			bottomNavigationListener = new BottomNavigationListener(this.getActivity());
			bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationListener);
		}
	}
	
	private void initializePresenter() {
		presenter = new MePresenter(this);
	}
	
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		return bottomNavigationListener.onNavigationItemSelected(item);
	}
}
