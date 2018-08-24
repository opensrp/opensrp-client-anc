package org.smartregister.anc.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.smartregister.anc.R;
import org.smartregister.anc.contract.LibraryContract;
import org.smartregister.anc.presenter.LibraryPresenter;

public class LibraryFragment extends Fragment implements LibraryContract.View {
	private LibraryContract.Presenter presenter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initializePresenter();
	}
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_library, container, false);
		View sortLayout = view.findViewById(R.id.library_placeholder);
		return view;
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}
	
	private void initializePresenter() {
		presenter = new LibraryPresenter(this);
	}
}
