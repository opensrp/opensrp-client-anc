package org.smartregister.anc.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.smartregister.anc.R;
import org.smartregister.anc.contract.LibraryContract;
import org.smartregister.anc.presenter.LibraryPresenter;

public class LibraryFragment extends Fragment implements LibraryContract.View{
	private LibraryContract.Presenter presenter;
	
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
	
	}
	
	private void initializePresenter() {
		presenter = new LibraryPresenter(this);
	}
	
	@Override
	public void onStart() {
		super.onStart();
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
				default:
					break;
			}
		}
	}
}
