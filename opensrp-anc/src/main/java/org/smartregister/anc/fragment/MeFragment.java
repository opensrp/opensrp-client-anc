package org.smartregister.anc.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.smartregister.anc.R;
import org.smartregister.anc.contract.MeContract;
import org.smartregister.anc.presenter.MePresenter;
import org.smartregister.anc.util.BottomNavigationBarActionHandler;
import org.smartregister.anc.util.DisableShitModeBottomNavigation;

public class MeFragment extends Fragment implements MeContract.View {
	private MeContract.Presenter presenter;
	
	protected BottomNavigationBarActionHandler
			bottomNavigationBarActionHandler = new BottomNavigationBarActionHandler();
	
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
	}
	
	private void setUpViews(View view) {
		BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);
		if (bottomNavigationView != null) {
			DisableShitModeBottomNavigation.disableShiftMode(bottomNavigationView);
			bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationBarActionHandler);
		}
	}
	
	private void initializePresenter() {
		presenter = new MePresenter(this);
	}
}
