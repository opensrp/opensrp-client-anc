package org.smartregister.anc.listener;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.BaseRegisterActivity;
import org.smartregister.anc.activity.LibraryActivity;
import org.smartregister.anc.activity.MeActivity;

public class BottomNavigationListener implements BottomNavigationView.OnNavigationItemSelectedListener {
	
	private Activity context;
	
	public BottomNavigationListener(Activity context) {
		this.context = context;
	}
	
	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_clients:
				((BaseRegisterActivity) context).switchToBaseFragment();
				break;
			case R.id.action_search:
				((BaseRegisterActivity) context).switchToFragment(1);
				break;
			case R.id.action_register:
				((BaseRegisterActivity) context).startRegistration();
				break;
			case R.id.action_library:
				goToLibrary();
				break;
			case R.id.action_me:
				goToMe();
				break;
			default:
				break;
		}
		return true;
	}
	
	private void goToLibrary() {
		Intent intent = new Intent(context, LibraryActivity.class);
		context.startActivity(intent);
	}
	
	private void goToMe() {
		Intent intent = new Intent(context, MeActivity.class);
		context.startActivity(intent);
	}
}
