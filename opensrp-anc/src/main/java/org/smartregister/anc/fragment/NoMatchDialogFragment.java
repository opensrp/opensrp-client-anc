package org.smartregister.anc.fragment;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.BaseRegisterActivity;
import org.smartregister.anc.activity.HomeRegisterActivity;
import org.smartregister.anc.contract.NoMatchDialogContract;
import org.smartregister.anc.presenter.NoMatchDialogPresenter;

@SuppressLint("ValidFragment")
public class NoMatchDialogFragment extends DialogFragment implements NoMatchDialogContract.View {
	private final NoMatchDialogActionHandler noMatchDialogActionHandler = new NoMatchDialogActionHandler();
	private final BaseRegisterActivity baseRegisterActivity;
	private final String whoAncId;
	private NoMatchDialogContract.Presenter presenter;
	
	public NoMatchDialogFragment(BaseRegisterActivity baseRegisterActivity, String whoAncId) {
		this.whoAncId = whoAncId;
		this.baseRegisterActivity = baseRegisterActivity;
	}
	
	public static NoMatchDialogFragment launchDialog(BaseRegisterActivity activity, String dialogTag, String whoAncId) {
		NoMatchDialogFragment noMatchDialogFragment = new NoMatchDialogFragment(activity, whoAncId);
		FragmentTransaction fragmentTransaction = activity.getFragmentManager().beginTransaction();
		Fragment prev = activity.getFragmentManager().findFragmentByTag(dialogTag);
		if (prev != null) {
			fragmentTransaction.remove(prev);
		}
		fragmentTransaction.addToBackStack(null);
		
		noMatchDialogFragment.show(fragmentTransaction, dialogTag);
		
		return noMatchDialogFragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
		presenter = new NoMatchDialogPresenter(this);
	}
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup dialogView = (ViewGroup)inflater.inflate(R.layout.dialog_no_woman_match, container, false);
		Button cancel = (Button)dialogView.findViewById(R.id.cancel_no_match_dialog);
		cancel.setOnClickListener(noMatchDialogActionHandler);
		Button advancedSearch = (Button)dialogView.findViewById(R.id.go_to_advanced_search);
		advancedSearch.setOnClickListener(noMatchDialogActionHandler);
		return dialogView;
	}
	
	@Override
	public BaseRegisterActivity getBaseActivity() {
		return baseRegisterActivity;
	}
	
	////////////////////////////////////////////////////////////////
	// Inner classes
	////////////////////////////////////////////////////////////////
	
	private class NoMatchDialogActionHandler implements View.OnClickListener {
		
		private final HomeRegisterActivity homeRegisterActivity = (HomeRegisterActivity)getActivity();
		
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
				case R.id.cancel_no_match_dialog:
					dismiss();
					break;
				case R.id.go_to_advanced_search:
					presenter.goToAdvancedSearch(whoAncId);
					dismiss();
					break;
				default:
					break;
			}
		}
	}
}
