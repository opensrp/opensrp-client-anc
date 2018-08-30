package org.smartregister.anc.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.BaseRegisterActivity;
import org.smartregister.anc.contract.MeContract;
import org.smartregister.anc.helper.BottomNavigationHelper;
import org.smartregister.anc.presenter.MePresenter;
import org.smartregister.anc.util.Constants;

public class MeFragment extends Fragment implements MeContract.View {
	private MeContract.Presenter presenter;
	
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
	
	}
	
	private void initializePresenter() {
		presenter = new MePresenter(this);
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}
	
	/**
	 * Handles the click events of attribute in the Me Fragment
	 */
	private class MeClickListener implements View.OnClickListener {
		
		@Override
		public void onClick(View view) {
			if (getActivity() == null) {
				return;
			}
			
			switch (view.getId()) {
				default:
					break;
			}
		}
		
		
	}
}
