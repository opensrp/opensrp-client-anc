package org.smartregister.anc.contract;

public interface LibraryContract {
	interface Presenter {
		void updateInitials();
	}
	
	interface View {
		void updateInitialsText(String initials);
	}
	
	interface Interactor {
	
	}
	
	interface Model {
		String getInitials();
	}
}
