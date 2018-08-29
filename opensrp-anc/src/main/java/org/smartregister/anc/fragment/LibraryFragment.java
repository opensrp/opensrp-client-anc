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
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.HomeRegisterActivity;
import org.smartregister.anc.contract.LibraryContract;
import org.smartregister.anc.listener.BottomNavigationListener;
import org.smartregister.anc.presenter.LibraryPresenter;
import org.smartregister.anc.util.BottomNavigationHelper;
import org.smartregister.anc.util.Constants;

public class LibraryFragment extends Fragment implements LibraryContract.View, BottomNavigationView.OnNavigationItemSelectedListener{
	private LibraryContract.Presenter presenter;
	private BottomNavigationListener bottomNavigationListener;
	private LibraryClickListener libraryClickListener = new LibraryClickListener();
	private BottomNavigationView bottomNavigationView;
	protected TextView initialMenuItem;
	protected TextView initialMenuItemText;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initializePresenter();
	}
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_library, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setUpViews(view);
	}
	
	private void setUpViews(View view) {
		bottomNavigationView = view.findViewById(R.id.bottom_navigation);
		if (bottomNavigationView != null) {
			BottomNavigationHelper.disableShiftMode(bottomNavigationView);
			BottomNavigationHelper.addMeMenuItem(bottomNavigationView,getContext());
			
			RelativeLayout relativeLayout = bottomNavigationView.findViewById(Constants.BOTTOM_NAV_MENU_ME);
			initialMenuItem = relativeLayout.findViewById(R.id.name_initials);
			initialMenuItemText = relativeLayout.findViewById(R.id.name_initials_text);
			
			bottomNavigationListener = new BottomNavigationListener(this.getActivity());
			bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationListener);
		}
		
		initialMenuItem.setOnClickListener(libraryClickListener);
		initialMenuItemText.setOnClickListener(libraryClickListener);
		
		presenter.updateInitials();
	}
	
	private void initializePresenter() {
		presenter = new LibraryPresenter(this);
	}
	
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		return bottomNavigationListener.onNavigationItemSelected(item);
	}
	
	@Override
	public void updateInitialsText(String initials) {
		if(initialMenuItem != null) {
			initialMenuItem.setText(initials);
		}
	}
	
	/**
	 * Handles the click events on the Library Fragment
	 */
	private class LibraryClickListener implements View.OnClickListener {
		
		@Override
		public void onClick(View view) {
			if (getActivity() == null) {
				return;
			}
			
			switch (view.getId()) {
				case R.id.name_initials:
					switchToMeFragment();
					break;
				case R.id.name_initials_text:
					switchToMeFragment();
					break;
				default:
					break;
			}
		}
		
		private void switchToMeFragment() {
			HomeRegisterActivity homeRegisterActivity = (HomeRegisterActivity) getActivity();
			homeRegisterActivity.switchToFragment(3);
		}
	}
}
