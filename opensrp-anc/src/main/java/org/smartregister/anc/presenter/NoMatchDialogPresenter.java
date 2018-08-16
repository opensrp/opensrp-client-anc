package org.smartregister.anc.presenter;

import android.annotation.SuppressLint;
import org.smartregister.anc.activity.BaseRegisterActivity;
import org.smartregister.anc.activity.HomeRegisterActivity;
import org.smartregister.anc.contract.NoMatchDialogContract;
import org.smartregister.anc.fragment.AdvancedSearchFragment;

import java.lang.ref.WeakReference;
import java.util.Objects;

public class NoMatchDialogPresenter implements NoMatchDialogContract.Presenter {
	private final BaseRegisterActivity parentActivity;
	private WeakReference<NoMatchDialogContract.View> viewReference;
	
	@SuppressLint("NewApi")
	public NoMatchDialogPresenter(NoMatchDialogContract.View view) {
		this.viewReference = new WeakReference<>(view);
		this.parentActivity = Objects.requireNonNull(getView()).getBaseActivity();
	}
	
	@Override
	public void goToAdvancedSearch(String whoAncId) {
		((HomeRegisterActivity) parentActivity).startAdvancedSearch();
		android.support.v4.app.Fragment currentFragment = parentActivity.findFragmentByPosition(HomeRegisterActivity.ADVANCED_SEARCH_POSITION);
		((AdvancedSearchFragment)currentFragment).getAncId().setText(whoAncId);
	}
	
	private NoMatchDialogContract.View getView() {
		if (viewReference != null)
			return viewReference.get();
		else
			return null;
	}
	
}
