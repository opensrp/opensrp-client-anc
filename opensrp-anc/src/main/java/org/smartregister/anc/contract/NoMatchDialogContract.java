package org.smartregister.anc.contract;

import org.smartregister.anc.activity.BaseRegisterActivity;

public interface NoMatchDialogContract {
	
	interface View {
		BaseRegisterActivity getBaseActivity();
	}
	
	interface Presenter {
		void goToAdvancedSearch(String whoAncId);
	}
}
