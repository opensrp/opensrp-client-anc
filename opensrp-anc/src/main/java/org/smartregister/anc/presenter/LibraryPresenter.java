package org.smartregister.anc.presenter;

import org.smartregister.anc.contract.LibraryContract;
import org.smartregister.anc.model.LibraryFragmentModel;

import java.lang.ref.WeakReference;

public class LibraryPresenter implements LibraryContract.Presenter {
	private WeakReference<LibraryContract.View> libraryView;
	private LibraryContract.Model model;
	
	public LibraryPresenter(LibraryContract.View view) {
		this.libraryView = new WeakReference<>(view);
		this.model = new LibraryFragmentModel();
	}
	
	protected LibraryContract.View getView() {
		if (libraryView != null)
			return libraryView.get();
		else
			return null;
	}
}
