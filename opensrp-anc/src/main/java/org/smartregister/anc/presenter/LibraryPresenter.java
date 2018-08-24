package org.smartregister.anc.presenter;

import org.smartregister.anc.contract.LibraryContract;

import java.lang.ref.WeakReference;

public class LibraryPresenter implements LibraryContract.Presenter {
	private WeakReference<LibraryContract.View> libraryView;
	
	public LibraryPresenter(LibraryContract.View view) {
		this.libraryView = new WeakReference<>(view);
	}
}
